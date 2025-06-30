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

/**
 * A list of possible(supported) spreadsheet value types.
 * A few helpers are provided to help translate {@link ValidationValueTypeName} to and from its equivalent java {@link Class}.
 */
public final class SpreadsheetValueType implements PublicStaticHelper {

    public final static String ANY = "*";

    public final static String BOOLEAN = ValidationValueTypeName.BOOLEAN_STRING;

    public final static String CELL = "cell";

    public final static String CELL_RANGE = "cellRange";

    public final static String COLUMN = "column";

    public final static String COLUMN_RANGE = "columnRange";

    public final static String DATE = ValidationValueTypeName.DATE_STRING;

    public final static String DATE_TIME = ValidationValueTypeName.DATE_TIME_STRING;

    public final static String ERROR = "error";

    public final static String LABEL = "label";

    public final static String NUMBER =ValidationValueTypeName.NUMBER_STRING;

    public final static String ROW = "row";

    public final static String ROW_RANGE = "rowRange";

    public final static String TEXT = ValidationValueTypeName.TEXT_STRING;

    public final static String TIME = ValidationValueTypeName.TIME_STRING;

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
            case BOOLEAN:
                javaType = Boolean.class;
                break;
            case CELL:
                javaType = SpreadsheetCellReference.class;
                break;
            case CELL_RANGE:
                javaType = SpreadsheetCellRangeReference.class;
                break;
            case COLUMN:
                javaType = SpreadsheetColumnReference.class;
                break;
            case COLUMN_RANGE:
                javaType = SpreadsheetColumnRangeReference.class;
                break;
            case DATE:
                javaType = LocalDate.class;
                break;
            case DATE_TIME:
                javaType = LocalDateTime.class;
                break;
            case ERROR:
                javaType = SpreadsheetError.class;
                break;
            case LABEL:
                javaType = SpreadsheetLabelName.class;
                break;
            case NUMBER:
                javaType = ExpressionNumber.class;
                break;
            case ROW:
                javaType = SpreadsheetRowReference.class;
                break;
            case ROW_RANGE:
                javaType = SpreadsheetRowRangeReference.class;
                break;
            case TEXT:
                javaType = String.class;
                break;
            case TIME:
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
