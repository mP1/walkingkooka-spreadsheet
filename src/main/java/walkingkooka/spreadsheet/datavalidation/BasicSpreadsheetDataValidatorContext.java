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

import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.expression.ExpressionReference;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetDataValidatorContext} which wraps a {@link ExpressionEvaluationContext}.
 */
final class BasicSpreadsheetDataValidatorContext implements SpreadsheetDataValidatorContext {

    /**
     * Factory that creates a {@link BasicSpreadsheetDataValidatorContext} including the
     * cell and value being validated.
     */
    static BasicSpreadsheetDataValidatorContext with(final ExpressionReference cellReference,
                                                     final Object value,
                                                     final ExpressionEvaluationContext context) {
        Objects.requireNonNull(cellReference, "cellReference");
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
        this.value = Optional.of(ExpressionNode.valueOrFail(value));
        this.context = context;
    }

    @Override
    public String currencySymbol() {
        return this.context.currencySymbol();
    }

    @Override
    public char decimalPoint() {
        return this.context.decimalPoint();
    }

    @Override
    public char exponentSymbol() {
        return this.context.exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.context.groupingSeparator();
    }

    @Override
    public char negativeSign() {
        return this.context.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return this.context.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.context.positiveSign();
    }

    @Override
    public ExpressionReference cellReference() {
        return this.cellReference;
    }

    private final ExpressionReference cellReference;

    @Override
    public Object function(final ExpressionNodeName functionName, final List<Object> list) {
        return this.context.function(functionName, list);
    }

    @Override
    public Optional<ExpressionNode> reference(final ExpressionReference reference) {
        return this.cellReference().equals(reference) ?
                this.value :
                this.context.reference(reference);
    }

    private final Optional<ExpressionNode> value;

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    @Override
    public <T> T convert(final Object value, final Class<T> targetType) {
        return this.context.convert(value, targetType);
    }

    private final ExpressionEvaluationContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}
