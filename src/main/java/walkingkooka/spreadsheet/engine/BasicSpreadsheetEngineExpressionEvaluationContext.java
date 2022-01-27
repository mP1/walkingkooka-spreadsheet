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

package walkingkooka.spreadsheet.engine;

import walkingkooka.Either;
import walkingkooka.convert.ConverterContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link ExpressionEvaluationContext} used exclusively by {@link BasicSpreadsheetEngine#parseFormulaIfNecessary(SpreadsheetCell, Function, SpreadsheetEngineContext)}
 * which uses this to convert a {@link walkingkooka.spreadsheet.parser.SpreadsheetParserToken#toExpression(ExpressionEvaluationContext)}.
 * <br>
 * None of the function or evaluation type methods should be called and all throw {@link UnsupportedOperationException}.
 */
final class BasicSpreadsheetEngineExpressionEvaluationContext implements ExpressionEvaluationContext {

    static BasicSpreadsheetEngineExpressionEvaluationContext with(final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineExpressionEvaluationContext(context);
    }

    private BasicSpreadsheetEngineExpressionEvaluationContext(final SpreadsheetEngineContext context) {
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
    public Optional<Object> reference(ExpressionReference expressionReference) {
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
                              final Class<?> type) {
        return this.converterContext()
                .canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        return this.converterContext()
                .convert(value, target);
    }

    private ConverterContext converterContext() {
        return this.context.metadata()
                .converterContext();
    }

    @Override
    public int defaultYear() {
        return this.context
                .metadata()
                .getOrFail(SpreadsheetMetadataPropertyName.DEFAULT_YEAR);
    }

    @Override
    public int twoDigitYear() {
        return this.context
                .metadata()
                .getOrFail(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR);
    }

    @Override
    public String currencySymbol() {
        return this.expressionNumberContext()
                .currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.expressionNumberContext()
                .decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.expressionNumberContext()
                .exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.expressionNumberContext()
                .groupingSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.expressionNumberContext()
                .percentageSymbol();
    }

    @Override
    public char negativeSign() {
        return this.expressionNumberContext()
                .negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.expressionNumberContext()
                .positiveSign();
    }

    private ExpressionNumberContext expressionNumberContext() {
        return this.metadata()
                .expressionNumberContext();
    }

    @Override
    public Locale locale() {
        return this.metadata()
                .getOrFail(SpreadsheetMetadataPropertyName.LOCALE);
    }

    @Override
    public MathContext mathContext() {
        return this.metadata().mathContext();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.metadata().expressionNumberKind();
    }

    private SpreadsheetMetadata metadata() {
        return this.context.metadata();
    }

    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}
