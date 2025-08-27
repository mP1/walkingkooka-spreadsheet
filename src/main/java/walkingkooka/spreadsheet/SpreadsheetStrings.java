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
import walkingkooka.text.CaseSensitivity;

/**
 * A collection of constants related to Spreadsheet string values.
 */
public final class SpreadsheetStrings implements PublicStaticHelper {

    /**
     * The preferred string representation of {@link Boolean#TRUE}.
     */
    public static final String BOOLEAN_TRUE = "TRUE";

    /**
     * The preferred string representation of {@link Boolean#FALSE}.
     */
    public static final String BOOLEAN_FALSE = "FALSE";

    /**
     * Function names are {@link CaseSensitivity#INSENSITIVE}
     */
    public static final CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.INSENSITIVE;

    /**
     * THe spreadsheet {@link walkingkooka.tree.expression.ExpressionEvaluationContext#isText(Object)}.
     */
    public static boolean isText(final Object value) {
        return value instanceof Character || value instanceof CharSequence;
    }

    /**
     * Stop creation
     */
    private SpreadsheetStrings() {
        throw new UnsupportedOperationException();
    }
}
