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

package walkingkooka.spreadsheet.expression;

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;

/**
 * A collection of helpers for getting {@link ExpressionFunction}.
 */
public final class SpreadsheetExpressionFunctions implements PublicStaticHelper {

    /**
     * Function names are {@link CaseSensitivity#INSENSITIVE}
     */
    public static final CaseSensitivity NAME_CASE_SENSITIVITY = SpreadsheetStrings.CASE_SENSITIVITY;

    /**
     * Factory that creates a {@link ExpressionFunctionName} with {@link #NAME_CASE_SENSITIVITY}.
     */
    public static ExpressionFunctionName name(final String name) {
        return ExpressionFunctionName.with(name)
                .setCaseSensitivity(NAME_CASE_SENSITIVITY);
    }

    /**
     * {@link ExpressionFunctionSelector#parse(String, CaseSensitivity)}
     */
    public static ExpressionFunctionSelector parseSelector(final String selector) {
        return ExpressionFunctionSelector.parse(
                selector,
                NAME_CASE_SENSITIVITY
        );
    }

    /**
     * {@link ErrorExpressionFunction}
     */
    public static ExpressionFunction<SpreadsheetError, ExpressionEvaluationContext> error() {
        return ErrorExpressionFunction.INSTANCE;
    }

    private SpreadsheetExpressionFunctions() {
        throw new UnsupportedOperationException();
    }
}
