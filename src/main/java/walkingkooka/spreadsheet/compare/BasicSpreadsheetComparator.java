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
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;

import java.util.Comparator;
import java.util.Objects;

/**
 * Wraps a {@link Comparator} along with a {@link Class type}.
 */
final class BasicSpreadsheetComparator<T> implements SpreadsheetComparator<T> {

    static <T> BasicSpreadsheetComparator<T> with(final Class<T> type,
                                                  final Comparator<? super T> comparator,
                                                  final SpreadsheetComparatorDirection direction,
                                                  final SpreadsheetComparatorName name) {
        return new BasicSpreadsheetComparator<>(
            Objects.requireNonNull(type, "type"),
            Objects.requireNonNull(comparator, "compare"),
            Objects.requireNonNull(direction, "direction"),
            Objects.requireNonNull(name, "name")
        );
    }

    private BasicSpreadsheetComparator(final Class<T> type,
                                       final Comparator<? super T> comparator,
                                       final SpreadsheetComparatorDirection direction,
                                       final SpreadsheetComparatorName name) {
        this.type = type;
        this.comparator = comparator;
        this.direction = direction;
        this.name = name;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    private final Class<T> type;

    @Override
    public int compare(final T left,
                       final T right) {
        return this.comparator.compare(
            left,
            right
        );
    }

    private final Comparator<? super T> comparator;

    @Override
    public SpreadsheetComparatorDirection direction() {
        return this.direction;
    }

    private final SpreadsheetComparatorDirection direction;

    @Override
    public SpreadsheetComparatorName name() {
        return this.name;
    }

    private final SpreadsheetComparatorName name;

    // Object..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.type,
            this.comparator,
            this.direction,
            this.name
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof BasicSpreadsheetComparator && this.equals0(Cast.to(other));
    }

    private boolean equals0(final BasicSpreadsheetComparator<?> other) {
        return this.type.equals(other.type) &&
            this.comparator.equals(other.comparator) &&
            this.direction.equals(other.direction) &&
            this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return this.name.value()
            .concat(
                this.direction.toStringWithEmptyDefault()
            );
    }
}
