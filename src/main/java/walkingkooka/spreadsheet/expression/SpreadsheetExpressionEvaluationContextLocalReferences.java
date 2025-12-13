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

import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.convert.CanConvert;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContextDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
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
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storage;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.form.FormHandlerContext;
import walkingkooka.validation.form.FormHandlerContextDelegator;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} that uses a {@link Function} to try and verify if a reference
 * has a local value.
 */
final class SpreadsheetExpressionEvaluationContextLocalReferences implements SpreadsheetExpressionEvaluationContext,
    ConverterContextDelegator,
    FormHandlerContextDelegator<SpreadsheetExpressionReference, SpreadsheetDelta>,
    JsonNodeMarshallUnmarshallContextDelegator,
    LocaleContextDelegator,
    TerminalContextDelegator,
    UsesToStringBuilder {

    static SpreadsheetExpressionEvaluationContextLocalReferences with(
        final Function<ExpressionReference, Optional<Optional<Object>>> localReferenceToValues,
        final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(localReferenceToValues, "localReferenceToValues");
        Objects.requireNonNull(context, "context");

        return new SpreadsheetExpressionEvaluationContextLocalReferences(
            localReferenceToValues,
            context
        );
    }

    private SpreadsheetExpressionEvaluationContextLocalReferences(final Function<ExpressionReference, Optional<Optional<Object>>> localReferenceToValues,
                                                                  final SpreadsheetExpressionEvaluationContext context) {
        super();
        this.localReferenceToValues = localReferenceToValues;
        this.context = context;
    }

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return this.context.stringEqualsCaseSensitivity();
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
    public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        return this.context.spreadsheetFormatterContext(cell);
    }

    @Override
    public SpreadsheetExpressionReference validationReference() {
        return this.context.validationReference();
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public Storage<StorageExpressionEvaluationContext> storage() {
        return this.context.storage();
    }

    // TerminalContextDelegator.........................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext exitTerminal() {
        this.context.exitTerminal();
        return this;
    }

    @Override
    public TerminalContext terminalContext() {
        return this.context;
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
        if (this.findLocalReference(labelName).isPresent()) {
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

        Optional<Optional<Object>> value = this.findLocalReference(reference);

        if (false == value.isPresent()) {
            value = this.context.reference(reference);
        }

        return value;
    }

    /**
     * Attempts to find if the given {@link ExpressionReference} has a value.
     */
    private Optional<Optional<Object>> findLocalReference(final ExpressionReference reference) {
        return this.localReferenceToValues.apply(reference);
    }

    /**
     * Provides the value for a given {@link SpreadsheetLabelName}.
     */
    private final Function<ExpressionReference, Optional<Optional<Object>>> localReferenceToValues;

    @Override
    public boolean isPure(final ExpressionFunctionName functionName) {
        this.failIfParameterName(functionName);

        // $functionName is not a named parameter let the wrapped context test the ExpressionFunction for purity.
        return this.context.isPure(functionName);
    }

    private void failIfParameterName(final ExpressionFunctionName functionName) {
        final String text = functionName.value();
        if (this.findLocalReference(SpreadsheetSelection.labelName(text)).isPresent()) {
            throw new IllegalArgumentException("Function name " + functionName + " is a parameter and not an actual function");
        }
    }

    @Override
    public Optional<Object> validationValue() {
        return this.context.reference(VALIDATION_VALUE)
            .orElse(Optional.empty());
    }

    // ConverterContextDelegator........................................................................................

    @Override
    public CanConvert canConvert() {
        return ConverterContextDelegator.super.canConvert();
    }

    @Override
    public ConverterContext converterContext() {
        return this.context;
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.context.converter();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    // FormHandlerContextDelegator......................................................................................

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return this.context.validatorContext(reference);
    }

    @Override
    public FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext() {
        return this.context;
    }

    // JsonNodeMarshallUnmarshallContextDelegator.......................................................................

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.context;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        return this.setSpreadsheetExpressionEvaluationContext(
            this.context.setObjectPostProcessor(processor)
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        return this.setSpreadsheetExpressionEvaluationContext(
            this.context.setPreProcessor(processor)
        );
    }

    private SpreadsheetExpressionEvaluationContext setSpreadsheetExpressionEvaluationContext(final SpreadsheetExpressionEvaluationContext context) {
        return this.context.equals(context) ?
            this :
            new SpreadsheetExpressionEvaluationContextLocalReferences(
                this.localReferenceToValues,
                context
            );
    }

    // LocaleContextDelegator...........................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.context;
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        final SpreadsheetExpressionEvaluationContext context = this.context;
        final SpreadsheetExpressionEvaluationContext cloned = context.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return context == cloned ?
            this :
            new SpreadsheetExpressionEvaluationContextLocalReferences(
                this.localReferenceToValues,
                Objects.requireNonNull(cloned, "environmentContext")
            );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetExpressionEvaluationContext before = this.context;
        final SpreadsheetExpressionEvaluationContext after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetExpressionEvaluationContextLocalReferences(
                this.localReferenceToValues,
                Objects.requireNonNull(after, "environmentContext")
            );
    }

    @Override
    public <T> SpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                          final T value) {
        this.context.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.context.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public LineEnding lineEnding() {
        return this.context.lineEnding();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.context.setLineEnding(lineEnding);
        return this;
    }

    @Override
    public Locale locale() {
        return this.context.locale();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLocale(final Locale locale) {
        this.context.setLocale(locale);
        return this;
    }

    @Override
    public LocalDateTime now() {
        return this.context.now(); // inherit unrelated defaults
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.context.serverUrl();
    }

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.context.spreadsheetId();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.context.setSpreadsheetId(spreadsheetId);
        return this;
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.context.user();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user) {
        this.context.setUser(user);
        return this;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.localReferenceToValues,
            this.context
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof SpreadsheetExpressionEvaluationContextLocalReferences &&
                this.equals0((SpreadsheetExpressionEvaluationContextLocalReferences) other));
    }

    private boolean equals0(final SpreadsheetExpressionEvaluationContextLocalReferences other) {
        return this.localReferenceToValues.equals(other.localReferenceToValues) &&
            this.context.equals(other.context);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    /**
     * The wrapped {@link SpreadsheetExpressionEvaluationContext}.
     */
    private final SpreadsheetExpressionEvaluationContext context;

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.localReferenceToValues);
        builder.value(this.context);
    }
}
