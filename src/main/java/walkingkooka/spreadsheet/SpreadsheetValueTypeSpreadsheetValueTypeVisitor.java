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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionReference;

final class SpreadsheetValueTypeSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    static String typeName(final Class<?> type) {
        final SpreadsheetValueTypeSpreadsheetValueTypeVisitor visitor = new SpreadsheetValueTypeSpreadsheetValueTypeVisitor();
        visitor.accept(type);
        return visitor.typeName;
    }

    SpreadsheetValueTypeSpreadsheetValueTypeVisitor() {
        super();
    }

    @Override
    protected void visitBigDecimal() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitBigInteger() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitBoolean() {
        this.typeName(SpreadsheetValueType.BOOLEAN);
    }

    @Override
    protected void visitByte() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitCellRange() {
        this.typeName(SpreadsheetValueType.CELL_RANGE);
    }

    @Override
    protected void visitCellReference() {
        this.typeName(SpreadsheetValueType.CELL);
    }

    @Override
    protected void visitCellReferenceOrRange() {
        throw new UnsupportedOperationException(SpreadsheetCellReferenceOrRange.class.getName());
    }

    @Override
    protected void visitCharacter() {
        this.typeName(SpreadsheetValueType.TEXT);
    }

    @Override
    protected void visitColumnReference() {
        this.typeName(SpreadsheetValueType.COLUMN);
    }

    @Override
    protected void visitColumnReferenceRange() {
        this.typeName(SpreadsheetValueType.COLUMN_RANGE);
    }

    @Override
    protected void visitDouble() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitExpressionNumber() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitExpressionReference() {
        throw new UnsupportedOperationException(ExpressionReference.class.getName());
    }

    @Override
    protected void visitFloat() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitInteger() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitLabel() {
        this.typeName(SpreadsheetValueType.LABEL);
    }

    @Override
    protected void visitLocalDate() {
        this.typeName(SpreadsheetValueType.DATE);
    }

    @Override
    protected void visitLocalDateTime() {
        this.typeName(SpreadsheetValueType.DATE_TIME);
    }

    @Override
    protected void visitLocalTime() {
        this.typeName(SpreadsheetValueType.TIME);
    }

    @Override
    protected void visitLong() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitNumber() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitRowReference() {
        this.typeName(SpreadsheetValueType.ROW);
    }

    @Override
    protected void visitRowReferenceRange() {
        this.typeName(SpreadsheetValueType.ROW_RANGE);
    }

    @Override
    protected void visitSpreadsheetError() {
        this.typeName(SpreadsheetValueType.ERROR);
    }

    @Override
    protected void visitSpreadsheetSelection() {
        throw new UnsupportedOperationException(SpreadsheetSelection.class.getName());
    }

    @Override
    protected void visitShort() {
        this.typeName(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitString() {
        this.typeName(SpreadsheetValueType.TEXT);
    }

    @Override
    protected void visitUnknown(final String typeName) {
        this.typeName(typeName);
    }

    private void typeName(final String typeName) {
        this.typeName = typeName;
    }

    private String typeName;

    @Override
    public String toString() {
        return this.typeName;
    }
}
