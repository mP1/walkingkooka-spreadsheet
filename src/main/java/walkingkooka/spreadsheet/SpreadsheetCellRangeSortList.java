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

package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceKind;

import java.util.AbstractList;
import java.util.List;

/**
 * A mutable {@link List} that is backed by an array and also holds the column or row containing these cells.
 */
final class SpreadsheetCellRangeSortList extends AbstractList<SpreadsheetCell> {

    static SpreadsheetCellRangeSortList with(final SpreadsheetColumnOrRowReferenceKind columnOrRow,
                                             final int size) {
        return new SpreadsheetCellRangeSortList(
                columnOrRow,
                size
        );
    }

    private SpreadsheetCellRangeSortList(final SpreadsheetColumnOrRowReferenceKind columnOrRow,
                                         final int size) {
        this.columnOrRow = columnOrRow;
        this.cells = new SpreadsheetCell[size];
    }

    final SpreadsheetColumnOrRowReferenceKind columnOrRow;

    @Override
    public SpreadsheetCell get(final int index) {
        return this.cells[index];
    }

    @Override
    public SpreadsheetCell set(final int index,
                               final SpreadsheetCell cell) {
        final SpreadsheetCell[] cells = this.cells;
        final SpreadsheetCell replaced = cells[index];
        cells[index] = cell;

        return replaced;
    }


    @Override
    public int size() {
        return this.cells.length;
    }

    // @VisibleForTesting
    final SpreadsheetCell[] cells;
}
