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

/**
 * A list of possible(supported) spreadsheet value types.
 */
public final class SpreadsheetValueType implements PublicStaticHelper {

    public final static String ANY = "*";

    public final static String BOOLEAN = "boolean";

    public final static String CELL = "cell";

    public final static String CELL_RANGE = "cell-range";

    public final static String COLUMN = "column";

    public final static String COLUMN_RANGE = "column-range";

    public final static String DATE = "date";

    public final static String DATE_TIME = "date-time";

    public final static String ERROR = "error";

    public final static String LABEL = "label";

    public final static String NUMBER = "number";

    public final static String ROW = "row";

    public final static String ROW_RANGE = "row-range";

    public final static String TEXT = "text";

    public final static String TIME = "time";

    /**
     * For the given type returns the value type name.
     */
    public static String typeName(final Class<?> type) {
        return SpreadsheetValueTypeSpreadsheetValueTypeVisitor.typeName(type);
    }

    /**
     * Private ctor
     */
    private SpreadsheetValueType() {
        throw new UnsupportedOperationException();
    }
}
