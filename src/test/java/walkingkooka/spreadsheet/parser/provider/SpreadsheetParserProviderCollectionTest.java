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
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserProviderCollectionTest implements SpreadsheetParserProviderTesting<SpreadsheetParserProviderCollection> {

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullProvidersFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserProviderCollection.with(null)
        );
    }

    @Test
    public void testSpreadsheetParserSelection() {
        final SpreadsheetParserProvider provider = spreadsheetParsePatternSpreadsheetParserProvider();

        this.spreadsheetParserAndCheck(
            SpreadsheetParserProviderCollection.with(
                Sets.of(provider)
            ),
            SpreadsheetParserSelector.parse("date-parse-pattern yyyy/mm/dd"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserName() {
        final SpreadsheetParserProvider provider = spreadsheetParsePatternSpreadsheetParserProvider();

        this.spreadsheetParserAndCheck(
            SpreadsheetParserProviderCollection.with(
                Sets.of(provider)
            ),
            SpreadsheetParserName.DATE_PARSER_PATTERN,
            Lists.of("yyyy/mm/dd"),
            PROVIDER_CONTEXT,
            SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                .parser()
        );
    }

    @Test
    public void testSpreadsheetParserNextToken() {
        final SpreadsheetParserProvider provider = spreadsheetParsePatternSpreadsheetParserProvider();

        this.spreadsheetParserNextTokenAndCheck(
            SpreadsheetParserProviderCollection.with(
                Sets.of(provider)
            ),
            SpreadsheetParserSelector.parse("date-parse-pattern")
        );
    }

    @Test
    public void testSpreadsheetFormatterSelector() {
        this.spreadsheetFormatterSelectorAndCheck(
            SpreadsheetParserName.DATE_TIME_PARSER_PATTERN.setValueText("")
        );
    }

    @Test
    public void testInfos() {
        final SpreadsheetParserProvider provider = spreadsheetParsePatternSpreadsheetParserProvider();

        this.spreadsheetParserInfosAndCheck(
            SpreadsheetParserProviderCollection.with(Sets.of(provider)),
            provider.spreadsheetParserInfos()
        );
    }

    @Override
    public SpreadsheetParserProviderCollection createSpreadsheetParserProvider() {
        return SpreadsheetParserProviderCollection.with(
            Sets.of(
                spreadsheetParsePatternSpreadsheetParserProvider()
            )
        );
    }

    private static SpreadsheetParserProvider spreadsheetParsePatternSpreadsheetParserProvider() {
        return SpreadsheetParserProviders.spreadsheetParsePattern(
            SpreadsheetFormatterProviders.spreadsheetFormatters()
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetParserProviderCollection> type() {
        return SpreadsheetParserProviderCollection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
