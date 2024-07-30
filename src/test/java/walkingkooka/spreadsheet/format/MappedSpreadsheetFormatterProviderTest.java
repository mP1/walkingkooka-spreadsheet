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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.tree.text.TextNode;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MappedSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<MappedSpreadsheetFormatterProvider>,
        SpreadsheetMetadataTesting {

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
    public void testSpreadsheetFormatterSelector() {
        final String pattern = "yyyy/mm/dd";

        this.spreadsheetFormatterAndCheck(
                NEW_FORMATTER_NAME + " " + pattern,
                SpreadsheetPattern.parseDateFormatPattern(pattern)
                        .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterName() {
        final String pattern = "yyyy/mm/dd";

        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with(NEW_FORMATTER_NAME),
                Lists.of(pattern),
                SpreadsheetPattern.parseDateFormatPattern(pattern)
                        .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentAutomatic() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("automatic")
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentDifferentFormatterName() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("" + NEW_FORMATTER_NAME),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTextComponentDifferentFormatterNameNotEmptyText() {
        this.spreadsheetFormatterNextTextComponentAndCheck(
                SpreadsheetFormatterSelector.parse("" + NEW_FORMATTER_NAME + " dd"),
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    // Short
    //  new-date-format-pattern
    //    "d/m/yy"
    //  Text "31/12/99"
    //
    //Medium
    //  new-date-format-pattern
    //    "d mmm yyyy"
    //  Text "31 Dec. 1999"
    //
    //Long
    //  new-date-format-pattern
    //    "d mmmm yyyy"
    //  Text "31 December 1999"
    //
    //Full
    //  new-date-format-pattern
    //    "dddd, d mmmm yyyy"
    //  Text "Friday, 31 December 1999"
    @Test
    public void testSpreadsheetFormatterSamples() {
        final SpreadsheetFormatterName name = SpreadsheetFormatterName.with(NEW_FORMATTER_NAME);

        this.spreadsheetFormatterSamplesAndCheck(
                name,
                SPREADSHEET_FORMATTER_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "Short",
                        name.setText("d/m/yy"),
                        TextNode.text("31/12/99")
                ),
                SpreadsheetFormatterSample.with(
                        "Medium",
                        name.setText("d mmm yyyy"),
                        TextNode.text("31 Dec. 1999")
                ),
                SpreadsheetFormatterSample.with(
                        "Long",
                        name.setText("d mmmm yyyy"),
                        TextNode.text("31 December 1999")
                ),
                SpreadsheetFormatterSample.with(
                        "Full",
                        name.setText("dddd, d mmmm yyyy"),
                        TextNode.text("Friday, 31 December 1999")
                )
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetFormatterInfosAndCheck(
                SpreadsheetFormatterInfo.with(
                        url("automatic"),
                        SpreadsheetFormatterName.AUTOMATIC
                ),
                SpreadsheetFormatterInfo.with(
                        url("collection"),
                        SpreadsheetFormatterName.COLLECTION
                ),
                SpreadsheetFormatterInfo.with(
                        url("date-format-pattern"),
                        SpreadsheetFormatterName.with(NEW_FORMATTER_NAME)
                ),
                SpreadsheetFormatterInfo.with(
                        url("date-time-format-pattern"),
                        SpreadsheetFormatterName.DATE_TIME_FORMAT_PATTERN
                ),
                SpreadsheetFormatterInfo.with(
                        url("general"),
                        SpreadsheetFormatterName.GENERAL
                ),
                SpreadsheetFormatterInfo.with(
                        url("number-format-pattern"),
                        SpreadsheetFormatterName.NUMBER_FORMAT_PATTERN
                ),
                SpreadsheetFormatterInfo.with(
                        url("spreadsheet-pattern-collection"),
                        SpreadsheetFormatterName.SPREADSHEET_PATTERN_COLLECTION
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
