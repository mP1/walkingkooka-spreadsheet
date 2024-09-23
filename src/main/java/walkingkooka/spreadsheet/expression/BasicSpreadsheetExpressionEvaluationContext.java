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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class BasicSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext,
        SpreadsheetConverterContextDelegator {

    static BasicSpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                            final SpreadsheetCellStore cellStore,
                                                            final AbsoluteUrl serverUrl,
                                                            final Function<ExpressionReference, Optional<Optional<Object>>> references,
                                                            final SpreadsheetMetadata spreadsheetMetadata,
                                                            final SpreadsheetConverterContext spreadsheetConverterContext,
                                                            final ExpressionFunctionProvider expressionFunctionProvider,
                                                            final ProviderContext providerContext) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(references, "references");
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(spreadsheetConverterContext, "spreadsheetConverterContext");
        Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new BasicSpreadsheetExpressionEvaluationContext(
                cell,
                cellStore,
                serverUrl,
                references,
                spreadsheetMetadata,
                spreadsheetConverterContext,
                expressionFunctionProvider,
                providerContext
        );
    }

    private BasicSpreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                        final SpreadsheetCellStore cellStore,
                                                        final AbsoluteUrl serverUrl,
                                                        final Function<ExpressionReference, Optional<Optional<Object>>> references,
                                                        final SpreadsheetMetadata spreadsheetMetadata,
                                                        final SpreadsheetConverterContext spreadsheetConverterContext,
                                                        final ExpressionFunctionProvider expressionFunctionProvider,
                                                        final ProviderContext providerContext) {
        super();
        this.cell = cell;
        this.cellStore = cellStore;
        this.serverUrl = serverUrl;
        this.references = references;

        this.spreadsheetMetadata = spreadsheetMetadata;

        this.spreadsheetConverterContext = spreadsheetConverterContext;

        this.expressionFunctionProvider = expressionFunctionProvider;
        this.providerContext = providerContext;
    }

    // SpreadsheetExpressionEvaluationContext............................................................................

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCell> cell;

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cellReference) {
        Objects.requireNonNull(cellReference, "cellReference");

        Optional<SpreadsheetCell> loaded;

        for (; ; ) {
            Optional<SpreadsheetCell> maybeCell = this.cell();
            if (maybeCell.isPresent()) {
                final SpreadsheetCell cell = maybeCell.get();
                if (cell.reference().equalsIgnoreReferenceKind(cellReference)) {
                    loaded = maybeCell;
                    break;
                }
            }

            loaded = this.cellStore.load(cellReference);
            break;
        }

        return loaded;
    }

    private final SpreadsheetCellStore cellStore;

    @Override
    public SpreadsheetParserToken parseFormula(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetParserContext parserContext = this.spreadsheetMetadata()
                .parserContext(this.spreadsheetConverterContext::now);

        return SpreadsheetParsers.expression()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(expression, parserContext)
                .get()
                .cast(SpreadsheetParserToken.class);
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
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public Object evaluate(final Expression expression) {
        Objects.requireNonNull(expression, "expression");

        Object result;

        try {
            result = expression.toValue(this);
        } catch (final RuntimeException exception) {
            result = this.handleException(exception);
        }

        return result;
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
    public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                   final List<Object> parameters) {
        return function
                .apply(
                        this.prepareParameters(function, parameters),
                        Cast.to(this)
                );
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        return SpreadsheetErrorKind.translate(exception);
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        return this.references.apply(reference);
    }

    private final Function<ExpressionReference, Optional<Optional<Object>>> references;

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
