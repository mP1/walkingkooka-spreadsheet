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
import walkingkooka.convert.CanConvert;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
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
import java.util.function.Function;

final class BasicSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
    EnvironmentContextDelegator,
    SpreadsheetConverterContextDelegator,
    TerminalContextDelegator {

    static BasicSpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                            final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                            final AbsoluteUrl serverUrl,
                                                            final SpreadsheetMetadata spreadsheetMetadata,
                                                            final SpreadsheetStoreRepository spreadsheetStoreRepository,
                                                            final SpreadsheetConverterContext spreadsheetConverterContext,
                                                            final EnvironmentContext environmentContext,
                                                            final Function<Optional<SpreadsheetCell>, SpreadsheetFormatterContext> spreadsheetFormatterContextFactory,
                                                            final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext,
                                                            final TerminalContext terminalContext,
                                                            final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                            final ProviderContext providerContext) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(spreadsheetExpressionReferenceLoader, "spreadsheetExpressionReferenceLoader");
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(spreadsheetStoreRepository, "spreadsheetStoreRepository");
        Objects.requireNonNull(spreadsheetConverterContext, "spreadsheetConverterContext");
        Objects.requireNonNull(environmentContext, "environmentContext");
        Objects.requireNonNull(spreadsheetFormatterContextFactory, "spreadsheetFormatterContextFactory");
        Objects.requireNonNull(formHandlerContext, "formHandlerContext");
        Objects.requireNonNull(terminalContext, "terminalContext");
        Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new BasicSpreadsheetExpressionEvaluationContext(
            cell,
            spreadsheetExpressionReferenceLoader,
            serverUrl,
            spreadsheetMetadata,
            spreadsheetStoreRepository,
            spreadsheetConverterContext,
            environmentContext,
            spreadsheetFormatterContextFactory,
            formHandlerContext,
            terminalContext,
            expressionFunctionProvider,
            providerContext
        );
    }

    private BasicSpreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                        final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                        final AbsoluteUrl serverUrl,
                                                        final SpreadsheetMetadata spreadsheetMetadata,
                                                        final SpreadsheetStoreRepository spreadsheetStoreRepository,
                                                        final SpreadsheetConverterContext spreadsheetConverterContext,
                                                        final EnvironmentContext environmentContext,
                                                        final Function<Optional<SpreadsheetCell>, SpreadsheetFormatterContext> spreadsheetFormatterContextFactory,
                                                        final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext,
                                                        final TerminalContext terminalContext,
                                                        final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                        final ProviderContext providerContext) {
        super();
        this.cell = cell;
        this.spreadsheetExpressionReferenceLoader = spreadsheetExpressionReferenceLoader;
        this.serverUrl = serverUrl;
        this.spreadsheetMetadata = spreadsheetMetadata;

        this.environmentContext = environmentContext;
        this.spreadsheetFormatterContextFactory = spreadsheetFormatterContextFactory;
        this.formHandlerContext = formHandlerContext;
        this.spreadsheetStoreRepository = spreadsheetStoreRepository;

        this.spreadsheetConverterContext = spreadsheetConverterContext;
        this.terminalContext = terminalContext;

        this.expressionFunctionProvider = expressionFunctionProvider;

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

        final SpreadsheetConverterContext context = this.spreadsheetConverterContext;

        final SpreadsheetParserContext parserContext = this.spreadsheetMetadata()
            .spreadsheetParserContext(
                this.cell,
                context,
                context
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

        final SpreadsheetStoreRepository repo = this.spreadsheetStoreRepository;
        this.spreadsheetMetadata = repo.metadatas()
            .save(metadata);
        // TODO maybe should clear parsed cell formulas.
    }

    /**
     * The current {@link SpreadsheetMetadata}. It will be replaced if a {@link #setSpreadsheetMetadata(SpreadsheetMetadata)} happens.
     */
    private SpreadsheetMetadata spreadsheetMetadata;

    @Override
    public AbsoluteUrl serverUrl() {
        return serverUrl;
    }

    private final AbsoluteUrl serverUrl;

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetFormatterContextFactory.apply(cell);
    }

    private final Function<Optional<SpreadsheetCell>, SpreadsheetFormatterContext> spreadsheetFormatterContextFactory;

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
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetStrings.CASE_SENSITIVITY;
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return Cast.to(
            this.expressionFunctionProvider.expressionFunction(
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

    private final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

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

        if (reference instanceof SpreadsheetExpressionReference) {
            SpreadsheetExpressionReference spreadsheetExpressionReference = (SpreadsheetExpressionReference) reference;
            final SpreadsheetSelection selection = this.resolveIfLabel(reference)
                .orElse(null);

            if (null != selection) {
                spreadsheetExpressionReference = selection.toExpressionReference();
            }
            //}
            if (spreadsheetExpressionReference instanceof SpreadsheetExpressionReference) {
                value = BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor.values(
                    (SpreadsheetExpressionReference) reference,
                    this.spreadsheetExpressionReferenceLoader,
                    this
                );
            }
        } else {
            if (reference instanceof EnvironmentValueName) {
                value = Optional.ofNullable(
                    Cast.to(
                        this.environmentValue(
                            (EnvironmentValueName<?>) reference
                        )
                    )
                );
            }
        }

        return value;
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public CanConvert canConvert() {
        return this.spreadsheetConverterContext; // inherit unrelated defaults
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetConverterContext;
    }

    private final SpreadsheetConverterContext spreadsheetConverterContext;

    // EnvironmentContext...............................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        final EnvironmentContext context = this.environmentContext;
        final EnvironmentContext cloned = context.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return context == cloned ?
            this :
            new BasicSpreadsheetExpressionEvaluationContext(
                this.cell,
                this.spreadsheetExpressionReferenceLoader,
                this.serverUrl,
                this.spreadsheetMetadata,
                this.spreadsheetStoreRepository,
                this.spreadsheetConverterContext,
                Objects.requireNonNull(cloned, "environmentContext"),
                this.spreadsheetFormatterContextFactory,
                this.formHandlerContext,
                this.terminalContext,
                this.expressionFunctionProvider,
                this.providerContext
            );
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
    public LocalDateTime now() {
        return this.environmentContext.now(); // inherit unrelated defaults
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
        return (SpreadsheetDelta)
            this.formHandlerContext.saveFormFieldValues(formFields);
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
    public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetConverterContext before = this.spreadsheetConverterContext;
        final SpreadsheetConverterContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicSpreadsheetExpressionEvaluationContext(
                this.cell,
                this.spreadsheetExpressionReferenceLoader,
                this.serverUrl,
                this.spreadsheetMetadata,
                this.spreadsheetStoreRepository,
                Objects.requireNonNull(
                    after,
                    "spreadsheetConverterContext.setProcessor returned null"
                ),
                this.environmentContext,
                this.spreadsheetFormatterContextFactory,
                this.formHandlerContext,
                this.terminalContext,
                this.expressionFunctionProvider,
                this.providerContext
            );
    }

    // TerminalContextDelegator.........................................................................................

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
