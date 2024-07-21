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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;

public final class EmptySpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<EmptySpreadsheetParserProvider> {

    @Test
    public void testSpreadsheetParserSelectorFails() {
        this.spreadsheetParserFails(
                SpreadsheetParserName.DATE_TIME_PARSER_PATTERN.setText("")
        );
    }

    @Test
    public void testSpreadsheetParserNameFails() {
        this.spreadsheetParserFails(
                SpreadsheetParserName.DATE_TIME_PARSER_PATTERN,
                Lists.empty()
        );
    }

    @Test
    public void testSpreadsheetParserNextTextComponentFails() {
        this.spreadsheetParserNextTextComponentFails(
                SpreadsheetParserName.DATE_TIME_PARSER_PATTERN.setText("")
        );
    }

    @Override
    public EmptySpreadsheetParserProvider createSpreadsheetParserProvider() {
        return EmptySpreadsheetParserProvider.INSTANCE;
    }

    @Override
    public Class<EmptySpreadsheetParserProvider> type() {
        return EmptySpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
