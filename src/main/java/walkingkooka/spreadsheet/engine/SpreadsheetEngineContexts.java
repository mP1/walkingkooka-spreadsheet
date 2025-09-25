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

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;

public final class SpreadsheetEngineContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngineContext}
     */
    public static SpreadsheetEngineContext basic(final AbsoluteUrl serverUrl,
                                                 final SpreadsheetMetadata metadata,
                                                 final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases,
                                                 final SpreadsheetContext spreadsheetContext,
                                                 final TerminalContext terminalContext) {
        return BasicSpreadsheetEngineContext.with(
            serverUrl,
            metadata,
            functionAliases,
            spreadsheetContext,
            terminalContext
        );
    }

    /**
     * {@see FakeSpreadsheetEngineContext}
     */
    public static SpreadsheetEngineContext fake() {
        return new FakeSpreadsheetEngineContext();
    }

    /**
     * Stops creation
     */
    private SpreadsheetEngineContexts() {
        throw new UnsupportedOperationException();
    }
}
