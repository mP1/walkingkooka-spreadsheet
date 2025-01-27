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
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

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
final class ConverterSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator {

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
                ) :
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

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
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
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor expression) {
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
    public Object evaluateExpression(final Expression expression) {
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
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return this.context.expressionFunction(name);
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        return this.context.handleException(exception);
    }

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
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

    // misc.............................................................................................................

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return this.context.stringEqualsCaseSensitivity();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    // DateTimeContext.................................................................................................

    @Override
    public DateTimeContext dateTimeContext() {
        return this.context;
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return this.context;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.converter + " " + this.context;
    }

    /**
     * The wrapped {@link SpreadsheetExpressionEvaluationContext}
     */
    // @VisibleForTesting
    final SpreadsheetExpressionEvaluationContext context;

}
