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

package walkingkooka.spreadsheet.engine;

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

/**
 * A {@link SpreadsheetEngineContext} used by {@link BasicSpreadsheetEngine} delegating all methods except for the evaluateXXX
 * methods using an existing
 */
final class BasicSpreadsheetEngineSpreadsheetEngineContext implements SpreadsheetEngineContext,
        SpreadsheetProviderDelegator,
        ProviderContextDelegator {

    static BasicSpreadsheetEngineSpreadsheetEngineContext with(final SpreadsheetEngineContext spreadsheetEngineContext,
                                                               final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext) {
        return new BasicSpreadsheetEngineSpreadsheetEngineContext(
                spreadsheetEngineContext,
                spreadsheetExpressionEvaluationContext
        );
    }

    private BasicSpreadsheetEngineSpreadsheetEngineContext(final SpreadsheetEngineContext spreadsheetEngineContext,
                                                           final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext) {
        this.spreadsheetEngineContext = spreadsheetEngineContext;
        this.spreadsheetExpressionEvaluationContext = spreadsheetExpressionEvaluationContext;
    }

    // SpreadsheetEngineContext.........................................................................................

    @Override
    public AbsoluteUrl serverUrl() {
        return this.spreadsheetEngineContext.serverUrl();
    }

    @Override
    public SpreadsheetEngineContext spreadsheetEngineContext(final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases) {
        return with(
                this.spreadsheetEngineContext.spreadsheetEngineContext(functionAliases),
                this.spreadsheetExpressionEvaluationContext
        );
    }

    private final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext;

    @Override
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula,
                                                      final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetEngineContext.parseFormula(
                formula,
                cell
        );
    }

    @Override
    public Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
        return this.spreadsheetEngineContext.toExpression(token);
    }

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
        return this.spreadsheetEngineContext.spreadsheetExpressionEvaluationContext(
                cell,
                loader
        );
    }

    @Override
    public Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                          final Optional<Object> value,
                                          final SpreadsheetFormatter formatter) {
        return this.spreadsheetEngineContext.formatValue(
                cell,
                value,
                formatter
        );
    }

    @Override
    public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                               final Optional<SpreadsheetFormatter> formatter) {
        return this.spreadsheetEngineContext.formatValueAndStyle(
                cell,
                formatter
        );
    }

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetEngineContext.storeRepository();
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetEngineContext.spreadsheetMetadata();
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetEngineContext.resolveLabel(labelName);
    }

    @Override
    public boolean isPure(final ExpressionFunctionName expressionFunctionName) {
        return this.spreadsheetEngineContext.isPure(expressionFunctionName);
    }

    // SpreadsheetProviderDelegator.....................................................................................

    @Override
    public SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetEngineContext;
    }

    // ProviderContextDelegator.........................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.spreadsheetEngineContext;
    }

    private final SpreadsheetEngineContext spreadsheetEngineContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetEngineContext.toString();
    }
}
