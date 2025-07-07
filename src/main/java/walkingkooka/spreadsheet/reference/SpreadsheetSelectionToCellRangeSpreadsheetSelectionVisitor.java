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

package walkingkooka.spreadsheet.reference;

import walkingkooka.text.CharSequences;

/**
 * This visitor is used to turn any {@link SpreadsheetSelection} into a {@link SpreadsheetCellRangeReference}.
 * <br>
 * Labels will be resolved until the final destination cell or cell-range is found. A single column wll be made into
 * a {@link SpreadsheetCellRangeReference} that includes all cells for that column.
 */
final class SpreadsheetSelectionToCellRangeSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static SpreadsheetCellRangeReference toCellRange(final SpreadsheetSelection selection) {
        final SpreadsheetSelectionToCellRangeSpreadsheetSelectionVisitor visitor = new SpreadsheetSelectionToCellRangeSpreadsheetSelectionVisitor();
        visitor.accept(selection);

        final SpreadsheetCellRangeReference cellRange = visitor.cellRange;
        if (null == cellRange) {
            throw new IllegalStateException("Missing cell range for " + CharSequences.quoteAndEscape(selection.toString()));
        }
        return cellRange;
    }

    // @VisibleForTesting
    SpreadsheetSelectionToCellRangeSpreadsheetSelectionVisitor() {
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cells) {
        this.cellRange = cells;
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        this.cellRange = cell.cellRange(cell);
    }

    @Override
    protected void visit(final SpreadsheetColumnReference column) {
        this.visit(column.toColumnRange());
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference columns) {
        this.cellRange = columns.setRowRange(SpreadsheetSelection.ALL_ROWS);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        throw new UnsupportedOperationException("Selection is a label " + CharSequences.quoteAndEscape(label.text()));
    }

    @Override
    protected void visit(final SpreadsheetRowReference row) {
        this.visit(row.toRowRange());
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference rowRange) {
        this.cellRange = rowRange.setColumnRange(SpreadsheetSelection.ALL_COLUMNS);
    }

    /**
     * The final result which must not be null.
     */
    private SpreadsheetCellRangeReference cellRange;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return String.valueOf(this.cellRange);
    }
}
