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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.util.List;

public final class FilteredSpreadsheetFormatterProviderTest implements SpreadsheetFormatterProviderTesting<FilteredSpreadsheetFormatterProvider>,
        ToStringTesting<FilteredSpreadsheetFormatterProvider> {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetFormatterName() {
        final SpreadsheetFormatterName name = SpreadsheetFormatterName.TEXT_FORMAT_PATTERN;
        final List<?> values = Lists.of("@@");

        this.spreadsheetFormatterAndCheck(
                name,
                values,
                CONTEXT,
                SpreadsheetFormatterProviders.spreadsheetFormatPattern()
                        .spreadsheetFormatter(
                                name,
                                values,
                                CONTEXT
                        )
        );
    }

    @Test
    public void testSpreadsheetFormatterWithFilteredFails() {
        final SpreadsheetFormatterName name = SpreadsheetFormatterName.DATE_FORMAT_PATTERN;
        final String pattern = "dd/mm/yyyy";
        final List<?> values = Lists.of(pattern);

        this.spreadsheetFormatterAndCheck(
                SpreadsheetFormatterProviders.spreadsheetFormatPattern(),
                name,
                values,
                CONTEXT,
                SpreadsheetPattern.parseDateFormatPattern(pattern)
                        .formatter()
        );


        this.spreadsheetFormatterFails(
                name,
                values,
                CONTEXT
        );
    }

    @Test
    public void testSpreadsheetFormatterInfos() {
        this.spreadsheetFormatterInfosAndCheck(
                SpreadsheetFormatterInfoSet.EMPTY.concat(
                        SpreadsheetFormatterInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern text-format-pattern")
                )
        );
    }

    @Override
    public FilteredSpreadsheetFormatterProvider createSpreadsheetFormatterProvider() {
        return FilteredSpreadsheetFormatterProvider.with(
                SpreadsheetFormatterProviders.spreadsheetFormatPattern(),
                SpreadsheetFormatterInfoSet.EMPTY.concat(
                        SpreadsheetFormatterInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern text-format-pattern")
                )
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createSpreadsheetFormatterProvider(),
                SpreadsheetFormatterProviders.spreadsheetFormatPattern()
                        .toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredSpreadsheetFormatterProvider> type() {
        return FilteredSpreadsheetFormatterProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
