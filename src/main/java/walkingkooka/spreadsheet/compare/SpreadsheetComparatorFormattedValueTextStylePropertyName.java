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
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps a {@link Comparator} sorting a {@link walkingkooka.tree.text.TextStylePropertyName} found in a {@link SpreadsheetCell#formattedValue()}.
 */
final class SpreadsheetComparatorFormattedValueTextStylePropertyName<T> implements SpreadsheetComparator<T> {

    static <T> SpreadsheetComparatorFormattedValueTextStylePropertyName<T> with(final TextStylePropertyName<T> textStylePropertyName,
                                                                                final Comparator<? super T> comparator) {
        return new SpreadsheetComparatorFormattedValueTextStylePropertyName<>(
            Objects.requireNonNull(textStylePropertyName, "textStylePropertyName"),
            Objects.requireNonNull(comparator, "comparator")
        );
    }

    private SpreadsheetComparatorFormattedValueTextStylePropertyName(final TextStylePropertyName<T> textStylePropertyName,
                                                                     final Comparator<? super T> comparator) {
        this.textStylePropertyName = textStylePropertyName;
        this.comparator = comparator;
        this.name = SpreadsheetComparatorName.with(
            textStylePropertyName.value()
        );
    }

    /**
     * Extracts the {@link TextStylePropertyName} value from the given {@link SpreadsheetCell#formattedValue()} using
     * {@link TextStylePropertyName#firstValueOrEmpty(TextNode)}.
     */
    @Override
    public Optional<T> extractValue(final SpreadsheetCell cell,
                                    final SpreadsheetComparatorContext context) {
        Objects.requireNonNull(context, "context");

        return null != cell ?
            cell.formattedValue()
                .flatMap(SpreadsheetComparatorFormattedValueTextStylePropertyName.this.textStylePropertyName::firstValueOrEmpty) :
            Optional.empty();
    }

    private final TextStylePropertyName<T> textStylePropertyName;

    // Comparator.......................................................................................................

    @Override
    public int compare(final T left,
                       final T right) {
        return this.comparator.compare(
            left,
            right
        );
    }

    private final Comparator<? super T> comparator;

    // HasName..........................................................................................................

    @Override
    public SpreadsheetComparatorName name() {
        return this.name;
    }

    private final SpreadsheetComparatorName name;

    // Object...................................................................,.......................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.textStylePropertyName,
            this.comparator
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetComparatorFormattedValueTextStylePropertyName && this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetComparatorFormattedValueTextStylePropertyName<?> other) {
        return this.textStylePropertyName.equals(other.textStylePropertyName) &&
            this.comparator.equals(other.comparator);
    }

    @Override
    public String toString() {
        return this.textStylePropertyName.value() + " " + this.comparator;
    }
}
