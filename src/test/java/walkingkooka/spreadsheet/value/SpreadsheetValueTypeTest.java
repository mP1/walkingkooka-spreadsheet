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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.BooleanList;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.list.StringList;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.color.Color;
import walkingkooka.color.HslColor;
import walkingkooka.color.HslColorComponent;
import walkingkooka.color.HsvColor;
import walkingkooka.color.HsvColorComponent;
import walkingkooka.color.RgbColor;
import walkingkooka.color.RgbColorComponent;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.LocalDateList;
import walkingkooka.datetime.LocalDateTimeList;
import walkingkooka.datetime.LocalTimeList;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.NumberList;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassAttributes;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.SpreadsheetStartup;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.AddExpression;
import walkingkooka.tree.expression.AndExpression;
import walkingkooka.tree.expression.CallExpression;
import walkingkooka.tree.expression.DivideExpression;
import walkingkooka.tree.expression.EqualsExpression;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.GreaterThanEqualsExpression;
import walkingkooka.tree.expression.GreaterThanExpression;
import walkingkooka.tree.expression.LambdaFunctionExpression;
import walkingkooka.tree.expression.LessThanEqualsExpression;
import walkingkooka.tree.expression.LessThanExpression;
import walkingkooka.tree.expression.ListExpression;
import walkingkooka.tree.expression.ModuloExpression;
import walkingkooka.tree.expression.MultiplyExpression;
import walkingkooka.tree.expression.NegativeExpression;
import walkingkooka.tree.expression.NotExpression;
import walkingkooka.tree.expression.OrExpression;
import walkingkooka.tree.expression.PowerExpression;
import walkingkooka.tree.expression.ReferenceExpression;
import walkingkooka.tree.expression.SubtractExpression;
import walkingkooka.tree.expression.ValueExpression;
import walkingkooka.tree.expression.XorExpression;
import walkingkooka.tree.json.JsonArray;
import walkingkooka.tree.json.JsonBoolean;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNull;
import walkingkooka.tree.json.JsonNumber;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.ValueType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetValueTypeTest implements PublicStaticHelperTesting<SpreadsheetValueType> {

    static {
        SpreadsheetStartup.init(); // required so all json marshaller/unmarshallers are registered.
    }

    @Test
    public void testAll() {
        this.checkEquals(
            Lists.of(
                SpreadsheetValueType.BOOLEAN,
                SpreadsheetValueType.DATE,
                SpreadsheetValueType.DATE_TIME,
                SpreadsheetValueType.EMAIL,
                SpreadsheetValueType.NUMBER,
                SpreadsheetValueType.TEXT,
                SpreadsheetValueType.TIME,
                SpreadsheetValueType.URL,
                SpreadsheetValueType.WHOLE_NUMBER
            ),
            new ArrayList<>(
                SpreadsheetValueType.ALL
            )
        );
    }

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetValueType.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetValueType.with("")
        );
    }

    @Test
    public void testWithAbsoluteUrl() {
        assertSame(
            SpreadsheetValueType.ABSOLUTE_URL,
            SpreadsheetValueType.with("url(absolute)")
        );
    }

    @Test
    public void testWithAlphaHslColor() {
        assertSame(
            SpreadsheetValueType.ALPHA_HSL_COLOR,
            SpreadsheetValueType.with("color(hsl-alpha)")
        );
    }

    @Test
    public void testWithAlphaHsvColor() {
        assertSame(
            SpreadsheetValueType.ALPHA_HSV_COLOR,
            SpreadsheetValueType.with("color(hsv-alpha)")
        );
    }

    @Test
    public void testWithAlphaRgbColor() {
        assertSame(
            SpreadsheetValueType.ALPHA_RGB_COLOR,
            SpreadsheetValueType.with("color(rgb-alpha)")
        );
    }

    @Test
    public void testWithDate() {
        assertSame(
            SpreadsheetValueType.DATE,
            SpreadsheetValueType.with("date")
        );
    }

    @Test
    public void testWithEmail() {
        assertSame(
            SpreadsheetValueType.EMAIL,
            SpreadsheetValueType.with("email")
        );
    }

    @Test
    public void testWithEmailAddress() {
        assertSame(
            SpreadsheetValueType.EMAIL_ADDRESS,
            SpreadsheetValueType.with("email-address")
        );
    }

    @Test
    public void testWithOpaqueHslColor() {
        assertSame(
            SpreadsheetValueType.OPAQUE_HSL_COLOR,
            SpreadsheetValueType.with("color(hsl-opaque)")
        );
    }

    @Test
    public void testWithOpaqueHsvColor() {
        assertSame(
            SpreadsheetValueType.OPAQUE_HSV_COLOR,
            SpreadsheetValueType.with("color(hsv-opaque)")
        );
    }

    @Test
    public void testWithOpaqueRgbColor() {
        assertSame(
            SpreadsheetValueType.OPAQUE_RGB_COLOR,
            SpreadsheetValueType.with("color(rgb-opaque)")
        );
    }

    @Test
    public void testWithUrl() {
        assertSame(
            SpreadsheetValueType.URL,
            SpreadsheetValueType.with("url")
        );
    }

    @Test
    public void testWithWholeNumber() {
        assertSame(
            SpreadsheetValueType.WHOLE_NUMBER,
            SpreadsheetValueType.with("whole-number")
        );
    }

    // fromClassName....................................................................................................

    @Test
    public void testFromClassNameWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetValueType.fromClassName(null)
        );
    }

    @Test
    public void testFromClassNameWithCell() {
        this.fromClassNameAndCheck(
            SpreadsheetSelection.A1,
            SpreadsheetValueType.CELL
        );
    }

    @Test
    public void testFromClassNameWithCellRange() {
        this.fromClassNameAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetValueType.CELL_RANGE
        );
    }

    @Test
    public void testFromClassNameWithColumn() {
        this.fromClassNameAndCheck(
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetValueType.COLUMN
        );
    }

    @Test
    public void testFromClassNameWithColumnRange() {
        this.fromClassNameAndCheck(
            SpreadsheetSelection.parseColumnRange("A:B"),
            SpreadsheetValueType.COLUMN_RANGE
        );
    }

    @Test
    public void testFromClassNameWithLabel() {
        this.fromClassNameAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            SpreadsheetValueType.LABEL
        );
    }

    @Test
    public void testFromClassNameWithRow() {
        this.fromClassNameAndCheck(
            SpreadsheetSelection.parseRow("12"),
            SpreadsheetValueType.ROW
        );
    }

    @Test
    public void testFromClassNameWithRowRange() {
        this.fromClassNameAndCheck(
            SpreadsheetSelection.parseRowRange("12:34"),
            SpreadsheetValueType.ROW_RANGE
        );
    }

    @Test
    public void testFromClassNameWithAlphaHslColor() {
        this.fromClassNameAndCheck(
            Color.BLACK.toHsl()
                .set(
                    HslColorComponent.alpha(0.5f)
                ),
            SpreadsheetValueType.ALPHA_HSL_COLOR
        );
    }

    @Test
    public void testFromClassNameWithAlphaHsvColor() {
        this.fromClassNameAndCheck(
            Color.BLACK.toHsv()
                .set(
                    HsvColorComponent.alpha(0.5f)
                ),
            SpreadsheetValueType.ALPHA_HSV_COLOR
        );
    }

    @Test
    public void testFromClassNameWithAlphaRgbColor() {
        this.fromClassNameAndCheck(
            Color.BLACK.set(
                RgbColorComponent.alpha((byte)127)
            ),
            SpreadsheetValueType.ALPHA_RGB_COLOR
        );
    }

    @Test
    public void testFromClassNameWithColor() {
        this.fromClassNameAndCheck(
            Color.class,
            SpreadsheetValueType.COLOR
        );
    }

    @Test
    public void testFromClassNameWithHslColor() {
        this.fromClassNameAndCheck(
            HslColor.class,
            SpreadsheetValueType.HSL_COLOR
        );
    }

    @Test
    public void testFromClassNameWithHsvColor() {
        this.fromClassNameAndCheck(
            HsvColor.class,
            SpreadsheetValueType.HSV_COLOR
        );
    }

    @Test
    public void testFromClassNameWithOpaqueHslColor() {
        this.fromClassNameAndCheck(
            Color.BLACK.toHsl(),
            SpreadsheetValueType.OPAQUE_HSL_COLOR
        );
    }

    @Test
    public void testFromClassNameWithOpaqueHsvColor() {
        this.fromClassNameAndCheck(
            Color.BLACK.toHsv(),
            SpreadsheetValueType.OPAQUE_HSV_COLOR
        );
    }

    @Test
    public void testFromClassNameWithOpaqueRgbColor() {
        this.fromClassNameAndCheck(
            Color.BLACK,
            SpreadsheetValueType.OPAQUE_RGB_COLOR
        );
    }

    @Test
    public void testFromClassNameWithRgbColor() {
        this.fromClassNameAndCheck(
            RgbColor.class,
            SpreadsheetValueType.RGB_COLOR
        );
    }
    
    @Test
    public void testFromClassNameWithExpressionNumber() {
        this.fromClassNameAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.zero(),
            SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testFromClassNameWithString() {
        this.fromClassNameAndCheck(
            String.class,
            SpreadsheetValueType.TEXT
        );
    }

    @Test
    public void testFromClassNameWithSpreadsheetError() {
        this.fromClassNameAndCheck(
            SpreadsheetError.class,
            SpreadsheetValueType.SPREADSHEET_ERROR
        );
    }

    @Test
    public void testFromClassNameWithValidationError() {
        this.fromClassNameAndCheck(
            ValidationError.class,
            SpreadsheetValueType.VALIDATION_ERROR
        );
    }

    private void fromClassNameAndCheck(final Object value,
                                       final ValueType expected) {
        this.fromClassNameAndCheck(
            value.getClass(),
            expected
        );
    }

    private void fromClassNameAndCheck(final Class<?> klass,
                                       final ValueType expected) {
        this.fromClassNameAndCheck(
            klass.getName(),
            expected
        );
    }

    private void fromClassNameAndCheck(final String className,
                                       final ValueType expected) {
        this.checkEquals(
            expected,
            SpreadsheetValueType.fromClassName(className)
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
    public void testToValueTypeWithAbsoluteUrl() {
        this.toValueTypeAndCheck(
            AbsoluteUrl.class,
            SpreadsheetValueType.ABSOLUTE_URL
        );
    }

    @Test
    public void testToValueTypeWithAlphaHslColor() {
        this.toValueTypeAndCheck(
            Color.BLACK.toHsl()
                .set(HslColorComponent.alpha(0.5f)),
            SpreadsheetValueType.ALPHA_HSL_COLOR
        );
    }

    @Test
    public void testToValueTypeWithAlphaHsvColor() {
        this.toValueTypeAndCheck(
            Color.BLACK.toHsv()
                .set(HsvColorComponent.alpha(0.5f)),
            SpreadsheetValueType.ALPHA_HSV_COLOR
        );
    }

    @Test
    public void testToValueTypeWithAlphaRgbColor() {
        this.toValueTypeAndCheck(
            Color.BLACK
                .set(RgbColorComponent.alpha((byte) 127)),
            SpreadsheetValueType.ALPHA_RGB_COLOR
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
    public void testToValueTypeWithBooleanList() {
        this.toValueTypeAndCheck(
            BooleanList.class,
            SpreadsheetValueType.BOOLEAN_LIST
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
    public void testToValueTypeWithColor() {
        this.toValueTypeAndCheck(
            Color.class,
            SpreadsheetValueType.COLOR
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
    public void testToValueTypeWithCsvStringList() {
        this.toValueTypeAndCheck(
            CsvStringList.class,
            SpreadsheetValueType.CSV_LIST
        );
    }

    @Test
    public void testToValueTypeWithDateList() {
        this.toValueTypeAndCheck(
            LocalDateList.class,
            SpreadsheetValueType.DATE_LIST
        );
    }

    @Test
    public void testToValueTypeWithDateTimeList() {
        this.toValueTypeAndCheck(
            LocalDateTimeList.class,
            SpreadsheetValueType.DATE_TIME_LIST
        );
    }

    @Test
    public void testToValueTypeWithEmail() {
        this.toValueTypeAndCheck(
            EmailAddress.class,
            SpreadsheetValueType.EMAIL
        );
    }

    @Test
    public void testToValueTypeWithEmailAddress() {
        this.toValueTypeAndCheck(
            EmailAddress.class,
            SpreadsheetValueType.EMAIL
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
    public void testToValueTypeWithHslColor() {
        this.toValueTypeAndCheck(
            HslColor.class,
            SpreadsheetValueType.HSL_COLOR
        );
    }

    @Test
    public void testToValueTypeWithHsvColor() {
        this.toValueTypeAndCheck(
            HsvColor.class,
            SpreadsheetValueType.HSV_COLOR
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
    public void testToValueTypeWithList() {
        this.toValueTypeAndCheck(
            List.class,
            SpreadsheetValueType.LIST
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
    public void testToValueTypeWithNumberList() {
        this.toValueTypeAndCheck(
            NumberList.class,
            SpreadsheetValueType.NUMBER_LIST
        );
    }

    @Test
    public void testToValueTypeWithOpaqueHslColor() {
        this.toValueTypeAndCheck(
            Color.BLACK.toHsl(),
            SpreadsheetValueType.OPAQUE_HSL_COLOR
        );
    }

    @Test
    public void testToValueTypeWithOpaqueHsvColor() {
        this.toValueTypeAndCheck(
            Color.BLACK.toHsv(),
            SpreadsheetValueType.OPAQUE_HSV_COLOR
        );
    }

    @Test
    public void testToValueTypeWithOpaqueRgbColor() {
        this.toValueTypeAndCheck(
            Color.BLACK,
            SpreadsheetValueType.OPAQUE_RGB_COLOR
        );
    }

    @Test
    public void testToValueTypeWithRgbColor() {
        this.toValueTypeAndCheck(
            RgbColor.class,
            SpreadsheetValueType.RGB_COLOR
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
    public void testToValueTypeWithStringList() {
        this.toValueTypeAndCheck(
            StringList.class,
            SpreadsheetValueType.STRING_LIST
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
    public void testToValueTypeWithTimeList() {
        this.toValueTypeAndCheck(
            LocalTimeList.class,
            SpreadsheetValueType.TIME_LIST
        );
    }

    @Test
    public void testToValueTypeWithValidationChoiceList() {
        this.toValueTypeAndCheck(
            ValidationChoiceList.class,
            SpreadsheetValueType.CHOICE_LIST
        );
    }

    @Test
    public void testToValueTypeWithValidationErrorList() {
        this.toValueTypeAndCheck(
            ValidationErrorList.class,
            SpreadsheetValueType.ERROR_LIST
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
            ValueType.with(expected)
        );
    }

    private void toValueTypeAndCheck(final Object type,
                                     final ValueType expected) {
        this.toValueTypeAndCheck(
            type.getClass(),
            Optional.of(expected)
        );
    }

    private void toValueTypeAndCheck(final Class<?> type,
                                     final ValueType expected) {
        this.toValueTypeAndCheck(
            type,
            Optional.of(expected)
        );
    }

    private void toValueTypeAndCheck(final Class<?> type,
                                     final Optional<ValueType> expected) {
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
    public void testToClassWithAbsoluteUrl() {
        this.toClassAndCheck(
            SpreadsheetValueType.ABSOLUTE_URL,
            AbsoluteUrl.class
        );
    }

    @Test
    public void testToClassWithAddExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.ADD_EXPRESSION,
            AddExpression.class
        );
    }

    @Test
    public void testToClassWithAlphaHsl() {
        this.toClassAndCheck(
            SpreadsheetValueType.ALPHA_HSL_COLOR,
            Color.BLACK.toHsl()
                .set(HslColorComponent.alpha(0.5f))
                .getClass()
        );
    }

    @Test
    public void testToClassWithAlphaHsv() {
        this.toClassAndCheck(
            SpreadsheetValueType.ALPHA_HSV_COLOR,
            Color.BLACK.toHsv()
                .set(HsvColorComponent.alpha(0.5f))
                .getClass()
        );
    }

    @Test
    public void testToClassWithAlphaRgb() {
        this.toClassAndCheck(
            SpreadsheetValueType.ALPHA_RGB_COLOR,
            Color.BLACK.set(
                RgbColorComponent.alpha((byte) 127)
            )
        );
    }

    @Test
    public void testToClassWithAndExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.AND_EXPRESSION,
            AndExpression.class
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
    public void testToClassWithBooleanList() {
        this.toClassAndCheck(
            SpreadsheetValueType.BOOLEAN_LIST,
            BooleanList.class
        );
    }

    @Test
    public void testToClassWithCallExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.CALL_EXPRESSION,
            CallExpression.class
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
    public void testToClassWithChoiceList() {
        this.toClassAndCheck(
            SpreadsheetValueType.CHOICE_LIST,
            ValidationChoiceList.class
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
    public void testToClassWithCsvList() {
        this.toClassAndCheck(
            SpreadsheetValueType.CSV_LIST,
            CsvStringList.class
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
    public void testToClassWithDateList() {
        this.toClassAndCheck(
            SpreadsheetValueType.DATE_LIST,
            LocalDateList.class
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
    public void testToClassWithDateTimeList() {
        this.toClassAndCheck(
            SpreadsheetValueType.DATE_TIME_LIST,
            LocalDateTimeList.class
        );
    }

    @Test
    public void testToClassWithDateTimeSymbols() {
        this.toClassAndCheck(
            SpreadsheetValueType.DATE_TIME_SYMBOLS,
            DateTimeSymbols.class
        );
    }

    @Test
    public void testToClassWithDecimalNumberSymbols() {
        this.toClassAndCheck(
            SpreadsheetValueType.DECIMAL_NUMBER_SYMBOLS,
            DecimalNumberSymbols.class
        );
    }

    @Test
    public void testToClassWithDivideExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.DIVIDE_EXPRESSION,
            DivideExpression.class
        );
    }

    @Test
    public void testToClassWithEmail() {
        this.toClassAndCheck(
            SpreadsheetValueType.EMAIL,
            EmailAddress.class
        );
    }

    @Test
    public void testToClassWithEmailAddress() {
        this.toClassAndCheck(
            SpreadsheetValueType.EMAIL_ADDRESS,
            EmailAddress.class
        );
    }

    @Test
    public void testToClassWithError() {
        this.toClassAndCheck(
            ValueType.ERROR
        );
    }

    @Test
    public void testToClassWithEqualsExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.EQUALS_EXPRESSION,
            EqualsExpression.class
        );
    }
    
    @Test
    public void testToClassWithExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.EXPRESSION,
            Expression.class
        );
    }

    @Test
    public void testToClassWithGreaterThanExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.GREATER_THAN_EXPRESSION_STRING,
            GreaterThanExpression.class
        );
    }
    
    @Test
    public void testToClassWithGreaterThanEqualsExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.GREATER_THAN_EQUALS_EXPRESSION_STRING,
            GreaterThanEqualsExpression.class
        );
    }
    
    @Test
    public void testToClassWithJsonArray() {
        this.toClassAndCheck(
            SpreadsheetValueType.JSON_ARRAY,
            JsonArray.class
        );
    }

    @Test
    public void testToClassWithJsonBoolean() {
        this.toClassAndCheck(
            SpreadsheetValueType.JSON_BOOLEAN,
            JsonBoolean.class
        );
    }

    @Test
    public void testToClassWithJsonNode() {
        this.toClassAndCheck(
            SpreadsheetValueType.JSON_NODE,
            JsonNode.class
        );
    }

    @Test
    public void testToClassWithJsonNull() {
        this.toClassAndCheck(
            SpreadsheetValueType.JSON_NULL,
            JsonNull.class
        );
    }

    @Test
    public void testToClassWithJsonNumber() {
        this.toClassAndCheck(
            SpreadsheetValueType.JSON_NUMBER,
            JsonNumber.class
        );
    }

    @Test
    public void testToClassWithJsonObject() {
        this.toClassAndCheck(
            SpreadsheetValueType.JSON_OBJECT,
            JsonObject.class
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
    public void testToClassWithLambdaFunctionExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.LAMBDA_FUNCTION_EXPRESSION_STRING,
            LambdaFunctionExpression.class
        );
    }

    @Test
    public void testToClassWithLessThanExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.LESS_THAN_EXPRESSION_STRING,
            LessThanExpression.class
        );
    }

    @Test
    public void testToClassWithLessThanEqualsExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.LESS_THAN_EQUALS_EXPRESSION_STRING,
            LessThanEqualsExpression.class
        );
    }

    @Test
    public void testToClassWithListEqualsExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.LIST_EXPRESSION_STRING,
            ListExpression.class
        );
    }

    @Test
    public void testToClassWithLocalDate() {
        this.toClassAndCheck(
            SpreadsheetValueType.LOCAL_DATE,
            LocalDate.class
        );
    }

    @Test
    public void testToClassWithLocalDateTime() {
        this.toClassAndCheck(
            SpreadsheetValueType.LOCAL_DATE_TIME,
            LocalDateTime.class
        );
    }

    @Test
    public void testToClassWithLocalTime() {
        this.toClassAndCheck(
            SpreadsheetValueType.LOCAL_TIME,
            LocalTime.class
        );
    }

    @Test
    public void testToClassWithModuloEqualsExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.MODULO_EXPRESSION_STRING,
            ModuloExpression.class
        );
    }

    @Test
    public void testToClassWithMultiplyEqualsExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.MULTIPLY_EXPRESSION_STRING,
            MultiplyExpression.class
        );
    }

    @Test
    public void testToClassWithNegativeExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.NEGATIVE_EXPRESSION_STRING,
            NegativeExpression.class
        );
    }

    @Test
    public void testToClassWithNotEqualsExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.NOT_EXPRESSION_STRING,
            NotExpression.class
        );
    }

    @Test
    public void testToClassWithNotExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.NOT_EXPRESSION_STRING,
            NotExpression.class
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
    public void testToClassWithNumberList() {
        this.toClassAndCheck(
            SpreadsheetValueType.NUMBER_LIST,
            NumberList.class
        );
    }

    @Test
    public void testToClassWithOpaqueHsl() {
        this.toClassAndCheck(
            SpreadsheetValueType.OPAQUE_HSL_COLOR,
            Color.BLACK.toHsl()
        );
    }

    @Test
    public void testToClassWithOpaqueHsv() {
        this.toClassAndCheck(
            SpreadsheetValueType.OPAQUE_HSV_COLOR,
            Color.BLACK.toHsv()
        );
    }

    @Test
    public void testToClassWithOpaqueRgb() {
        this.toClassAndCheck(
            SpreadsheetValueType.OPAQUE_RGB_COLOR,
            Color.BLACK
        );
    }

    @Test
    public void testToClassWithOrExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.OR_EXPRESSION_STRING,
            OrExpression.class
        );
    }

    @Test
    public void testToClassWithPowerExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.POWER_EXPRESSION_STRING,
            PowerExpression.class
        );
    }

    @Test
    public void testToClassWithReferenceExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.REFERENCE_EXPRESSION_STRING,
            ReferenceExpression.class
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
    public void testToClassWithSpreadsheetError() {
        this.toClassAndCheck(
            SpreadsheetValueType.SPREADSHEET_ERROR,
            SpreadsheetError.class
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
    public void testToClassWithStringLists() {
        this.toClassAndCheck(
            SpreadsheetValueType.STRING_LIST,
            StringList.class
        );
    }

    @Test
    public void testToClassWithSubtractExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.SUBTRACT_EXPRESSION_STRING,
            SubtractExpression.class
        );
    }
    
    @Test
    public void testToClassWithTime() {
        this.toClassAndCheck(
            SpreadsheetValueType.TIME,
            LocalTime.class
        );
    }

    @Test
    public void testToClassWithTimeList() {
        this.toClassAndCheck(
            SpreadsheetValueType.TIME_LIST,
            LocalTimeList.class
        );
    }

    @Test
    public void testToClassWithUrl() {
        this.toClassAndCheck(
            SpreadsheetValueType.URL,
            AbsoluteUrl.class
        );
    }

    @Test
    public void testToClassWithValidationError() {
        this.toClassAndCheck(
            SpreadsheetValueType.VALIDATION_ERROR,
            ValidationError.class
        );
    }

    @Test
    public void testToClassWithValidationErrorList() {
        this.toClassAndCheck(
            SpreadsheetValueType.VALIDATION_ERROR_LIST,
            ValidationErrorList.class
        );
    }

    @Test
    public void testToClassWithValueExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.VALUE_EXPRESSION_STRING,
            ValueExpression.class
        );
    }
    
    @Test
    public void testToClassWithWholeNumber() {
        this.toClassAndCheck(
            SpreadsheetValueType.WHOLE_NUMBER,
            ExpressionNumber.class
        );
    }

    @Test
    public void testToClassWithXorExpression() {
        this.toClassAndCheck(
            SpreadsheetValueType.XOR_EXPRESSION_STRING,
            XorExpression.class
        );
    }

    private void toClassAndCheck(final ValueType valueType) {
        this.toClassAndCheck(
            valueType,
            Optional.empty()
        );
    }

    private void toClassAndCheck(final ValueType valueType,
                                 final Object expected) {
        this.toClassAndCheck(
            valueType,
            expected.getClass()
        );
    }

    private void toClassAndCheck(final String valueType,
                                 final Class<?> expected) {
        this.toClassAndCheck(
            ValueType.with(valueType),
            expected
        );
    }

    private void toClassAndCheck(final ValueType valueType,
                                 final Class<?> expected) {
        this.toClassAndCheck(
            valueType,
            Optional.of(expected)
        );
    }

    private void toClassAndCheck(final ValueType valueType,
                                 final Optional<Class<?>> expected) {
        this.checkEquals(
            expected,
            SpreadsheetValueType.toClass(valueType),
            valueType::toString
        );
    }

    // isColor..........................................................................................................

    @Test
    public void testIsColorWithAlphaHslColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.ALPHA_HSL_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithAlphaHsvColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.ALPHA_HSV_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithAlphaRgbColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.ALPHA_RGB_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithAny() {
        this.isColorAndCheck(
            SpreadsheetValueType.ANY,
            false
        );
    }

    @Test
    public void testIsColorWithBoolean() {
        this.isColorAndCheck(
            SpreadsheetValueType.BOOLEAN,
            false
        );
    }
    
    @Test
    public void testIsColorWithColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithHslColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.HSL_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithHsvColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.HSV_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithNumber() {
        this.isColorAndCheck(
            SpreadsheetValueType.NUMBER,
            false
        );
    }

    @Test
    public void testIsColorWithOpaqueHslColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.OPAQUE_HSL_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithOpaqueHsvColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.OPAQUE_HSV_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithOpaqueRgbColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.OPAQUE_RGB_COLOR,
            true
        );
    }
    
    @Test
    public void testIsColorWithRgbColor() {
        this.isColorAndCheck(
            SpreadsheetValueType.RGB_COLOR,
            true
        );
    }

    @Test
    public void testIsColorWithText() {
        this.isColorAndCheck(
            SpreadsheetValueType.TEXT,
            false
        );
    }
    
    private void isColorAndCheck(final ValueType valueType,
                                 final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetValueType.isColor(valueType)
        );
    }

    // isReference..........................................................................................................

    @Test
    public void testIsReferenceWithAny() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.ANY,
            false
        );
    }

    @Test
    public void testIsReferenceWithCell() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.CELL,
            true
        );
    }

    @Test
    public void testIsReferenceWithCellRange() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.CELL_RANGE,
            true
        );
    }

    @Test
    public void testIsReferenceWithColumn() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.COLUMN,
            true
        );
    }

    @Test
    public void testIsReferenceWithColumnRange() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.COLUMN_RANGE,
            true
        );
    }

    @Test
    public void testIsReferenceWithLabel() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.LABEL,
            true
        );
    }

    @Test
    public void testIsReferenceWithRow() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.ROW,
            true
        );
    }

    @Test
    public void testIsReferenceWithRowRange() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.ROW_RANGE,
            true
        );
    }

    @Test
    public void testIsReferenceWithNumber() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.NUMBER,
            false
        );
    }

    @Test
    public void testIsReferenceWithString() {
        this.isReferenceAndCheck(
            SpreadsheetValueType.STRING,
            false
        );
    }

    private void isReferenceAndCheck(final ValueType valueType,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetValueType.isReference(valueType)
        );
    }
    
    // json.............................................................................................................

    @Test
    public void testConstantsJsonTypeNames() throws Exception {
        final Set<ValueType> missing = SortedSets.tree();
        final JsonNodeMarshallContext context = JsonNodeMarshallContexts.basic();

        for (final Field constant : SpreadsheetValueType.class.getFields()) {
            if (false == FieldAttributes.STATIC.is(constant)) {
                continue;
            }

            if (JavaVisibility.of(constant) != JavaVisibility.PUBLIC) {
                continue;
            }

            if (constant.getType() != ValueType.class) {
                continue;
            }

            final ValueType valueType = ((ValueType) constant.get(null));

            final Class<?> type = SpreadsheetValueType.toClass(valueType)
                .orElse(null);
            if (null != type && false == ClassAttributes.ABSTRACT.is(type)) {
                final JsonString string = context.typeName(type)
                    .orElse(null);
                if (null == string) {
                    missing.add(valueType);
                }
            }
        }

        this.checkEquals(
            Sets.empty(),
            missing
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
