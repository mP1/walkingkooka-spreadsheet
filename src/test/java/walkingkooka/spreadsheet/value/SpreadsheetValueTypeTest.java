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
import walkingkooka.currency.CurrencyValue;
import walkingkooka.currency.HasCurrencyCodeTesting;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.LocalDateList;
import walkingkooka.datetime.LocalDateTimeList;
import walkingkooka.datetime.LocalTimeList;
import walkingkooka.environment.Environment;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.NumberList;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
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
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
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

public final class SpreadsheetValueTypeTest implements PublicStaticHelperTesting<SpreadsheetValueType>,
    HasCurrencyCodeTesting {

    static {
        SpreadsheetStartup.init(); // required so all json marshaller/unmarshallers are registered.
    }

    @Test
    public void testAll() {
        this.checkEquals(
            Lists.of(
                SpreadsheetValueType.BOOLEAN,
                SpreadsheetValueType.CURRENCY,
                SpreadsheetValueType.DATE,
                SpreadsheetValueType.DATE_TIME,
                SpreadsheetValueType.EMAIL,
                SpreadsheetValueType.NUMBER,
                SpreadsheetValueType.WHOLE_NUMBER,
                SpreadsheetValueType.TEXT,
                SpreadsheetValueType.TIME,
                SpreadsheetValueType.URL
            ),
            new ArrayList<>(
                SpreadsheetValueType.ALL
            )
        );
    }

    // fromClassName....................................................................................................

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
    public void testFromClassNameWithCurrencyValue() {
        this.fromClassNameAndCheck(
            CurrencyValue.with(
                123,
                CURRENCY_CODE
            ),
            SpreadsheetValueType.CURRENCY_VALUE
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
                RgbColorComponent.alpha((byte) 127)
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
            klass.getSimpleName(),
            expected
        );
    }

    private void fromClassNameAndCheck(final String className,
                                       final ValueType expected) {
        this.checkEquals(
            Optional.of(expected),
            ValueType.fromClassName(className),
            () -> "ValueType.fromClassName " + CharSequences.quoteAndEscape(className)
        );
    }

    // toValueType......................................................................................................

    @Test
    public void testFromClassTypeWithAbsoluteUrl() {
        this.fromClassAndCheck(
            AbsoluteUrl.class,
            SpreadsheetValueType.ABSOLUTE_URL
        );
    }

    @Test
    public void testFromClassTypeWithAlphaHslColor() {
        this.fromClassAndCheck(
            Color.BLACK.toHsl()
                .set(HslColorComponent.alpha(0.5f)),
            SpreadsheetValueType.ALPHA_HSL_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithAlphaHsvColor() {
        this.fromClassAndCheck(
            Color.BLACK.toHsv()
                .set(HsvColorComponent.alpha(0.5f)),
            SpreadsheetValueType.ALPHA_HSV_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithAlphaRgbColor() {
        this.fromClassAndCheck(
            Color.BLACK
                .set(RgbColorComponent.alpha((byte) 127)),
            SpreadsheetValueType.ALPHA_RGB_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithBoolean() {
        this.fromClassAndCheck(
            Boolean.class,
            SpreadsheetValueType.BOOLEAN
        );
    }

    @Test
    public void testFromClassTypeWithBooleanList() {
        this.fromClassAndCheck(
            BooleanList.class,
            SpreadsheetValueType.BOOLEAN_LIST
        );
    }

    @Test
    public void testFromClassTypeWithCell() {
        this.fromClassAndCheck(
            SpreadsheetCellReference.class,
            SpreadsheetValueType.CELL
        );
    }

    @Test
    public void testFromClassTypeWithCellRange() {
        this.fromClassAndCheck(
            SpreadsheetCellRangeReference.class,
            SpreadsheetValueType.CELL_RANGE
        );
    }

    @Test
    public void testFromClassTypeWithColor() {
        this.fromClassAndCheck(
            Color.class,
            SpreadsheetValueType.COLOR
        );
    }

    @Test
    public void testFromClassTypeWithColumn() {
        this.fromClassAndCheck(
            SpreadsheetColumnReference.class,
            SpreadsheetValueType.COLUMN
        );
    }

    @Test
    public void testFromClassTypeWithColumnRange() {
        this.fromClassAndCheck(
            SpreadsheetColumnRangeReference.class,
            SpreadsheetValueType.COLUMN_RANGE
        );
    }

    @Test
    public void testFromClassTypeWithCsvStringList() {
        this.fromClassAndCheck(
            CsvStringList.class,
            SpreadsheetValueType.CSV_LIST
        );
    }

    @Test
    public void testFromClassTypeWithDateList() {
        this.fromClassAndCheck(
            LocalDateList.class,
            SpreadsheetValueType.DATE_LIST
        );
    }

    @Test
    public void testFromClassTypeWithDateTimeList() {
        this.fromClassAndCheck(
            LocalDateTimeList.class,
            SpreadsheetValueType.DATE_TIME_LIST
        );
    }

    @Test
    public void testFromClassTypeWithEmail() {
        this.fromClassAndCheck(
            EmailAddress.class,
            SpreadsheetValueType.EMAIL
        );
    }

    @Test
    public void testFromClassTypeWithEmailAddress() {
        this.fromClassAndCheck(
            EmailAddress.class,
            SpreadsheetValueType.EMAIL
        );
    }

    @Test
    public void testFromClassTypeWithExpressionNumber() {
        this.fromClassAndCheck(
            ExpressionNumber.class,
            SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testFromClassTypeWithExpressionNumberBigDecimal() {
        this.fromClassAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.zero()
                .getClass(),
            SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testFromClassTypeWithExpressionNumberDouble() {
        this.fromClassAndCheck(
            ExpressionNumberKind.DOUBLE.zero()
                .getClass(),
            SpreadsheetValueType.NUMBER
        );
    }

    @Test
    public void testFromClassTypeWithHslColor() {
        this.fromClassAndCheck(
            HslColor.class,
            SpreadsheetValueType.HSL_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithHsvColor() {
        this.fromClassAndCheck(
            HsvColor.class,
            SpreadsheetValueType.HSV_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithLabel() {
        this.fromClassAndCheck(
            SpreadsheetLabelName.class,
            SpreadsheetValueType.LABEL
        );
    }

    @Test
    public void testFromClassTypeWithList() {
        this.fromClassAndCheck(
            List.class,
            SpreadsheetValueType.LIST
        );
    }

    @Test
    public void testFromClassTypeWithLocalDate() {
        this.fromClassAndCheck(
            LocalDate.class,
            SpreadsheetValueType.DATE
        );
    }

    @Test
    public void testFromClassTypeWithLocalDateTime() {
        this.fromClassAndCheck(
            LocalDateTime.class,
            SpreadsheetValueType.DATE_TIME
        );
    }

    @Test
    public void testFromClassTypeWithNumberList() {
        this.fromClassAndCheck(
            NumberList.class,
            SpreadsheetValueType.NUMBER_LIST
        );
    }

    @Test
    public void testFromClassTypeWithOpaqueHslColor() {
        this.fromClassAndCheck(
            Color.BLACK.toHsl(),
            SpreadsheetValueType.OPAQUE_HSL_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithOpaqueHsvColor() {
        this.fromClassAndCheck(
            Color.BLACK.toHsv(),
            SpreadsheetValueType.OPAQUE_HSV_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithOpaqueRgbColor() {
        this.fromClassAndCheck(
            Color.BLACK,
            SpreadsheetValueType.OPAQUE_RGB_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithRgbColor() {
        this.fromClassAndCheck(
            RgbColor.class,
            SpreadsheetValueType.RGB_COLOR
        );
    }

    @Test
    public void testFromClassTypeWithRow() {
        this.fromClassAndCheck(
            SpreadsheetRowReference.class,
            SpreadsheetValueType.ROW
        );
    }

    @Test
    public void testFromClassTypeWithRowRange() {
        this.fromClassAndCheck(
            SpreadsheetRowRangeReference.class,
            SpreadsheetValueType.ROW_RANGE
        );
    }

    @Test
    public void testFromClassTypeWithSpreadsheetError() {
        this.fromClassAndCheck(
            SpreadsheetError.class,
            SpreadsheetValueType.SPREADSHEET_ERROR
        );
    }

    @Test
    public void testFromClassTypeWithString() {
        this.fromClassAndCheck(
            String.class,
            SpreadsheetValueType.TEXT
        );
    }

    @Test
    public void testFromClassTypeWithStringList() {
        this.fromClassAndCheck(
            StringList.class,
            SpreadsheetValueType.STRING_LIST
        );
    }

    @Test
    public void testFromClassTypeWithLocalTime() {
        this.fromClassAndCheck(
            LocalTime.class,
            SpreadsheetValueType.TIME
        );
    }

    @Test
    public void testFromClassTypeWithTimeList() {
        this.fromClassAndCheck(
            LocalTimeList.class,
            SpreadsheetValueType.TIME_LIST
        );
    }

    @Test
    public void testFromClassTypeWithValidationChoiceList() {
        this.fromClassAndCheck(
            ValidationChoiceList.class,
            SpreadsheetValueType.CHOICE_LIST
        );
    }

    @Test
    public void testFromClassTypeWithValidationErrorList() {
        this.fromClassAndCheck(
            ValidationErrorList.class,
            SpreadsheetValueType.VALIDATION_ERROR_LIST
        );
    }

    @Test
    public void testFromClassTypeWithUnknownType() {
        this.fromClassAndCheck(
            this.getClass(),
            Optional.empty()
        );
    }

    private void fromClassAndCheck(final Class<?> type,
                                   final String expected) {
        this.fromClassAndCheck(
            type,
            ValueType.fromClassName(expected)
        );
    }

    private void fromClassAndCheck(final Object type,
                                   final ValueType expected) {
        this.fromClassAndCheck(
            type.getClass(),
            Optional.of(expected)
        );
    }

    private void fromClassAndCheck(final Class<?> type,
                                   final ValueType expected) {
        this.fromClassAndCheck(
            type,
            Optional.of(expected)
        );
    }

    private void fromClassAndCheck(final Class<?> type,
                                   final Optional<ValueType> expected) {
        this.checkEquals(
            expected,
            ValueType.fromClass(type),
            type::getName
        );
    }

    // type.............................................................................................................

    @Test
    public void testTypeWithAbsoluteUrl() {
        this.typeAndCheck(
            SpreadsheetValueType.ABSOLUTE_URL,
            AbsoluteUrl.class
        );
    }

    @Test
    public void testTypeWithAlphaHsl() {
        this.typeAndCheck(
            SpreadsheetValueType.ALPHA_HSL_COLOR,
            Color.BLACK.toHsl()
                .set(HslColorComponent.alpha(0.5f))
                .getClass()
        );
    }

    @Test
    public void testTypeWithAlphaHsv() {
        this.typeAndCheck(
            SpreadsheetValueType.ALPHA_HSV_COLOR,
            Color.BLACK.toHsv()
                .set(HsvColorComponent.alpha(0.5f))
                .getClass()
        );
    }

    @Test
    public void testTypeWithAlphaRgb() {
        this.typeAndCheck(
            SpreadsheetValueType.ALPHA_RGB_COLOR,
            Color.BLACK.set(
                RgbColorComponent.alpha((byte) 127)
            )
        );
    }

    @Test
    public void testTypeWithBoolean() {
        this.typeAndCheck(
            SpreadsheetValueType.BOOLEAN,
            Boolean.class
        );
    }

    @Test
    public void testTypeWithBooleanList() {
        this.typeAndCheck(
            SpreadsheetValueType.BOOLEAN_LIST,
            BooleanList.class
        );
    }

    @Test
    public void testTypeWithCell() {
        this.typeAndCheck(
            SpreadsheetValueType.CELL,
            SpreadsheetCellReference.class
        );
    }

    @Test
    public void testTypeWithCellRange() {
        this.typeAndCheck(
            SpreadsheetValueType.CELL_RANGE,
            SpreadsheetCellRangeReference.class
        );
    }

    @Test
    public void testTypeWithChoiceList() {
        this.typeAndCheck(
            SpreadsheetValueType.CHOICE_LIST,
            ValidationChoiceList.class
        );
    }

    @Test
    public void testTypeWithColumn() {
        this.typeAndCheck(
            SpreadsheetValueType.COLUMN,
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testTypeWithColumnRange() {
        this.typeAndCheck(
            SpreadsheetValueType.COLUMN_RANGE,
            SpreadsheetColumnRangeReference.class
        );
    }

    @Test
    public void testTypeWithCsvList() {
        this.typeAndCheck(
            SpreadsheetValueType.CSV_LIST,
            CsvStringList.class
        );
    }

    @Test
    public void testTypeWithDate() {
        this.typeAndCheck(
            SpreadsheetValueType.DATE,
            LocalDate.class
        );
    }

    @Test
    public void testTypeWithDateList() {
        this.typeAndCheck(
            SpreadsheetValueType.DATE_LIST,
            LocalDateList.class
        );
    }

    @Test
    public void testTypeWithDateTime() {
        this.typeAndCheck(
            SpreadsheetValueType.DATE_TIME,
            LocalDateTime.class
        );
    }

    @Test
    public void testTypeWithDateTimeList() {
        this.typeAndCheck(
            SpreadsheetValueType.DATE_TIME_LIST,
            LocalDateTimeList.class
        );
    }

    @Test
    public void testTypeWithDateTimeSymbols() {
        this.typeAndCheck(
            SpreadsheetValueType.DATE_TIME_SYMBOLS,
            DateTimeSymbols.class
        );
    }

    @Test
    public void testTypeWithDecimalNumberSymbols() {
        this.typeAndCheck(
            SpreadsheetValueType.DECIMAL_NUMBER_SYMBOLS,
            DecimalNumberSymbols.class
        );
    }

    @Test
    public void testTypeWithEmail() {
        this.typeAndCheck(
            SpreadsheetValueType.EMAIL,
            EmailAddress.class
        );
    }

    @Test
    public void testTypeWithEnvironment() {
        this.typeAndCheck(
            SpreadsheetValueType.ENVIRONMENT,
            Environment.class
        );
    }

    @Test
    public void testTypeWithError() {
        this.typeAndCheck(
            ValueType.ERROR,
            ValidationError.class
        );
    }

    @Test
    public void testTypeWithJsonArray() {
        this.typeAndCheck(
            SpreadsheetValueType.JSON_ARRAY,
            JsonArray.class
        );
    }

    @Test
    public void testTypeWithJsonBoolean() {
        this.typeAndCheck(
            SpreadsheetValueType.JSON_BOOLEAN,
            JsonBoolean.class
        );
    }

    @Test
    public void testTypeWithJson() {
        this.typeAndCheck(
            SpreadsheetValueType.JSON,
            JsonNode.class
        );
    }

    @Test
    public void testTypeWithJsonNull() {
        this.typeAndCheck(
            SpreadsheetValueType.JSON_NULL,
            JsonNull.class
        );
    }

    @Test
    public void testTypeWithJsonNumber() {
        this.typeAndCheck(
            SpreadsheetValueType.JSON_NUMBER,
            JsonNumber.class
        );
    }

    @Test
    public void testTypeWithJsonObject() {
        this.typeAndCheck(
            SpreadsheetValueType.JSON_OBJECT,
            JsonObject.class
        );
    }

    @Test
    public void testTypeWithLabel() {
        this.typeAndCheck(
            SpreadsheetValueType.LABEL,
            SpreadsheetLabelName.class
        );
    }

    @Test
    public void testTypeWithLocalDate() {
        this.typeAndCheck(
            SpreadsheetValueType.LOCAL_DATE,
            LocalDate.class
        );
    }

    @Test
    public void testTypeWithLocalDateTime() {
        this.typeAndCheck(
            SpreadsheetValueType.LOCAL_DATE_TIME,
            LocalDateTime.class
        );
    }

    @Test
    public void testTypeWithLocalTime() {
        this.typeAndCheck(
            SpreadsheetValueType.LOCAL_TIME,
            LocalTime.class
        );
    }

    @Test
    public void testTypeWithNumber() {
        this.typeAndCheck(
            SpreadsheetValueType.NUMBER,
            ExpressionNumber.class
        );
    }

    @Test
    public void testTypeWithNumberList() {
        this.typeAndCheck(
            SpreadsheetValueType.NUMBER_LIST,
            NumberList.class
        );
    }

    @Test
    public void testTypeWithOpaqueHsl() {
        this.typeAndCheck(
            SpreadsheetValueType.OPAQUE_HSL_COLOR,
            Color.BLACK.toHsl()
        );
    }

    @Test
    public void testTypeWithOpaqueHsv() {
        this.typeAndCheck(
            SpreadsheetValueType.OPAQUE_HSV_COLOR,
            Color.BLACK.toHsv()
        );
    }

    @Test
    public void testTypeWithOpaqueRgb() {
        this.typeAndCheck(
            SpreadsheetValueType.OPAQUE_RGB_COLOR,
            Color.BLACK
        );
    }

    @Test
    public void testTypeWithRow() {
        this.typeAndCheck(
            SpreadsheetValueType.ROW,
            SpreadsheetRowReference.class
        );
    }

    @Test
    public void testTypeWithRowRange() {
        this.typeAndCheck(
            SpreadsheetValueType.ROW_RANGE,
            SpreadsheetRowRangeReference.class
        );
    }

    @Test
    public void testTypeWithSpreadsheetError() {
        this.typeAndCheck(
            SpreadsheetValueType.SPREADSHEET_ERROR,
            SpreadsheetError.class
        );
    }

    @Test
    public void testTypeWithString() {
        this.typeAndCheck(
            SpreadsheetValueType.TEXT,
            String.class
        );
    }

    @Test
    public void testTypeWithStringLists() {
        this.typeAndCheck(
            SpreadsheetValueType.STRING_LIST,
            StringList.class
        );
    }

    @Test
    public void testTypeWithTime() {
        this.typeAndCheck(
            SpreadsheetValueType.TIME,
            LocalTime.class
        );
    }

    @Test
    public void testTypeWithTimeList() {
        this.typeAndCheck(
            SpreadsheetValueType.TIME_LIST,
            LocalTimeList.class
        );
    }

    @Test
    public void testTypeWithUrl() {
        this.typeAndCheck(
            SpreadsheetValueType.URL,
            Url.class
        );
    }

    @Test
    public void testTypeWithValidationError() {
        this.typeAndCheck(
            SpreadsheetValueType.VALIDATION_ERROR,
            ValidationError.class
        );
    }

    @Test
    public void testTypeWithValidationErrorList() {
        this.typeAndCheck(
            SpreadsheetValueType.VALIDATION_ERROR_LIST,
            ValidationErrorList.class
        );
    }

    @Test
    public void testTypeWithWholeNumber() {
        this.typeAndCheck(
            SpreadsheetValueType.WHOLE_NUMBER,
            Number.class
        );
    }

    private void typeAndCheck(final ValueType valueType) {
        this.typeAndCheck(
            valueType,
            Optional.empty()
        );
    }

    private void typeAndCheck(final ValueType valueType,
                              final Object expected) {
        this.typeAndCheck(
            valueType,
            expected.getClass()
        );
    }

    private void typeAndCheck(final String valueType,
                              final Class<?> expected) {
        this.typeAndCheck(
            ValueType.fromClassNameOrFail(valueType),
            expected
        );
    }

    private void typeAndCheck(final ValueType valueType,
                              final Class<?> expected) {
        this.checkEquals(
            expected,
            valueType.type(),
            valueType::toString
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

            if(ValueType.ANY.equals(valueType)) {
                continue;
            }

            final Class<?> type = valueType.type();
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
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
