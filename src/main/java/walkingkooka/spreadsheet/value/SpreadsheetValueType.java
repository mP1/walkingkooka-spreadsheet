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

import walkingkooka.collect.list.BooleanList;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.StringList;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.color.HslColor;
import walkingkooka.color.HsvColor;
import walkingkooka.color.RgbColor;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyValue;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.LocalDateList;
import walkingkooka.datetime.LocalDateTimeList;
import walkingkooka.datetime.LocalTimeList;
import walkingkooka.environment.Environment;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.NumberList;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.DataUrl;
import walkingkooka.net.MailToUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.formula.parser.ConditionRightEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.json.JsonArray;
import walkingkooka.tree.json.JsonBoolean;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNull;
import walkingkooka.tree.json.JsonNumber;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonString;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.ValueType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Currency;
import java.util.List;
import java.util.Set;

/**
 * A list of possible(supported) spreadsheet value types.
 * A few helpers are provided to help translate {@link ValueType} to and from its equivalent java {@link Class}.
 * Aliases or apparent duplicates exist such as {@link #DATE} and {@link #LOCAL_DATE} which should support
 * marshalling/unmarshalling values between {@link ValueType} and java object instances.
 */
public final class SpreadsheetValueType implements PublicStaticHelper {

    public static final ValueType ANY = ValueType.ANY;

    public static final ValueType ABSOLUTE_URL = ValueType.fromClassOrFail(AbsoluteUrl.class);

    public final static ValueType ALPHA_HSV_COLOR = ValueType.register(
        "color(hsv-alpha)",
        Color.BLACK_HSV_50_ALPHA.getClass()
    );

    public final static ValueType ALPHA_HSL_COLOR = ValueType.register(
        "color/hsl-alpha",
        Color.BLACK_HSL_50_ALPHA.getClass()
    );

    public final static ValueType ALPHA_RGB_COLOR = ValueType.register(
        "color(rgb-alpha)",
        Color.BLACK_50_ALPHA.getClass()
    );

    public static final ValueType BOOLEAN = ValueType.BOOLEAN;

    public static final ValueType BOOLEAN_LIST = ValueType.fromClassOrFail(BooleanList.class);

    public static final ValueType CELL = ValueType.register(
        "reference/cell",
        SpreadsheetCellReference.class
    );

    public static final ValueType CELL_RANGE = ValueType.register(
        "reference/cell-range",
        SpreadsheetCellRangeReference.class
    );

    public static final ValueType CHOICE_LIST = ValueType.fromClassOrFail(ValidationChoiceList.class);


    public final static ValueType COLOR = ValueType.register(
        "color",
        Color.class
    );

    public static final ValueType COLUMN = ValueType.register(
        "reference/column",
        SpreadsheetColumnReference.class
    );

    public static final ValueType COLUMN_RANGE = ValueType.register(
        "reference/column-range",
        SpreadsheetColumnRangeReference.class
    );

    public static final ValueType CONDITION = ValueType.register(
        "condition",
        ConditionRightEqualsSpreadsheetFormulaParserToken.class
    );

    public static final ValueType CSV_LIST = ValueType.fromClassOrFail(CsvStringList.class);

    public static final ValueType CURRENCY = ValueType.fromClassOrFail(Currency.class);

    public static final ValueType CURRENCY_CODE = ValueType.fromClassOrFail(CurrencyCode.class);

    public static final ValueType CURRENCY_VALUE = ValueType.fromClassOrFail(CurrencyValue.class);

    public final static ValueType DATA_URL = ValueType.fromClassOrFail(DataUrl.class);

    public static final ValueType DATE = ValueType.DATE;

    public static final ValueType DATE_LIST = ValueType.fromClassOrFail(LocalDateList.class);

    public static final ValueType DATE_TIME = ValueType.DATE_TIME;

    public static final ValueType DATE_TIME_LIST = ValueType.fromClassOrFail(LocalDateTimeList.class);

    public final static ValueType DATE_TIME_SYMBOLS = ValueType.fromClassOrFail(DateTimeSymbols.class);

    public final static ValueType DECIMAL_NUMBER_SYMBOLS = ValueType.fromClassOrFail(DecimalNumberSymbols.class);

    public static final ValueType EMAIL = ValueType.fromClassOrFail(EmailAddress.class);

    public static final ValueType ENVIRONMENT = ValueType.fromClassOrFail(Environment.class);

    public static final ValueType ERROR = ValueType.fromClassOrFail(SpreadsheetError.class);

    public final static ValueType HSL_COLOR = ValueType.register(
        "color/hsl",
        HslColor.class
    );

    public final static ValueType HSV_COLOR = ValueType.register(
        "color/hsv",
        HsvColor.class
    );

    public final static ValueType JSON = ValueType.fromClassOrFail(JsonNode.class);

    public final static ValueType JSON_ARRAY = ValueType.fromClassOrFail(JsonArray.class);

    public final static ValueType JSON_BOOLEAN = ValueType.fromClassOrFail(JsonBoolean.class);

    public final static ValueType JSON_NULL = ValueType.fromClassOrFail(JsonNull.class);

    public final static ValueType JSON_NUMBER = ValueType.fromClassOrFail(JsonNumber.class);

    public final static ValueType JSON_OBJECT = ValueType.fromClassOrFail(JsonObject.class);

    public final static ValueType JSON_STRING = ValueType.fromClassOrFail(JsonString.class);

    public final static ValueType LIST = ValueType.fromClassOrFail(List.class);

    public final static ValueType LOCALE = ValueType.LOCALE;

    public static final ValueType LABEL = ValueType.register(
        "reference/label",
        SpreadsheetLabelName.class
    );

    public static final ValueType LOCAL_DATE = ValueType.fromClassOrFail(LocalDate.class);

    public static final ValueType LOCAL_DATE_TIME = ValueType.fromClassOrFail(LocalDateTime.class);

    public static final ValueType LOCAL_TIME = ValueType.fromClassOrFail(LocalTime.class);

    public final static ValueType MAIL_TO_URL = ValueType.fromClassOrFail(MailToUrl.class);

    public static final ValueType NUMBER = ValueType.NUMBER;

    public static final ValueType NUMBER_LIST = ValueType.fromClassOrFail(NumberList.class);

    public final static ValueType OPAQUE_HSL_COLOR = ValueType.register(
        "color/hsl-opaque",
        Color.BLACK_HSL.getClass()
    );

    public final static ValueType OPAQUE_HSV_COLOR = ValueType.register(
        "color/hsv-opaque",
        Color.BLACK_HSV.getClass()
    );

    public final static ValueType OPAQUE_RGB_COLOR = ValueType.register(
        "color/rgb-opaque",
        Color.BLACK.getClass()
    );

    public final static ValueType RGB_COLOR = ValueType.register(
        "color/rgb",
        RgbColor.class
    );

    public final static ValueType REFERENCE = ValueType.register(
        "reference",
        SpreadsheetSelection.class
    );

    public final static ValueType RELATIVE_URL = ValueType.fromClassOrFail(RelativeUrl.class);

    public static final ValueType ROW = ValueType.register(
        "reference/row",
        SpreadsheetRowReference.class
    );

    public static final ValueType ROW_RANGE = ValueType.register(
        "reference/row-range",
        SpreadsheetRowRangeReference.class
    );

    public static final ValueType SPREADSHEET_ERROR = ValueType.register(
        "error/spreadsheet",
        SpreadsheetError.class
    );

    public static final ValueType TEMPLATE_VALUE_NAME = ValueType.register(
        "template/value-name",
        TemplateValueName.class
    );

    public static final ValueType TEXT = ValueType.TEXT;

    public static final ValueType STRING_LIST = ValueType.fromClassOrFail(StringList.class);

    public static final ValueType TIME = ValueType.TIME;

    public static final ValueType TIME_LIST = ValueType.fromClassOrFail(LocalTimeList.class);

    public static final ValueType URL = ValueType.fromClassOrFail(Url.class);

    public static final ValueType VALIDATION_ERROR = ValueType.fromClassOrFail(ValidationError.class);

    public static final ValueType VALIDATION_ERROR_LIST = ValueType.fromClassOrFail(ValidationErrorList.class);

    public static final ValueType WHOLE_NUMBER = ValueType.WHOLE_NUMBER;

    /**
     * Does not include all types, only those that typically appear in a cell
     */
    public static final Set<ValueType> ALL = Sets.of(
        BOOLEAN,
        CURRENCY,
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
        CURRENCY,
        DATE,
        DATE_TIME,
        EMAIL,
        SPREADSHEET_ERROR,
        NUMBER,
        TEXT,
        TIME,
        URL,
        WHOLE_NUMBER
    );

    /**
     * Private ctor
     */
    private SpreadsheetValueType() {
        throw new UnsupportedOperationException();
    }
}
