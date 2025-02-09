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

final class SpreadsheetSelectionTestSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static boolean test(final SpreadsheetSelection selection,
                        final SpreadsheetSelection test) {
        final SpreadsheetSelectionTestSpreadsheetSelectionVisitor visitor = new SpreadsheetSelectionTestSpreadsheetSelectionVisitor(selection);
        visitor.accept(test);
        return visitor.test;
    }

    SpreadsheetSelectionTestSpreadsheetSelectionVisitor(final SpreadsheetSelection selection) {
        this.selection = selection;
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        this.test = this.selection.testCellRange(cellRange);
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        this.test = this.selection.testCell(cell);
    }

    @Override
    protected void visit(final SpreadsheetColumnReference column) {
        this.test = this.selection.testColumn(column);
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference columnRange) {
        throw new UnsupportedOperationException(columnRange.toString());
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        throw new UnsupportedOperationException(label.toString());
    }

    @Override
    protected void visit(final SpreadsheetRowReference row) {
        this.test = this.selection.testRow(row);
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference rowRange) {
        throw new UnsupportedOperationException(rowRange.toString());
    }

    private final SpreadsheetSelection selection;

    /**
     * The result of the test.
     */
    private boolean test;

    @Override
    public String toString() {
        return this.selection.toString();
    }
}
