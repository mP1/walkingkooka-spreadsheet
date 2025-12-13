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

package walkingkooka.spreadsheet.convert;

import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetValueTypeVisitor;

final class SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelectionSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    static SpreadsheetSelection convert(final SpreadsheetSelection selection,
                                        final Class<?> target) {
        final SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelectionSpreadsheetValueTypeVisitor visitor = new SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelectionSpreadsheetValueTypeVisitor(selection);
        visitor.accept(target);
        final SpreadsheetSelection result = visitor.value;
        if(null == result) {
            throw new IllegalArgumentException("Can't convert " + selection + " to " + target);
        }
        return result;
    }

    // @VisibleForTesting
    SpreadsheetConverterSpreadsheetSelectionToSpreadsheetSelectionSpreadsheetValueTypeVisitor(final SpreadsheetSelection selection) {
        super();
        this.selection = selection;
    }

    @Override
    protected void visitCellRange() {
        this.value = this.selection.toCellRange();
    }

    @Override
    protected void visitCellReference() {
        this.value = this.selection.toCell();
    }

    @Override
    protected void visitCellReferenceOrRange() {
        this.value = this.selection.toCellOrCellRange();
    }

    @Override
    protected void visitColumnOrRowReferenceOrRange() {
        this.value = this.selection;
    }

    @Override
    protected void visitColumnReference() {
        this.value = this.selection.toColumn();
    }

    @Override
    protected void visitColumnReferenceOrRange() {
        this.value = this.selection.toColumnOrColumnRange();
    }

    @Override
    protected void visitColumnRangeReference() {
        this.value = this.selection.toColumnRange();
    }

    @Override
    protected void visitExpressionReference() {
        this.value = this.selection.toExpressionReference();
    }

    @Override
    protected void visitRowReference() {
        this.value = this.selection.toRow();
    }

    @Override
    protected void visitRowRangeReference() {
        this.value = this.selection.toRowRange();
    }

    @Override
    protected void visitRowReferenceOrRange() {
        this.value = this.selection.toRowOrRowRange();
    }

    @Override
    protected void visitSpreadsheetSelection() {
        this.value = this.selection;
    }

    private final SpreadsheetSelection selection;

    private SpreadsheetSelection value;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.selection + " " + this.value;
    }
}
