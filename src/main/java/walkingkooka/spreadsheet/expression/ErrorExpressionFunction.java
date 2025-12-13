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

import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;

import java.util.List;
import java.util.Optional;

/**
 * A function that exists only to implement expressions with errors like #REF! which when executed will return a {@link SpreadsheetError}
 * with the given {@link SpreadsheetErrorKind#value()}
 */
final class ErrorExpressionFunction implements ExpressionFunction<SpreadsheetError, ExpressionEvaluationContext> {

    /**
     * Singleton
     */
    final static ErrorExpressionFunction INSTANCE = new ErrorExpressionFunction();

    private ErrorExpressionFunction() {
        super();
    }

    @Override
    public Optional<ExpressionFunctionName> name() {
        return NAME;
    }

    private final static Optional<ExpressionFunctionName> NAME = Optional.of(
        SpreadsheetExpressionFunctions.name("error")
    );

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int i) {
        return PARAMETERS;
    }

    final static ExpressionFunctionParameter<ExpressionNumber> ERROR_VALUE = ExpressionFunctionParameterName.with("error-value")
        .required(ExpressionNumber.class)
        .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES);


    private final static List<ExpressionFunctionParameter<?>> PARAMETERS = ExpressionFunctionParameter.list(ERROR_VALUE);

    @Override
    public Class<SpreadsheetError> returnType() {
        return SpreadsheetError.class;
    }

    @Override
    public SpreadsheetError apply(final List<Object> parameters,
                                  final ExpressionEvaluationContext context) {
        this.checkParameterCount(parameters);
        return SpreadsheetErrorKind.withValue(
            ERROR_VALUE.getOrFail(parameters, 0, context)
                .intValueExact()
        ).toError();
    }

    @Override
    public boolean isPure(final ExpressionPurityContext context) {
        return true;
    }

    @Override
    public String toString() {
        return this.name().get()
            .toString();
    }
}
