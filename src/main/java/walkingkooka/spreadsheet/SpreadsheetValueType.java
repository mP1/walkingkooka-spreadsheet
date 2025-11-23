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

import walkingkooka.collect.list.BooleanList;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.StringList;
import walkingkooka.collect.set.Sets;
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
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.json.JsonArray;
import walkingkooka.tree.json.JsonBoolean;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNull;
import walkingkooka.tree.json.JsonNumber;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.ValueType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A list of possible(supported) spreadsheet value types.
 * A few helpers are provided to help translate {@link ValueType} to and from its equivalent java {@link Class}.
 * Aliases or apparent duplicates exist such as {@link #DATE} and {@link #LOCAL_DATE} which should support
 * marshalling/unmarshalling values between {@link ValueType} and java object instances.
 */
public final class SpreadsheetValueType implements PublicStaticHelper {

    public static final ValueType ANY = ValueType.ANY;

    public static final String ABSOLUTE_URL_STRING = ValueType.ABSOLUTE_URL_STRING;

    public static final ValueType ABSOLUTE_URL = ValueType.with(ABSOLUTE_URL_STRING);

    public final static String ALPHA_HSV_COLOR_STRING = "color(hsv-alpha)";

    public final static ValueType ALPHA_HSV_COLOR = ValueType.with(ALPHA_HSV_COLOR_STRING);

    public final static String ALPHA_HSL_COLOR_STRING = "color(hsl-alpha)";

    public final static ValueType ALPHA_HSL_COLOR = ValueType.with(ALPHA_HSL_COLOR_STRING);

    public final static String ALPHA_RGB_COLOR_STRING = "color(rgb-alpha)";

    public final static ValueType ALPHA_RGB_COLOR = ValueType.with(ALPHA_RGB_COLOR_STRING);

    public static final String BOOLEAN_STRING = ValueType.BOOLEAN_STRING;

    public static final ValueType BOOLEAN = ValueType.BOOLEAN;

    public static final String BOOLEAN_LIST_STRING = ValueType.BOOLEAN_LIST_STRING;

    public static final ValueType BOOLEAN_LIST = ValueType.BOOLEAN_LIST;

    public static final String CELL_STRING = "cell";

    public static final ValueType CELL = ValueType.with(CELL_STRING);

    public static final String CELL_RANGE_STRING = "cell-range";

    public static final ValueType CELL_RANGE = ValueType.with(CELL_RANGE_STRING);

    public static final String CHOICE_LIST_STRING = ValueType.CHOICE_LIST_STRING;

    public static final ValueType CHOICE_LIST = ValueType.with(CHOICE_LIST_STRING);
    
    public final static String COLOR_STRING = "color";

    public final static ValueType COLOR = ValueType.with(COLOR_STRING);

    public static final String COLUMN_STRING = "column";

    public static final ValueType COLUMN = ValueType.with(COLUMN_STRING);

    public static final String COLUMN_RANGE_STRING = "column-range";

    public static final ValueType COLUMN_RANGE = ValueType.with(COLUMN_RANGE_STRING);

    public static final String CONDITION_STRING = "condition";

    public static final ValueType CONDITION = ValueType.with(CONDITION_STRING);

    public static final String CSV_LIST_STRING = ValueType.CSV_LIST_STRING;

    public static final ValueType CSV_LIST = ValueType.with(CSV_LIST_STRING);

    public final static String DATA_URL_STRING = ValueType.DATA_URL_STRING;

    public final static ValueType DATA_URL = ValueType.DATA_URL;

    public static final String DATE_STRING = ValueType.DATE_STRING;

    public static final ValueType DATE = ValueType.DATE;

    public static final String DATE_LIST_STRING = ValueType.DATE_LIST_STRING;

    public static final ValueType DATE_LIST = ValueType.DATE_LIST;

    public static final String DATE_TIME_STRING = ValueType.DATE_TIME_STRING;

    public static final ValueType DATE_TIME = ValueType.DATE_TIME;

    public static final String DATE_TIME_LIST_STRING = ValueType.DATE_TIME_LIST_STRING;

    public static final ValueType DATE_TIME_LIST = ValueType.DATE_TIME_LIST;

    public final static String DATE_TIME_SYMBOLS_STRING = ValueType.DATE_TIME_SYMBOLS_STRING;

    public final static ValueType DATE_TIME_SYMBOLS = ValueType.DATE_TIME_SYMBOLS;

    public final static String DECIMAL_NUMBER_SYMBOLS_STRING = ValueType.DECIMAL_NUMBER_SYMBOLS_STRING;

    public final static ValueType DECIMAL_NUMBER_SYMBOLS = ValueType.DECIMAL_NUMBER_SYMBOLS;

    public static final String EMAIL_ADDRESS_STRING = "email-address";

    public static final ValueType EMAIL_ADDRESS = ValueType.with(EMAIL_ADDRESS_STRING);

    public static final String EMAIL_STRING = ValueType.EMAIL_STRING;

    public static final ValueType EMAIL = ValueType.EMAIL;
    
    public static final String ERROR_STRING = ValueType.ERROR_STRING;

    public static final ValueType ERROR = ValueType.with(ERROR_STRING);

    public static final String ERROR_LIST_STRING = ValueType.ERROR_LIST_STRING;

    public static final ValueType ERROR_LIST = ValueType.with(ERROR_LIST_STRING);

    public final static String HSL_COLOR_STRING = "color(hsl)";

    public final static ValueType HSL_COLOR = ValueType.with(HSL_COLOR_STRING);

    public final static String HSV_COLOR_STRING = "color(hsv)";

    public final static ValueType HSV_COLOR = ValueType.with(HSV_COLOR_STRING);

    public final static String JSON_NODE_STRING = ValueType.JSON_NODE_STRING;

    public final static ValueType JSON_NODE = ValueType.JSON_NODE;

    public final static String JSON_ARRAY_STRING = ValueType.JSON_ARRAY_STRING;

    public final static ValueType JSON_ARRAY = ValueType.JSON_ARRAY;

    public final static String JSON_BOOLEAN_STRING = ValueType.JSON_BOOLEAN_STRING;

    public final static ValueType JSON_BOOLEAN = ValueType.JSON_BOOLEAN;

    public final static String JSON_NULL_STRING = ValueType.JSON_NULL_STRING;

    public final static ValueType JSON_NULL = ValueType.JSON_NULL;

    public final static String JSON_NUMBER_STRING = ValueType.JSON_NUMBER_STRING;

    public final static ValueType JSON_NUMBER = ValueType.JSON_NUMBER;

    public final static String JSON_OBJECT_STRING = ValueType.JSON_OBJECT_STRING;

    public final static ValueType JSON_OBJECT = ValueType.JSON_OBJECT;

    public final static String JSON_STRING_STRING = ValueType.JSON_STRING_STRING;

    public final static ValueType JSON_STRING = ValueType.JSON_STRING;

    public final static String LIST_STRING = ValueType.LIST_STRING;

    public final static ValueType LIST = ValueType.LIST;
    
    public final static String LOCALE_STRING = ValueType.LOCALE_STRING;

    public final static ValueType LOCALE = ValueType.LOCALE;

    public static final String LABEL_STRING = "label";

    public static final ValueType LABEL = ValueType.with(LABEL_STRING);

    public static final String LOCAL_DATE_STRING = "local-date";

    public static final ValueType LOCAL_DATE = ValueType.with(LOCAL_DATE_STRING);

    public static final String LOCAL_DATE_TIME_STRING = "local-date-time";

    public static final ValueType LOCAL_DATE_TIME = ValueType.with(LOCAL_DATE_TIME_STRING);

    public static final String LOCAL_TIME_STRING = "local-time";

    public static final ValueType LOCAL_TIME = ValueType.with(LOCAL_TIME_STRING);

    public final static String MAIL_TO_URL_STRING = ValueType.MAIL_TO_URL_STRING;

    public final static ValueType MAIL_TO_URL = ValueType.MAIL_TO_URL;

    public static final String NUMBER_STRING = ValueType.NUMBER_STRING;

    public static final ValueType NUMBER = ValueType.NUMBER;

    public static final String NUMBER_LIST_STRING = ValueType.NUMBER_LIST_STRING;

    public static final ValueType NUMBER_LIST = ValueType.NUMBER_LIST;

    public final static String OPAQUE_HSL_COLOR_STRING = "color(hsl-opaque)";

    public final static ValueType OPAQUE_HSL_COLOR = ValueType.with(OPAQUE_HSL_COLOR_STRING);

    public final static String OPAQUE_HSV_COLOR_STRING = "color(hsv-opaque)";

    public final static ValueType OPAQUE_HSV_COLOR = ValueType.with(OPAQUE_HSV_COLOR_STRING);

    public final static String OPAQUE_RGB_COLOR_STRING = "color(rgb-opaque)";

    public final static ValueType OPAQUE_RGB_COLOR = ValueType.with(OPAQUE_RGB_COLOR_STRING);

    public final static String RGB_COLOR_STRING = "color(rgb)";

    public final static ValueType RGB_COLOR = ValueType.with(RGB_COLOR_STRING);

    public final static String RELATIVE_URL_STRING = ValueType.RELATIVE_URL_STRING;

    public final static ValueType RELATIVE_URL = ValueType.RELATIVE_URL;

    public static final String ROW_STRING = "row";

    public static final ValueType ROW = ValueType.with(ROW_STRING);

    public static final String ROW_RANGE_STRING = "row-range";

    public static final ValueType ROW_RANGE = ValueType.with(ROW_RANGE_STRING);

    public static final String TEMPLATE_VALUE_NAME_STRING = "template-value-name";

    public static final ValueType TEMPLATE_VALUE_NAME = ValueType.with(TEMPLATE_VALUE_NAME_STRING);

    public static final String STRING_STRING = "string";

    public static final ValueType STRING = ValueType.with(STRING_STRING);

    public static final String STRING_LIST_STRING = ValueType.STRING_LIST_STRING;

    public static final ValueType STRING_LIST = ValueType.STRING_LIST;

    public static final String TEXT_STRING = ValueType.TEXT_STRING;

    public static final ValueType TEXT = ValueType.TEXT;
    
    public static final String TIME_STRING = ValueType.TIME_STRING;

    public static final ValueType TIME = ValueType.TIME;

    public static final String TIME_LIST_STRING = ValueType.TIME_LIST_STRING;

    public static final ValueType TIME_LIST = ValueType.TIME_LIST;

    public static final String URL_STRING = ValueType.URL_STRING;

    public static final ValueType URL = ValueType.URL;

    public static final String VALUE_OR_EXPRESSION_STRING = "value-or-expression";

    public static final ValueType VALUE_OR_EXPRESSION = ValueType.with(VALUE_OR_EXPRESSION_STRING);

    public static final String WHOLE_NUMBER_STRING = ValueType.WHOLE_NUMBER_STRING;

    public static final ValueType WHOLE_NUMBER = ValueType.WHOLE_NUMBER;

    /**
     * Does not include all types, only those that typically appear in a cell
     */
    public static final Set<ValueType> ALL = Sets.of(
        BOOLEAN,
        DATE,
        DATE_TIME,
        EMAIL,
        NUMBER,
        TEXT,
        TIME,
        URL,
        WHOLE_NUMBER
    );

    /**
     * Used to build a UI search elements.
     */
    public final static Set<ValueType> ALL_CELL_VALUE_TYPES = Sets.of(
        BOOLEAN,
        DATE,
        DATE_TIME,
        EMAIL,
        ERROR,
        NUMBER,
        TEXT,
        TIME,
        URL,
        WHOLE_NUMBER
    );

    /**
     * For the given type returns the value type name, or {@link Optional#empty()} if the type is unknown.
     */
    public static Optional<ValueType> toValueType(final Class<?> type) {
        return SpreadsheetValueTypeToValueTypeSpreadsheetValueTypeVisitor.valueType(type);
    }

    /**
     * Translates a {@link ValueType} into its java {@link Class} equivalent.
     * If the type is unknown an {@link Optional#empty()} is returned.
     */
    public static Optional<Class<?>> toClass(final ValueType valueType) {
        Objects.requireNonNull(valueType, "valueType");

        final Class<?> javaType;

        switch (valueType.text()) {
            case ABSOLUTE_URL_STRING:
                javaType = AbsoluteUrl.class;
                break;
            case ALPHA_HSL_COLOR_STRING:
                javaType = ALPHA_HSL_COLOR_CLASS;
                break;
            case ALPHA_HSV_COLOR_STRING:
                javaType = ALPHA_HSV_COLOR_CLASS;
                break;
            case ALPHA_RGB_COLOR_STRING:
                javaType = ALPHA_RGB_COLOR_CLASS;
                break;
            case BOOLEAN_STRING:
                javaType = Boolean.class;
                break;
            case BOOLEAN_LIST_STRING:
                javaType = BooleanList.class;
                break;
            case CELL_STRING:
                javaType = SpreadsheetCellReference.class;
                break;
            case CELL_RANGE_STRING:
                javaType = SpreadsheetCellRangeReference.class;
                break;
            case CHOICE_LIST_STRING:
                javaType = ValidationChoiceList.class;
                break;
            case COLOR_STRING:
                javaType = Color.class;
                break;
            case COLUMN_STRING:
                javaType = SpreadsheetColumnReference.class;
                break;
            case COLUMN_RANGE_STRING:
                javaType = SpreadsheetColumnRangeReference.class;
                break;
            case CSV_LIST_STRING:
                javaType = CsvStringList.class;
                break;
            case DATE_STRING:
                javaType = LocalDate.class;
                break;
            case DATE_LIST_STRING:
                javaType = LocalDateList.class;
                break;
            case DATE_TIME_STRING:
                javaType = LocalDateTime.class;
                break;
            case DATE_TIME_LIST_STRING:
                javaType = LocalDateTimeList.class;
                break;
            case DATE_TIME_SYMBOLS_STRING:
                javaType = DateTimeSymbols.class;
                break;
            case DECIMAL_NUMBER_SYMBOLS_STRING:
                javaType = DecimalNumberSymbols.class;
                break;
            case EMAIL_ADDRESS_STRING:
            case EMAIL_STRING:
                javaType = EmailAddress.class;
                break;
            case ERROR_STRING:
                javaType = SpreadsheetError.class;
                break;
            case ERROR_LIST_STRING:
                javaType = ValidationErrorList.class;
                break;
            case HSL_COLOR_STRING:
                javaType = HslColor.class;
                break;
            case HSV_COLOR_STRING:
                javaType = HsvColor.class;
                break;
            case JSON_ARRAY_STRING:
                javaType = JsonArray.class;
                break;
            case JSON_BOOLEAN_STRING:
                javaType = JsonBoolean.class;
                break;
            case JSON_NODE_STRING:
                javaType = JsonNode.class;
                break;
            case JSON_NULL_STRING:
                javaType = JsonNull.class;
                break;
            case JSON_NUMBER_STRING:
                javaType = JsonNumber.class;
                break;
            case JSON_OBJECT_STRING:
                javaType = JsonObject.class;
                break;
            case LABEL_STRING:
                javaType = SpreadsheetLabelName.class;
                break;
            case LIST_STRING:
                javaType = List.class;
                break;
            case LOCAL_DATE_STRING:
                javaType = LocalDate.class;
                break;
            case LOCAL_DATE_TIME_STRING:
                javaType = LocalDateTime.class;
                break;
            case LOCAL_TIME_STRING:
                javaType = LocalTime.class;
                break;
            case LOCALE_STRING:
                javaType = Locale.class;
                break;
            case NUMBER_STRING:
                javaType = ExpressionNumber.class;
                break;
            case NUMBER_LIST_STRING:
                javaType = NumberList.class;
                break;
            case OPAQUE_HSL_COLOR_STRING:
                javaType = OPAQUE_HSL_COLOR_CLASS;
                break;
            case OPAQUE_HSV_COLOR_STRING:
                javaType = OPAQUE_HSV_COLOR_CLASS;
                break;
            case OPAQUE_RGB_COLOR_STRING:
                javaType = OPAQUE_RGB_COLOR_CLASS;
                break;
            case RGB_COLOR_STRING:
                javaType = RgbColor.class;
                break;
            case ROW_STRING:
                javaType = SpreadsheetRowReference.class;
                break;
            case ROW_RANGE_STRING:
                javaType = SpreadsheetRowRangeReference.class;
                break;
            case STRING_STRING:
                javaType = String.class;
                break;
            case STRING_LIST_STRING:
                javaType = StringList.class;
                break;
            case TEXT_STRING:
                javaType = String.class;
                break;
            case TIME_STRING:
                javaType = LocalTime.class;
                break;
            case TIME_LIST_STRING:
                javaType = LocalTimeList.class;
                break;
            case URL_STRING:
                javaType = AbsoluteUrl.class;
                break;
            case WHOLE_NUMBER_STRING:
                javaType = ExpressionNumber.class;
                break;
            default:
                javaType = null;
                break;
        }

        return Optional.ofNullable(javaType);
    }

    private final static Class<?> ALPHA_HSL_COLOR_CLASS = Color.BLACK.toHsl()
        .set(HslColorComponent.alpha(0.5f))
        .getClass();

    private final static Class<?> ALPHA_HSV_COLOR_CLASS = Color.BLACK.toHsv()
        .set(HsvColorComponent.alpha(0.5f))
        .getClass();

    private final static Class<?> ALPHA_RGB_COLOR_CLASS = Color.BLACK
        .set(RgbColorComponent.alpha((byte)127))
        .getClass();
    
    private final static Class<?> OPAQUE_HSL_COLOR_CLASS = Color.BLACK.toHsl()
        .getClass();

    private final static Class<?> OPAQUE_HSV_COLOR_CLASS = Color.BLACK.toHsv()
        .getClass();

    private final static Class<?> OPAQUE_RGB_COLOR_CLASS = Color.BLACK
        .getClass();

    public static ValueType with(final String name) {
        Objects.requireNonNull(name, "name");

        final ValueType valueType;

        switch (name) {
            case ABSOLUTE_URL_STRING:
                valueType = ABSOLUTE_URL;
                break;
            case ValueType.ANY_STRING:
                valueType = ANY;
                break;
            case ALPHA_HSL_COLOR_STRING:
                valueType = ALPHA_HSL_COLOR;
                break;
            case ALPHA_HSV_COLOR_STRING:
                valueType = ALPHA_HSV_COLOR;
                break;
            case ALPHA_RGB_COLOR_STRING:
                valueType = ALPHA_RGB_COLOR;
                break;
            case BOOLEAN_STRING:
                valueType = BOOLEAN;
                break;
            case CELL_STRING:
                valueType = CELL;
                break;
            case CELL_RANGE_STRING:
                valueType = CELL_RANGE;
                break;
            case COLOR_STRING:
                valueType = COLOR;
                break;
            case COLUMN_STRING:
                valueType = COLUMN;
                break;
            case COLUMN_RANGE_STRING:
                valueType = COLUMN_RANGE;
                break;
            case DATE_STRING:
                valueType = DATE;
                break;
            case DATE_TIME_STRING:
                valueType = DATE_TIME;
                break;
            case EMAIL_ADDRESS_STRING:
                valueType = EMAIL_ADDRESS;
                break;
            case EMAIL_STRING:
                valueType = EMAIL;
                break;
            case ERROR_STRING:
                valueType = ERROR;
                break;
            case HSL_COLOR_STRING:
                valueType = HSL_COLOR;
                break;
            case HSV_COLOR_STRING:
                valueType = HSV_COLOR;
                break;
            case LABEL_STRING:
                valueType = LABEL;
                break;
            case LOCAL_DATE_STRING:
                valueType = LOCAL_DATE;
                break;
            case LOCAL_DATE_TIME_STRING:
                valueType = LOCAL_DATE_TIME;
                break;
            case LOCAL_TIME_STRING:
                valueType = LOCAL_TIME;
                break;
            case NUMBER_STRING:
                valueType = NUMBER;
                break;
            case OPAQUE_HSL_COLOR_STRING:
                valueType = OPAQUE_HSL_COLOR;
                break;
            case OPAQUE_HSV_COLOR_STRING:
                valueType = OPAQUE_HSV_COLOR;
                break;
            case OPAQUE_RGB_COLOR_STRING:
                valueType = OPAQUE_RGB_COLOR;
                break;
            case RGB_COLOR_STRING:
                valueType = RGB_COLOR;
                break;
            case ROW_STRING:
                valueType = ROW;
                break;
            case ROW_RANGE_STRING:
                valueType = ROW_RANGE;
                break;
            case STRING_STRING:
                valueType = STRING;
                break;
            case TEXT_STRING:
                valueType = TEXT;
                break;
            case TIME_STRING:
                valueType = TIME;
                break;
            case URL_STRING:
                valueType = URL;
                break;
            case WHOLE_NUMBER_STRING:
                valueType = WHOLE_NUMBER;
                break;
            default:
                valueType = ValueType.with(name);
                break;
        }

        return valueType;
    }

    /**
     * Returns true if the {@link ValueType} is a color.
     */
    public static boolean isColor(final ValueType type) {
        Objects.requireNonNull(type, "type");

        return type.prefix().equals(COLOR_STRING);
    }

    /**
     * Private ctor
     */
    private SpreadsheetValueType() {
        throw new UnsupportedOperationException();
    }
}
