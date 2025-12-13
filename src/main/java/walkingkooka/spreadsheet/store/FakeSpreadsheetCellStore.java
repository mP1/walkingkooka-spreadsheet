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

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.validation.ValueType;

import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetCellStore extends FakeStore<SpreadsheetCellReference, SpreadsheetCell> implements SpreadsheetCellStore, Fake {

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                              final SpreadsheetCellRangeReferencePath path,
                                              final int offset,
                                              final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteCells(final SpreadsheetCellRangeReference range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int rowCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int columnCount() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a view of all cells in the given row.
     */
    @Override
    public Set<SpreadsheetCell> row(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a view of all cells in the given column.
     */
    @Override
    public Set<SpreadsheetCell> column(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double maxColumnWidth(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double maxRowHeight(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> findCellsWithValueType(final SpreadsheetCellRangeReference range,
                                                       final ValueType valueType,
                                                       final int max) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int countCellsWithValueType(final SpreadsheetCellRangeReference range,
                                       final ValueType valueType) {
        throw new UnsupportedOperationException();
    }
}
