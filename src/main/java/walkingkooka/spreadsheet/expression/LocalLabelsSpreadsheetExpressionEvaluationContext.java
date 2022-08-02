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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} that wraps another delegating most method calls, except for a few
 * directly related to labels and resolving references to values. Some labels in an executed expression may actually be
 * local parameters scoped to just this context and that logic is handled by this class, basically everything else is
 * forwarded to the wrapped {@link SpreadsheetExpressionEvaluationContext}.
 */
final class LocalLabelsSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
        UsesToStringBuilder {

    static SpreadsheetExpressionEvaluationContext with(
            final Map<SpreadsheetLabelName, Object> nameAndValues,
            final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(nameAndValues, "nameAndValues");
        Objects.requireNonNull(context, "context");

        final int count = nameAndValues.size();
        return 0 == count ?
                context :
                with0(count, nameAndValues, context);
    }

    private static LocalLabelsSpreadsheetExpressionEvaluationContext with0(final int count,
                                                                           final Map<SpreadsheetLabelName, Object> nameAndValues,
                                                                           final SpreadsheetExpressionEvaluationContext context) {
        final LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue[] copy = new LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue[count];
        int i = 0;
        for (final Map.Entry<SpreadsheetLabelName, Object> nameAndValue : nameAndValues.entrySet()) {
            copy[i] = LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue.with(
                    nameAndValue.getKey(),
                    nameAndValue.getValue()
            );
            i++;
        }

        return new LocalLabelsSpreadsheetExpressionEvaluationContext(
                copy,
                context
        );
    }

    private LocalLabelsSpreadsheetExpressionEvaluationContext(final LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue[] nameAndValues,
                                                              final SpreadsheetExpressionEvaluationContext context) {
        super();
        this.nameAndValues = nameAndValues;
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
        this.failIfNamedValue(functionName);

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
    public Object evaluateFunction(final FunctionExpressionName functionExpressionName,
                                   final List<Object> parameters) {
        return this.context.evaluateFunction(
                functionExpressionName,
                parameters
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
    public Optional<Object> reference(final ExpressionReference reference) {
        final Optional<Object> found = this.findLocalLabel(reference)
                .map(nv -> nv.value(this));
        return found.isPresent() ?
                found :
                Optional.of(this.context.reference(reference)); // j2cl missing Optional#or
    }

    private Optional<LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue> findLocalLabel(final ExpressionReference reference) {
        return Arrays.stream(this.nameAndValues)
                .filter(nv -> nv.name.equals(reference))
                .findFirst();
    }

    @Override
    public boolean isPure(final FunctionExpressionName functionName) {
        this.failIfNamedValue(functionName);

        // $functionName is not a named parameter let the wrapped context test the namedFunction for purity.
        return this.context.isPure(functionName);
    }

    private void failIfNamedValue(final FunctionExpressionName functionName) {
        for (final LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue parameterAndValue : this.nameAndValues) {
            final SpreadsheetLabelName namedParameter = parameterAndValue.name;
            if (namedParameter.caseSensitivity().equals(namedParameter.value(), functionName.value())) {
                throw new IllegalArgumentException("Function name " + functionName + " is a named value and not an actual namedFunction");
            }
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
    public char groupingSeparator() {
        return this.context.groupingSeparator();
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

    private final LocalLabelsSpreadsheetExpressionEvaluationContextNameAndValue[] nameAndValues;

    private final SpreadsheetExpressionEvaluationContext context;

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.nameAndValues);
        builder.value(this.context);
    }
}
