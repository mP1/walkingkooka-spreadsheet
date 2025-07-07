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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetComparatorProviderCollectionTest implements SpreadsheetComparatorProviderTesting<SpreadsheetComparatorProviderCollection> {

    @Test
    public void testWithNullProvidersFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetComparatorProviderCollection.with(null)
        );
    }

    @Test
    public void testSpreadsheetConverterSelector() {
        final SpreadsheetComparatorProvider provider = SpreadsheetComparatorProviders.spreadsheetComparators();

        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorProviderCollection.with(Sets.of(provider)),
            SpreadsheetComparatorSelector.parse("day-of-month"),
            ProviderContexts.fake(),
            SpreadsheetComparators.dayOfMonth()
        );
    }

    @Test
    public void testSpreadsheetConverterName() {
        final SpreadsheetComparatorProvider provider = SpreadsheetComparatorProviders.spreadsheetComparators();

        this.spreadsheetComparatorAndCheck(
            SpreadsheetComparatorProviderCollection.with(Sets.of(provider)),
            SpreadsheetComparatorName.with("day-of-month"),
            Lists.empty(),
            ProviderContexts.fake(),
            SpreadsheetComparators.dayOfMonth()
        );
    }

    @Test
    public void testInfos() {
        final SpreadsheetComparatorProvider provider = SpreadsheetComparatorProviders.spreadsheetComparators();

        this.spreadsheetComparatorInfosAndCheck(
            SpreadsheetComparatorProviderCollection.with(Sets.of(provider)),
            provider.spreadsheetComparatorInfos()
        );
    }

    @Override
    public SpreadsheetComparatorProviderCollection createSpreadsheetComparatorProvider() {
        return SpreadsheetComparatorProviderCollection.with(
            Sets.of(
                SpreadsheetComparatorProviders.spreadsheetComparators()
            )
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetComparatorProviderCollection> type() {
        return SpreadsheetComparatorProviderCollection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
