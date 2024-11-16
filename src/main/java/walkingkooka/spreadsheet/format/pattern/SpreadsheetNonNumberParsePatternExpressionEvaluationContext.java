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
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link ExpressionEvaluationContext} view of a {@link ExpressionNumberConverterContext}.
 * Note a few methods throw {@link UnsupportedOperationException}.
 */
final class SpreadsheetNonNumberParsePatternExpressionEvaluationContext implements ExpressionEvaluationContext {

    static SpreadsheetNonNumberParsePatternExpressionEvaluationContext with(final ExpressionNumberConverterContext context) {
        return new SpreadsheetNonNumberParsePatternExpressionEvaluationContext(context);
    }

    private SpreadsheetNonNumberParsePatternExpressionEvaluationContext(final ExpressionNumberConverterContext context) {
        super();
        this.context = context;
    }

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetStrings.CASE_SENSITIVITY;
    }

    @Override
    public ExpressionEvaluationContext context(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(final Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isText(final Object value) {
        return value instanceof Character || value instanceof CharSequence;
    }

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                   final List<Object> parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object handleException(final RuntimeException exception) {
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
    public long dateOffset() {
        return this.context.dateOffset();
    }

    // DateTimeContext..................................................................................................

    @Override
    public List<String> ampms() {
        return this.context.ampms();
    }

    @Override
    public int defaultYear() {
        return this.context.defaultYear();
    }

    @Override
    public List<String> monthNames() {
        return this.context.monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.context.monthNameAbbreviations();
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public int twoToFourDigitYear(final int year) {
        return this.context.twoToFourDigitYear(year);
    }

    @Override
    public int twoDigitYear() {
        return this.context.twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return this.context.weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.context.weekDayNameAbbreviations();
    }

    // DecimalNumberContext............................................................................................

    @Override
    public String currencySymbol() {
        return this.context.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.context.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.context.exponentSymbol();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
    }

    @Override
    public char groupSeparator() {
        return this.context.groupSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.context.percentageSymbol();
    }

    @Override
    public char negativeSign() {
        return this.context.negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.context.positiveSign();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    private final ExpressionNumberConverterContext context;

    public String toString() {
        return this.context.toString();
    }
}
