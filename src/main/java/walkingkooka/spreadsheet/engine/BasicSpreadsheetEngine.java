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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetColumnOrRow;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelectionNavigation;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * The default or basic implementation of {@link SpreadsheetEngine} that includes support for evaluating nodes,
 * when they are refreshed and not when they are set.
 */
final class BasicSpreadsheetEngine implements SpreadsheetEngine {

    /**
     * Factory that creates a new {@link BasicSpreadsheetEngine}
     */
    static BasicSpreadsheetEngine with(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        return new BasicSpreadsheetEngine(
                metadata
        );
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine(final SpreadsheetMetadata metadata) {
        this.metadata = metadata;
    }

    // LOAD CELL........................................................................................................

    /**
     * Loads the cell honouring the {@link SpreadsheetEngineEvaluation} which may result in loading and evaluating other cells.
     */
    @Override
    public SpreadsheetDelta loadCell(final SpreadsheetCellReference reference,
                                     final SpreadsheetEngineEvaluation evaluation,
                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(reference, "reference");
        checkEvaluation(evaluation);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            this.loadCell0(reference, evaluation, changes, context);
            return this.prepareDelta(
                    changes,
                    reference.cellRange(),
                    context
            );
        }
    }

    void loadCell0(final SpreadsheetCellReference reference,
                   final SpreadsheetEngineEvaluation evaluation,
                   final BasicSpreadsheetEngineChanges changes,
                   final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetCell> loaded = context.storeRepository()
                .cells()
                .load(reference);
        loaded.map(c -> {
            final SpreadsheetCell evaluated = this.maybeParseAndEvaluateAndFormat(c, evaluation, context);
            changes.onLoad(evaluated); // might have just loaded a cell without any updates but want to record cell.
            return evaluated;
        });
        changes.refreshUpdated();
    }

    // SAVE CELL........................................................................................................

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            this.maybeParseAndEvaluateAndFormat(cell,
                    SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                    context);
            changes.refreshUpdated();
            return this.prepareDelta(
                    changes,
                    cell.reference().cellRange(),
                    context
            );
        }
    }

    // DELETE CELL....................................................................................................

    /**
     * DELETE the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta deleteCell(final SpreadsheetCellReference reference,
                                       final SpreadsheetEngineContext context) {
        checkReference(reference);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            context.storeRepository()
                    .cells()
                    .delete(reference);
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    @Override
    public SpreadsheetDelta loadColumn(final SpreadsheetColumnReference column,
                                       final SpreadsheetEngineContext context) {
        return SpreadsheetDelta.EMPTY
                .setColumns(
                        toSet(
                                context.storeRepository()
                                        .columns()
                                        .load(column)
                        )
                );
    }

    // SAVE COLUMN.....................................................................................................

    /**
     * Saves the {@link SpreadsheetColumn} and then loads and saves all the cells in that column.
     */
    @Override
    public SpreadsheetDelta saveColumn(final SpreadsheetColumn column,
                                       final SpreadsheetEngineContext context) {
        Objects.requireNonNull(column, "column");
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            final SpreadsheetStoreRepository repo = context.storeRepository();
            repo.columns()
                    .save(column);

            // load cells in column and save them again, this will re-evaluate as necessary.
            final SpreadsheetCellStore cells = repo.cells();
            for (final SpreadsheetCell cell : cells.column(column.reference())) {
                this.maybeParseAndEvaluateAndFormat(
                        cell,
                        SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                        context
                );
            }

            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    // LOAD ROW.....................................................................................................
    @Override
    public SpreadsheetDelta loadRow(final SpreadsheetRowReference row,
                                    final SpreadsheetEngineContext context) {
        return SpreadsheetDelta.EMPTY
                .setRows(
                        toSet(
                                context.storeRepository()
                                        .rows()
                                        .load(row)
                        )
                );
    }

    // SAVE ROW.....................................................................................................

    /**
     * Saves the {@link SpreadsheetRow} and then loads and saves all the cells in that row.
     */
    @Override
    public SpreadsheetDelta saveRow(final SpreadsheetRow row,
                                    final SpreadsheetEngineContext context) {
        Objects.requireNonNull(row, "row");
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            final SpreadsheetStoreRepository repo = context.storeRepository();
            repo.rows()
                    .save(row);

            // load cells in row and save them again, this will re-evaluate as necessary.
            final SpreadsheetCellStore cells = repo.cells();
            for (final SpreadsheetCell cell : cells.row(row.reference())) {
                this.maybeParseAndEvaluateAndFormat(
                        cell,
                        SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                        context
                );
            }

            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    // DELETE / INSERT / COLUMN / ROW ..................................................................................

    @Override
    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        checkColumn(column);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn.with(column.value(), count, this, context)
                    .delete();
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    @Override
    public SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        checkRow(row);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow.with(row.value(), count, this, context)
                    .delete();
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    @Override
    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        checkColumn(column);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn.with(column.value(), count,
                            this,
                            context)
                    .insert();
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    @Override
    public SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        checkRow(row);
        checkCount(count);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, context)) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow.with(row.value(), count, this, context)
                    .insert();
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    private static void checkCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    @Override
    public SpreadsheetDelta loadCells(final SpreadsheetCellRange range,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(range, "range");
        checkEvaluation(evaluation);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            final SpreadsheetCellStore store = context.storeRepository()
                    .cells();

            range.cellStream()
                    .forEach(reference -> {
                        if (!changes.isLoaded(reference)) {
                            final Optional<SpreadsheetCell> loaded = store.load(reference);
                            if (loaded.isPresent()) {
                                final SpreadsheetCell evaluated = this.maybeParseAndEvaluateAndFormat(loaded.get(), evaluation, context);
                                changes.onLoad(evaluated); // might have just loaded a cell without any updates but want to record cell.
                                    }
                                }
                            }
                    );

            changes.refreshUpdated();
            return this.prepareDelta(
                    changes,
                    range,
                    context
            ).setWindow(Optional.of(range));
        }
    }

    @Override
    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                      final SpreadsheetCellRange from,
                                      final SpreadsheetCellRange to,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, context)) {
            BasicSpreadsheetEngineFillCells.execute(cells, from, to, this, context);
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    private SpreadsheetDelta prepareDelta(final BasicSpreadsheetEngineChanges changes,
                                          final SpreadsheetEngineContext context) {
        return this.prepareDelta(
                changes,
                null,
                context
        );
    }

    /**
     * Creates a {@link SpreadsheetDelta} to hold the given cells and then queries to fetch the labels for those cells.
     */
    private SpreadsheetDelta prepareDelta(final BasicSpreadsheetEngineChanges changes,
                                          final SpreadsheetCellRange window,
                                          final SpreadsheetEngineContext context) {
        final Set<SpreadsheetCell> updatedCells = changes.updatedCells();
        final Set<SpreadsheetColumn> updatedColumns = changes.updatedColumns();
        final Set<SpreadsheetRow> updatedRows = changes.updatedRows();

        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setColumns(updatedColumns)
                .setRows(updatedRows);

        final SpreadsheetStoreRepository repo = context.storeRepository();

        final Map<SpreadsheetColumnReference, SpreadsheetColumn> columns = Maps.sorted();
        final SpreadsheetColumnStore columnStore = repo.columns();

        for (final SpreadsheetColumn column : updatedColumns) {
            final SpreadsheetColumnReference columnReference = column.reference();

            addIfNecessary(
                    columnReference,
                    columns,
                    columnStore
            );
        }

        final Map<SpreadsheetRowReference, SpreadsheetRow> rows = Maps.sorted();
        final SpreadsheetRowStore rowStore = repo.rows();

        for (final SpreadsheetRow row : updatedRows) {
            final SpreadsheetRowReference rowReference = row.reference();

            addIfNecessary(
                    rowReference,
                    rows,
                    rowStore
            );
        }

        final Set<SpreadsheetLabelMapping> labels = Sets.sorted();
        final SpreadsheetLabelStore labelStore = repo.labels();

        // record columns and rows for updated cells...
        for (final SpreadsheetCell cell : updatedCells) {
            final SpreadsheetCellReference cellReference = cell.reference();

            addIfNecessary(
                    cellReference.column(),
                    columns,
                    columnStore
            );

            addIfNecessary(
                    cellReference.row(),
                    rows,
                    rowStore
            );

            addLabels(
                    cell.reference(),
                    labelStore,
                    labels
            );
        }

        // add labels within the range of the given window.
        if (null != window) {
            final Set<SpreadsheetCellReference> cellReferences = Sets.hash();

            // include all columns and rows within the window.
            window.cellStream()
                    .forEach(c -> {
                        if (cellReferences.add(c)) {
                            addIfNecessary(
                                    c.column(),
                                    columns,
                                    columnStore
                            );

                            addIfNecessary(
                                    c.row(),
                                    rows,
                                    rowStore
                            );

                            addLabels(c, labelStore, labels);
                        }
                    });
        }

        // load columns and rows for the deleted cells.
        final Set<SpreadsheetCellReference> deletedCells = changes.deletedCells();

        for (final SpreadsheetCellReference deletedCell : deletedCells) {
            addIfNecessary(
                    deletedCell.column(),
                    columns,
                    columnStore
            );

            addIfNecessary(
                    deletedCell.row(),
                    rows,
                    rowStore
            );
        }

        return delta
                .setColumns(sortedSet(columns)) // order is important because labels and cells for hidden columns/rows are filtered.
                .setRows(sortedSet(rows))
                .setCells(updatedCells)
                .setLabels(labels)
                .setDeletedCells(deletedCells)
                .setDeletedColumns(changes.deletedColumns())
                .setDeletedRows(changes.deletedRows());
    }

    private <R extends SpreadsheetColumnOrRowReference & Comparable<R>, H extends HasSpreadsheetReference<R>> void addIfNecessary(final R reference,
                                                                                                                                  final Map<R, H> referenceToHas,
                                                                                                                                  final SpreadsheetStore<R, H> store) {
        if (!referenceToHas.containsKey(reference)) {
            referenceToHas.put(
                    reference,
                    store.load(reference)
                            .orElse(null)
            );
        }
    }

    private static void addLabels(final SpreadsheetCellReference reference,
                                  final SpreadsheetLabelStore store,
                                  final Set<SpreadsheetLabelMapping> all) {
        for (final SpreadsheetLabelName label : store.labels(reference)) {
            all.add(label.mapping(reference));
        }
    }

    private static <T> Set<T> sortedSet(final Map<?, T> columnsOrRows) {
        final Set<T> set = Sets.sorted();

        for (final T value : columnsOrRows.values()) {
            // not all columns or rows have a SpreadsheetColumn or SpreadsheetRow with values.
            if (null != value) {
                set.add(value);
            }
        }

        return set;
    }

    // labels............................................................................................................

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        checkMapping(mapping);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            context.storeRepository()
                    .labels()
                    .save(mapping);
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    @Override
    public SpreadsheetDelta removeLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        checkLabel(label);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            context.storeRepository()
                    .labels()
                    .delete(label);
            changes.refreshUpdated();
            return this.prepareDelta(changes, context);
        }
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName label,
                                                       final SpreadsheetEngineContext context) {
        checkLabel(label);
        checkContext(context);

        return context.storeRepository()
                .labels()
                .load(label);
    }

    // cell eval........................................................................................................

    /**
     * Attempts to evaluate the cell, parsing and evaluating as necessary depending on the {@link SpreadsheetEngineEvaluation}
     */
    SpreadsheetCell maybeParseAndEvaluateAndFormat(final SpreadsheetCell cell,
                                                   final SpreadsheetEngineEvaluation evaluation,
                                                   final SpreadsheetEngineContext context) {
        final SpreadsheetCell result = evaluation.formulaEvaluateAndStyle(cell, this, context);
        context.storeRepository()
                .cells()
                .save(result); // update cells enabling caching of parsing and value and errors.
        return result;
    }

    SpreadsheetCell formulaEvaluateAndStyle(final SpreadsheetCell cell,
                                            final SpreadsheetEngineContext context) {
        return this.formatAndApplyStyle(
                cell.setFormula(
                        this.parseFormulaAndEvaluate(
                                cell,
                                context)
                ),
                context);
    }

    private SpreadsheetFormula parseFormulaAndEvaluate(final SpreadsheetCell cell,
                                                       final SpreadsheetEngineContext context) {
        return this.evaluateIfPossible(
                cell.setFormula(
                        this.parseFormulaIfNecessary(
                                cell,
                                Function.identity(),
                                context
                        )
                ),
                context);
    }

    // PARSE .........................................................................................................

    /**
     * Parsers the formula for this cell, and sets its expression or error if parsing fails.
     */
    SpreadsheetFormula parseFormulaIfNecessary(final SpreadsheetCell cell,
                                               final Function<SpreadsheetParserToken, SpreadsheetParserToken> parsed,
                                               final SpreadsheetEngineContext context) {
        SpreadsheetFormula formula = cell.formula();

        try {
            final String text = formula.text();
            if (text.isEmpty()) {
                formula = formula.setToken(EMPTY_TOKEN)
                        .setExpression(EMPTY_EXPRESSION); // will evaluate to empty string
            } else {
                // if a token is NOT present parse the formula text
                SpreadsheetParserToken token = formula.token()
                        .orElse(null);
                if (null == token) {
                    token = context.parseFormula(text);
                }
                if (null != token) {
                    token = parsed.apply(token);
                    formula = formula.setText(token.text())
                            .setToken(Optional.of(token));
                }
                // if expression is absent, convert token into expression
                if (null != token && false == formula.expression().isPresent()) {
                    formula = formula.setExpression(
                            token.toExpression(
                                    BasicSpreadsheetEngineExpressionEvaluationContext.with(context)
                            )
                    );
                }
            }

        } catch (final Exception failed) {
            // parsing or token to expression failed set the error message
            formula = this.setError(formula, failed);
        }

        return formula;
    }

    /**
     * This {@link SpreadsheetParserToken} is set upon {@link SpreadsheetFormula} when the {@link SpreadsheetFormula#text()} is empty.
     */
    // VisibleForTesting
    final static Optional<SpreadsheetParserToken> EMPTY_TOKEN = Optional.of(
            SpreadsheetParserToken.text(
                    Lists.<ParserToken>of( // J2clTranspiler: Error:BasicSpreadsheetEngine.java:386: The method of(H...) of type Lists is not applicable as the formal varargs element type H is not accessible here
                            SpreadsheetParserToken.textLiteral("", "")
                    ),
                    "")
    );

    /**
     * This {@link Expression} is set upon {@link SpreadsheetFormula} when the {@link SpreadsheetFormula#text()} is empty.
     */
    // VisibleForTesting
    final static Optional<Expression> EMPTY_EXPRESSION = Optional.of(
            Expression.value("")
    );

    // EVAL .........................................................................................................

    /**
     * If a value is available try and re-use or if an expression is present evaluate it.
     */
    private SpreadsheetFormula evaluateIfPossible(final SpreadsheetCell cell,
                                                  final SpreadsheetEngineContext context) {
        final SpreadsheetFormula formula = cell.formula();

        return formula.value()
                .orElse(null) instanceof SpreadsheetError ?
                formula : // value present - using cached.
                this.evaluate(cell, context);
    }

    private SpreadsheetFormula evaluate(final SpreadsheetCell cell,
                                        final SpreadsheetEngineContext context) {
        SpreadsheetFormula formula = cell.formula();

        try {
            final Optional<Expression> expression = formula.expression();
            if (expression.isPresent()) {
                formula = formula.setValue(
                        Optional.ofNullable(
                                context.evaluate(
                                        expression.get(),
                                        Optional.of(cell)
                                )
                        )
                );
            }

        } catch (final Exception cause) {
            formula = this.setError(formula, cause);
        }
        return formula;
    }

    // ERROR HANDLING..............................................................................................

    /**
     * Updates the formula value after translating the {@link Throwable}.
     */
    private SpreadsheetFormula setError(final SpreadsheetFormula formula,
                                        final Throwable cause) {
        return formula.setValue(
                Optional.of(
                        SpreadsheetErrorKind.translate(cause)
                )
        );
    }

    // FORMAT .........................................................................................................

    /**
     * If a value is present use the pattern to format and apply the styling.
     */
    private SpreadsheetCell formatAndApplyStyle(final SpreadsheetCell cell,
                                                final SpreadsheetEngineContext context) {
        SpreadsheetCell result = cell;

        // try and use the cells custom format otherwise use a default from the context.
        SpreadsheetFormatter formatter = context.metadata()
                .formatter();
        final Optional<SpreadsheetCellFormat> maybeFormat = cell.format();
        if (maybeFormat.isPresent()) {
            final SpreadsheetCellFormat format = this.parsePatternIfNecessary(maybeFormat.get(), context);
            result = cell.setFormat(Optional.of(format));

            formatter = format.formatter()
                    .orElseThrow(() -> new SpreadsheetEngineException("Invalid cell format " + format));
        }

        final SpreadsheetFormula formula = cell.formula();
        final Optional<Object> value = formula.value();
        final SpreadsheetCell beforeConditionalRules =
                value.isPresent() && !formula.error().isPresent() ?
                        result.setFormatted(
                                Optional.of(
                                        this.formatAndApplyStyle0(
                                                value.get(),
                                                formatter,
                                                result.style(),
                                                context
                                        )
                                )
                        ) :
                        this.formatAndApplyStyleValueAbsent(result);

        return this.locateAndApplyConditionalFormattingRule(beforeConditionalRules, context);
    }

    /**
     * Returns a {@link SpreadsheetCellFormat} parsing the pattern if necessary.
     */
    private SpreadsheetCellFormat parsePatternIfNecessary(final SpreadsheetCellFormat format,
                                                          final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetFormatter> formatter = format.formatter();
        return formatter.isPresent() ?
                format :
                this.parsePattern(format, context);
    }

    /**
     * Returns an updated {@link SpreadsheetCellFormat} after parsing the pattern into a {@link SpreadsheetFormatter}.
     */
    private SpreadsheetCellFormat parsePattern(final SpreadsheetCellFormat format,
                                               final SpreadsheetEngineContext context) {
        return format.setFormatter(Optional.of(context.parsePattern(format.pattern())));
    }

    /**
     * Uses the formatter to format the value, merging the style and returns an updated {@link TextNode}.
     */
    private TextNode formatAndApplyStyle0(final Object value,
                                          final SpreadsheetFormatter formatter,
                                          final TextStyle style,
                                          final SpreadsheetEngineContext context) {
        return context.format(value, formatter)
                .map(f -> style.replace(f.toTextNode()))
                .orElse(EMPTY_TEXT_NODE);
    }

    private final static TextNode EMPTY_TEXT_NODE = TextNode.text("");

    /**
     * Locates and returns the first matching conditional rule style.
     */
    private SpreadsheetCell locateAndApplyConditionalFormattingRule(final SpreadsheetCell cell,
                                                                    final SpreadsheetEngineContext context) {
        SpreadsheetCell result = cell;

        final Set<SpreadsheetConditionalFormattingRule> rules = Sets.sorted(SpreadsheetConditionalFormattingRule.PRIORITY_COMPARATOR);
        rules.addAll(context.storeRepository()
                .rangeToConditionalFormattingRules()
                .loadCellReferenceValues(cell.reference()));
        for (SpreadsheetConditionalFormattingRule rule : rules) {
            final Object test = context.evaluate(
                    rule.formula()
                            .expression()
                            .get(),
                    Optional.of(
                            cell
                    )
            );
            final Boolean booleanResult = context.metadata()
                    .converterContext()
                    .convertOrFail(test, Boolean.class);
            if (Boolean.TRUE.equals(booleanResult)) {
                final TextNode formatted = cell.formatted()
                        .orElseThrow(() -> new BasicSpreadsheetEngineException("Missing formatted cell=" + cell));
                result = cell.setFormatted(
                        Optional.of(
                                rule.style()
                                        .apply(cell)
                                        .replace(formatted)));
            }
        }
        return result;
    }

    // FORMAT ERROR ....................................................................................................

    /**
     * Handles apply style to the error if present or defaulting to empty {@link String}.
     * The error becomes the text and no formatting or color is applied.
     */
    private SpreadsheetCell formatAndApplyStyleValueAbsent(final SpreadsheetCell cell) {
        final Optional<SpreadsheetError> error = cell.formula()
                .error();

        return error.isPresent() ?
                cell.setFormatted(
                        Optional.of(
                                cell.style()
                                        .replace(
                                                TextNode.text(
                                                        error.get()
                                                                .value()
                                                )
                                        )
                        )
                ) :
                cell;
    }

    // max..............................................................................................................

    @Override
    public double columnWidth(final SpreadsheetColumnReference columnReference,
                              final SpreadsheetEngineContext context) {
        double columnWidth = 0;

        final SpreadsheetStoreRepository repo = context.storeRepository();
        final Optional<SpreadsheetColumn> column = repo.columns()
                .load(columnReference);

        if (!column.isPresent() || !column.get().hidden()) {
            columnWidth = context.storeRepository()
                    .cells()
                    .maxColumnWidth(columnReference);
            if (0 == columnWidth) {
                columnWidth = columnWidthOrRowHeight(TextStylePropertyName.WIDTH);
            }
            return columnWidth;
        }

        return columnWidth;
    }

    @Override
    public double rowHeight(final SpreadsheetRowReference rowReference,
                            final SpreadsheetEngineContext context) {
        double rowHeight = 0;

        final SpreadsheetStoreRepository repo = context.storeRepository();
        final Optional<SpreadsheetRow> row = repo.rows()
                .load(rowReference);

        if (!row.isPresent() || !row.get().hidden()) {
            rowHeight = context.storeRepository()
                    .cells()
                    .maxRowHeight(rowReference);
            if (0 == rowHeight) {
                rowHeight = columnWidthOrRowHeight(TextStylePropertyName.HEIGHT);
            }
            return rowHeight;
        }

        return rowHeight;
    }

    /**
     * Gets the double value for the given {@link TextStylePropertyName} which is either WIDTH or HEIGHT>
     */
    private double columnWidthOrRowHeight(final TextStylePropertyName<Length<?>> propertyName) {
        return this.metadata.getEffectiveStylePropertyOrFail(propertyName).pixelValue();
    }

    private final SpreadsheetMetadata metadata;

    // RANGE............................................................................................................

    @Override
    public SpreadsheetCellRange range(final SpreadsheetViewport viewport,
                                      final Optional<SpreadsheetSelection> selection,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(viewport, "viewport");
        Objects.requireNonNull(selection, "selection");
        checkContext(context);

        final SpreadsheetCellReference reference = context.resolveCellReference(viewport.cellOrLabel());

        final SpreadsheetColumnReferenceRange columns = this.columnRange(
                reference.column(),
                viewport.xOffset(),
                viewport.width(),
                context
        );

        final SpreadsheetRowReferenceRange rows = this.rowRange(
                reference.row(),
                viewport.yOffset(),
                viewport.height(),
                context
        );

        SpreadsheetCellRange cells = columns.setRowReferenceRange(rows);

        if (selection.isPresent()) {
            cells = this.range1(
                    selection.get(),
                    cells,
                    viewport,
                    context
            );
        }
        return cells;
    }

    /**
     * Uses the given home cell of the viewport and a X offset and width to compute the start and end columns.
     */
    SpreadsheetColumnReferenceRange columnRange(final SpreadsheetColumnReference column,
                                                final double xOffset,
                                                final double width,
                                                final SpreadsheetEngineContext context) {
        // columns
        double x = xOffset;
        SpreadsheetColumnReference leftColumn = column;

        // consume xOffset
        if (0 != xOffset) {
            if (xOffset < 0) {
                for (; ; ) {
                    if (leftColumn.isFirst()) {
                        x = 0;
                        break;
                    }
                    leftColumn = leftColumn.addSaturated(-1);
                    x = x + this.columnWidth(leftColumn, context);
                    if (x >= 0) {
                        break;
                    }
                }

                x = x + 0;
            } else {
                for (; ; ) {
                    final double columnWidth = this.columnWidth(leftColumn, context);
                    if (x - columnWidth < 0) {
                        break;
                    }
                    x = x - columnWidth;
                    leftColumn = leftColumn.addSaturated(+1);
                }
            }
        }

        x = x + width;
        SpreadsheetColumnReference rightColumn = leftColumn;

        for (; ; ) {
            if (rightColumn.isLast()) {
                x = width + (xOffset < 0 ? +xOffset : 0);
                leftColumn = rightColumn;

                for (; ; ) {
                    x = x - this.columnWidth(leftColumn, context);
                    if (x <= 0) {
                        break;
                    }
                    leftColumn = leftColumn.addSaturated(-1);
                }

                if (xOffset < 0) {
                    if (!leftColumn.isFirst()) {
                        x = xOffset;
                        for (; ; ) {
                            leftColumn = leftColumn.addSaturated(-1);
                            x = x + this.columnWidth(leftColumn, context);
                            if (x >= 0) {
                                break;
                            }
                        }
                    }
                }
                break;
            }
            x = x - this.columnWidth(rightColumn, context);
            if (x <= 0) {
                break;
            }
            rightColumn = rightColumn.addSaturated(+1);
        }

        return leftColumn.columnRange(rightColumn);
    }

    /**
     * Uses the given home cell of the viewport and a Y offset and height to compute the start and end rows.
     */
    SpreadsheetRowReferenceRange rowRange(final SpreadsheetRowReference row,
                                          final double yOffset,
                                          final double height,
                                          final SpreadsheetEngineContext context) {
        // rows
        double y = yOffset;
        SpreadsheetRowReference topRow = row;

        // consume yOffset
        if (0 != yOffset) {
            if (yOffset < 0) {
                for (; ; ) {
                    if (topRow.isFirst()) {
                        y = 0;
                        break;
                    }
                    topRow = topRow.addSaturated(-1);
                    y = y + this.rowHeight(topRow, context);
                    if (y >= 0) {
                        break;
                    }
                }

                y = y + 0;
            } else {
                for (; ; ) {
                    final double rowHeight = this.rowHeight(topRow, context);
                    if (y - rowHeight < 0) {
                        break;
                    }
                    y = y - rowHeight;
                    topRow = topRow.addSaturated(+1);
                }
            }
        }

        y = y + height;
        SpreadsheetRowReference bottomRow = topRow;

        for (; ; ) {
            if (bottomRow.isLast()) {
                y = height + (yOffset < 0 ? +yOffset : 0);
                topRow = bottomRow;

                for (; ; ) {
                    y = y - this.rowHeight(topRow, context);
                    if (y <= 0) {
                        break;
                    }
                    topRow = topRow.addSaturated(-1);
                }

                if (yOffset < 0) {
                    if (!topRow.isFirst()) {
                        y = yOffset;
                        for (; ; ) {
                            topRow = topRow.addSaturated(-1);
                            y = y + this.rowHeight(topRow, context);
                            if (y >= 0) {
                                break;
                            }
                        }
                    }
                }
                break;
            }
            y = y - this.rowHeight(bottomRow, context);
            if (y <= 0) {
                break;
            }
            bottomRow = bottomRow.addSaturated(+1);
        }

        return topRow.rowRange(bottomRow);
    }

    double sumColumnWidths(final SpreadsheetColumnReference start,
                           final SpreadsheetColumnReference end,
                           final SpreadsheetEngineContext context) {
        double sum = 0;
        SpreadsheetColumnReference column = start;

        do {
            sum += this.columnWidth(column, context);
            column = column.addSaturated(1);
        } while (!column.isLast() && column.compareTo(end) <= 0);

        return sum;
    }

    double sumRowHeights(final SpreadsheetRowReference start,
                         final SpreadsheetRowReference end,
                         final SpreadsheetEngineContext context) {
        double sum = 0;
        SpreadsheetRowReference row = start;

        do {
            sum += this.rowHeight(row, context);
            row = row.addSaturated(1);
        } while (!row.isLast() && row.compareTo(end) <= 0);

        return sum;
    }

    private SpreadsheetCellRange range1(final SpreadsheetSelection selection,
                                        final SpreadsheetCellRange range,
                                        final SpreadsheetViewport viewport,
                                        final SpreadsheetEngineContext context) {
        // if selection is outside range, need to adjust the range.
        return selection.testCellRange(range) ?
                range :
                BasicSpreadsheetEngineRangeSpreadsheetSelectionVisitor.pan(
                        range,
                        viewport,
                        selection,
                        this,
                        context
                );
    }

    @Override
    public Optional<SpreadsheetViewportSelection> navigate(final SpreadsheetViewportSelection selection,
                                                           final SpreadsheetEngineContext context) {
        Objects.requireNonNull(selection, "selection");
        checkContext(context);

        final Optional<SpreadsheetViewportSelectionNavigation> maybeNavigation = selection.navigation();
        return maybeNavigation.isPresent() ?
                this.navigateNavigation(selection, context) :
                this.navigateWithoutNavigation(selection, context);
    }

    private Optional<SpreadsheetViewportSelection> navigateNavigation(final SpreadsheetViewportSelection selection,
                                                                      final SpreadsheetEngineContext context) {
        final SpreadsheetStoreRepository repository = context.storeRepository();

        return selection.navigation()
                .get()
                .perform(
                        selection.selection(),
                        selection.anchor(),
                        repository.columns(),
                        repository.rows()
                );
    }

    /**
     * Assumes a selection without navigation, returning an {@link SpreadsheetEngine#NO_VIEWPORT_SELECTION} if
     * the selection is hidden.
     */
    private Optional<SpreadsheetViewportSelection> navigateWithoutNavigation(final SpreadsheetViewportSelection selection,
                                                                             final SpreadsheetEngineContext context) {
        final SpreadsheetStoreRepository repository = context.storeRepository();

        return selection.selection()
                .isHidden(repository.columns()::isHidden, repository.rows()::isHidden) ?
                SpreadsheetEngine.NO_VIEWPORT_SELECTION :
                Optional.of(selection);
    }

    // checkers.........................................................................................................

    private static void checkLabel(final SpreadsheetLabelName name) {
        Objects.requireNonNull(name, "name");
    }

    private static void checkMapping(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");
    }

    private static void checkReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private static void checkColumn(SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");
    }

    private static void checkRow(SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");
    }

    private static void checkEvaluation(final SpreadsheetEngineEvaluation evaluation) {
        Objects.requireNonNull(evaluation, "evaluation");
    }

    private static void checkContext(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");
    }

    // j2cl helpers....................................................................................................

    // The J2CL Optional does not support map.
    static <V extends SpreadsheetColumnOrRow<R>, R extends SpreadsheetColumnOrRowReference> Set<V> toSet(final Optional<V> columnOrRow) {
        return columnOrRow.isPresent() ?
                Sets.of(columnOrRow.get()) :
                Sets.empty();
    }

    // Object..........................................................................................................

    @Override
    public String toString() {
        return this.metadata.toString();
    }
}
