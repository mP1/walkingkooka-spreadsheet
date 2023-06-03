/*
 * Copyright 2022 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
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
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} that wraps another delegating most method calls, except for a few
 * directly related to labels and resolving references to values. Some labels in an executed expression may actually be
 * local parameters scoped to just this context and that logic is handled by this class, basically everything else is
 * forwarded to the wrapped {@link SpreadsheetExpressionEvaluationContext}.
 */
final class LocalLabelsSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
        UsesToStringBuilder {

    static SpreadsheetExpressionEvaluationContext with(
            final Function<SpreadsheetLabelName, Optional<Optional<Object>>> labelToValues,
            final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(labelToValues, "labelToValues");
        Objects.requireNonNull(context, "context");

        return new LocalLabelsSpreadsheetExpressionEvaluationContext(
                labelToValues,
                context
        );
    }

    private LocalLabelsSpreadsheetExpressionEvaluationContext(final Function<SpreadsheetLabelName, Optional<Optional<Object>>> labelToValues,
                                                              final SpreadsheetExpressionEvaluationContext context) {
        super();
        this.labelToValues = labelToValues;
        this.context = context;
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return this.context.caseSensitivity();
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.context.cell();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference spreadsheetCellReference) {
        return this.context.loadCell(spreadsheetCellReference);
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.context.spreadsheetMetadata();
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.context.serverUrl();
    }

    // ExpressionEvaluationContext......................................................................................

    @Override
    public Object evaluate(final Expression expression) {
        return expression.toValue(this);
    }

    @Override
    public SpreadsheetParserToken parseExpression(final TextCursor cursor) {
        return this.context.parseExpression(cursor);
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> function(final FunctionExpressionName functionName) {
        this.failIfParameterName(functionName);

        return this.context.function(functionName);
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        return this.context.prepareParameter(
                parameter,
                value
        );
    }

    @Override
    public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                   final List<Object> parameters) {
        return function
                .apply(
                        this.prepareParameters(function, parameters),
                        Cast.to(this)
                );
    }

    @Override
    public Object handleException(final RuntimeException thrown) {
        return this.context.handleException(thrown);
    }

    /**
     * Complains if the given selection is a local label with a value.
     */
    @Override
    public SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection) {
        if (selection.isLabelName() && this.findLocalLabel((SpreadsheetLabelName) selection).isPresent()) {
            throw new IllegalArgumentException("Label " + selection + " has a value");
        }

        return this.context.resolveIfLabel(selection);
    }

    /**
     * The reference could be a local named parameter check that first then ask the wrapped context.
     */
    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        Optional<Optional<Object>> value = null;

        if (reference instanceof SpreadsheetLabelName) {
            value = this.findLocalLabel((SpreadsheetLabelName) reference);

            if (!value.isPresent()) {
                value = null;
            }
        }

        if (null == value) {
            value = this.context.reference(reference);
        }

        return value;
    }

    /**
     * Finds the unevaluated value if the reference is a {@link SpreadsheetLabelName} otherwise returns {@link Optional#empty()}.
     */
    private Optional<Optional<Object>> findLocalLabel(final SpreadsheetLabelName label) {
        return this.labelToValues.apply(label);
    }

    /**
     * Provides the value for a given {@link SpreadsheetLabelName}.
     */
    private final Function<SpreadsheetLabelName, Optional<Optional<Object>>> labelToValues;

    @Override
    public boolean isPure(final FunctionExpressionName functionName) {
        this.failIfParameterName(functionName);

        // $functionName is not a named parameter let the wrapped context test the namedFunction for purity.
        return this.context.isPure(functionName);
    }

    private void failIfParameterName(final FunctionExpressionName functionName) {
        final String text = functionName.value();
        if (SpreadsheetSelection.isLabelText(text) && this.findLocalLabel(SpreadsheetSelection.labelName(text)).isPresent()) {
            throw new IllegalArgumentException("Function name " + functionName + " is a parameter and not an actual function");
        }
    }

    // ConverterContext.................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.context.canConvert(
                value,
                type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        return this.context.convert(value, target);
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.context.converter();
    }

    // DateTimeContext.................................................................................................

    @Override
    public List<String> ampms() {
        return this.context.ampms();
    }

    @Override
    public List<String> monthNames() {
        return this.context.monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.context.monthNames();
    }

    @Override
    public List<String> weekDayNames() {
        return this.context.weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.context.weekDayNameAbbreviations();
    }

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public int defaultYear() {
        return this.context.defaultYear();
    }

    @Override
    public int twoDigitYear() {
        return this.context.twoDigitYear();
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
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    @Override
    public char negativeSign() {
        return this.context.negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.context.positiveSign();
    }

    /**
     * The wrapped {@link SpreadsheetExpressionEvaluationContext}.
     */
    private final SpreadsheetExpressionEvaluationContext context;

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.labelToValues);
        builder.value(this.context);
    }
}
