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

import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.formula.SpreadsheetParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.text.TextNode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A delegator for {@link SpreadsheetEngineContext}.
 */
public interface SpreadsheetEngineContextDelegator extends SpreadsheetEngineContext,
        ProviderContextDelegator,
        SpreadsheetProviderDelegator {

    @Override
    default SpreadsheetParserToken parseFormula(final TextCursor formula) {
        return this.spreadsheetEngineContext()
                .parseFormula(formula);
    }

    @Override
    default Optional<Expression> toExpression(final SpreadsheetParserToken token) {
        return this.spreadsheetEngineContext()
                .toExpression(token);
    }

    @Override
    default SpreadsheetEngineContext spreadsheetEngineContext(final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases) {
        return this.spreadsheetEngineContext()
                .spreadsheetEngineContext(functionAliases);
    }

    @Override
    default Object evaluate(final Expression expression,
                            final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetEngineContext()
                .evaluate(
                        expression,
                        cell
                );
    }

    @Override
    default boolean evaluateAsBoolean(final Expression expression,
                                      final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetEngineContext()
                .evaluateAsBoolean(
                        expression,
                        cell
                );
    }

    @Override
    default Optional<TextNode> formatValue(final Object value,
                                           final SpreadsheetFormatter formatter) {
        return this.spreadsheetEngineContext()
                .formatValue(
                        value,
                        formatter
                );
    }

    @Override
    default SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                                final Optional<SpreadsheetFormatter> formatter) {
        return this.spreadsheetEngineContext()
                .formatValueAndStyle(
                        cell,
                        formatter
                );
    }

    @Override
    default boolean isPure(final ExpressionFunctionName name) {
        return this.spreadsheetEngineContext()
                .isPure(name);
    }

    @Override
    default LocalDateTime now() {
        return this.spreadsheetEngineContext()
                .now();
    }

    @Override
    default SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetEngineContext()
                .resolveLabel(labelName);
    }

    @Override
    default SpreadsheetCellRange sortCells(final SpreadsheetCellRange cells,
                                           final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators,
                                           final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedFromTo) {
        return this.spreadsheetEngineContext()
                .sortCells(
                        cells,
                        comparators,
                        movedFromTo
                );
    }

    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetEngineContext()
                .spreadsheetMetadata();
    }

    @Override
    default SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetEngineContext()
                .storeRepository();
    }


    SpreadsheetEngineContext spreadsheetEngineContext();

    @Override
    default SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetEngineContext();
    }

    @Override
    default ProviderContext providerContext() {
        return this.spreadsheetEngineContext();
    }
}
