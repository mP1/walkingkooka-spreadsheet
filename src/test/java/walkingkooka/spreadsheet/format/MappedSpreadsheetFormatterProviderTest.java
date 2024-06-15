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

public final class MappedSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<MappedSpreadsheetFormatterProvider> {

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> MappedSpreadsheetFormatterProvider.with(
                        null,
                        SpreadsheetFormatterProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> MappedSpreadsheetFormatterProvider.with(
                        Sets.of(
                                SpreadsheetFormatterInfo.with(
                                        SpreadsheetFormatterProviders.BASE_URL.appendPath(UrlPath.parse("date-format-pattern")),
                                        SpreadsheetFormatterName.with("new-date-format-pattern")
                                )
                        ),
                        null
                )
        );
    }

    private final static String NEW_FORMATTER_NAME = "new-date-format-pattern";

    @Test
    public void testSpreadsheetFormatter() {
        final String pattern = "yyyy/mm/dd";

        this.spreadsheetFormatterAndCheck(
                NEW_FORMATTER_NAME + " " + pattern,
                SpreadsheetPattern.parseDateFormatPattern(pattern).formatter()
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetFormatterInfosAndCheck(
                SpreadsheetFormatterInfo.with(
                        url("date-format-pattern"),
                        SpreadsheetFormatterName.with(NEW_FORMATTER_NAME)
                ),
                SpreadsheetFormatterInfo.with(
                        url("date-time-format-pattern"),
                        SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN
                ),
                SpreadsheetFormatterInfo.with(
                        url("number-format-pattern"),
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN
                ),
                SpreadsheetFormatterInfo.with(
                        url("text-format-pattern"),
                        SpreadsheetFormatterName.TEXT_FORMAT_PATTERN
                ),
                SpreadsheetFormatterInfo.with(
                        url("time-format-pattern"),
                        SpreadsheetFormatterName.TIME_FORMAT_PATTERN
                )
        );
    }

    @Override
    public MappedSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        final SpreadsheetFormatterProvider provider = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

        return MappedSpreadsheetFormatterProvider.with(
                Sets.of(
                        SpreadsheetFormatterInfo.with(
                                url("date-format-pattern"),
                                SpreadsheetFormatterName.with(NEW_FORMATTER_NAME)
                        )
                ),
                provider
        );
    }

    private static AbsoluteUrl url(final String formatterName) {
        return SpreadsheetFormatterProviders.BASE_URL.appendPath(UrlPath.parse(formatterName));
    }

    @Override
    public Class<MappedSpreadsheetFormatterProvider> type() {
        return MappedSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
