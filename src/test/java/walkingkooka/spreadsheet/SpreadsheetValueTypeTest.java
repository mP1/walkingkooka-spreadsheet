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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class SpreadsheetValueTypeTest implements PublicStaticHelperTesting<SpreadsheetValueType> {

    @Test
    public void testBoolean() {
        this.typeNameAndCheck(
                Boolean.class,
                SpreadsheetValueType.BOOLEAN
        );
    }

    @Test
    public void testCell() {
        this.typeNameAndCheck(
                SpreadsheetCellReference.class,
                SpreadsheetValueType.CELL
        );
    }

    @Test
    public void testCellRange() {
        this.typeNameAndCheck(
                SpreadsheetCellRange.class,
                SpreadsheetValueType.CELL_RANGE
        );
    }

    @Test
    public void testColumn() {
        this.typeNameAndCheck(
                SpreadsheetColumnReference.class,
                SpreadsheetValueType.COLUMN
        );
    }

    @Test
    public void testColumnRange() {
        this.typeNameAndCheck(
                SpreadsheetColumnReferenceRange.class,
                SpreadsheetValueType.COLUMN_RANGE
        );
    }

    @Test
    public void testError() {
        this.typeNameAndCheck(
                SpreadsheetError.class,
                SpreadsheetValueType.ERROR
        );
    }

    @Test
    public void testExpressionNumber() {
        this.typeNameAndCheck(
                ExpressionNumber.class,
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testExpressionNumberBigDecimal() {
        this.typeNameAndCheck(
                ExpressionNumberKind.BIG_DECIMAL.numberType(),
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testExpressionNumberDouble() {
        this.typeNameAndCheck(
                ExpressionNumberKind.DOUBLE.numberType(),
                SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testLabel() {
        this.typeNameAndCheck(
                SpreadsheetLabelName.class,
                SpreadsheetValueType.LABEL
        );
    }

    @Test
    public void testLocalDate() {
        this.typeNameAndCheck(
                LocalDate.class,
                SpreadsheetValueType.DATE
        );
    }

    @Test
    public void testLocalDateTime() {
        this.typeNameAndCheck(
                LocalDateTime.class,
                SpreadsheetValueType.DATE_TIME
        );
    }

    @Test
    public void testRow() {
        this.typeNameAndCheck(
                SpreadsheetRowReference.class,
                SpreadsheetValueType.ROW
        );
    }

    @Test
    public void testRowRange() {
        this.typeNameAndCheck(
                SpreadsheetRowReferenceRange.class,
                SpreadsheetValueType.ROW_RANGE
        );
    }

    @Test
    public void testString() {
        this.typeNameAndCheck(
                String.class,
                SpreadsheetValueType.TEXT
        );
    }

    @Test
    public void testLocalTime() {
        this.typeNameAndCheck(
                LocalTime.class,
                SpreadsheetValueType.TIME
        );
    }

    private void typeNameAndCheck(final Class<?> type,
                                  final String typeName) {
        this.checkEquals(
                typeName,
                SpreadsheetValueType.typeName(type),
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
