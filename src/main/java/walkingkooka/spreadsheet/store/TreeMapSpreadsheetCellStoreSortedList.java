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

package walkingkooka.spreadsheet.store;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A very simple abstraction that holds {@link SpreadsheetCell cells} sorted by the given {@link Comparator} only exists
 * to support {@link TreeMapSpreadsheetCellStore#loadCellRange(SpreadsheetCellRangeReference, SpreadsheetCellRangeReferencePath, int, int)}.
 */
final class TreeMapSpreadsheetCellStoreSortedList {

    static TreeMapSpreadsheetCellStoreSortedList with(final SpreadsheetCellRangeReferencePath path) {
        return new TreeMapSpreadsheetCellStoreSortedList(path);
    }

    private TreeMapSpreadsheetCellStoreSortedList(final SpreadsheetCellRangeReferencePath path) {
        super();
        this.comparator = SpreadsheetCellReference.cellComparator(
            path.comparator()
        );
        this.cells = Lists.array();
    }

    Optional<SpreadsheetCell> get(final SpreadsheetCellReference reference) {
        return this.offset(
            this.indexOf(reference)
        );
    }

    /**
     * Returns the first cell at or after the given {@link SpreadsheetCellReference}.
     * This will be useful when finding cells belonging to a range, where some cells are missing.
     * <pre>
     * A1, B2, B3, B4
     *
     * getOrNext(B1) -> B2
     * </pre>
     */
    Optional<SpreadsheetCell> getOrNext(final SpreadsheetCellReference reference) {
        int index = this.indexOf(reference);

        SpreadsheetCell getOrNext = null;

        final List<SpreadsheetCell> cells = this.cells;
        if (index < 0) {
            index = -index - 1;
        }
        if (index < cells.size()) {
            getOrNext = cells.get(index);
        }

        return Optional.ofNullable(getOrNext);
    }

    Optional<SpreadsheetCell> offset(final int index) {
        final List<SpreadsheetCell> cells = this.cells;
        return Optional.ofNullable(
            index < 0 || index >= cells.size() ?
                null :
                cells.get(index)
        );
    }

    void remove(final SpreadsheetCellReference cell) {
        final int index = this.indexOf(cell);
        if (index >= 0) {
            this.cells.remove(index);
        }
    }

    void addOrReplace(final SpreadsheetCell cell) {
        final List<SpreadsheetCell> cells = this.cells;

        int index = this.indexOf(cell.reference());
        if (index >= 0) {
            cells.set(index, cell);
        } else {
            index = -index - 1;

            if (index >= cells.size()) {
                cells.add(cell);
            } else {
                cells.add(
                    index,
                    cell
                );
            }
        }
    }

    int indexOfOrNext(final SpreadsheetCellReference reference) {
        int index = this.indexOf(reference);

        if (index < 0) {
            index = -index - 1;
        }
        if (index >= cells.size()) {
            index = -1;
        }

        return index;
    }

    private int indexOf(final SpreadsheetCellReference reference) {
        return Collections.binarySearch(
            this.cells,
            reference.setFormula(SpreadsheetFormula.EMPTY),
            this.comparator
        );
    }

    private final Comparator<SpreadsheetCell> comparator;

    // VisibleForTesting
    final List<SpreadsheetCell> cells;

    @Override
    public String toString() {
        return this.cells.toString();
    }
}
