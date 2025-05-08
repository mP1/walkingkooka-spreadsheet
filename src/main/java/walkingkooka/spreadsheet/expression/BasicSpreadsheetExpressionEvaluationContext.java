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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
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
import walkingkooka.storage.StorageStore;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.validation.form.FormHandlerContext;
import walkingkooka.validation.form.FormHandlerContextDelegator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class BasicSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
        FormHandlerContextDelegator<SpreadsheetExpressionReference, SpreadsheetDelta>,
        SpreadsheetConverterContextDelegator {

    static BasicSpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                            final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                            final AbsoluteUrl serverUrl,
                                                            final SpreadsheetMetadata spreadsheetMetadata,
                                                            final SpreadsheetStoreRepository spreadsheetStoreRepository,
                                                            final SpreadsheetConverterContext spreadsheetConverterContext,
                                                            final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext,
                                                            final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                            final ProviderContext providerContext) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(spreadsheetExpressionReferenceLoader, "spreadsheetExpressionReferenceLoader");
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(spreadsheetStoreRepository, "spreadsheetStoreRepository");
        Objects.requireNonNull(spreadsheetConverterContext, "spreadsheetConverterContext");
        Objects.requireNonNull(formHandlerContext, "formHandlerContext");
        Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new BasicSpreadsheetExpressionEvaluationContext(
                cell,
                spreadsheetExpressionReferenceLoader,
                serverUrl,
                spreadsheetMetadata,
                spreadsheetStoreRepository,
                spreadsheetConverterContext,
                formHandlerContext,
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
                                                        final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext,
                                                        final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                        final ProviderContext providerContext) {
        super();
        this.cell = cell;
        this.spreadsheetExpressionReferenceLoader = spreadsheetExpressionReferenceLoader;
        this.serverUrl = serverUrl;
        this.spreadsheetMetadata = spreadsheetMetadata;

        this.formHandlerContext = formHandlerContext;
        this.spreadsheetStoreRepository = spreadsheetStoreRepository;

        this.spreadsheetConverterContext = spreadsheetConverterContext;
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

        final SpreadsheetParserContext parserContext = this.spreadsheetMetadata()
                .spreadsheetParserContext(this.spreadsheetConverterContext);

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
        if(false == oldId.equals(newId)) {
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
    public Optional<Object> validationValue() {
        return this.reference(
                SpreadsheetSelection.labelName("VALUE")
        ).orElse(Optional.empty());
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public StorageStore storage() {
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

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        Optional<Optional<Object>> value = Optional.empty();

        ExpressionReference expressionReference = reference;
        if (expressionReference instanceof SpreadsheetExpressionReference) {
            expressionReference = this.resolveIfLabel(
                    (SpreadsheetExpressionReference)expressionReference
            ).toExpressionReference();
        }
        if (expressionReference instanceof SpreadsheetExpressionReference) {
            value = BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor.values(
                    (SpreadsheetExpressionReference) reference,
                    this.spreadsheetExpressionReferenceLoader,
                    this
            );
        }

        return value;
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public CanConvert canConvert() {
        return this.spreadsheetConverterContext; // inherit unrelated defaults
    }

    @Override
    public LocalDateTime now() {
        return this.spreadsheetConverterContext.now(); // inherit unrelated defaults
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetConverterContext;
    }

    private final SpreadsheetConverterContext spreadsheetConverterContext;

    /**
     * ProviderContext required by the numerous {@link walkingkooka.plugin.Provider providers}
     */
    private final ProviderContext providerContext;

    // FormHandlerContext...............................................................................................

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return Cast.to(
                this.formHandlerContext.validatorContext(reference)
        );
    }

    @Override
    public FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext() {
        return this.formHandlerContext;
    }

    private final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cell().toString();
    }
}
