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

package walkingkooka.spreadsheet.store;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetCellStore} that tries to parse any formula text into an {@link Expression} when necessary for any
 * cells that are saved. When cells are loaded, the {@link SpreadsheetFormula} text is updated using the {@link Expression}.
 * Most other methods simply delegate without modification to the wrapped {@link SpreadsheetCellStore}.
 */
final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore implements SpreadsheetCellStore {

    static SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore with(final SpreadsheetCellStore store,
                                                                               final SpreadsheetMetadata metadata,
                                                                               final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                               final LocaleContext localeContext,
                                                                               final ProviderContext providerContext) {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider");
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(providerContext, "providerContext");

        return store instanceof SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore ?
                setMetadata(
                        (SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore) store,
                        metadata,
                        spreadsheetParserProvider,
                        localeContext,
                        providerContext
                ) :
                new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore(
                        store,
                        metadata,
                        spreadsheetParserProvider,
                        localeContext,
                        providerContext
                );
    }

    /**
     * If the {@link SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore} has a different {@link SpreadsheetMetadata}
     * create with the wrapped store and new metadata.
     */
    private static SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore setMetadata(
            final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore store,
            final SpreadsheetMetadata metadata,
            final SpreadsheetParserProvider spreadsheetParserProvider,
            final LocaleContext localeContext,
            final ProviderContext providerContext) {

        return metadata.equals(store.metadata) ?
                store :
                new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore(
                        store.store,
                        metadata,
                        spreadsheetParserProvider,
                        localeContext,
                        providerContext
                );
    }

    private SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore(final SpreadsheetCellStore store,
                                                                           final SpreadsheetMetadata metadata,
                                                                           final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                           final LocaleContext localeContext,
                                                                           final ProviderContext providerContext) {
        this.store = store;
        this.metadata = metadata;
        this.spreadsheetParserProvider = spreadsheetParserProvider;

        this.localeContext = localeContext;
        this.providerContext = providerContext;
    }

    @Override
    public Optional<SpreadsheetCell> load(final SpreadsheetCellReference cellReference) {
        return this.store.load(cellReference).map(this::fixFormulaText);
    }

    // save begin.........................................................................................................s

    @Override
    public SpreadsheetCell save(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");

        return this.fixFormulaText(
                this.store.save(
                        this.ensureFormulaHasToken(cell)
                )
        );
    }

    /**
     * If the {@link SpreadsheetFormula#token()} or {@link SpreadsheetFormula#expression()} are missing, parsing the text
     * and rebuilding the expression is performed. This has the side effect that the value/error will also be cleared.
     */
    private SpreadsheetCell ensureFormulaHasToken(final SpreadsheetCell cell) {
        SpreadsheetFormula formula = cell.formula();
        final String text = formula.text();

        SpreadsheetCell result = cell;
        if (!text.isEmpty()) {
            // any value or error will be lost if token/expression is updated
            SpreadsheetFormulaParserToken token = formula.token()
                    .orElse(null);
            try {
                if (null == token) {
                    token = this.parseFormulaTextExpression(
                            text,
                            cell
                    );
                    formula = formula
                            .setToken(Optional.of(token));
                }
                if (null != token) {
                    formula = formula.setText(token.text());
                    formula = formula.setExpression(
                            token.toExpression(
                                    this.expressionEvaluationContext(cell)
                            )
                    ); // also clears value/error
                }
            } catch (final Exception failed) {
                formula = formula.setValue(
                        Optional.of(
                                SpreadsheetErrorKind.translate(failed)
                        )
                );
            }
            result = cell.setFormula(formula);
        }

        return result;
    }

    /**
     * Parses the formula text into an {@link SpreadsheetFormulaParserToken}.
     */
    private SpreadsheetFormulaParserToken parseFormulaTextExpression(final String text,
                                                                     final SpreadsheetCell cell) {
        final SpreadsheetMetadata metadata = this.metadata;
        final ProviderContext providerContext = this.providerContext;

        return metadata.spreadsheetParser(
                        this.spreadsheetParserProvider,
                        providerContext
                ).parseText(
                        text,
                        metadata.spreadsheetParserContext(
                                Optional.of(cell),
                                this.localeContext,
                                providerContext
                        )
                ).cast(SpreadsheetFormulaParserToken.class);
    }

    // batch............................................................................................................

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                              final SpreadsheetCellRangeReferencePath path,
                                              final int offset,
                                              final int count) {
        return this.store.loadCellRange(
                range,
                path,
                offset,
                count
        );
    }

    @Override
    public void deleteCells(final SpreadsheetCellRangeReference range) {
        this.store.deleteCells(range);
    }

    // watchers.........................................................................................................

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetCell> remover) {
        return this.store.addSaveWatcher(remover);
    }

    @Override
    public void delete(final SpreadsheetCellReference cellReference) {
        this.store.delete(cellReference);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetCellReference> remover) {
        return this.store.addDeleteWatcher(remover);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetCellReference> ids(final int offset,
                                             final int count) {
        return this.store.ids(
                offset,
                count
        );
    }

    @Override
    public List<SpreadsheetCell> values(final int offset,
                                        final int count) {
        return this.fixFormulaTextList(
                this.store.values(
                        offset,
                        count
                )
        );
    }

    @Override
    public List<SpreadsheetCell> between(final SpreadsheetCellReference from,
                                         final SpreadsheetCellReference to) {
        return this.fixFormulaTextList(
                this.store.between(
                        from,
                        to
                )
        );
    }

    @Override
    public int rowCount() {
        return this.store.rowCount();
    }

    @Override
    public int columnCount() {
        return this.store.columnCount();
    }

    @Override
    public Set<SpreadsheetCell> row(final SpreadsheetRowReference row) {
        return this.fixFormulaTextSet(this.store.row(row));
    }

    @Override
    public Set<SpreadsheetCell> column(final SpreadsheetColumnReference column) {
        return this.fixFormulaTextSet(this.store.column(column));
    }

    @Override
    public double maxColumnWidth(final SpreadsheetColumnReference column) {
        return this.store.maxColumnWidth(column);
    }

    @Override
    public double maxRowHeight(final SpreadsheetRowReference row) {
        return this.store.maxRowHeight(row);
    }

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        return this.store.nextEmptyColumn(row);
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        return this.store.nextEmptyRow(column);
    }

    @Override
    public Set<SpreadsheetCell> findCellsWithValueType(final SpreadsheetCellRangeReference range,
                                                       final String valueType,
                                                       final int max) {
        return this.fixFormulaTextSet(
                this.store.findCellsWithValueType(
                        range,
                        valueType,
                        max
                )
        );
    }

    @Override
    public int countCellsWithValueType(final SpreadsheetCellRangeReference range,
                                       final String valueType) {
        return this.store.countCellsWithValueType(
                range,
                valueType
        );
    }

    // helpers that do the formula tokenization/text thing..............................................................

    private List<SpreadsheetCell> fixFormulaTextList(final List<SpreadsheetCell> cells) {
        return cells.stream()
                .map(this::fixFormulaText)
                .collect(Collectors.toCollection(Lists::array));
    }

    private Set<SpreadsheetCell> fixFormulaTextSet(final Set<SpreadsheetCell> cells) {
        return cells.stream()
                .map(this::fixFormulaText)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    private SpreadsheetCell fixFormulaText(final SpreadsheetCell cell) {
        SpreadsheetCell fixed = cell;

        SpreadsheetFormula formula = cell.formula();

        SpreadsheetFormulaParserToken token = formula.token()
                .orElse(null);
        if (null != token) {
            token = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor.update(
                    cell,
                    token,
                    this.metadata,
                    this.providerContext, // HasNow
                    this.localeContext
            );
            final String text = token.text();
            if (!formula.text().equals(text)) {
                // if the text is different update token and expression
                fixed = fixed.setFormula(
                        formula.setText(token.text())
                                .setToken(Optional.of(token))
                                .setExpression(
                                        token.toExpression(
                                                this.expressionEvaluationContext(cell)
                                        )
                                )
                );
            }
        }

        return fixed;
    }

    private ExpressionEvaluationContext expressionEvaluationContext(final SpreadsheetCell cell) {
        return SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreExpressionEvaluationContext.with(
                cell,
                this.metadata,
                this.providerContext, // HasNow
                this.localeContext
        );
    }

    // @VisibleForTesting
    final SpreadsheetCellStore store;

    // @VisibleForTesting
    final SpreadsheetMetadata metadata;

    final SpreadsheetParserProvider spreadsheetParserProvider;

    final LocaleContext localeContext;

    final ProviderContext providerContext;

    @Override
    public String toString() {
        return this.metadata + " " + this.store + " " + this.spreadsheetParserProvider + " " + this.localeContext + " " + this.providerContext;
    }
}
