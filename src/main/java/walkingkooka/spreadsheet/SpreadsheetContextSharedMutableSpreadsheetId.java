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
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link SpreadsheetContext} that allows the {@link SpreadsheetId} to be set via the environment which will change
 * the {@link #storeRepository()}.
 * This {@link SpreadsheetContext} is intended to be used by terminal sessions, which allow the {@link SpreadsheetId}
 * to be changed.
 */
final class SpreadsheetContextSharedMutableSpreadsheetId extends SpreadsheetContextShared {

    static SpreadsheetContextSharedMutableSpreadsheetId with(final SpreadsheetEngine spreadsheetEngine,
                                                             final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToStoreRepository,
                                                             final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                             final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                             final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                             final LocaleContext localeContext,
                                                             final SpreadsheetProvider spreadsheetProvider,
                                                             final ProviderContext providerContext) {
        Objects.requireNonNull(spreadsheetEngine, "spreadsheetEngine");
        Objects.requireNonNull(spreadsheetIdToStoreRepository, "spreadsheetIdToStoreRepository");
        Objects.requireNonNull(spreadsheetMetadataContext, "spreadsheetMetadataContext");
        Objects.requireNonNull(spreadsheetEngineContextFactory, "spreadsheetEngineContextFactory");
        Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext");
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetContextSharedMutableSpreadsheetId(
            spreadsheetEngine,
            spreadsheetIdToStoreRepository,
            spreadsheetMetadataContext,
            spreadsheetEngineContextFactory,
            null, // SpreadsheetEngineContext will be created in ctor
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }

    private SpreadsheetContextSharedMutableSpreadsheetId(final SpreadsheetEngine spreadsheetEngine,
                                                         final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToStoreRepository,
                                                         final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                         final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                         final SpreadsheetEngineContext spreadsheetEngineContext,
                                                         final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                         final LocaleContext localeContext,
                                                         final SpreadsheetProvider spreadsheetProvider,
                                                         final ProviderContext providerContext) {
        super(
            spreadsheetEngine,
            spreadsheetEngineContextFactory,
            spreadsheetEngineContext,
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );

        this.spreadsheetIdToStoreRepository = spreadsheetIdToStoreRepository;

        this.spreadsheetMetadataContext = spreadsheetMetadataContext;
    }

    // storeRepository..................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetIdToStoreRepository.apply(this.spreadsheetId());
    }

    private final Function<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToStoreRepository;


    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    public SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetMetadataContext;
    }

    private final SpreadsheetMetadataContext spreadsheetMetadataContext;

    // httpRouter.......................................................................................................

    @Override
    public Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        throw new UnsupportedOperationException();
    }

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetContext replaceEnvironmentContext(final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                 final SpreadsheetEngineContext spreadsheetEngineContext,
                                                 final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                 final LocaleContext localeContext,
                                                 final SpreadsheetProvider spreadsheetProvider,
                                                 final ProviderContext providerContext) {
        return new SpreadsheetContextSharedMutableSpreadsheetId(
            this.spreadsheetEngine,
            this.spreadsheetIdToStoreRepository,
            this.spreadsheetMetadataContext,
            spreadsheetEngineContextFactory,
            null, // recreate SpreadsheetEngineContext
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }

    /**
     * Allow {@link SpreadsheetId} to be set with a new value or removed, something that might happen within a terminal session.
     */
    @Override
    boolean canChangeSpreadsheetId() {
        return true;
    }
}
