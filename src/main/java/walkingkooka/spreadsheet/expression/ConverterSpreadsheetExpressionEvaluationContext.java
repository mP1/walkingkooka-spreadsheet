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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
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
import walkingkooka.storage.Storage;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;

import java.math.MathContext;
import java.util.Comparator;
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
final class ConverterSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
    DateTimeContextDelegator,
    DecimalNumberContextDelegator,
    JsonNodeMarshallUnmarshallContextDelegator,
    LocaleContextDelegator,
    TerminalContextDelegator {

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
    public boolean canNumbersHaveGroupSeparator() {
        return this.context.canNumbersHaveGroupSeparator();
    }

    @Override
    public long dateOffset() {
        return this.context.dateOffset();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.context.expressionNumberKind();
    }

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    @Override
    public char valueSeparator() {
        return this.context.valueSeparator();
    }

    // SpreadsheetExpressionEvaluationContext delegate..................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        return SpreadsheetExpressionEvaluationContexts.cell(
            cell,
            this
        );
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.context.cell();
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
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor expression) {
        return this.context.parseFormula(expression);
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.context.resolveLabel(labelName);
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
    public AbsoluteUrl serverUrl() {
        return this.context.serverUrl();
    }

    @Override
    public SpreadsheetExpressionReference validationReference() {
        return this.context.validationReference();
    }

    @Override
    public Optional<Object> validationValue() {
        return this.context.validationValue();
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

    // FormHandlerContext...............................................................................................

    @Override
    public Form<SpreadsheetExpressionReference> form() {
        return this.context.form();
    }

    @Override
    public Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        return this.context.formFieldReferenceComparator();
    }

    @Override
    public Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
        return this.context.loadFormFieldValue(reference);
    }

    @Override
    public SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> fields) {
        return this.context.saveFormFieldValues(fields);
    }

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return this.context.validatorContext(reference);
    }

    // eval scoped......................................................................................................

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
    // special case the default adding the extra scope inc/dec
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

    private int scope;

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

    // JsonNodeMarshallUnmarshallContextDelegator.......................................................................

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
            new ConverterSpreadsheetExpressionEvaluationContext(
                this.converter,
                context
            );
    }

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this;
    }

    // LocaleContextDelegator...........................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.context;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLocale(final Locale locale) {
        this.context.setLocale(locale);
        return this;
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        final SpreadsheetExpressionEvaluationContext context = this.context;
        final SpreadsheetExpressionEvaluationContext cloned = context.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return context == cloned ?
            this :
            new ConverterSpreadsheetExpressionEvaluationContext(
                this.converter,
                Objects.requireNonNull(cloned, "environmentContext")
            );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetExpressionEvaluationContext before = this.context;
        final SpreadsheetExpressionEvaluationContext after = before.setEnvironmentContext(environmentContext);


        return before.equals(after) ?
            this :
            new ConverterSpreadsheetExpressionEvaluationContext(
                this.converter,
                Objects.requireNonNull(after, "environmentContext")
            );
    }

    @Override
    public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        return this.context.environmentValue(name);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<EnvironmentValueName<?>> environmentValueNames() {
        return this.context.environmentValueNames();
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

    // HasLineEnding....................................................................................................

    @Override
    public LineEnding lineEnding() {
        return this.context.lineEnding();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.context.setLineEnding(lineEnding);
        return this;
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
