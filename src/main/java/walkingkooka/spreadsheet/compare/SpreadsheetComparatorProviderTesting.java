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
import walkingkooka.collect.set.Sets;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.ProviderTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetComparatorProviderTesting<T extends SpreadsheetComparatorProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetComparatorWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetComparatorProvider()
                        .spreadsheetComparator(
                                null,
                                ProviderContexts.fake()
                        )
        );
    }

    @Test
    default void testSpreadsheetComparatorWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetComparatorProvider()
                        .spreadsheetComparator(
                                SpreadsheetComparatorName.with("ignored"),
                                null
                        )
        );
    }

    default void spreadsheetComparatorFails(final SpreadsheetComparatorName name,
                                            final ProviderContext context) {
        this.spreadsheetComparatorFails(
                this.createSpreadsheetComparatorProvider(),
                name,
                context
        );
    }

    default void spreadsheetComparatorFails(final SpreadsheetComparatorProvider provider,
                                            final SpreadsheetComparatorName name,
                                            final ProviderContext context) {
        assertThrows(
                IllegalArgumentException.class,
                () -> provider.spreadsheetComparator(
                        name,
                        context
                )
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorName name,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.spreadsheetComparatorAndCheck(
                this.createSpreadsheetComparatorProvider(),
                name,
                context,
                expected
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final SpreadsheetComparatorName name,
                                               final ProviderContext context,
                                               final SpreadsheetComparator<?> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetComparator(
                        name,
                        context
                ),
                name::toString
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorInfo... expected) {
        this.spreadsheetComparatorInfosAndCheck(
                this.createSpreadsheetComparatorProvider(),
                expected
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorProvider provider,
                                                    final SpreadsheetComparatorInfo... expected) {
        this.spreadsheetComparatorInfosAndCheck(
                provider,
                SpreadsheetComparatorInfoSet.with(
                        Sets.of(
                                expected
                        )
                )
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorInfoSet expected) {
        this.spreadsheetComparatorInfosAndCheck(
                this.createSpreadsheetComparatorProvider(),
                expected
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorProvider provider,
                                                    final SpreadsheetComparatorInfoSet expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetComparatorInfos(),
                provider::toString
        );
    }

    T createSpreadsheetComparatorProvider();
}
