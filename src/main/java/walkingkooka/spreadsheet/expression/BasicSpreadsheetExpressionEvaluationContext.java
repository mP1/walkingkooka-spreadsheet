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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class BasicSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
        SpreadsheetConverterContextDelegator {

    static BasicSpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                            final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                            final AbsoluteUrl serverUrl,
                                                            final SpreadsheetMetadata spreadsheetMetadata,
                                                            final SpreadsheetConverterContext spreadsheetConverterContext,
                                                            final ExpressionFunctionProvider expressionFunctionProvider,
                                                            final ProviderContext providerContext) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(spreadsheetExpressionReferenceLoader, "spreadsheetExpressionReferenceLoader");
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(spreadsheetConverterContext, "spreadsheetConverterContext");
        Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new BasicSpreadsheetExpressionEvaluationContext(
                cell,
                spreadsheetExpressionReferenceLoader,
                serverUrl,
                spreadsheetMetadata,
                spreadsheetConverterContext,
                expressionFunctionProvider,
                providerContext
        );
    }

    private BasicSpreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                        final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                        final AbsoluteUrl serverUrl,
                                                        final SpreadsheetMetadata spreadsheetMetadata,
                                                        final SpreadsheetConverterContext spreadsheetConverterContext,
                                                        final ExpressionFunctionProvider expressionFunctionProvider,
                                                        final ProviderContext providerContext) {
        super();
        this.cell = cell;
        this.spreadsheetExpressionReferenceLoader = spreadsheetExpressionReferenceLoader;
        this.serverUrl = serverUrl;
        this.spreadsheetMetadata = spreadsheetMetadata;
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
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetMetadata;
    }

    private final SpreadsheetMetadata spreadsheetMetadata;

    @Override
    public AbsoluteUrl serverUrl() {
        return serverUrl;
    }

    private final AbsoluteUrl serverUrl;

    // ExpressionEvaluationContext......................................................................................

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetStrings.CASE_SENSITIVITY;
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return this.expressionFunctionProvider.expressionFunction(
                name,
                Lists.empty(),
                this.providerContext
        );
    }

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
        return this.expressionFunction(name)
                .isPure(this);
    }

    private final ExpressionFunctionProvider expressionFunctionProvider;

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
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetConverterContext;
    }

    private final SpreadsheetConverterContext spreadsheetConverterContext;

    /**
     * ProviderContext required by the numerous {@link walkingkooka.plugin.Provider providers}
     */
    private final ProviderContext providerContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cell().toString();
    }
}
