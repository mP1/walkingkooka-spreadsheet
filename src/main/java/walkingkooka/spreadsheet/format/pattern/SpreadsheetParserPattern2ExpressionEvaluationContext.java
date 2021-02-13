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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.Either;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A {@link ExpressionEvaluationContext} view of a {@link ExpressionNumberConverterContext}.
 * Note a few methods throw {@link UnsupportedOperationException}.
 */
final class SpreadsheetParserPattern2ExpressionEvaluationContext implements ExpressionEvaluationContext {

    static SpreadsheetParserPattern2ExpressionEvaluationContext with(final ExpressionNumberConverterContext context) {
        return new SpreadsheetParserPattern2ExpressionEvaluationContext(context);
    }

    private SpreadsheetParserPattern2ExpressionEvaluationContext(final ExpressionNumberConverterContext context) {
        super();
        this.context = context;
    }

    @Override
    public Object evaluate(final Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPure(final FunctionExpressionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Expression> reference(final ExpressionReference expressionReference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionFunction<?, ExpressionFunctionContext> function(final FunctionExpressionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(final FunctionExpressionName name,
                           final List<Object> parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> target) {
        return this.context.canConvert(value, target);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        return this.context.convert(value, target);
    }

    @Override
    public int defaultYear() {
        return this.context.defaultYear();
    }

    @Override
    public int twoDigitYear() {
        return this.context.twoDigitYear();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return null;
    }

    private final ExpressionNumberConverterContext context;

    public String toString() {
        return this.context.toString();
    }
}
