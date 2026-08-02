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
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps a {@link Comparator} that extracts the value from a given {@link SpreadsheetCell} and converts that to a {@link Class type},
 * using the given {@link Comparator} to {@link Comparator#compare(Object, Object)} to compare extracted values.
 */
final class SpreadsheetComparatorValue<T> implements SpreadsheetComparator<T> {

    static <T> SpreadsheetComparatorValue<T> with(final Class<T> type,
                                                  final Comparator<? super T> comparator,
                                                  final SpreadsheetComparatorName name) {
        return new SpreadsheetComparatorValue<>(
            Objects.requireNonNull(type, "type"),
            Objects.requireNonNull(comparator, "comparator"),
            Objects.requireNonNull(name, "name")
        );
    }

    private SpreadsheetComparatorValue(final Class<T> type,
                                       final Comparator<? super T> comparator,
                                       final SpreadsheetComparatorName name) {
        this.type = type;
        this.comparator = comparator;
        this.name = name;
    }

    /**
     * Extracts the value or error from the given {@link SpreadsheetCell}
     */
    @Override
    public Optional<T> extractValue(final SpreadsheetCell cell,
                                    final SpreadsheetComparatorContext context) {
        return Optional.ofNullable(
            context.convert(
                null != cell ?
                    cell.formula().
                        errorOrValue()
                        .orElse(null) :
                    null,
                this.type
            ).orElseLeft(null)
        );
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
            this.name
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetComparatorValue && this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetComparatorValue<?> other) {
        return this.type.equals(other.type) &&
            this.comparator.equals(other.comparator) &&
            this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return this.name.value();
    }
}
