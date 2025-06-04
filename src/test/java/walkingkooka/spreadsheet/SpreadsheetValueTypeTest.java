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

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.validation.ValidationValueTypeName;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class SpreadsheetValueTypeTest implements PublicStaticHelperTesting<SpreadsheetValueType> {

    @Test
    public void testValueTypeWithBoolean() {
        this.valueTypeAndCheck(
                Boolean.class,
                SpreadsheetValueType.BOOLEAN
        );
    }

    @Test
    public void testValueTypeWithCell() {
        this.valueTypeAndCheck(
                SpreadsheetCellReference.class,
                SpreadsheetValueType.CELL
        );
    }

    @Test
    public void testValueTypeWithCellRange() {
        this.valueTypeAndCheck(
                SpreadsheetCellRangeReference.class,
                SpreadsheetValueType.CELL_RANGE
        );
    }

    @Test
    public void testValueTypeWithColumn() {
        this.valueTypeAndCheck(
                SpreadsheetColumnReference.class,
                SpreadsheetValueType.COLUMN
        );
    }

    @Test
    public void testValueTypeWithColumnRange() {
        this.valueTypeAndCheck(
                SpreadsheetColumnRangeReference.class,
                SpreadsheetValueType.COLUMN_RANGE
        );
    }

    @Test
    public void testValueTypeWithError() {
        this.valueTypeAndCheck(
                SpreadsheetError.class,
                SpreadsheetValueType.ERROR
        );
    }

    @Test
    public void testValueTypeWithExpressionNumber() {
        this.valueTypeAndCheck(
                ExpressionNumber.class,
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testValueTypeWithExpressionNumberBigDecimal() {
        this.valueTypeAndCheck(
                ExpressionNumberKind.BIG_DECIMAL.numberType(),
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testValueTypeWithExpressionNumberDouble() {
        this.valueTypeAndCheck(
                ExpressionNumberKind.DOUBLE.numberType(),
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testValueTypeWithLabel() {
        this.valueTypeAndCheck(
                SpreadsheetLabelName.class,
                SpreadsheetValueType.LABEL
        );
    }

    @Test
    public void testValueTypeWithLocalDate() {
        this.valueTypeAndCheck(
                LocalDate.class,
                SpreadsheetValueType.DATE
        );
    }

    @Test
    public void testValueTypeWithLocalDateTime() {
        this.valueTypeAndCheck(
                LocalDateTime.class,
                SpreadsheetValueType.DATE_TIME
        );
    }

    @Test
    public void testValueTypeWithRow() {
        this.valueTypeAndCheck(
                SpreadsheetRowReference.class,
                SpreadsheetValueType.ROW
        );
    }

    @Test
    public void testValueTypeWithRowRange() {
        this.valueTypeAndCheck(
                SpreadsheetRowRangeReference.class,
                SpreadsheetValueType.ROW_RANGE
        );
    }

    @Test
    public void testValueTypeWithString() {
        this.valueTypeAndCheck(
                String.class,
                SpreadsheetValueType.TEXT
        );
    }

    @Test
    public void testValueTypeWithLocalTime() {
        this.valueTypeAndCheck(
                LocalTime.class,
                SpreadsheetValueType.TIME
        );
    }

    private void valueTypeAndCheck(final Class<?> type,
                                   final String valueType) {
        this.valueTypeAndCheck(
                type,
                ValidationValueTypeName.with(valueType)
        );
    }


    private void valueTypeAndCheck(final Class<?> type,
                                   final ValidationValueTypeName valueType) {
        this.checkEquals(
                valueType,
                SpreadsheetValueType.valueType(type),
                type::getName
        );
    }

    @Override
    public Class<SpreadsheetValueType> type() {
        return SpreadsheetValueType.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
