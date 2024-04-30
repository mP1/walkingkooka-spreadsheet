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

import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceKind;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * {@link List} that holds multiple {@link SpreadsheetColumnOrRowSpreadsheetComparators} typically one for each sortable
 * column with a range sort.
 */
final class SpreadsheetColumnOrRowSpreadsheetComparatorsList extends AbstractList<SpreadsheetColumnOrRowSpreadsheetComparators>
        implements ImmutableListDefaults<SpreadsheetColumnOrRowSpreadsheetComparatorsList, SpreadsheetColumnOrRowSpreadsheetComparators> {

    static SpreadsheetColumnOrRowSpreadsheetComparatorsList with(final List<SpreadsheetColumnOrRowSpreadsheetComparators> columnOrRows) {
        Objects.requireNonNull(columnOrRows, "columnOrRows");

        return columnOrRows instanceof SpreadsheetColumnOrRowSpreadsheetComparatorsList ?
                (SpreadsheetColumnOrRowSpreadsheetComparatorsList) columnOrRows :
                copyAndCreate(columnOrRows);
    }

    static SpreadsheetColumnOrRowSpreadsheetComparatorsList copyAndCreate(final List<SpreadsheetColumnOrRowSpreadsheetComparators> columnOrRows) {
        final SpreadsheetColumnOrRowSpreadsheetComparators[] copy = columnOrRows.toArray(
                new SpreadsheetColumnOrRowSpreadsheetComparators[columnOrRows.size()]
        );

        if (copy.length == 0) {
            throw new IllegalArgumentException("Expected several sorted column/rows got 0");
        }

        SpreadsheetColumnOrRowReferenceKind first = null;
        int i = 0;

        for (final SpreadsheetColumnOrRowSpreadsheetComparators columnOrRowComparators : columnOrRows) {
            final SpreadsheetColumnOrRowReference columnOrRow = columnOrRowComparators.columnOrRow();
            if (null == first) {
                first = columnOrRow.columnOrRowReferenceKind();
            } else {
                if (first != columnOrRow.columnOrRowReferenceKind()) {
                    throw new IllegalArgumentException(
                            "All sorted columns/rows must be " +
                                    first +
                                    " but " +
                                    i +
                                    " is " +
                                    first.flip()
                    );
                }
            }

            i++;
        }

        return new SpreadsheetColumnOrRowSpreadsheetComparatorsList(copy);
    }

    private SpreadsheetColumnOrRowSpreadsheetComparatorsList(final SpreadsheetColumnOrRowSpreadsheetComparators[] comparators) {
        this.comparators = comparators;
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparators get(final int index) {
        return this.comparators[index];
    }

    @Override
    public int size() {
        return this.comparators.length;
    }

    private final SpreadsheetColumnOrRowSpreadsheetComparators[] comparators;

    // ImmutableList....................................................................................................

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorsList setElements(final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators) {
        final SpreadsheetColumnOrRowSpreadsheetComparatorsList copy = with(comparators);
        return this.equals(copy) ?
                this :
                copy;
    }
}
