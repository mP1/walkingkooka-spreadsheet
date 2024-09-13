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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.tree.text.TextNode;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MappedSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<MappedSpreadsheetFormatterProvider>,
        SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

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
                        SpreadsheetFormatterInfoSet.EMPTY.concat(
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
                CONTEXT,
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
                CONTEXT,
                SpreadsheetPattern.parseDateFormatPattern(pattern)
                        .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenAutomatic() {
        this.spreadsheetFormatterNextTokenAndCheck(
                SpreadsheetFormatterSelector.parse("automatic")
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenDifferentFormatterName() {
        this.spreadsheetFormatterNextTokenAndCheck(
                SpreadsheetFormatterSelector.parse("" + NEW_FORMATTER_NAME),
                SpreadsheetFormatterSelectorToken.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    @Test
    public void testSpreadsheetFormatterNextTokenDifferentFormatterNameNotEmptyText() {
        this.spreadsheetFormatterNextTokenAndCheck(
                SpreadsheetFormatterSelector.parse("" + NEW_FORMATTER_NAME + " dd"),
                SpreadsheetFormatterSelectorToken.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetFormatterSelectorTokenAlternative.with(
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
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
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

    // https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern new-date-format-pattern
    @Test
    public void testSpreadsheetInfos() {
        this.spreadsheetFormatterInfosAndCheck(
                SpreadsheetFormatterInfo.with(
                        url("automatic"),
                        SpreadsheetFormatterName.AUTOMATIC
                ),
                SpreadsheetFormatterInfo.with(
                        url("date-format-pattern"),
                        SpreadsheetFormatterName.with(NEW_FORMATTER_NAME)
                )
        );
    }

    @Override
    public MappedSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        final SpreadsheetFormatterProvider provider = SpreadsheetFormatterProviders.spreadsheetFormatPattern();

        return MappedSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterInfoSet.with(
                        Sets.of(
                                SpreadsheetFormatterInfo.with(
                                        url("automatic"),
                                        SpreadsheetFormatterName.AUTOMATIC
                                ),
                                SpreadsheetFormatterInfo.with(
                                        url("date-format-pattern"),
                                        SpreadsheetFormatterName.with(NEW_FORMATTER_NAME)
                                )
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
