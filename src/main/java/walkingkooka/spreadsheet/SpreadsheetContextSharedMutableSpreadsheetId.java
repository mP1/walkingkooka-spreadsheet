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

import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.terminal.TerminalContexts;

import java.util.Objects;

/**
 * A {@link SpreadsheetContext} that allows the {@link SpreadsheetId} to be set via the environment which will change
 * the {@link #storeRepository()}.
 * This {@link SpreadsheetContext} is intended to be used by terminal sessions, which allow the {@link SpreadsheetId}
 * to be changed.
 */
final class SpreadsheetContextSharedMutableSpreadsheetId extends SpreadsheetContextShared {

    static SpreadsheetContextSharedMutableSpreadsheetId with(final SpreadsheetEngine spreadsheetEngine,
                                                             final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                             final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                             final CurrencyLocaleContext currencyLocaleContext,
                                                             final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                             final SpreadsheetProvider spreadsheetProvider,
                                                             final ProviderContext providerContext) {
        Objects.requireNonNull(spreadsheetEngine, "spreadsheetEngine");
        Objects.requireNonNull(spreadsheetContextSupplier, "spreadsheetContextSupplier");
        Objects.requireNonNull(spreadsheetMetadataContext, "spreadsheetMetadataContext");
        Objects.requireNonNull(currencyLocaleContext, "currencyLocaleContext");
        Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetContextSharedMutableSpreadsheetId(
            spreadsheetEngine,
            spreadsheetContextSupplier,
            spreadsheetMetadataContext,
            null, // SpreadsheetEngineContext will be created in ctor
            currencyLocaleContext,
            spreadsheetEnvironmentContext,
            spreadsheetProvider,
            providerContext
        );
    }

    private SpreadsheetContextSharedMutableSpreadsheetId(final SpreadsheetEngine spreadsheetEngine,
                                                         final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                         final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                         final SpreadsheetEngineContext spreadsheetEngineContext,
                                                         final CurrencyLocaleContext currencyLocaleContext,
                                                         final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                         final SpreadsheetProvider spreadsheetProvider,
                                                         final ProviderContext providerContext) {
        super(
            spreadsheetEngine,
            spreadsheetEngineContext,
            currencyLocaleContext,
            spreadsheetEnvironmentContext,
            spreadsheetProvider,
            providerContext
        );

        this.spreadsheetContextSupplier = spreadsheetContextSupplier;

        this.spreadsheetMetadataContext = spreadsheetMetadataContext;
    }

    // storeRepository..................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetContextSupplier.spreadsheetContextOrFail(
            this.spreadsheetIdOrFail()
        ).storeRepository();
    }

    private final SpreadsheetContextSupplier spreadsheetContextSupplier;


    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    public SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetMetadataContext;
    }

    private final SpreadsheetMetadataContext spreadsheetMetadataContext;

    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    SpreadsheetEngineContext createSpreadsheetEngineContext() {
        return SpreadsheetEngineContexts.spreadsheetEnvironmentContext(
            this.spreadsheetContextSupplier,
            this.currencyContext(),
            this.spreadsheetEnvironmentContext(),
            this.localeContext(),
            this.spreadsheetMetadataContext,
            TerminalContexts.fake(),
            this.spreadsheetProvider(),
            this.providerContext()
        );
    }

    // httpRouter.......................................................................................................

    @Override
    public Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        throw new UnsupportedOperationException();
    }

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetContext replaceEnvironmentContext(final SpreadsheetEngineContext spreadsheetEngineContext,
                                                 final CurrencyLocaleContext currencyLocaleContext,
                                                 final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                 final SpreadsheetProvider spreadsheetProvider,
                                                 final ProviderContext providerContext) {
        return new SpreadsheetContextSharedMutableSpreadsheetId(
            this.spreadsheetEngine,
            this.spreadsheetContextSupplier,
            this.spreadsheetMetadataContext,
            null, // recreate SpreadsheetEngineContext
            currencyLocaleContext,
            spreadsheetEnvironmentContext,
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
