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

package walkingkooka.spreadsheet.value;

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

/**
 * This visitor is used by {@link SpreadsheetValueVisitor} to visit subclasses of {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection}.
 */
final class SpreadsheetValueVisitorSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static SpreadsheetValueVisitorSpreadsheetSelectionVisitor with(final SpreadsheetValueVisitor visitor) {
        return new SpreadsheetValueVisitorSpreadsheetSelectionVisitor(visitor);
    }

    private SpreadsheetValueVisitorSpreadsheetSelectionVisitor(final SpreadsheetValueVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        this.visitor.visit(cellRange);
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        this.visitor.visit(cell);
    }

    @Override
    protected void visit(final SpreadsheetColumnReference column) {
        this.visitor.visit(column);
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference columnRange) {
        this.visitor.visit(columnRange);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.visitor.visit(label);
    }

    @Override
    protected void visit(final SpreadsheetRowReference row) {
        this.visitor.visit(row);
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference rowRange) {
        this.visitor.visit(rowRange);
    }

    private final SpreadsheetValueVisitor visitor;

    @Override
    public String toString() {
        return this.visitor.toString();
    }
}
