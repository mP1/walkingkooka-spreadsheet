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

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MergedMappedSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<MergedMappedSpreadsheetFormatterProvider>,
        SpreadsheetMetadataTesting {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
                NullPointerException.class,
                () -> MergedMappedSpreadsheetFormatterProvider.with(
                        null,
                        SpreadsheetFormatterProviders.fake()
                )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> MergedMappedSpreadsheetFormatterProvider.with(
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

    private final static String RENAMED_DATE_FORMAT_PATTERN = "new-date-format-pattern";

    @Test
    public void testSpreadsheetFormatterSelector() {
        final String pattern = "yyyy/mm/dd";

        this.spreadsheetFormatterAndCheck(
                RENAMED_DATE_FORMAT_PATTERN + " " + pattern,
                CONTEXT,
                SpreadsheetPattern.parseDateFormatPattern(pattern)
                        .formatter()
        );
    }

    @Test
    public void testSpreadsheetFormatterName() {
        final String pattern = "yyyy/mm/dd";

        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterName.with(RENAMED_DATE_FORMAT_PATTERN),
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
                SpreadsheetFormatterSelector.parse("" + RENAMED_DATE_FORMAT_PATTERN),
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
                SpreadsheetFormatterSelector.parse("" + RENAMED_DATE_FORMAT_PATTERN + " dd"),
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
        final SpreadsheetFormatterName name = SpreadsheetFormatterName.with(RENAMED_DATE_FORMAT_PATTERN);

        this.spreadsheetFormatterSamplesAndCheck(
                name,
                SPREADSHEET_FORMATTER_PROVIDER_SAMPLES_CONTEXT,
                SpreadsheetFormatterSample.with(
                        "Short",
                        name.setValueText("d/m/yy"),
                        TextNode.text("31/12/99")
                ),
                SpreadsheetFormatterSample.with(
                        "Medium",
                        name.setValueText("d mmm yyyy"),
                        TextNode.text("31 Dec. 1999")
                ),
                SpreadsheetFormatterSample.with(
                        "Long",
                        name.setValueText("d mmmm yyyy"),
                        TextNode.text("31 December 1999")
                ),
                SpreadsheetFormatterSample.with(
                        "Full",
                        name.setValueText("dddd, d mmmm yyyy"),
                        TextNode.text("Friday, 31 December 1999")
                )
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern new-date-format-pattern
    @Test
    public void testSpreadsheetInfos() {
        final SpreadsheetFormatterInfoSet spreadsheetFormatPattern = SpreadsheetFormatterProviders.spreadsheetFormatters()
                .spreadsheetFormatterInfos();

        final SpreadsheetFormatterInfoSet withRename = SpreadsheetFormatterInfoSet.with(
                spreadsheetFormatPattern.stream()
                        .map(
                                i -> i.name().equals(SpreadsheetFormatterName.DATE_FORMAT_PATTERN) ?
                                        SpreadsheetFormatterInfo.with(
                                                url("date-format-pattern"),
                                                SpreadsheetFormatterName.with(RENAMED_DATE_FORMAT_PATTERN)
                                        ) :
                                        i
                        ).collect(Collectors.toSet())
        );

        this.checkNotEquals(
                spreadsheetFormatPattern,
                withRename
        );

        this.spreadsheetFormatterInfosAndCheck(
                withRename
        );
    }

    @Override
    public MergedMappedSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        final SpreadsheetFormatterProvider provider = SpreadsheetFormatterProviders.spreadsheetFormatters();

        return MergedMappedSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterInfoSet.with(
                        Sets.of(
                                SpreadsheetFormatterInfo.with(
                                        url("date-format-pattern"),
                                        SpreadsheetFormatterName.with(RENAMED_DATE_FORMAT_PATTERN)
                                )
                        )
                ),
                provider
        );
    }

    private static AbsoluteUrl url(final String formatterName) {
        return SpreadsheetFormatterProviders.BASE_URL.appendPath(UrlPath.parse(formatterName));
    }

    // class............................................................................................................

    @Override
    public Class<MergedMappedSpreadsheetFormatterProvider> type() {
        return MergedMappedSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
