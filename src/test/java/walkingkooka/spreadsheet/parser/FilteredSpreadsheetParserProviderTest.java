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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.List;

public final class FilteredSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<FilteredSpreadsheetParserProvider>,
    SpreadsheetMetadataTesting,
    ToStringTesting<FilteredSpreadsheetParserProvider> {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetParserName() {
        final SpreadsheetParserName name = SpreadsheetParserName.DATE_PARSER_PATTERN;
        final List<?> values = Lists.of("dd/mmm/yyyy");

        this.spreadsheetParserAndCheck(
            name,
            values,
            CONTEXT,
            SpreadsheetParserProviders.spreadsheetParsePattern(SPREADSHEET_FORMATTER_PROVIDER)
                .spreadsheetParser(
                    name,
                    values,
                    CONTEXT
                )
        );
    }

    @Test
    public void testSpreadsheetParserWithFilteredFails() {
        final SpreadsheetParserName name = SpreadsheetParserName.TIME_PARSER_PATTERN;
        final String pattern = "h/m/s.SSS AM/PM";
        final List<?> values = Lists.of(pattern);

        this.spreadsheetParserAndCheck(
            SpreadsheetParserProviders.spreadsheetParsePattern(SPREADSHEET_FORMATTER_PROVIDER),
            name,
            values,
            CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern(pattern)
                .parser()
        );


        this.spreadsheetParserFails(
            name,
            values,
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserInfos() {
        this.spreadsheetParserInfosAndCheck(
            SpreadsheetParserInfoSet.EMPTY.concat(
                SpreadsheetParserInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date-parse-pattern date-parse-pattern")
            )
        );
    }

    @Override
    public FilteredSpreadsheetParserProvider createSpreadsheetParserProvider() {
        return FilteredSpreadsheetParserProvider.with(
            SpreadsheetParserProviders.spreadsheetParsePattern(
                SPREADSHEET_FORMATTER_PROVIDER
            ),
            SpreadsheetParserInfoSet.EMPTY.concat(
                SpreadsheetParserInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetParser/date-parse-pattern date-parse-pattern")
            )
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createSpreadsheetParserProvider(),
            SpreadsheetParserProviders.spreadsheetParsePattern(SPREADSHEET_FORMATTER_PROVIDER)
                .toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredSpreadsheetParserProvider> type() {
        return FilteredSpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
