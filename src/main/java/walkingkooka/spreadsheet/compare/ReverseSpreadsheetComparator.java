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

import walkingkooka.Cast;
import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;

import java.util.Objects;

/**
 * Wraps another {@link SpreadsheetComparator} and reverses the compare result.
 */
final class ReverseSpreadsheetComparator<T> implements SpreadsheetComparator<T> {

    static <T> SpreadsheetComparator<T> with(final SpreadsheetComparator<T> comparator) {
        return comparator instanceof ReverseSpreadsheetComparator ?
            ((ReverseSpreadsheetComparator<T>) comparator).comparator :
            new ReverseSpreadsheetComparator<>(
                Objects.requireNonNull(comparator, "compare")
            );
    }

    private ReverseSpreadsheetComparator(final SpreadsheetComparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Class<T> type() {
        return this.comparator.type();
    }

    @Override
    public int compare(final T left,
                       final T right) {
        return -
            Comparators.normalize(
                this.comparator.compare(
                    left,
                    right
                )
            );
    }

    @Override
    public SpreadsheetComparatorDirection direction() {
        return this.comparator.direction()
            .flip();
    }

    @Override
    public SpreadsheetComparatorName name() {
        return this.comparator.name();
    }

    private final SpreadsheetComparator<T> comparator;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.comparator.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof ReverseSpreadsheetComparator && this.equals0(Cast.to(other));
    }

    private boolean equals0(final ReverseSpreadsheetComparator<?> other) {
        return this.comparator.equals(other.comparator);
    }

    @Override
    public String toString() {
        final SpreadsheetComparator<T> comparator = this.comparator;

        return comparator.name() + " " + comparator.direction().flip();
    }
}
