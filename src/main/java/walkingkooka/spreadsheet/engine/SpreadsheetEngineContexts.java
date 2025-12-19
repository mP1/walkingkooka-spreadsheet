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

package walkingkooka.spreadsheet.engine;

import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.terminal.TerminalContext;

public final class SpreadsheetEngineContexts implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetEngineContext}
     */
    public static SpreadsheetEngineContext fake() {
        return new FakeSpreadsheetEngineContext();
    }

    /**
     * {@see SpreadsheetEngineContextSharedSpreadsheetContext}
     */
    public static SpreadsheetEngineContext spreadsheetContext(final SpreadsheetMetadataMode mode,
                                                              final SpreadsheetContext spreadsheetContext,
                                                              final TerminalContext terminalContext) {
        return SpreadsheetEngineContextSharedSpreadsheetContext.with(
            mode,
            spreadsheetContext,
            terminalContext
        );
    }

    /**
     * {@see SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext}
     */
    public static SpreadsheetEngineContext spreadsheetEnvironmentContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                         final LocaleContext localeContext,
                                                                         final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                                         final TerminalContext terminalContext,
                                                                         final SpreadsheetProvider spreadsheetProvider,
                                                                         final ProviderContext providerContext) {
        return SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetMetadataContext,
            terminalContext,
            spreadsheetProvider,
            providerContext
        );
    }

    /**
     * Stops creation
     */
    private SpreadsheetEngineContexts() {
        throw new UnsupportedOperationException();
    }
}
