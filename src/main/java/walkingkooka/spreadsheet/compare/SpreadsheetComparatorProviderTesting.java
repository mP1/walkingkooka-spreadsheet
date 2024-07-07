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
import walkingkooka.plugin.ProviderTesting;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetComparatorProviderTesting<T extends SpreadsheetComparatorProvider> extends ProviderTesting<T> {

    @Test
    default void testSpreadsheetComparatorWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetComparatorProvider()
                        .spreadsheetComparator(null)
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorName name) {
        this.spreadsheetComparatorAndCheck(
                this.createSpreadsheetComparatorProvider(),
                name,
                Optional.empty()
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final SpreadsheetComparatorName name) {
        this.spreadsheetComparatorAndCheck(
                provider,
                name,
                Optional.empty()
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorName name,
                                               final SpreadsheetComparator<?> expected) {
        this.spreadsheetComparatorAndCheck(
                this.createSpreadsheetComparatorProvider(),
                name,
                Optional.of(expected)
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final SpreadsheetComparatorName name,
                                               final SpreadsheetComparator<?> expected) {
        this.spreadsheetComparatorAndCheck(
                provider,
                name,
                Optional.of(expected)
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorName name,
                                               final Optional<SpreadsheetComparator<?>> expected) {
        this.spreadsheetComparatorAndCheck(
                this.createSpreadsheetComparatorProvider(),
                name,
                expected
        );
    }

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final SpreadsheetComparatorName name,
                                               final Optional<SpreadsheetComparator<?>> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetComparator(name),
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
                Sets.of(
                        expected
                )
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final Set<SpreadsheetComparatorInfo> expected) {
        this.spreadsheetComparatorInfosAndCheck(
                this.createSpreadsheetComparatorProvider(),
                expected
        );
    }

    default void spreadsheetComparatorInfosAndCheck(final SpreadsheetComparatorProvider provider,
                                                    final Set<SpreadsheetComparatorInfo> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetComparatorInfos(),
                provider::toString
        );
    }

    T createSpreadsheetComparatorProvider();
}
