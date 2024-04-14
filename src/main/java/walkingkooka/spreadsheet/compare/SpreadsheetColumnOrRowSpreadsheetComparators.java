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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;

import java.util.List;
import java.util.Objects;

/**
 * For a single column or row holds a list of {@link SpreadsheetComparator}.
 */
public final class SpreadsheetColumnOrRowSpreadsheetComparators {

    public static SpreadsheetColumnOrRowSpreadsheetComparators with(final SpreadsheetColumnOrRowReference columnOrRow,
                                                                    final List<SpreadsheetComparator<?>> comparators) {

        return new SpreadsheetColumnOrRowSpreadsheetComparators(
                Objects.requireNonNull(columnOrRow, "columnOrRows"),
                Lists.immutable(
                        Objects.requireNonNull(comparators, "comparators")
                )
        );
    }

    private SpreadsheetColumnOrRowSpreadsheetComparators(final SpreadsheetColumnOrRowReference columnOrRow,
                                                         final List<SpreadsheetComparator<?>> comparators) {
        if (comparators.isEmpty()) {
            throw new IllegalArgumentException("Expected at least 1 comparator got none");
        }

        this.columnOrRow = columnOrRow;
        this.comparators = comparators;
    }

    public SpreadsheetColumnOrRowReference columnOrRow() {
        return this.columnOrRow;
    }

    private final SpreadsheetColumnOrRowReference columnOrRow;

    public List<SpreadsheetComparator<?>> comparators() {
        return this.comparators;
    }

    private final List<SpreadsheetComparator<?>> comparators;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.columnOrRow,
                this.comparators
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetColumnOrRowSpreadsheetComparators && this.equals0((SpreadsheetColumnOrRowSpreadsheetComparators) other);
    }

    private boolean equals0(final SpreadsheetColumnOrRowSpreadsheetComparators other) {
        return this.columnOrRow.equals(other.columnOrRow) &&
                this.comparators.equals(other.comparators);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.columnOrRow)
                .value(this.comparators)
                .build();
    }
}
