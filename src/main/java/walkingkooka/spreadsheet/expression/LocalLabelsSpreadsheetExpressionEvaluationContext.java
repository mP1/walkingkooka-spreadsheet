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
import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.storage.StorageStore;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.validation.form.FormHandlerContext;
import walkingkooka.validation.form.FormHandlerContextDelegator;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} that wraps another delegating most method calls, except for a few
 * directly related to labels and resolving references to values. Some labels in an executed expression may actually be
 * local parameters scoped to just this context and that logic is handled by this class, basically everything else is
 * forwarded to the wrapped {@link SpreadsheetExpressionEvaluationContext}.
 */
final class LocalLabelsSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator,
        FormHandlerContextDelegator<SpreadsheetExpressionReference, SpreadsheetDelta>,
        UsesToStringBuilder {

    static LocalLabelsSpreadsheetExpressionEvaluationContext with(
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
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return this.context.stringEqualsCaseSensitivity();
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
    public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        return SpreadsheetExpressionEvaluationContexts.cell(
                cell,
                this
        );
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        return this.context.loadCell(cell);
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        return this.context.loadCellRange(range);
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return this.context.loadLabel(labelName);
    }

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        return this.context.nextEmptyColumn(row);
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        return this.context.nextEmptyRow(column);
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.context.spreadsheetMetadata();
    }

    @Override
    public void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        this.context.setSpreadsheetMetadata(metadata);
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.context.serverUrl();
    }

    @Override
    public SpreadsheetExpressionReference validationReference() {
        return this.context.validationReference();
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public StorageStore storage() {
        return this.context.storage();
    }

    // ExpressionEvaluationContext......................................................................................

    @Override
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor cursor) {
        return this.context.parseFormula(cursor);
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName functionName) {
        this.failIfParameterName(functionName);

        return this.context.expressionFunction(functionName);
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
    public Object handleException(final RuntimeException thrown) {
        return this.context.handleException(thrown);
    }

    /**
     * Complains if the given selection is a local label with a value
     */
    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        if (this.findLocalLabel(labelName).isPresent()) {
            throw new IllegalArgumentException("Label " + labelName + " has a value");
        }

        return this.context.resolveLabel(labelName);
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
    public boolean isPure(final ExpressionFunctionName functionName) {
        this.failIfParameterName(functionName);

        // $functionName is not a named parameter let the wrapped context test the namedFunction for purity.
        return this.context.isPure(functionName);
    }

    private void failIfParameterName(final ExpressionFunctionName functionName) {
        final String text = functionName.value();
        if (SpreadsheetSelection.isLabelText(text) && this.findLocalLabel(SpreadsheetSelection.labelName(text)).isPresent()) {
            throw new IllegalArgumentException("Function name " + functionName + " is a parameter and not an actual function");
        }
    }

    @Override
    public Optional<Object> validationValue() {
        return this.context.reference(VALIDATION_VALUE)
                .orElse(Optional.empty());
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

    @Override
    public long dateOffset() {
        return this.context.dateOffset();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
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

    // FormHandlerContextDelegator......................................................................................

    @Override
    public LocalDateTime now() {
        return this.context.now();
    }

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return this.context.validatorContext(reference);
    }

    @Override
    public FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext() {
        return this.context;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    /**
     * The wrapped {@link SpreadsheetExpressionEvaluationContext}.
     */
    private final SpreadsheetExpressionEvaluationContext context;

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.labelToValues);
        builder.value(this.context);
    }
}
