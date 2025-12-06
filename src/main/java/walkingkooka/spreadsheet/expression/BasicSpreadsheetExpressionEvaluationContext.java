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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContextMode;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
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
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
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
final class BasicSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
    EnvironmentContextDelegator,
    SpreadsheetConverterContextDelegator,
    TerminalContextDelegator {

    static BasicSpreadsheetExpressionEvaluationContext with(final AbsoluteUrl serverUrl,
                                                            final SpreadsheetMetadata spreadsheetMetadata,
                                                            final SpreadsheetEngineContextMode mode,
                                                            final SpreadsheetStoreRepository spreadsheetStoreRepository,
                                                            final EnvironmentContext environmentContext,
                                                            final Optional<SpreadsheetCell> cell,
                                                            final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                            final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                            final LocaleContext localeContext,
                                                            final TerminalContext terminalContext,
                                                            final SpreadsheetProvider spreadsheetProvider,
                                                            final ProviderContext providerContext) {
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(spreadsheetStoreRepository, "spreadsheetStoreRepository");
        Objects.requireNonNull(environmentContext, "environmentContext");
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(spreadsheetExpressionReferenceLoader, "spreadsheetExpressionReferenceLoader");
        Objects.requireNonNull(spreadsheetLabelNameResolver, "spreadsheetLabelNameResolver");
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(terminalContext, "terminalContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new BasicSpreadsheetExpressionEvaluationContext(
            serverUrl,
            spreadsheetMetadata,
            mode,
            spreadsheetStoreRepository,
            environmentContext,
            cell,
            spreadsheetExpressionReferenceLoader,
            spreadsheetLabelNameResolver,
            null, // SpreadsheetConverterContext
            null, // formHandlerContext
            null, // JsonNodeMarshallContextObjectPostProcessor
            null, // JsonNodeUnmarshallContextPreProcessor
            localeContext,
            terminalContext,
            spreadsheetProvider,
            null, // ExpressionFunctionProvider
            providerContext
        );
    }

    private BasicSpreadsheetExpressionEvaluationContext(final AbsoluteUrl serverUrl,
                                                        final SpreadsheetMetadata spreadsheetMetadata,
                                                        final SpreadsheetEngineContextMode mode,
                                                        final SpreadsheetStoreRepository spreadsheetStoreRepository,
                                                        final EnvironmentContext environmentContext,
                                                        final Optional<SpreadsheetCell> cell,
                                                        final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                        final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                        final SpreadsheetConverterContext spreadsheetConverterContext,
                                                        final JsonNodeMarshallContextObjectPostProcessor jsonNodeMarshallContextObjectPostProcessor,
                                                        final JsonNodeUnmarshallContextPreProcessor jsonNodeUnmarshallContextPreProcessor,
                                                        final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext,
                                                        final LocaleContext localeContext,
                                                        final TerminalContext terminalContext,
                                                        final SpreadsheetProvider spreadsheetProvider,
                                                        final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                        final ProviderContext providerContext) {
        super();
        this.serverUrl = serverUrl;
        this.spreadsheetMetadata = spreadsheetMetadata;
        this.mode = mode;

        this.spreadsheetStoreRepository = spreadsheetStoreRepository;

        this.environmentContext = environmentContext;

        this.cell = cell;
        this.spreadsheetExpressionReferenceLoader = spreadsheetExpressionReferenceLoader;
        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;

        this.spreadsheetConverterContext = spreadsheetConverterContext; // may be null
        this.jsonNodeMarshallContextObjectPostProcessor = jsonNodeMarshallContextObjectPostProcessor;
        this.jsonNodeUnmarshallContextPreProcessor = jsonNodeUnmarshallContextPreProcessor;

        this.formHandlerContext = formHandlerContext;
        this.localeContext = localeContext;
        this.terminalContext = terminalContext;

        this.expressionFunctionProvider = expressionFunctionProvider; // may be null
        this.spreadsheetProvider = spreadsheetProvider;

        this.providerContext = providerContext;
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
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetParserContext parserContext = this.spreadsheetMetadata()
            .spreadsheetParserContext(
                this.cell,
                this.localeContext,
                this.environmentContext
            );

        return SpreadsheetFormulaParsers.expression()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(expression, parserContext)
            .get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        return this.spreadsheetStoreRepository.cells()
            .nextEmptyColumn(row);
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        return this.spreadsheetStoreRepository.cells()
            .nextEmptyRow(column);
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetMetadata;
    }

    @Override
    public void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        final SpreadsheetMetadata old = this.spreadsheetMetadata;
        final SpreadsheetId oldId = old.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);
        final SpreadsheetId newId = metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);
        if (false == oldId.equals(newId)) {
            throw new IllegalArgumentException("Invalid metadata id " + newId + " is different from " + oldId);
        }

        this.spreadsheetMetadata = this.spreadsheetStoreRepository.metadatas()
            .save(metadata);
        // TODO maybe should clear parsed cell formulas.

        this.spreadsheetConverterContext = null;
        this.expressionFunctionProvider = null;
    }

    /**
     * The current {@link SpreadsheetMetadata}. It will be replaced if a {@link #setSpreadsheetMetadata(SpreadsheetMetadata)} happens.
     */
    private SpreadsheetMetadata spreadsheetMetadata;

    private BasicSpreadsheetExpressionEvaluationContext setMode(final SpreadsheetEngineContextMode mode) {
        return this.mode.equals(mode) ?
            this :
            new BasicSpreadsheetExpressionEvaluationContext(
                this.serverUrl,
                this.spreadsheetMetadata,
                Objects.requireNonNull(mode, "mode"),
                this.spreadsheetStoreRepository,
                this.environmentContext,
                this.cell,
                this.spreadsheetExpressionReferenceLoader,
                this.spreadsheetLabelNameResolver,
                null, // spreadsheetConverterContext  clear force recreate!
                this.jsonNodeMarshallContextObjectPostProcessor,
                this.jsonNodeUnmarshallContextPreProcessor,
                this.formHandlerContext,
                this.localeContext,
                this.terminalContext,
                this.spreadsheetProvider,
                null, // expressionFunctionProvider clear force recreate
                this.providerContext
            );
    }

    private final SpreadsheetEngineContextMode mode;

    @Override
    public LocaleContext localeContext() {
        return this.localeContext; // short circuit SpreadsheetConverterContextDelegator
    }

    private final LocaleContext localeContext;

    /**
     * Lazily created {@link ExpressionFunctionProvider}, should be nulled whenever the {@link SpreadsheetMetadata} changes.
     */
    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider() {
        if(null == this.expressionFunctionProvider) {
            this.expressionFunctionProvider = this.spreadsheetMetadata.expressionFunctionProvider(
                this.mode.function(),
                this.spreadsheetProvider
            );
        }
        return this.expressionFunctionProvider;
    }

    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    private final SpreadsheetProvider spreadsheetProvider;

    @Override
    public AbsoluteUrl serverUrl() {
        return serverUrl;
    }

    private final AbsoluteUrl serverUrl;

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetMetadata.spreadsheetFormatterContext(
            cell,
            (final Optional<Object> v) -> this.setMode(
                SpreadsheetEngineContextMode.FORMATTING
            ).addLocalVariable(
                SpreadsheetExpressionEvaluationContext.FORMAT_VALUE,
                v
            ),
            this.spreadsheetLabelNameResolver,
            this.localeContext,
            this.spreadsheetProvider,
            this.providerContext
        );
    }


    @Override
    public Optional<Object> validationValue() {
        return this.reference(VALIDATION_VALUE)
            .orElse(Optional.empty());
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public Storage<StorageExpressionEvaluationContext> storage() {
        return this.spreadsheetStoreRepository.storage();
    }

    private final SpreadsheetStoreRepository spreadsheetStoreRepository;

    // ExpressionEvaluationContext......................................................................................

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return Cast.to(
            this.expressionFunctionProvider()
                .expressionFunction(
                    name,
                    Lists.empty(),
                    this.providerContext
                )
        );
    }

    private final ProviderContext providerContext;

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
                    value = BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor.values(
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
        if(null == this.spreadsheetConverterContext) {
            this.spreadsheetConverterContext = this.spreadsheetMetadata.spreadsheetConverterContext(
                this.cell,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                this.mode.converter(),
                this.spreadsheetLabelNameResolver,
                this.spreadsheetProvider, // SpreadsheetConverterProvider
                this.localeContext,
                this.providerContext
            );
        }
        return this.spreadsheetConverterContext;
    }

    /**
     * Lazily created using {@link #mode} to select a {@link SpreadsheetMetadata}.
     */
    private SpreadsheetConverterContext spreadsheetConverterContext;

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
        return new BasicSpreadsheetExpressionEvaluationContext(
            this.serverUrl,
            this.spreadsheetMetadata,
            this.mode,
            this.spreadsheetStoreRepository,
            this.environmentContext,
            this.cell,
            this.spreadsheetExpressionReferenceLoader,
            this.spreadsheetLabelNameResolver,
            context,
            jsonNodeMarshallContextObjectPostProcessor,
            jsonNodeUnmarshallContextPreProcessor,
            this.formHandlerContext,
            this.localeContext,
            this.terminalContext,
            this.spreadsheetProvider,
            this.expressionFunctionProvider,
            this.providerContext
        );
    }

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.environmentContext.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        return this.environmentContext == environmentContext ?
            this :
            new BasicSpreadsheetExpressionEvaluationContext(
                this.serverUrl,
                this.spreadsheetMetadata,
                this.mode,
                this.spreadsheetStoreRepository,
                Objects.requireNonNull(environmentContext, "environmentContext"),
                this.cell,
                this.spreadsheetExpressionReferenceLoader,
                this.spreadsheetLabelNameResolver,
                null, // spreadsheetConverterContext  clear force recreate!
                this.jsonNodeMarshallContextObjectPostProcessor,
                this.jsonNodeUnmarshallContextPreProcessor,
                this.formHandlerContext,
                this.localeContext,
                this.terminalContext,
                this.spreadsheetProvider,
                this.expressionFunctionProvider,
                this.providerContext
            );
    }

    @Override
    public LineEnding lineEnding() {
        return this.environmentContext.lineEnding();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.environmentContext.setLineEnding(lineEnding);
        return this;
    }
    
    @Override
    public Locale locale() {
        return this.environmentContext.locale();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setLocale(final Locale locale) {
        this.environmentContext.setLocale(locale);
        return this;
    }

    @Override
    public LocalDateTime now() {
        return this.environmentContext.now(); // inherit unrelated defaults
    }

    @Override
    public Optional<EmailAddress> user() {
        return this.environmentContext.user();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user) {
        this.environmentContext.setUser(user);
        return this;
    }

    @Override
    public <T> SpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                          final T value) {
        this.environmentContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

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

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cell().toString();
    }
}
