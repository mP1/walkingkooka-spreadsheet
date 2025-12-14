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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.CanConvert;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;
import walkingkooka.validation.form.FormHandlerContext;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} that includes a {@link SpreadsheetMetadata}.
 */
final class SpreadsheetExpressionEvaluationContextSpreadsheetContext implements SpreadsheetExpressionEvaluationContext,
    EnvironmentContextDelegator,
    SpreadsheetConverterContextDelegator,
    TerminalContextDelegator {

    static SpreadsheetExpressionEvaluationContextSpreadsheetContext with(final SpreadsheetMetadataMode mode,
                                                                         final Optional<SpreadsheetCell> cell,
                                                                         final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                         final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                                         final SpreadsheetContext spreadsheetContext,
                                                                         final TerminalContext terminalContext) {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(spreadsheetExpressionReferenceLoader, "spreadsheetExpressionReferenceLoader");
        Objects.requireNonNull(spreadsheetLabelNameResolver, "spreadsheetLabelNameResolver");
        Objects.requireNonNull(spreadsheetContext, "spreadsheetContext");
        Objects.requireNonNull(terminalContext, "terminalContext");

        return new SpreadsheetExpressionEvaluationContextSpreadsheetContext(
            mode,
            cell,
            spreadsheetExpressionReferenceLoader,
            spreadsheetLabelNameResolver,
            null, // SpreadsheetConverterContext
            null, // ExpressionFunctionProvider
            null, // formHandlerContext
            null, // JsonNodeMarshallContextObjectPostProcessor
            null, // JsonNodeUnmarshallContextPreProcessor
            null, // SpreadsheetParser
            spreadsheetContext,
            null, // SpreadsheetParserContext
            terminalContext
        );
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext(final SpreadsheetMetadataMode mode,
                                                                     final Optional<SpreadsheetCell> cell,
                                                                     final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                     final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                                     final SpreadsheetConverterContext spreadsheetConverterContext,
                                                                     final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                     final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor,
                                                                     final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor,
                                                                     final SpreadsheetParser spreadsheetParser,
                                                                     final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext,
                                                                     final SpreadsheetContext spreadsheetContext,
                                                                     final SpreadsheetParserContext spreadsheetParserContext,
                                                                     final TerminalContext terminalContext) {
        super();
        this.mode = mode;

        this.cell = cell;
        this.spreadsheetExpressionReferenceLoader = spreadsheetExpressionReferenceLoader;
        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;

        this.expressionFunctionProvider = expressionFunctionProvider; // may be null
        this.spreadsheetConverterContext = spreadsheetConverterContext; // may be null
        this.jsonNodeMarshallContextObjectPostProcessor = jsonNodeMarshallContextObjectPostProcessor;
        this.jsonNodeUnmarshallContextPreProcessor = jsonNodeUnmarshallContextPreProcessor;
        this.spreadsheetParser = spreadsheetParser;

        this.formHandlerContext = formHandlerContext;
        this.spreadsheetContext = spreadsheetContext;
        this.spreadsheetParserContext = spreadsheetParserContext;
        this.terminalContext = terminalContext;
    }

    // SpreadsheetExpressionEvaluationContext............................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        return SpreadsheetExpressionEvaluationContexts.cell(
            cell,
            this
        );
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCell> cell;

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetContext.spreadsheetMetadata();
    }

    @Override
    public void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        final SpreadsheetMetadata old = this.spreadsheetMetadata();
        final SpreadsheetId oldId = old.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);
        final SpreadsheetId newId = metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);
        if (false == oldId.equals(newId)) {
            throw new IllegalArgumentException("Invalid metadata id " + newId + " is different from " + oldId);
        }

        // TODO maybe should clear parsed cell formulas.
        this.spreadsheetContext.saveMetadata(metadata);

        // re-create these instances which use SpreadsheetMetadata properties
        this.spreadsheetConverterContext = null;
        this.expressionFunctionProvider = null;
        this.spreadsheetParserContext = null;
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext setMode(final SpreadsheetMetadataMode mode) {
        return this.mode.equals(mode) ?
            this :
            new SpreadsheetExpressionEvaluationContextSpreadsheetContext(
                Objects.requireNonNull(mode, "mode"),
                this.cell,
                this.spreadsheetExpressionReferenceLoader,
                this.spreadsheetLabelNameResolver,
                null, // expressionFunctionProvider clear force recreate
                null, // spreadsheetConverterContext  clear force recreate!
                this.jsonNodeMarshallContextObjectPostProcessor,
                this.jsonNodeUnmarshallContextPreProcessor,
                null, // recreate SpreadsheetParser
                this.formHandlerContext,
                this.spreadsheetContext,
                null, // re-create SpreadsheetParserContext
                this.terminalContext
            );
    }

    private final SpreadsheetMetadataMode mode;

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        return this.spreadsheetExpressionReferenceLoader.loadCell(
            cell,
            this
        );
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        return this.spreadsheetExpressionReferenceLoader.loadCellRange(
            range,
            this
        );
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetExpressionReferenceLoader.loadLabel(labelName);
    }

    final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader;

    @Override
    public LocaleContext localeContext() {
        return this.spreadsheetContext; // short circuit SpreadsheetConverterContextDelegator
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetLabelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver;

    @Override
    public CanConvert canConvert() {
        return this.spreadsheetConverterContext(); // inherit unrelated defaults
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        if (null == this.spreadsheetConverterContext) {
            final SpreadsheetContext spreadsheetContext = this.spreadsheetContext;

            this.spreadsheetConverterContext = this.spreadsheetMetadata()
                .spreadsheetConverterContext(
                    this.cell,
                    SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                    this.mode.converter(),
                    this.spreadsheetLabelNameResolver,
                    spreadsheetContext, // SpreadsheetProvider, // SpreadsheetConverterProvider
                    this, // LocaleContext
                    spreadsheetContext.providerContext()
                );
        }
        return this.spreadsheetConverterContext;
    }

    /**
     * Lazily created using {@link #mode} to select a {@link SpreadsheetMetadata}.
     */
    private SpreadsheetConverterContext spreadsheetConverterContext;

    @Override
    public SpreadsheetFormulaParserToken parseExpression(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetParserContext parserContext = this.spreadsheetParserContext();

        return SpreadsheetFormulaParsers.expression()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(expression, parserContext)
            .get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    @Override
    public SpreadsheetFormulaParserToken parseValueOrExpression(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetParserContext parserContext = this.spreadsheetParserContext();

        return SpreadsheetFormulaParsers.valueOrExpression(this.spreadsheetParser())
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(expression, parserContext)
            .get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    private SpreadsheetParser spreadsheetParser() {
        if (null == this.spreadsheetParser) {
            final SpreadsheetContext spreadsheetContext = this.spreadsheetContext;

            this.spreadsheetParser = this.spreadsheetMetadata()
                .spreadsheetParser(
                    spreadsheetContext, // SpreadsheetParserProvider
                    spreadsheetContext.providerContext()
                );
        }
        return this.spreadsheetParser;
    }

    private SpreadsheetParser spreadsheetParser;

    private SpreadsheetParserContext spreadsheetParserContext() {
        if (null == this.spreadsheetParserContext) {
            this.spreadsheetParserContext = this.spreadsheetMetadata()
                .spreadsheetParserContext(
                    this.cell,
                    this, // LocaleContext
                    this // now
                );
        }
        return this.spreadsheetParserContext;
    }

    private SpreadsheetParserContext spreadsheetParserContext;

    /**
     * Lazily created {@link ExpressionFunctionProvider}, should be nulled whenever the {@link SpreadsheetMetadata} changes.
     */
    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider() {
        if (null == this.expressionFunctionProvider) {
            this.expressionFunctionProvider = this.spreadsheetMetadata()
                .expressionFunctionProvider(
                    this.mode.function(),
                    this.spreadsheetContext // SpreadsheetProvider
                );
        }
        return this.expressionFunctionProvider;
    }

    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        final SpreadsheetContext spreadsheetContext = this.spreadsheetContext;

        return this.spreadsheetMetadata()
            .spreadsheetFormatterContext(
                cell,
                (final Optional<Object> v) -> this.setMode(
                    SpreadsheetMetadataMode.FORMATTING
                ).addLocalVariable(
                    SpreadsheetExpressionEvaluationContext.FORMAT_VALUE,
                    v
                ),
                this.spreadsheetLabelNameResolver,
                spreadsheetContext, // LocaleContext
                spreadsheetContext, // SpreadsheetProvider
                spreadsheetContext.providerContext() // ProviderContext
            );
    }

    // ExpressionEvaluationContext......................................................................................

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return Cast.to(
            this.expressionFunctionProvider()
                .expressionFunction(
                    name,
                    Lists.empty(),
                    this.spreadsheetContext.providerContext() // ProviderContext
                )
        );
    }

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
        return this.expressionFunction(name)
            .isPure(this);
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        return parameter.convertOrFail(value, this);
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        return SpreadsheetErrorKind.translate(exception);
    }

    /**
     * Resolves several types of {@link ExpressionReference} into values.
     * <ul>
     * <li>Resolves {@link SpreadsheetLabelName} to a {@link SpreadsheetCell} returning its value.</li>
     * <li>Loads a {@link SpreadsheetCell} to a {@link SpreadsheetCell} returning its value.</li>
     * <li>For {@link EnvironmentValueName} loads the {@link EnvironmentContext#environmentValue(EnvironmentValueName)}.</li>
     * </ul>
     */
    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        Optional<Optional<Object>> value = Optional.empty();

        final Set<Object> cycle = Sets.ordered();

        Object temp = reference;

        while (temp instanceof ExpressionReference) {
            // cycle detection
            if (false == cycle.add(temp)) {
                throw new IllegalArgumentException("Cycle detected from " + reference + " with " + temp);
            }

            if (temp instanceof SpreadsheetExpressionReference) {
                SpreadsheetExpressionReference spreadsheetExpressionReference = (SpreadsheetExpressionReference) temp;
                final SpreadsheetSelection selection = this.resolveIfLabel(
                    (ExpressionReference) temp
                ).orElse(null);

                if (null != selection) {
                    spreadsheetExpressionReference = selection.toExpressionReference();
                }
                if (spreadsheetExpressionReference instanceof SpreadsheetExpressionReference) {
                    value = SpreadsheetExpressionEvaluationContextSpreadsheetContextReferenceSpreadsheetSelectionVisitor.values(
                        spreadsheetExpressionReference,
                        this.spreadsheetExpressionReferenceLoader,
                        this
                    );
                }
            } else {
                if (temp instanceof EnvironmentValueName) {
                    value = Optional.ofNullable(
                        Cast.to(
                            this.environmentValue(
                                (EnvironmentValueName<?>) temp
                            )
                        )
                    );
                }
            }

            temp = value.map(v -> v.orElse(null))
                .orElse(null);
        }

        return value;
    }

    // FormHandlerContext...............................................................................................

    @Override
    public Form<SpreadsheetExpressionReference> form() {
        return this.formHandlerContext.form();
    }

    @Override
    public Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        return this.formHandlerContext.formFieldReferenceComparator();
    }

    @Override
    public Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
        return this.formHandlerContext.loadFormFieldValue(reference);
    }

    @Override
    public SpreadsheetDelta saveFormFieldValues(List<FormField<SpreadsheetExpressionReference>> formFields) {
        return this.formHandlerContext.saveFormFieldValues(formFields);
    }

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return Cast.to(
            this.formHandlerContext.validatorContext(reference)
        );
    }

    private final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext;

    // JsonNodeUnmarshallContext........................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final SpreadsheetConverterContext before = this.spreadsheetConverterContext();
        final SpreadsheetConverterContext after = before.setObjectPostProcessor(processor);

        return before.equals(after) ?
            this :
            this.setSpreadsheetConverterContext(
                after,
                processor,
                this.jsonNodeUnmarshallContextPreProcessor
            );
    }

    private final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor;

    @Override
    public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetConverterContext before = this.spreadsheetConverterContext();
        final SpreadsheetConverterContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            this.setSpreadsheetConverterContext(
                after,
                this.jsonNodeMarshallContextObjectPostProcessor,
                processor
            );
    }

    private final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor;

    private SpreadsheetExpressionEvaluationContext setSpreadsheetConverterContext(final SpreadsheetConverterContext context,
                                                                                  final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor,
                                                                                  final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor) {
        return new SpreadsheetExpressionEvaluationContextSpreadsheetContext(
            this.mode,
            this.cell,
            this.spreadsheetExpressionReferenceLoader,
            this.spreadsheetLabelNameResolver,
            context,
            this.expressionFunctionProvider,
            jsonNodeMarshallContextObjectPostProcessor,
            jsonNodeUnmarshallContextPreProcessor,
            this.spreadsheetParser,
            this.formHandlerContext,
            this.spreadsheetContext,
            this.spreadsheetParserContext,
            this.terminalContext
        );
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public Storage<StorageExpressionEvaluationContext> storage() {
        return this.spreadsheetContext.storeRepository()
            .storage();
    }

    private final SpreadsheetContext spreadsheetContext;

    // TerminalContextDelegator.........................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext exitTerminal() {
        this.terminalContext.exitTerminal();
        return this;
    }

    @Override
    public TerminalContext terminalContext() {
        return this.terminalContext;
    }

    private final TerminalContext terminalContext;

    // ValidationExpressionEvaluationContext............................................................................

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        return this.spreadsheetContext.storeRepository()
            .cells()
            .nextEmptyColumn(row);
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        return this.spreadsheetContext.storeRepository()
            .cells()
            .nextEmptyRow(column);
    }

    @Override
    public Optional<Object> validationValue() {
        return this.reference(VALIDATION_VALUE)
            .orElse(Optional.empty());
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        return this.setSpreadsheetContext(
            this.spreadsheetContext.setEnvironmentContext(
                this.spreadsheetContext.cloneEnvironment()
            )
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        return this.setSpreadsheetContext(
            this.spreadsheetContext.setEnvironmentContext(environmentContext)
        );
    }

    private SpreadsheetExpressionEvaluationContext setSpreadsheetContext(final SpreadsheetContext spreadsheetContext) {
        final SpreadsheetContext before = this.spreadsheetContext;;

        return before == spreadsheetContext ?
            this :
            new SpreadsheetExpressionEvaluationContextSpreadsheetContext(
                this.mode,
                this.cell,
                this.spreadsheetExpressionReferenceLoader,
                this.spreadsheetLabelNameResolver,
                null, // spreadsheetConverterContext  clear force recreate!
                this.expressionFunctionProvider,
                this.jsonNodeMarshallContextObjectPostProcessor,
                this.jsonNodeUnmarshallContextPreProcessor,
                null, // re-create SpreadsheetParser
                this.formHandlerContext,
                spreadsheetContext,
                null, // re-create SpreadsheetParserContext
                this.terminalContext
            );
    }

    @Override
    public <T> SpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                          final T value) {
        this.environmentContext()
            .setEnvironmentValue(
                name,
                value
            );
        return this;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext()
            .removeEnvironmentValue(name);
        return this;
    }

    @Override
    public LineEnding lineEnding() {
        return this.environmentContext()
            .lineEnding();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.environmentContext()
            .setLineEnding(lineEnding);
        return this;
    }

    @Override
    public Locale locale() {
        return this.environmentContext()
            .locale();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLocale(final Locale locale) {
        this.environmentContext()
            .setLocale(locale);
        return this;
    }

    @Override
    public LocalDateTime now() {
        return this.environmentContext()
            .now(); // inherit unrelated defaults
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.environmentContext()
            .serverUrl();
    }

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.environmentContext()
            .spreadsheetId();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.environmentContext()
            .setSpreadsheetId(spreadsheetId);
        return this;
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.environmentContext()
            .user();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user) {
        this.environmentContext()
            .setUser(user);
        return this;
    }

    @Override
    public SpreadsheetEnvironmentContext environmentContext() {
        return this.spreadsheetContext;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cell().toString();
    }
}
