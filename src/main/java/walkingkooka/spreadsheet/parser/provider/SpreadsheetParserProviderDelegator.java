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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;

import java.util.List;
import java.util.Optional;

public interface SpreadsheetParserProviderDelegator extends SpreadsheetParserProvider {

    @Override
    default SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                                final ProviderContext context) {
        return this.spreadsheetParserProvider()
            .spreadsheetParser(
                selector,
                context
            );
    }

    @Override
    default SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                                final List<?> values,
                                                final ProviderContext context) {
        return this.spreadsheetParserProvider()
            .spreadsheetParser(
                name,
                values,
                context
            );
    }

    @Override
    default Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        return this.spreadsheetParserProvider().spreadsheetParserNextToken(selector);
    }

    @Override
    default Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        return this.spreadsheetParserProvider().spreadsheetFormatterSelector(selector);
    }

    @Override
    default SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return this.spreadsheetParserProvider().spreadsheetParserInfos();
    }

    SpreadsheetParserProvider spreadsheetParserProvider();
}
