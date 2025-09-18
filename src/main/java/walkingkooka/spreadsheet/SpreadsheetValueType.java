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
import walkingkooka.validation.ValidationValueTypeName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A list of possible(supported) spreadsheet value types.
 * A few helpers are provided to help translate {@link ValidationValueTypeName} to and from its equivalent java {@link Class}.
 */
public final class SpreadsheetValueType implements PublicStaticHelper {

    public static final ValidationValueTypeName ANY = ValidationValueTypeName.ANY;

    public static final String BOOLEAN_STRING = ValidationValueTypeName.BOOLEAN_STRING;

    public static final ValidationValueTypeName BOOLEAN = ValidationValueTypeName.BOOLEAN;

    public static final String CELL_STRING = "cell";

    public static final ValidationValueTypeName CELL = ValidationValueTypeName.with(CELL_STRING);

    public static final String CELL_RANGE_STRING = "cellRange";

    public static final ValidationValueTypeName CELL_RANGE = ValidationValueTypeName.with(CELL_RANGE_STRING);

    public static final String COLUMN_STRING = "column";

    public static final ValidationValueTypeName COLUMN = ValidationValueTypeName.with(COLUMN_STRING);

    public static final String COLUMN_RANGE_STRING = "columnRange";

    public static final ValidationValueTypeName COLUMN_RANGE = ValidationValueTypeName.with(COLUMN_RANGE_STRING);

    public static final String CONDITION_STRING = "condition";

    public static final ValidationValueTypeName CONDITION = ValidationValueTypeName.with(CONDITION_STRING);

    public static final String DATE_STRING = ValidationValueTypeName.DATE_STRING;

    public static final ValidationValueTypeName DATE = ValidationValueTypeName.DATE;

    public static final String DATE_TIME_STRING = ValidationValueTypeName.DATE_TIME_STRING;

    public static final ValidationValueTypeName DATE_TIME = ValidationValueTypeName.DATE_TIME;

    public static final String ERROR_STRING = "error";

    public static final ValidationValueTypeName ERROR = ValidationValueTypeName.with(ERROR_STRING);

    public static final String LABEL_STRING = "label";

    public static final ValidationValueTypeName LABEL = ValidationValueTypeName.with(LABEL_STRING);

    public static final String NUMBER_STRING = ValidationValueTypeName.NUMBER_STRING;

    public static final ValidationValueTypeName NUMBER = ValidationValueTypeName.NUMBER;

    public static final String ROW_STRING = "row";

    public static final ValidationValueTypeName ROW = ValidationValueTypeName.with(ROW_STRING);

    public static final String ROW_RANGE_STRING = "rowRange";

    public static final ValidationValueTypeName ROW_RANGE = ValidationValueTypeName.with(ROW_RANGE_STRING);

    public static final String TEMPLATE_VALUE_NAME_STRING = "templateValueName";

    public static final ValidationValueTypeName TEMPLATE_VALUE_NAME = ValidationValueTypeName.with(TEMPLATE_VALUE_NAME_STRING);

    public static final String TEXT_STRING = ValidationValueTypeName.TEXT_STRING;

    public static final ValidationValueTypeName TEXT = ValidationValueTypeName.TEXT;

    public static final String TIME_STRING = ValidationValueTypeName.TIME_STRING;

    public static final ValidationValueTypeName TIME = ValidationValueTypeName.TIME;

    public static final String VALUE_OR_EXPRESSION_STRING = "valueOrExpression";

    public static final ValidationValueTypeName VALUE_OR_EXPRESSION = ValidationValueTypeName.with(VALUE_OR_EXPRESSION_STRING);

    /**
     * Does not include all types, only those that typically appear in a cell
     */
    public static final Set<ValidationValueTypeName> ALL = Sets.of(
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
    public final static Set<ValidationValueTypeName> ALL_CELL_TYPES = Sets.of(
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
    public static Optional<ValidationValueTypeName> toValueType(final Class<?> type) {
        return SpreadsheetValueTypeToValueTypeSpreadsheetValueTypeVisitor.valueType(type);
    }

    /**
     * Translates a {@link ValidationValueTypeName} into its java {@link Class} equivalent.
     * If the type is unknown an {@link Optional#empty()} is returned.
     */
    public static Optional<Class<?>> toClass(final ValidationValueTypeName valueType) {
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
            case NUMBER_STRING:
                javaType = ExpressionNumber.class;
                break;
            case ROW_STRING:
                javaType = SpreadsheetRowReference.class;
                break;
            case ROW_RANGE_STRING:
                javaType = SpreadsheetRowRangeReference.class;
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
