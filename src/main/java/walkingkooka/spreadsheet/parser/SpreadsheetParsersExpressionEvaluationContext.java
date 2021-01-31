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

package walkingkooka.spreadsheet.parser;

import walkingkooka.Either;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class SpreadsheetParsersExpressionEvaluationContext implements ExpressionEvaluationContext {

    static SpreadsheetParsersExpressionEvaluationContext with(final SpreadsheetParserContext context) {
        return new SpreadsheetParsersExpressionEvaluationContext(context);
    }

    private SpreadsheetParsersExpressionEvaluationContext(final SpreadsheetParserContext context) {
        super();
        this.context = context;
    }

    @Override
    public Object evaluate(final Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Expression> reference(final ExpressionReference reference) {
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
    public boolean canConvert(final Object value, final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Either<T, String> convert(Object value, Class<T> target) {
        throw new UnsupportedOperationException();
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
        return this.context.expressionNumberKind();
    }

    private final SpreadsheetParserContext context;

    public String toString() {
        return this.context.toString();
    }
}
