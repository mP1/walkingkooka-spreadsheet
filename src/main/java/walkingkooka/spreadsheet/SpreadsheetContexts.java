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

import walkingkooka.currency.CurrencyContext;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.function.Function;

public final class SpreadsheetContexts implements PublicStaticHelper {

    /**
     * {@see SpreadsheetContextSharedFixedSpreadsheetId}
     */
    public static SpreadsheetContext fixedSpreadsheetId(final SpreadsheetEngine spreadsheetEngine,
                                                        final SpreadsheetStoreRepository storeRepository,
                                                        final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory,
                                                        final CurrencyContext currencyContext,
                                                        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                        final LocaleContext localeContext,
                                                        final SpreadsheetProvider spreadsheetProvider,
                                                        final ProviderContext providerContext) {
        return SpreadsheetContextSharedFixedSpreadsheetId.with(
            spreadsheetEngine,
            storeRepository,
            httpRouterFactory,
            currencyContext,
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }

    /**
     * {@see FakeSpreadsheetContext}
     */
    public static SpreadsheetContext fake() {
        return new FakeSpreadsheetContext();
    }

    /**
     * {@see SpreadsheetContextSharedMutableSpreadsheetId}
     */
    public static SpreadsheetContext mutableSpreadsheetId(final SpreadsheetEngine spreadsheetEngine,
                                                          final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                          final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                          final CurrencyContext currencyContext,
                                                          final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                          final LocaleContext localeContext,
                                                          final SpreadsheetProvider spreadsheetProvider,
                                                          final ProviderContext providerContext) {
        return SpreadsheetContextSharedMutableSpreadsheetId.with(
            spreadsheetEngine,
            spreadsheetContextSupplier,
            spreadsheetMetadataContext,
            currencyContext,
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetContexts() {
        throw new UnsupportedOperationException();
    }
}
