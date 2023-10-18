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

package walkingkooka.spreadsheet.reference;

import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

/**
 * Base class for all Spreadsheet {@link ExpressionReference}
 */
abstract public class SpreadsheetExpressionReference extends SpreadsheetSelection implements ExpressionReference {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetExpressionReference() {
        super();
    }

    @Override
    public final boolean testParameterName(final ExpressionFunctionParameterName parameterName) {
        return this.isLabelName() &&
                SpreadsheetLabelName.CASE_SENSITIVITY.equals(
                        this.toString(),
                        parameterName.value()
                );
    }

    /**
     * Returns this {@link SpreadsheetExpressionReference} in relative form. This is a no-op for {@link SpreadsheetLabelName}.
     */
    abstract public SpreadsheetExpressionReference toRelative();
}
