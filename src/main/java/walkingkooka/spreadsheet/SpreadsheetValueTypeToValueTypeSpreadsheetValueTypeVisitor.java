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
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitBigInteger() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitBoolean() {
        this.valueType(SpreadsheetValueType.BOOLEAN_STRING);
    }

    @Override
    protected void visitByte() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitCellRange() {
        this.valueType(SpreadsheetValueType.CELL_RANGE_STRING);
    }

    @Override
    protected void visitCellReference() {
        this.valueType(SpreadsheetValueType.CELL_STRING);
    }

    @Override
    protected void visitCharacter() {
        this.valueType(SpreadsheetValueType.TEXT_STRING);
    }

    @Override
    protected void visitColumnReference() {
        this.valueType(SpreadsheetValueType.COLUMN_STRING);
    }

    @Override
    protected void visitColumnRangeReference() {
        this.valueType(SpreadsheetValueType.COLUMN_RANGE_STRING);
    }

    @Override
    protected void visitDouble() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitExpressionNumber() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitFloat() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitInteger() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitLabel() {
        this.valueType(SpreadsheetValueType.LABEL_STRING);
    }

    @Override
    protected void visitLocalDate() {
        this.valueType(SpreadsheetValueType.DATE_STRING);
    }

    @Override
    protected void visitLocalDateTime() {
        this.valueType(SpreadsheetValueType.DATE_TIME_STRING);
    }

    @Override
    protected void visitLocalTime() {
        this.valueType(SpreadsheetValueType.TIME_STRING);
    }

    @Override
    protected void visitLong() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitNumber() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitRowReference() {
        this.valueType(SpreadsheetValueType.ROW_STRING);
    }

    @Override
    protected void visitRowRangeReference() {
        this.valueType(SpreadsheetValueType.ROW_RANGE_STRING);
    }

    @Override
    protected void visitSpreadsheetError() {
        this.valueType(SpreadsheetValueType.ERROR_STRING);
    }

    @Override
    protected void visitShort() {
        this.valueType(SpreadsheetValueType.NUMBER_STRING);
    }

    @Override
    protected void visitString() {
        this.valueType(SpreadsheetValueType.TEXT_STRING);
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
