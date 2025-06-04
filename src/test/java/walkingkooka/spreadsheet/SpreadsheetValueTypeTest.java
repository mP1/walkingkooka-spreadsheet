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
    public void testBoolean() {
        this.valueTypeAndCheck(
                Boolean.class,
                SpreadsheetValueType.BOOLEAN
        );
    }

    @Test
    public void testCell() {
        this.valueTypeAndCheck(
                SpreadsheetCellReference.class,
                SpreadsheetValueType.CELL
        );
    }

    @Test
    public void testCellRange() {
        this.valueTypeAndCheck(
                SpreadsheetCellRangeReference.class,
                SpreadsheetValueType.CELL_RANGE
        );
    }

    @Test
    public void testColumn() {
        this.valueTypeAndCheck(
                SpreadsheetColumnReference.class,
                SpreadsheetValueType.COLUMN
        );
    }

    @Test
    public void testColumnRange() {
        this.valueTypeAndCheck(
                SpreadsheetColumnRangeReference.class,
                SpreadsheetValueType.COLUMN_RANGE
        );
    }

    @Test
    public void testError() {
        this.valueTypeAndCheck(
                SpreadsheetError.class,
                SpreadsheetValueType.ERROR
        );
    }

    @Test
    public void testExpressionNumber() {
        this.valueTypeAndCheck(
                ExpressionNumber.class,
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testExpressionNumberBigDecimal() {
        this.valueTypeAndCheck(
                ExpressionNumberKind.BIG_DECIMAL.numberType(),
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testExpressionNumberDouble() {
        this.valueTypeAndCheck(
                ExpressionNumberKind.DOUBLE.numberType(),
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testLabel() {
        this.valueTypeAndCheck(
                SpreadsheetLabelName.class,
                SpreadsheetValueType.LABEL
        );
    }

    @Test
    public void testLocalDate() {
        this.valueTypeAndCheck(
                LocalDate.class,
                SpreadsheetValueType.DATE
        );
    }

    @Test
    public void testLocalDateTime() {
        this.valueTypeAndCheck(
                LocalDateTime.class,
                SpreadsheetValueType.DATE_TIME
        );
    }

    @Test
    public void testRow() {
        this.valueTypeAndCheck(
                SpreadsheetRowReference.class,
                SpreadsheetValueType.ROW
        );
    }

    @Test
    public void testRowRange() {
        this.valueTypeAndCheck(
                SpreadsheetRowRangeReference.class,
                SpreadsheetValueType.ROW_RANGE
        );
    }

    @Test
    public void testString() {
        this.valueTypeAndCheck(
                String.class,
                SpreadsheetValueType.TEXT
        );
    }

    @Test
    public void testLocalTime() {
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
