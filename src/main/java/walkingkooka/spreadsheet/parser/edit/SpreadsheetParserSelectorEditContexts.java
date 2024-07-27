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

package walkingkooka.spreadsheet.parser.edit;

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;

public final class SpreadsheetParserSelectorEditContexts implements PublicStaticHelper {

    /**
     * {see BasicSpreadsheetParserSelectorEditContext}
     */
    public static SpreadsheetParserSelectorEditContext basic(final SpreadsheetParserProvider spreadsheetParserProvider,
                                                             final SpreadsheetParserContext spreadsheetParserContext,
                                                             final SpreadsheetFormatterContext spreadsheetFormatterContext,
                                                             final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        return BasicSpreadsheetParserSelectorEditContext.with(
                spreadsheetParserProvider,
                spreadsheetParserContext,
                spreadsheetFormatterContext,
                spreadsheetFormatterProvider
        );
    }

    /**
     * {@see FakeSpreadsheetParserSelectorEditContext}
     */
    public static FakeSpreadsheetParserSelectorEditContext fake() {
        return new FakeSpreadsheetParserSelectorEditContext();
    }

    private SpreadsheetParserSelectorEditContexts() {
        throw new UnsupportedOperationException();
    }
}
