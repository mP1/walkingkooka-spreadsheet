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
import walkingkooka.collect.list.Lists;
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
import walkingkooka.validation.ValueTypeName;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetValueTypeTest implements PublicStaticHelperTesting<SpreadsheetValueType> {

    @Test
    public void testAll() {
        this.checkEquals(
            Lists.of(
                SpreadsheetValueType.BOOLEAN,
                SpreadsheetValueType.DATE,
                SpreadsheetValueType.DATE_TIME,
                SpreadsheetValueType.NUMBER,
                SpreadsheetValueType.TEXT,
                SpreadsheetValueType.TIME
            ),
            new ArrayList<>(
                SpreadsheetValueType.ALL
            )
        );
    }

    // toValueType......................................................................................................

    @Test
    public void testToValueTypeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetValueType.toValueType(null)
        );
    }

    @Test
    public void testToValueTypeWithBoolean() {
        this.toValueTypeAndCheck(
            Boolean.class,
            SpreadsheetValueType.BOOLEAN
        );
    }

    @Test
    public void testToValueTypeWithCell() {
        this.toValueTypeAndCheck(
            SpreadsheetCellReference.class,
            SpreadsheetValueType.CELL
        );
    }

    @Test
    public void testToValueTypeWithCellRange() {
        this.toValueTypeAndCheck(
            SpreadsheetCellRangeReference.class,
            SpreadsheetValueType.CELL_RANGE
        );
    }

    @Test
    public void testToValueTypeWithColumn() {
        this.toValueTypeAndCheck(
            SpreadsheetColumnReference.class,
            SpreadsheetValueType.COLUMN
        );
    }

    @Test
    public void testToValueTypeWithColumnRange() {
        this.toValueTypeAndCheck(
            SpreadsheetColumnRangeReference.class,
            SpreadsheetValueType.COLUMN_RANGE
        );
    }

    @Test
    public void testToValueTypeWithError() {
        this.toValueTypeAndCheck(
            SpreadsheetError.class,
            SpreadsheetValueType.ERROR
        );
    }

    @Test
    public void testToValueTypeWithExpressionNumber() {
        this.toValueTypeAndCheck(
            ExpressionNumber.class,
            SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testToValueTypeWithExpressionNumberBigDecimal() {
        this.toValueTypeAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.numberType(),
            SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testToValueTypeWithExpressionNumberDouble() {
        this.toValueTypeAndCheck(
            ExpressionNumberKind.DOUBLE.numberType(),
            SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testToValueTypeWithLabel() {
        this.toValueTypeAndCheck(
            SpreadsheetLabelName.class,
            SpreadsheetValueType.LABEL
        );
    }

    @Test
    public void testToValueTypeWithLocalDate() {
        this.toValueTypeAndCheck(
            LocalDate.class,
            SpreadsheetValueType.DATE
        );
    }

    @Test
    public void testToValueTypeWithLocalDateTime() {
        this.toValueTypeAndCheck(
            LocalDateTime.class,
            SpreadsheetValueType.DATE_TIME
        );
    }

    @Test
    public void testToValueTypeWithRow() {
        this.toValueTypeAndCheck(
            SpreadsheetRowReference.class,
            SpreadsheetValueType.ROW
        );
    }

    @Test
    public void testToValueTypeWithRowRange() {
        this.toValueTypeAndCheck(
            SpreadsheetRowRangeReference.class,
            SpreadsheetValueType.ROW_RANGE
        );
    }

    @Test
    public void testToValueTypeWithString() {
        this.toValueTypeAndCheck(
            String.class,
            SpreadsheetValueType.TEXT
        );
    }

    @Test
    public void testToValueTypeWithLocalTime() {
        this.toValueTypeAndCheck(
            LocalTime.class,
            SpreadsheetValueType.TIME
        );
    }

    @Test
    public void testToValueTypeWithUnknownType() {
        this.toValueTypeAndCheck(
            this.getClass(),
            Optional.empty()
        );
    }

    private void toValueTypeAndCheck(final Class<?> type,
                                     final String expected) {
        this.toValueTypeAndCheck(
            type,
            ValueTypeName.with(expected)
        );
    }


    private void toValueTypeAndCheck(final Class<?> type,
                                     final ValueTypeName expected) {
        this.toValueTypeAndCheck(
            type,
            Optional.of(expected)
        );
    }

    private void toValueTypeAndCheck(final Class<?> type,
                                     final Optional<ValueTypeName> expected) {
        this.checkEquals(
            expected,
            SpreadsheetValueType.toValueType(type),
            type::getName
        );
    }

    // toClass..........................................................................................................

    @Test
    public void testToClassWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetValueType.toClass(null)
        );
    }

    @Test
    public void testToClassWithBoolean() {
        this.toClassAndCheck(
            SpreadsheetValueType.BOOLEAN,
            Boolean.class
        );
    }

    @Test
    public void testToClassWithCell() {
        this.toClassAndCheck(
            SpreadsheetValueType.CELL,
            SpreadsheetCellReference.class
        );
    }

    @Test
    public void testToClassWithCellRange() {
        this.toClassAndCheck(
            SpreadsheetValueType.CELL_RANGE,
            SpreadsheetCellRangeReference.class
        );
    }

    @Test
    public void testToClassWithColumn() {
        this.toClassAndCheck(
            SpreadsheetValueType.COLUMN,
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testToClassWithColumnRange() {
        this.toClassAndCheck(
            SpreadsheetValueType.COLUMN_RANGE,
            SpreadsheetColumnRangeReference.class
        );
    }

    @Test
    public void testToClassWithDate() {
        this.toClassAndCheck(
            SpreadsheetValueType.DATE,
            LocalDate.class
        );
    }

    @Test
    public void testToClassWithDateTime() {
        this.toClassAndCheck(
            SpreadsheetValueType.DATE_TIME,
            LocalDateTime.class
        );
    }

    @Test
    public void testToClassWithError() {
        this.toClassAndCheck(
            SpreadsheetValueType.ERROR,
            SpreadsheetError.class
        );
    }

    @Test
    public void testToClassWithLabel() {
        this.toClassAndCheck(
            SpreadsheetValueType.LABEL,
            SpreadsheetLabelName.class
        );
    }

    @Test
    public void testToClassWithNumber() {
        this.toClassAndCheck(
            SpreadsheetValueType.NUMBER,
            ExpressionNumber.class
        );
    }

    @Test
    public void testToClassWithRow() {
        this.toClassAndCheck(
            SpreadsheetValueType.ROW,
            SpreadsheetRowReference.class
        );
    }

    @Test
    public void testToClassWithRowRange() {
        this.toClassAndCheck(
            SpreadsheetValueType.ROW_RANGE,
            SpreadsheetRowRangeReference.class
        );
    }

    @Test
    public void testToClassWithString() {
        this.toClassAndCheck(
            SpreadsheetValueType.TEXT,
            String.class
        );
    }

    @Test
    public void testToClassWithLocalTime() {
        this.toClassAndCheck(
            SpreadsheetValueType.TIME, 
            LocalTime.class
        );
    }

    private void toClassAndCheck(final String valueType,
                                 final Class<?> expected) {
        this.toClassAndCheck(
            ValueTypeName.with(valueType),
            expected
        );
    }

    private void toClassAndCheck(final ValueTypeName valueType,
                                 final Class<?> expected) {
        this.toClassAndCheck(
            valueType,
            Optional.of(expected)
        );
    }

    private void toClassAndCheck(final ValueTypeName valueType,
                                 final Optional<Class<?>> expected) {
        this.checkEquals(
            expected,
            SpreadsheetValueType.toClass(valueType),
            valueType::toString
        );
    }


    // class............................................................................................................

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
