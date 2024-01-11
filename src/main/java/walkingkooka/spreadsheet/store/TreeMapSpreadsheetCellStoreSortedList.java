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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * A very simple abstraction that holds {@link SpreadsheetCell cells} sorted by the given {@link Comparator} only exists
 * to support {@link TreeMapSpreadsheetCellStore#loadCells(SpreadsheetCellRange, SpreadsheetCellRangePath, int, int)}.
 */
final class TreeMapSpreadsheetCellStoreSortedList {

    static TreeMapSpreadsheetCellStoreSortedList with(final Comparator<SpreadsheetCellReference> comparator) {
        return new TreeMapSpreadsheetCellStoreSortedList(comparator);
    }

    private TreeMapSpreadsheetCellStoreSortedList(final Comparator<SpreadsheetCellReference> comparator) {
        super();
        this.comparator = SpreadsheetCellReference.cellComparator(comparator);
        this.cells = Lists.array();
    }

    Optional<SpreadsheetCell> get(final SpreadsheetCellReference reference) {
        return this.offset(
                this.indexOf(reference)
        );
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

        final int index = this.indexOf(cell.reference());
        if (-1 == index) {
            cells.add(index + 1, cell);
        } else {
            final int index2 = -index - 1;
            if (index2 >= cells.size()) {
                cells.add(cell);
            } else {
                cells.set(
                        index, // 2
                        cell
                );
            }
        }
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