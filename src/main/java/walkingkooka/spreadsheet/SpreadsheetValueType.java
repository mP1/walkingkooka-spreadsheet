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

import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.validation.ValueTypeName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A list of possible(supported) spreadsheet value types.
 * A few helpers are provided to help translate {@link ValueTypeName} to and from its equivalent java {@link Class}.
 */
public final class SpreadsheetValueType implements PublicStaticHelper {

    public static final ValueTypeName ANY = ValueTypeName.ANY;

    public static final String BOOLEAN_STRING = ValueTypeName.BOOLEAN_STRING;

    public static final ValueTypeName BOOLEAN = ValueTypeName.BOOLEAN;

    public static final String CELL_STRING = "cell";

    public static final ValueTypeName CELL = ValueTypeName.with(CELL_STRING);

    public static final String CELL_RANGE_STRING = "cell-range";

    public static final ValueTypeName CELL_RANGE = ValueTypeName.with(CELL_RANGE_STRING);

    public static final String COLUMN_STRING = "column";

    public static final ValueTypeName COLUMN = ValueTypeName.with(COLUMN_STRING);

    public static final String COLUMN_RANGE_STRING = "column-range";

    public static final ValueTypeName COLUMN_RANGE = ValueTypeName.with(COLUMN_RANGE_STRING);

    public static final String CONDITION_STRING = "condition";

    public static final ValueTypeName CONDITION = ValueTypeName.with(CONDITION_STRING);

    public static final String DATE_STRING = ValueTypeName.DATE_STRING;

    public static final ValueTypeName DATE = ValueTypeName.DATE;

    public static final String DATE_TIME_STRING = ValueTypeName.DATE_TIME_STRING;

    public static final ValueTypeName DATE_TIME = ValueTypeName.DATE_TIME;

    public static final String ERROR_STRING = "error";

    public static final ValueTypeName ERROR = ValueTypeName.with(ERROR_STRING);

    public static final String LABEL_STRING = "label";

    public static final ValueTypeName LABEL = ValueTypeName.with(LABEL_STRING);

    public static final String LOCAL_DATE_STRING = "local-date";

    public static final ValueTypeName LOCAL_DATE = ValueTypeName.with(LOCAL_DATE_STRING);

    public static final String LOCAL_DATE_TIME_STRING = "local-date-time";

    public static final ValueTypeName LOCAL_DATE_TIME = ValueTypeName.with(LOCAL_DATE_TIME_STRING);

    public static final String NUMBER_STRING = ValueTypeName.NUMBER_STRING;

    public static final ValueTypeName NUMBER = ValueTypeName.NUMBER;

    public static final String ROW_STRING = "row";

    public static final ValueTypeName ROW = ValueTypeName.with(ROW_STRING);

    public static final String ROW_RANGE_STRING = "row-range";

    public static final ValueTypeName ROW_RANGE = ValueTypeName.with(ROW_RANGE_STRING);

    public static final String TEMPLATE_VALUE_NAME_STRING = "template-value-name";

    public static final ValueTypeName TEMPLATE_VALUE_NAME = ValueTypeName.with(TEMPLATE_VALUE_NAME_STRING);

    public static final String STRING_STRING = "string";

    public static final ValueTypeName STRING = ValueTypeName.with(STRING_STRING);
    
    public static final String TEXT_STRING = ValueTypeName.TEXT_STRING;

    public static final ValueTypeName TEXT = ValueTypeName.TEXT;

    public static final String TIME_STRING = ValueTypeName.TIME_STRING;

    public static final ValueTypeName TIME = ValueTypeName.TIME;

    public static final String VALUE_OR_EXPRESSION_STRING = "value-or-expression";

    public static final ValueTypeName VALUE_OR_EXPRESSION = ValueTypeName.with(VALUE_OR_EXPRESSION_STRING);

    /**
     * Does not include all types, only those that typically appear in a cell
     */
    public static final Set<ValueTypeName> ALL = Sets.of(
        BOOLEAN,
        DATE,
        DATE_TIME,
        NUMBER,
        TEXT,
        TIME
    );

    /**
     * Used to build a UI search elements.
     */
    public final static Set<ValueTypeName> ALL_CELL_TYPES = Sets.of(
        BOOLEAN,
        DATE,
        DATE_TIME,
        ERROR,
        NUMBER,
        TEXT,
        TIME
    );

    /**
     * For the given type returns the value type name, or {@link Optional#empty()} if the type is unknown.
     */
    public static Optional<ValueTypeName> toValueType(final Class<?> type) {
        return SpreadsheetValueTypeToValueTypeSpreadsheetValueTypeVisitor.valueType(type);
    }

    /**
     * Translates a {@link ValueTypeName} into its java {@link Class} equivalent.
     * If the type is unknown an {@link Optional#empty()} is returned.
     */
    public static Optional<Class<?>> toClass(final ValueTypeName valueType) {
        Objects.requireNonNull(valueType, "valueType");

        final Class<?> javaType;

        switch (valueType.text()) {
            case BOOLEAN_STRING:
                javaType = Boolean.class;
                break;
            case CELL_STRING:
                javaType = SpreadsheetCellReference.class;
                break;
            case CELL_RANGE_STRING:
                javaType = SpreadsheetCellRangeReference.class;
                break;
            case COLUMN_STRING:
                javaType = SpreadsheetColumnReference.class;
                break;
            case COLUMN_RANGE_STRING:
                javaType = SpreadsheetColumnRangeReference.class;
                break;
            case DATE_STRING:
                javaType = LocalDate.class;
                break;
            case DATE_TIME_STRING:
                javaType = LocalDateTime.class;
                break;
            case ERROR_STRING:
                javaType = SpreadsheetError.class;
                break;
            case LABEL_STRING:
                javaType = SpreadsheetLabelName.class;
                break;
            case LOCAL_DATE_STRING:
                javaType = LocalDate.class;
                break;
            case LOCAL_DATE_TIME_STRING:
                javaType = LocalDateTime.class;
                break;
            case NUMBER_STRING:
                javaType = ExpressionNumber.class;
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
            case TEXT_STRING:
                javaType = String.class;
                break;
            case TIME_STRING:
                javaType = LocalTime.class;
                break;
            default:
                javaType = null;
                break;
        }

        return Optional.ofNullable(javaType);
    }

    /**
     * Private ctor
     */
    private SpreadsheetValueType() {
        throw new UnsupportedOperationException();
    }
}
