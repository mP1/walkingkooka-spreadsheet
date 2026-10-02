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

import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.color.HslColor;
import walkingkooka.color.HsvColor;
import walkingkooka.color.RgbColor;
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
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.ValueType;

import java.util.Set;

/**
 * A list of possible(supported) spreadsheet value types.
 * A few helpers are provided to help translate {@link ValueType} to and from its equivalent java {@link Class}.
 * Aliases or apparent duplicates exist such as {@link #DATE} which should support marshalling/unmarshalling values
 * between {@link ValueType} and java object instances.
 */
public final class SpreadsheetValueType implements PublicStaticHelper {

    public static final ValueType ANY = ValueType.ANY;

    // color............................................................................................................

    public final static ValueType COLOR = ValueType.register(
        "color",
        Color.class
    );

    public final static ValueType ALPHA_HSV_COLOR = ValueType.register(
        "color/HsvAlpha",
        Color.BLACK_HSV_50_ALPHA.getClass()
    );

    public final static ValueType ALPHA_HSL_COLOR = ValueType.register(
        "color/HslAlpha",
        Color.BLACK_HSL_50_ALPHA.getClass()
    );

    public final static ValueType ALPHA_RGB_COLOR = ValueType.register(
        "color/RgbColorAlpha",
        Color.BLACK_50_ALPHA.getClass()
    );

    public final static ValueType HSL_COLOR = ValueType.register(
        "color/Hsl",
        HslColor.class
    );

    public final static ValueType HSV_COLOR = ValueType.register(
        "color/Hsv",
        HsvColor.class
    );

    public final static ValueType OPAQUE_HSL_COLOR = ValueType.register(
        "color/HslOpaque",
        Color.BLACK_HSL.getClass()
    );

    public final static ValueType OPAQUE_HSV_COLOR = ValueType.register(
        "color/HsvOpaque",
        Color.BLACK_HSV.getClass()
    );

    public final static ValueType OPAQUE_RGB_COLOR = ValueType.register(
        "color/RgbOpaque",
        Color.BLACK.getClass()
    );

    public final static ValueType RGB_COLOR = ValueType.register(
        "color/Rgb",
        RgbColor.class
    );

    // references.......................................................................................................

    public final static ValueType REFERENCE = ValueType.register(
        "reference",
        SpreadsheetSelection.class
    );

    public static final ValueType CELL = ValueType.register(
        "reference/CellReference",
        SpreadsheetCellReference.class
    );

    public static final ValueType CELL_RANGE = ValueType.register(
        "reference/CellRangeReference",
        SpreadsheetCellRangeReference.class
    );

    public static final ValueType COLUMN = ValueType.register(
        "reference/ColumnReference",
        SpreadsheetColumnReference.class
    );

    public static final ValueType COLUMN_RANGE = ValueType.register(
        "reference/ColumnRangeReference",
        SpreadsheetColumnRangeReference.class
    );

    public static final ValueType LABEL = ValueType.register(
        "reference/Label",
        SpreadsheetLabelName.class
    );

    public static final ValueType ROW = ValueType.register(
        "reference/RowReference",
        SpreadsheetRowReference.class
    );

    public static final ValueType ROW_RANGE = ValueType.register(
        "reference/RowRangeReference",
        SpreadsheetRowRangeReference.class
    );

    // ValueType........................................................................................................

    public static final ValueType ABSOLUTE_URL = ValueType.ABSOLUTE_URL;

    public static final ValueType BOOLEAN = ValueType.BOOLEAN;

    public static final ValueType BOOLEAN_LIST = ValueType.BOOLEAN_LIST;

    public static final ValueType CHOICE_LIST = ValueType.CHOICE_LIST;

    public static final ValueType CONDITION = ValueType.register(
        "Condition",
        ConditionRightEqualsSpreadsheetFormulaParserToken.class
    );

    public static final ValueType CSV = ValueType.CSV;

    public static final ValueType CURRENCY = ValueType.CURRENCY;

    public static final ValueType CURRENCY_CODE = ValueType.CURRENCY_CODE;

    public static final ValueType CURRENCY_VALUE = ValueType.CURRENCY_VALUE;

    public final static ValueType DATA_URL = ValueType.DATA_URL;

    public static final ValueType DATE = ValueType.DATE;

    public static final ValueType DATE_LIST = ValueType.DATE_LIST;

    public static final ValueType DATE_TIME = ValueType.DATE_TIME;

    public static final ValueType DATE_TIME_LIST = ValueType.DATE_TIME_LIST;

    public final static ValueType DATE_TIME_SYMBOLS = ValueType.DATE_TIME_SYMBOLS;

    public final static ValueType DECIMAL_NUMBER_SYMBOLS = ValueType.DECIMAL_NUMBER_SYMBOLS;

    public static final ValueType EMAIL = ValueType.EMAIL;

    public static final ValueType ENVIRONMENT = ValueType.ENVIRONMENT;

    public static final ValueType ERROR = ValueType.fromClassOrFail(SpreadsheetError.class);

    public final static ValueType JSON = ValueType.JSON_PARENT;

    public final static ValueType JSON_ARRAY = ValueType.JSON_ARRAY;

    public final static ValueType JSON_BOOLEAN = ValueType.JSON_BOOLEAN;

    public final static ValueType JSON_NULL = ValueType.JSON_NULL;

    public final static ValueType JSON_NUMBER = ValueType.JSON_NUMBER;

    public final static ValueType JSON_OBJECT = ValueType.JSON_OBJECT;

    public final static ValueType JSON_STRING = ValueType.JSON_STRING;

    public final static ValueType LIST = ValueType.LIST_PARENT;

    public final static ValueType LOCALE_PARENT = ValueType.LOCALE_PARENT;

    public final static ValueType LOCALE = ValueType.LOCALE;

    public final static ValueType LOCALE_LANGUAGE_TAG = ValueType.LOCALE_LANGUAGE_TAG;

    public final static ValueType LOCALE_LANGUAGE_TAG_SET = ValueType.LOCALE_LANGUAGE_TAG_SET;

    public final static ValueType MAIL_TO_URL = ValueType.MAIL_TO_URL;

    public static final ValueType NUMBER_PARENT = ValueType.NUMBER_PARENT;

    public static final ValueType NUMBER = ValueType.NUMBER;

    public static final ValueType NUMBER_LIST = ValueType.NUMBER_LIST;

    public final static ValueType RELATIVE_URL = ValueType.RELATIVE_URL;

    public static final ValueType SPREADSHEET_ERROR = ValueType.register(
        "error/SpreadsheetError",
        SpreadsheetError.class
    );

    public static final ValueType TEMPLATE_VALUE_NAME = ValueType.register(
        "template/TemplateValueName",
        TemplateValueName.class
    );

    public static final ValueType TEXT_PARENT = ValueType.TEXT_PARENT;

    public static final ValueType TEXT = ValueType.TEXT;

    public static final ValueType STRING_LIST = ValueType.STRING_LIST;

    public static final ValueType TIME = ValueType.TIME;

    public static final ValueType TIME_LIST = ValueType.TIME_LIST;

    public static final ValueType URL = ValueType.URL_PARENT;

    public static final ValueType VALIDATION_ERROR = ValueType.fromClassOrFail(ValidationError.class);

    public static final ValueType VALIDATION_ERROR_LIST = ValueType.fromClassOrFail(ValidationErrorList.class);

    public static final ValueType WHOLE_NUMBER = ValueType.WHOLE_NUMBER_PARENT;

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
