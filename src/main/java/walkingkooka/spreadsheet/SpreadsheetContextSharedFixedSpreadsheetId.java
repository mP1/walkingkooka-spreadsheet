/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet;

import walkingkooka.locale.LocaleContext;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link SpreadsheetContext} for a single unchanging {@link SpreadsheetId} for a single spreadsheet.
 */
final class SpreadsheetContextSharedFixedSpreadsheetId extends SpreadsheetContextShared {

    static SpreadsheetContextSharedFixedSpreadsheetId with(final SpreadsheetStoreRepository storeRepository,
                                                           final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                           final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory,
                                                           final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                           final LocaleContext localeContext,
                                                           final SpreadsheetProvider spreadsheetProvider,
                                                           final ProviderContext providerContext) {
        Objects.requireNonNull(storeRepository, "storeRepository");
        Objects.requireNonNull(spreadsheetEngineContextFactory, "spreadsheetEngineContextFactory");
        Objects.requireNonNull(httpRouterFactory, "httpRouterFactory");
        Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext");
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetContextSharedFixedSpreadsheetId(
            storeRepository,
            null, // SpreadsheetStoreRepository
            spreadsheetEngineContextFactory,
            null, // SpreadsheetEngineContext will be created in ctor
            httpRouterFactory,
            null, // HttpRouter
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }

    private SpreadsheetContextSharedFixedSpreadsheetId(final SpreadsheetStoreRepository storeRepository,
                                                       final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                       final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                       final SpreadsheetEngineContext spreadsheetEngineContext,
                                                       final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory,
                                                       final Router<HttpRequestAttribute<?>, HttpHandler> httpRouter,
                                                       final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                       final LocaleContext localeContext,
                                                       final SpreadsheetProvider spreadsheetProvider,
                                                       final ProviderContext providerContext) {
        super(
            spreadsheetEngineContextFactory,
            spreadsheetEngineContext,
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );

        this.storeRepository = storeRepository;

        // lazy create
        this.spreadsheetMetadataContext = null != spreadsheetMetadataContext ?
            spreadsheetMetadataContext :
            SpreadsheetMetadataContexts.basic(
                this::createMetadata,
                storeRepository.metadatas()
            );

        this.httpRouter = httpRouter;
        this.httpRouterFactory = httpRouterFactory;
    }

    // storeRepository..................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    public SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetMetadataContext;
    }

    private final SpreadsheetMetadataContext spreadsheetMetadataContext;

    // httpRouter......................................................................................................

    @Override
    public Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        if (null == this.httpRouter) {
            this.httpRouter = this.httpRouterFactory.apply(this.spreadsheetEngineContext());
        }
        return this.httpRouter;
    }

    /**
     * The lazy cached router for this spreadsheet.
     */
    private Router<HttpRequestAttribute<?>, HttpHandler> httpRouter;

    private final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory;

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetContext replaceEnvironmentContext(final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                 final SpreadsheetEngineContext spreadsheetEngineContext,
                                                 final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                 final LocaleContext localeContext,
                                                 final SpreadsheetProvider spreadsheetProvider,
                                                 final ProviderContext providerContext) {
        return new SpreadsheetContextSharedFixedSpreadsheetId(
            this.storeRepository, // keep
            this.spreadsheetMetadataContext, // keep
            spreadsheetEngineContextFactory,
            spreadsheetEngineContext,
            this.httpRouterFactory,
            null, // recreate HttpRouter,
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }

    @Override
    boolean canChangeSpreadsheetId() {
        return false;
    }
}
