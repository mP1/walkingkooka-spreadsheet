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

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} that supports a custom {@link Converter} to be used to convert
 * *ONLY* parameter values when executing a {@link ExpressionFunction}.
 *
 * <br>
 * This is necessary because some functions do not need the default conversion of values to another using the format or
 * parse patterns and more. The <pre>lower</pre> namedFunction handling of numbers is an example where this
 * {@link SpreadsheetExpressionEvaluationContext} is useful.
 * <br>
 */
final class ConverterSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext {

    static ConverterSpreadsheetExpressionEvaluationContext with(final Converter<SpreadsheetExpressionEvaluationContext> converter,
                                                                final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(context, "context");

        return context instanceof ConverterSpreadsheetExpressionEvaluationContext ?
                unwrap(
                        converter,
                        (ConverterSpreadsheetExpressionEvaluationContext) context
                ) :
                new ConverterSpreadsheetExpressionEvaluationContext(
                        converter,
                        context
                );
    }

    private static ConverterSpreadsheetExpressionEvaluationContext unwrap(final Converter<SpreadsheetExpressionEvaluationContext> converter,
                                                                          final ConverterSpreadsheetExpressionEvaluationContext context) {
        return new ConverterSpreadsheetExpressionEvaluationContext(
                converter,
                context.context
        );
    }


    private ConverterSpreadsheetExpressionEvaluationContext(final Converter<SpreadsheetExpressionEvaluationContext> converter,
                                                            final SpreadsheetExpressionEvaluationContext context) {
        this.converter = converter;
        this.context = context;
    }

    // Converter........................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.enableConverter ?
                this.converter.canConvert(
                value,
                type,
                this
        ) :
                this.context.canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        return this.enableConverter ?
                this.converter.convert(
                value,
                target,
                this
        ) :
                this.context.convert(value, target);
    }

    @Override
    public <T> T convertOrFail(final Object value,
                               final Class<T> target) {
        return this.enableConverter ?
                this.converter.convertOrFail(
                value,
                target,
                this
        ):
                this.context.convertOrFail(value, target);
    }

    @Override
    public <T> Either<T, String> failConversion(final Object value,
                                                final Class<T> target) {
        return this.enableConverter ?
                this.converter.failConversion(value, target) :
                this.context.failConversion(value, target);
    }

    @Override
    public <T> Either<T, String> failConversion(final Object value,
                                                final Class<T> target,
                                                final Throwable cause) {
        return this.enableConverter ?
                this.converter.failConversion(value, target, cause) :
                this.context.failConversion(value, target, cause);
    }

    /**
     * The {@link Converter} that is overriding the general support methods in the wrapped {@link SpreadsheetExpressionEvaluationContext}.
     */
    // @VisibleForTesting
    final Converter<SpreadsheetExpressionEvaluationContext> converter;

    @Override
    public long dateOffset() {
        return this.context.dateOffset();
    }

    // SpreadsheetExpressionEvaluationContext delegate..................................................................

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.context.cell();
    }

    @Override
    public SpreadsheetCell cellOrFail() {
        return this.context.cellOrFail();
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.context.converter();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        return this.context.loadCell(cell);
    }

    @Override
    public SpreadsheetParserToken parseFormula(final TextCursor expression) {
        return this.context.parseFormula(expression);
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.context.resolveLabel(labelName);
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.context.spreadsheetMetadata();
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.context.serverUrl();
    }

    // eval scoped......................................................................................................

    /**
     * Evaluates the given {@link Expression} using this as the context which should result in the format
     * being used.
     */
    @Override
    public Object evaluate(final Expression expression) {
        Objects.requireNonNull(expression, "expression");

        Object result;

        try {
            result = expression.toValue(this);
        } catch (final RuntimeException exception) {
            result = this.handleException(exception);
        }

        return result;
    }

    private int scope;

    /**
     * Prepares the parameter value.
     */
    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        try {
            this.enableConverter = true;
            return parameter.convertOrFail(
                    value,
                    0 == this.scope ?
                            this :
                            this.context
            );
        } finally {
            this.enableConverter = false;
        }
    }

    /**
     * This flag is only ever true when preparing a parameter value.
     */
    private boolean enableConverter;

    /**
     * Prepares the parameters which includes calling the given {@link #converter} when converting the parameter values,
     * rather than delegating to the same method on the wrapped {@link SpreadsheetExpressionEvaluationContext}.
     */
    @Override
    public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                   final List<Object> parameters) {
        this.scope++;
        try {
            return this.context.evaluateFunction(
                    function,
                    parameters
            );
        } finally {
            this.scope--;
        }
    }

    // eval delegated..................................................................................................

    @Override
    public Optional<ExpressionFunction<?, ExpressionEvaluationContext>> expressionFunction(final FunctionExpressionName name) {
        return this.context.expressionFunction(name);
    }

    @Override
    public Set<ExpressionFunctionInfo> expressionFunctionInfos() {
        return this.context.expressionFunctionInfos();
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        return this.context.handleException(exception);
    }

    @Override
    public boolean isPure(final FunctionExpressionName name) {
        return this.context.isPure(name);
    }

    // references.....................................................................................................

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        return this.context.reference(reference);
    }

    @Override
    public Object referenceOrFail(final ExpressionReference reference) {
        return this.context.referenceOrFail(reference);
    }

    @Override
    public ExpressionEvaluationException referenceNotFound(final ExpressionReference reference) {
        return this.context.referenceNotFound(reference);
    }

    // DateTimeContext.................................................................................................

    @Override
    public List<String> ampms() {
        return this.context.ampms();
    }

    @Override
    public String ampm(final int hourOfDay) {
        return this.context.ampm(hourOfDay);
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
    public String monthName(final int month) {
        return this.context.monthName(month);
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.context.monthNameAbbreviations();
    }

    @Override
    public String monthNameAbbreviation(final int month) {
        return this.context.monthNameAbbreviation(month);
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public int twoDigitYear() {
        return this.context.twoDigitYear();
    }

    @Override
    public int twoToFourDigitYear(final int year) {
        return this.context.twoToFourDigitYear(year);
    }

    @Override
    public List<String> weekDayNames() {
        return this.context.weekDayNames();
    }

    @Override
    public String weekDayName(final int day) {
        return this.context.weekDayName(day);
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.context.weekDayNameAbbreviations();
    }

    @Override
    public String weekDayNameAbbreviation(final int day) {
        return this.context.weekDayNameAbbreviation(day);
    }

    // DecimalContext...................................................................................................

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
    public MathContext mathContext() {
        return this.context.mathContext();
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

    // misc............................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return this.context.caseSensitivity();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    /**
     * The wrapped {@link SpreadsheetExpressionEvaluationContext}
     */
    // @VisibleForTesting
    final SpreadsheetExpressionEvaluationContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.converter + " " + this.context;
    }
}
