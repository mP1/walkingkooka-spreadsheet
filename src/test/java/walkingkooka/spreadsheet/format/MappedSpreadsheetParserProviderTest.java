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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MappedSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<MappedSpreadsheetParserProvider> {

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> MappedSpreadsheetParserProvider.with(
                        null,
                        SpreadsheetParserProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> MappedSpreadsheetParserProvider.with(
                        Sets.of(
                                SpreadsheetParserInfo.with(
                                        url("date-parse-pattern"),
                                        SpreadsheetParserName.with("new-date-parse-pattern")
                                )
                        ),
                        null
                )
        );
    }

    private final static String NEW_PARSER_NAME = "new-date-parse-pattern";

    @Test
    public void testSpreadsheetParser() {
        final String pattern = "yyyy/mm/dd";

        this.spreadsheetParserAndCheck(
                NEW_PARSER_NAME + " " + pattern,
                SpreadsheetPattern.parseDateParsePattern(pattern).parser()
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetParserInfosAndCheck(
                SpreadsheetParserInfo.with(
                        url("date-parse-pattern"),
                        SpreadsheetParserName.with(NEW_PARSER_NAME) // only this parser has a new name the others remain unchanged.
                ),
                SpreadsheetParserInfo.with(
                        url("date-time-parse-pattern"),
                        SpreadsheetParserName.DATE_TIME_PARSE_PATTERN
                ),
                SpreadsheetParserInfo.with(
                        url("number-parse-pattern"),
                        SpreadsheetParserName.NUMBER_PARSE_PATTERN
                ),
                SpreadsheetParserInfo.with(
                        url("time-parse-pattern"),
                        SpreadsheetParserName.TIME_PARSE_PATTERN
                )
        );
    }

    @Override
    public MappedSpreadsheetParserProvider createSpreadsheetParserProvider() {
        final SpreadsheetParserProvider provider = SpreadsheetParserProviders.spreadsheetParsePattern();

        return MappedSpreadsheetParserProvider.with(
                Sets.of(
                        SpreadsheetParserInfo.with(
                                url("date-parse-pattern"),
                                SpreadsheetParserName.with(NEW_PARSER_NAME)
                        )
                ),
                provider
        );
    }

    private static AbsoluteUrl url(final String parserName) {
        return SpreadsheetParserProviders.BASE_URL.appendPath(UrlPath.parse(parserName));
    }

    @Override
    public Class<MappedSpreadsheetParserProvider> type() {
        return MappedSpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
