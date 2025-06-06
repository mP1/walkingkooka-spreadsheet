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

import walkingkooka.validation.ValidationValueTypeName;

import java.util.Optional;

/**
 * Translates a {@link Class} into a {@link ValidationValueTypeName}, handling the "differences" eg, TEXT = {@link String}.
 */
final class SpreadsheetValueTypeToValueTypeSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    static Optional<ValidationValueTypeName> valueType(final Class<?> type) {
        final SpreadsheetValueTypeToValueTypeSpreadsheetValueTypeVisitor visitor = new SpreadsheetValueTypeToValueTypeSpreadsheetValueTypeVisitor();
        visitor.accept(type);
        return Optional.ofNullable(
                visitor.valueType
        );
    }

    // @VisibleForTesting
    SpreadsheetValueTypeToValueTypeSpreadsheetValueTypeVisitor() {
        super();
        this.valueType = null;
    }

    @Override
    protected void visitBigDecimal() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitBigInteger() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitBoolean() {
        this.valueType(SpreadsheetValueType.BOOLEAN);
    }

    @Override
    protected void visitByte() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitCellRange() {
        this.valueType(SpreadsheetValueType.CELL_RANGE);
    }

    @Override
    protected void visitCellReference() {
        this.valueType(SpreadsheetValueType.CELL);
    }

    @Override
    protected void visitCharacter() {
        this.valueType(SpreadsheetValueType.TEXT);
    }

    @Override
    protected void visitColumnReference() {
        this.valueType(SpreadsheetValueType.COLUMN);
    }

    @Override
    protected void visitColumnRangeReference() {
        this.valueType(SpreadsheetValueType.COLUMN_RANGE);
    }

    @Override
    protected void visitDouble() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitExpressionNumber() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitFloat() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitInteger() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitLabel() {
        this.valueType(SpreadsheetValueType.LABEL);
    }

    @Override
    protected void visitLocalDate() {
        this.valueType(SpreadsheetValueType.DATE);
    }

    @Override
    protected void visitLocalDateTime() {
        this.valueType(SpreadsheetValueType.DATE_TIME);
    }

    @Override
    protected void visitLocalTime() {
        this.valueType(SpreadsheetValueType.TIME);
    }

    @Override
    protected void visitLong() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitNumber() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitRowReference() {
        this.valueType(SpreadsheetValueType.ROW);
    }

    @Override
    protected void visitRowRangeReference() {
        this.valueType(SpreadsheetValueType.ROW_RANGE);
    }

    @Override
    protected void visitSpreadsheetError() {
        this.valueType(SpreadsheetValueType.ERROR);
    }

    @Override
    protected void visitShort() {
        this.valueType(SpreadsheetValueType.NUMBER);
    }

    @Override
    protected void visitString() {
        this.valueType(SpreadsheetValueType.TEXT);
    }

    private void valueType(final String typeName) {
        this.valueType = ValidationValueTypeName.with(typeName);
    }

    private ValidationValueTypeName valueType;

    @Override
    public String toString() {
        return this.valueType.toString();
    }
}
