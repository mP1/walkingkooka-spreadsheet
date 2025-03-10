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

package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.Either;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetDataValidatorContext} which wraps a {@link ExpressionEvaluationContext}.
 */
final class BasicSpreadsheetDataValidatorContext implements SpreadsheetDataValidatorContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator {

    /**
     * Factory that creates a {@link BasicSpreadsheetDataValidatorContext} including the
     * cell and value being validated.
     */
    static BasicSpreadsheetDataValidatorContext with(final ExpressionReference cellReference,
                                                     final Object value,
                                                     final ExpressionEvaluationContext context) {
        Objects.requireNonNull(cellReference, "cell");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return new BasicSpreadsheetDataValidatorContext(cellReference, value, context);
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetDataValidatorContext(final ExpressionReference cellReference,
                                                 final Object value,
                                                 final ExpressionEvaluationContext context) {
        super();
        this.cellReference = cellReference;
        this.value = Optional.of(value);
        this.context = context;
    }

    @Override
    public ExpressionReference cellReference() {
        return this.cellReference;
    }

    private final ExpressionReference cellReference;

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
    }

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

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        return parameter.convertOrFail(value, this);
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        return this.cellReference().equals(reference) ?
                Optional.of(this.value) :
                this.context.reference(reference);
    }

    private final Optional<Object> value;

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetStrings.CASE_SENSITIVITY;
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    // DateTimeContext..................................................................................................

    @Override
    public DateTimeContext dateTimeContext() {
        return this.context;
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return this.context;
    }

    // Convert..........................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.context.canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.context.convert(value, type);
    }

    @Override
    public long dateOffset() {
        return this.context.dateOffset();
    }

    private final ExpressionEvaluationContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}
