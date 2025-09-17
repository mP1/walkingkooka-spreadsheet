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

package walkingkooka.spreadsheet.compare.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;

import java.util.List;

public final class FilteredSpreadsheetComparatorProviderTest implements SpreadsheetComparatorProviderTesting<FilteredSpreadsheetComparatorProvider>,
    ToStringTesting<FilteredSpreadsheetComparatorProvider> {

    private final static ProviderContext CONTEXT = ProviderContexts.fake();

    @Test
    public void testSpreadsheetComparatorName() {
        final SpreadsheetComparatorName name = SpreadsheetComparatorName.DAY_OF_MONTH;
        final List<?> values = Lists.empty();

        this.spreadsheetComparatorAndCheck(
            name,
            values,
            CONTEXT,
            SpreadsheetComparatorProviders.spreadsheetComparators()
                .spreadsheetComparator(
                    name,
                    values,
                    CONTEXT
                )
        );
    }

    @Test
    public void testSpreadsheetComparatorNameWithFilteredFails() {
        final SpreadsheetComparatorName name = SpreadsheetComparatorName.YEAR;

        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorProviders.spreadsheetComparators(),
            name,
            Lists.empty(),
            CONTEXT,
            SpreadsheetComparators.year()
        );

        this.spreadsheetComparatorFails(
            name,
            Lists.empty(),
            CONTEXT
        );
    }

    @Test
    public void testSpreadsheetComparatorInfos() {
        this.spreadsheetComparatorInfosAndCheck(
            SpreadsheetComparatorInfoSet.EMPTY.concat(
                SpreadsheetComparatorInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-month day-of-month")
            )
        );
    }

    @Override
    public FilteredSpreadsheetComparatorProvider createSpreadsheetComparatorProvider() {
        return FilteredSpreadsheetComparatorProvider.with(
            SpreadsheetComparatorProviders.spreadsheetComparators(),
            SpreadsheetComparatorInfoSet.EMPTY.concat(
                SpreadsheetComparatorInfo.parse("https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-month day-of-month")
            )
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createSpreadsheetComparatorProvider(),
            SpreadsheetComparatorProviders.spreadsheetComparators()
                .toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<FilteredSpreadsheetComparatorProvider> type() {
        return FilteredSpreadsheetComparatorProvider.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
