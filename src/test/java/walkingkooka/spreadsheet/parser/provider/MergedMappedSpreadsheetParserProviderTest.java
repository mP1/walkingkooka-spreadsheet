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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MergedMappedSpreadsheetParserProviderTest implements SpreadsheetParserProviderTesting<MergedMappedSpreadsheetParserProvider>,
    SpreadsheetMetadataTesting {

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullInfosFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedMappedSpreadsheetParserProvider.with(
                null,
                SpreadsheetParserProviders.fake()
            )
        );
    }

    @Test
    public void testWithNullProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> MergedMappedSpreadsheetParserProvider.with(
                SpreadsheetParserInfoSet.EMPTY.concat(
                    SpreadsheetParserInfo.with(
                        url("time"),
                        SpreadsheetParserName.TIME
                    )
                ),
                null
            )
        );
    }

    private final static String RENAMED_TIME_PARSER_PATTERN = "renamed-time";

    @Test
    public void testSpreadsheetParserSelectorWithRenamed() {
        final String pattern = "hh/mm";

        this.spreadsheetParserAndCheck(
            RENAMED_TIME_PARSER_PATTERN + " " + pattern,
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern(pattern).parser()
        );
    }

    @Test
    public void testSpreadsheetParserSelectorWithProviderName() {
        final String pattern = "hh/mm";

        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.DATE_TIME + " " + pattern,
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateTimeParsePattern(pattern).parser()
        );
    }

    @Test
    public void testSpreadsheetParserNameAndValuesWithRenamed() {
        final String pattern = "hh/mm/ss";

        this.spreadsheetParserAndCheck(
            SpreadsheetParserName.with(RENAMED_TIME_PARSER_PATTERN),
            Lists.of(pattern),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseTimeParsePattern(pattern).parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextToken() {
        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserSelector.parse(RENAMED_TIME_PARSER_PATTERN),
            SpreadsheetParserProviders.spreadsheetParsePattern(
                SpreadsheetFormatterProviders.spreadsheetFormatters()
            ).spreadsheetParserNextToken(
                SpreadsheetParserName.TIME.setValueText("")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithRenamed() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(RENAMED_TIME_PARSER_PATTERN),
            SpreadsheetParserProviders.spreadsheetParsePattern(
                SpreadsheetFormatterProviders.spreadsheetFormatters()
            ).spreadsheetFormatterSelector(
                SpreadsheetParserName.TIME.setValueText("")
            )
        );
    }

    @Test
    public void testSpreadsheetFormatterSelectorWithProviderName() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserSelector.parse(SpreadsheetParserName.DATE_TIME + " hh:mm:ss"),
            SpreadsheetParserProviders.spreadsheetParsePattern(
                SpreadsheetFormatterProviders.spreadsheetFormatters()
            ).spreadsheetFormatterSelector(
                SpreadsheetParserName.DATE_TIME.setValueText("hh:mm:ss")
            )
        );
    }

    @Test
    public void testSpreadsheetInfos() {
        final SpreadsheetParserInfoSet spreadsheetParserPattern = SpreadsheetParserProviders.spreadsheetParsePattern(SPREADSHEET_FORMATTER_PROVIDER)
            .spreadsheetParserInfos();

        final SpreadsheetParserInfoSet withRename = SpreadsheetParserInfoSet.with(
            spreadsheetParserPattern.stream()
                .map(
                    i -> i.name()
                        .equals(SpreadsheetParserName.TIME) ?
                        SpreadsheetParserInfo.with(
                            url("time"),
                            SpreadsheetParserName.with(RENAMED_TIME_PARSER_PATTERN)
                        ) :
                        i
                ).collect(Collectors.toSet())
        );

        this.checkNotEquals(
            spreadsheetParserPattern,
            withRename
        );

        this.spreadsheetParserInfosAndCheck(
            withRename
        );
    }

    @Override
    public MergedMappedSpreadsheetParserProvider createSpreadsheetParserProvider() {
        final SpreadsheetParserProvider provider = SpreadsheetParserProviders.spreadsheetParsePattern(SPREADSHEET_FORMATTER_PROVIDER);

        return MergedMappedSpreadsheetParserProvider.with(
            SpreadsheetParserInfoSet.EMPTY.concat(
                SpreadsheetParserInfo.with(
                    url("time"),
                    SpreadsheetParserName.with(RENAMED_TIME_PARSER_PATTERN)
                )
            ),
            provider
        );
    }

    private static AbsoluteUrl url(final String parserName) {
        return SpreadsheetParserProviders.BASE_URL.appendPath(UrlPath.parse(parserName));
    }

    // class............................................................................................................

    @Override
    public Class<MergedMappedSpreadsheetParserProvider> type() {
        return MergedMappedSpreadsheetParserProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
