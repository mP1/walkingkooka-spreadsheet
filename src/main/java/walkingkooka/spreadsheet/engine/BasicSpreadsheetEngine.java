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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetColumnOrRow;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigation;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigationContext;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportNavigationContexts;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The default or basic implementation of {@link SpreadsheetEngine} that includes support for evaluating nodes,
 * when they are refreshed and not when they are set.
 */
final class BasicSpreadsheetEngine implements SpreadsheetEngine {

    /**
     * Singleton
     */
    final static BasicSpreadsheetEngine INSTANCE = new BasicSpreadsheetEngine();

    /**
     * Private ctor.
     */
    private BasicSpreadsheetEngine() {
        super();
    }

    // LOAD CELL........................................................................................................

    /**
     * Loads the selected {@link SpreadsheetSelection cells} honouring the {@link SpreadsheetEngineEvaluation} which may
     * result in loading and evaluating other cells. Note if the cells were not found and labels are requested the
     * labels will be loaded and present in the {@link SpreadsheetDelta}.
     */
    @Override
    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        checkSelection(selection);
        Objects.requireNonNull(evaluation, "evaluation");
        checkDeltaProperties(deltaProperties);
        checkContext(context);

        final Optional<SpreadsheetCellRange> cells = selection.toCellRange(context.storeRepository().labels()::cellRange);

        return cells.isPresent() ?
                this.loadCells0(
                        cells.get(),
                        evaluation,
                        deltaProperties,
                        context
                ) :
                SpreadsheetDelta.EMPTY;
    }

    private SpreadsheetDelta loadCells0(final SpreadsheetCellRange cellRange,
                                        final SpreadsheetEngineEvaluation evaluation,
                                        final Set<SpreadsheetDeltaProperties> deltaProperties,
                                        final SpreadsheetEngineContext context) {
        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, deltaProperties, context)) {
            final SpreadsheetStoreRepository repository = context.storeRepository();

            final Set<SpreadsheetCellReference> loadedOrDeleted = Sets.sorted();

            for (final SpreadsheetCell cell : repository.cells()
                    .loadCells(cellRange)) {

                final SpreadsheetCell evaluated = this.maybeParseAndEvaluateAndFormat(
                        cell,
                        evaluation,
                        context
                );
                changes.onLoad(evaluated); // might have just loaded a cell without any updates but want to record cell.

                loadedOrDeleted.add(cell.reference());
            }

            SpreadsheetDelta delta = this.prepareDelta(
                    changes,
                    context
            );

            // load any labels for the requested cell when it was NOT found and labels are requested.
            if (deltaProperties.contains(SpreadsheetDeltaProperties.LABELS)) {
                final Set<SpreadsheetCellReference> deleted = delta.deletedCells();
                loadedOrDeleted.addAll(deleted);

                final Set<SpreadsheetCellReference> allDeleted = Sets.sorted();
                allDeleted.addAll(deleted);

                final Set<SpreadsheetLabelMapping> labels = Sets.sorted();
                labels.addAll(delta.labels());

                final SpreadsheetLabelStore labelStore = repository.labels();

                for (final SpreadsheetCellReference cell : cellRange) {
                    if (loadedOrDeleted.contains(cell)) {
                        continue;
                    }
                    labels.addAll(
                            labelStore.labels(cell)
                    );
                    allDeleted.add(cell);
                }

                delta = delta.setDeletedCells(allDeleted)
                        .setLabels(labels);
            }

            return delta;
        }
    }

    /**
     * Loads the given cell, returning true if it was successful.
     */
    boolean loadCell0(final SpreadsheetCellReference reference,
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

        return loaded.isPresent();
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
            final SpreadsheetCell saved = this.maybeParseAndEvaluateAndFormat(
                    cell,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    context
            );
            changes.onCellSavedImmediate(saved);
            return this.prepareDelta(
                    changes,
                    context
            );
        }
    }

    // SAVE CELLS.......................................................................................................

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta saveCells(final Set<SpreadsheetCell> cells,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        checkContext(context);

        return cells.isEmpty() ?
                SpreadsheetDelta.EMPTY :
                this.saveCellsNotEmpty(cells, context);
    }

    private SpreadsheetDelta saveCellsNotEmpty(final Set<SpreadsheetCell> cells,
                                               final SpreadsheetEngineContext context) {
        // save all cells.
        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, context)) {
            for (final SpreadsheetCell cell : cells) {
                final SpreadsheetCell saved = this.maybeParseAndEvaluateAndFormat(
                        cell,
                        SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                        context
                );

                changes.onCellSavedBatch(saved);
            }

            return this.prepareDelta(
                    changes,
                    context
            );
        }
    }

    // DELETE CELL....................................................................................................

    /**
     * DELETE the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    @Override
    public SpreadsheetDelta deleteCells(final SpreadsheetSelection selection,
                                        final SpreadsheetEngineContext context) {
        checkSelection(selection);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, context)) {
            final SpreadsheetStoreRepository repository = context.storeRepository();
            final Optional<SpreadsheetCellRange> cells = selection.toCellRange(
                    repository.labels()::cellRange
            );
            if (cells.isPresent()) {
                repository.cells().deleteCells(cells.get());
            }
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
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn.with(
                    column.value(),
                    count,
                    this,
                    context
            ).delete();

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
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow.with(
                            row.value(),
                            count,
                            this,
                            context
                    )
                    .delete();

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
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn.with(
                            column.value(),
                            count,
                            this,
                            context
                    )
                    .insert();

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
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow.with(
                            row.value(),
                            count,
                            this,
                            context
                    )
                    .insert();

            return this.prepareDelta(changes, context);
        }
    }

    private static void checkCount(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    @Override
    public SpreadsheetDelta loadCells(final Set<SpreadsheetCellRange> ranges,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(ranges, "ranges");
        checkEvaluation(evaluation);
        checkDeltaProperties(deltaProperties);
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.IMMEDIATE.createChanges(this, deltaProperties, context)) {
            final SpreadsheetCellStore store = context.storeRepository()
                    .cells();

            for (final SpreadsheetCellRange range : ranges) {
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
            }

            final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.with(ranges);

            return this.prepareDelta(
                    changes,
                    window,
                    context
            ).setWindow(window);
        }
    }

    @Override
    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                      final SpreadsheetCellRange from,
                                      final SpreadsheetCellRange to,
                                      final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(from, "parse");
        Objects.requireNonNull(to, "to");
        checkContext(context);

        try (final BasicSpreadsheetEngineChanges changes = BasicSpreadsheetEngineChangesMode.BATCH.createChanges(this, context)) {
            BasicSpreadsheetEngineFillCells.execute(
                    cells,
                    from,
                    to,
                    this,
                    context
            );

            return this.prepareDelta(changes, context);
        }
    }

    /**
     * Creates a {@link SpreadsheetDelta} to hold the given cells and then queries to fetch the labels for those cells.
     */
    private SpreadsheetDelta prepareDelta(final BasicSpreadsheetEngineChanges changes,
                                          final SpreadsheetEngineContext context) {
        changes.refreshUpdated();

        return this.prepareDelta(
                changes,
                changes.deletedAndUpdatedCellRange().map(
                        r -> SpreadsheetViewportWindows.with(
                                Sets.of(r)
                        )
                ).orElse(SpreadsheetViewportWindows.EMPTY),
                context
        );
    }

    /**
     * Creates a {@link SpreadsheetDelta} to hold the given cells and then queries to fetch the labels for those cells.
     */
    private SpreadsheetDelta prepareDelta(final BasicSpreadsheetEngineChanges changes,
                                          final SpreadsheetViewportWindows window,
                                          final SpreadsheetEngineContext context) {
        changes.refreshUpdated();

        final Set<SpreadsheetDeltaProperties> deltaProperties = changes.deltaProperties;

        final boolean addCells = deltaProperties.contains(SpreadsheetDeltaProperties.CELLS);

        final boolean addColumns = deltaProperties.contains(SpreadsheetDeltaProperties.COLUMNS);
        final boolean addRows = deltaProperties.contains(SpreadsheetDeltaProperties.ROWS);
        final boolean addLabels = deltaProperties.contains(SpreadsheetDeltaProperties.LABELS);

        final boolean addDeletedCells = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_CELLS);
        final boolean addDeletedColumns = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_COLUMNS);
        final boolean addDeletedRows = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_ROWS);

        final Set<SpreadsheetCell> updatedCells = addCells ? changes.updatedCells() : SpreadsheetDelta.NO_CELLS;
        final Set<SpreadsheetColumn> updatedColumns = addColumns ? changes.updatedColumns() : SpreadsheetDelta.NO_COLUMNS;
        final Set<SpreadsheetRow> updatedRows = addRows ? changes.updatedRows() : SpreadsheetDelta.NO_ROWS;

        final SpreadsheetStoreRepository repo = context.storeRepository();

        final Map<SpreadsheetColumnReference, SpreadsheetColumn> columns = Maps.sorted();
        final SpreadsheetColumnStore columnStore = repo.columns();

        // $updatedColumns will be empty if SpreadsheetDeltaProperties.COLUMNS is missing
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

        // $updatedRows will be empty if SpreadsheetDeltaProperties.ROWS is missing
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

        if (addColumns || addRows || addLabels) {
            for (final SpreadsheetCell cell : updatedCells) {
                final SpreadsheetCellReference cellReference = cell.reference();

                if (addColumns) {
                    addIfNecessary(
                            cellReference.column(),
                            columns,
                            columnStore
                    );
                }

                if (addRows) {
                    addIfNecessary(
                            cellReference.row(),
                            rows,
                            rowStore
                    );
                }

                if (addLabels) {
                    addLabels(
                            cell.reference(),
                            labelStore,
                            labels
                    );
                }
            }
        }

        // add labels within the range of the given window.
        if (!window.isEmpty()) {
            // if not adding columns/rows/labels theres no point looping over ranges their cells
            if (addCells && (addColumns || addRows || addLabels)) {
                final Set<SpreadsheetCellReference> cellReferences = Sets.hash();


                for (final SpreadsheetCellRange range : window.cellRanges()) {

                    // include all columns and rows within the window.
                    range.cellStream()
                            .forEach(c -> {
                                if (cellReferences.add(c)) {
                                    if (addColumns) {
                                        addIfNecessary(
                                                c.column(),
                                                columns,
                                                columnStore
                                        );
                                    }

                                    if (addRows) {
                                        addIfNecessary(
                                                c.row(),
                                                rows,
                                                rowStore
                                        );
                                    }
                                        }
                                    }
                            );

                    if (addLabels) {
                        addLabels(
                                range,
                                labelStore,
                                labels
                        );
                    }
                }
            }
        }

        // load columns and rows for the deleted cells.
        final Set<SpreadsheetCellReference> deletedCells = changes.deletedCells();

        if (addDeletedCells && (addDeletedColumns || addDeletedRows)) {
            for (final SpreadsheetCellReference deletedCell : deletedCells) {
                if (addDeletedColumns) {
                    addIfNecessary(
                            deletedCell.column(),
                            columns,
                            columnStore
                    );
                }

                if (addDeletedRows) {
                    addIfNecessary(
                            deletedCell.row(),
                            rows,
                            rowStore
                    );
                }
            }

        }

        // order is important because labels and cells for hidden columns/rows are filtered.
        SpreadsheetDelta delta = SpreadsheetDelta.EMPTY;
        if (addColumns) {
            delta = delta.setColumns(sortedSet(columns));
        }
        if (addRows) {
            delta = delta.setRows(sortedSet(rows));
        }
        if (addCells) {
            delta = delta.setCells(updatedCells);
        }
        if (addLabels) {
            delta = delta.setLabels(labels);
        }
        if (addDeletedCells) {
            delta = delta.setDeletedCells(deletedCells);
        }
        if (addDeletedColumns) {
            delta = delta.setDeletedColumns(changes.deletedColumns());
        }
        if (addDeletedRows) {
            delta = delta.setDeletedRows(changes.deletedRows());
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.COLUMN_WIDTHS)) {
            final Map<SpreadsheetColumnReference, Double> columnsWidths = Maps.sorted(SpreadsheetRowReference.COLUMN_OR_ROW_REFERENCE_KIND_IGNORED_COMPARATOR);

            for (final SpreadsheetCell cell : updatedCells) {
                this.addColumnWidthIfNecessary(
                        cell.reference()
                                .column()
                                .setReferenceKind(SpreadsheetReferenceKind.RELATIVE),
                        columnsWidths,
                        context
                );
            }

            for (final SpreadsheetCellReference cell : deletedCells) {
                this.addColumnWidthIfNecessary(
                        cell.column()
                                .setReferenceKind(SpreadsheetReferenceKind.RELATIVE),
                        columnsWidths,
                        context
                );
            }

            delta = delta.setColumnWidths(columnsWidths);
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.ROW_HEIGHTS)) {
            final Map<SpreadsheetRowReference, Double> rowsHeights = Maps.sorted(SpreadsheetRowReference.COLUMN_OR_ROW_REFERENCE_KIND_IGNORED_COMPARATOR);

            for (final SpreadsheetCell cell : updatedCells) {
                this.addRowHeightIfNecessary(
                        cell.reference()
                                .row()
                                .setReferenceKind(SpreadsheetReferenceKind.RELATIVE),
                        rowsHeights,
                        context
                );
            }

            for (final SpreadsheetCellReference cell : deletedCells) {
                this.addRowHeightIfNecessary(
                        cell.row()
                                .setReferenceKind(SpreadsheetReferenceKind.RELATIVE),
                        rowsHeights,
                        context
                );
            }

            delta = delta.setRowHeights(rowsHeights);
        }

        final boolean hasColumnCount = deltaProperties.contains(SpreadsheetDeltaProperties.COLUMN_COUNT);
        final boolean hasRowCount = deltaProperties.contains(SpreadsheetDeltaProperties.ROW_COUNT);

        if (hasColumnCount || hasRowCount) {
            if (hasColumnCount) {
                delta = delta.setColumnCount(
                        OptionalInt.of(
                                this.columnCount(context)
                        )
                );
            }
            if (hasRowCount) {
                delta = delta.setRowCount(
                        OptionalInt.of(
                                this.rowCount(context)
                        )
                );
            }
        }

        return delta;
    }

    private void addColumnWidthIfNecessary(final SpreadsheetColumnReference column,
                                           final Map<SpreadsheetColumnReference, Double> columnsWidths,
                                           final SpreadsheetEngineContext context) {
        if (false == columnsWidths.containsKey(column)) {
            final double width = this.columnWidth(column, context);
            if (width > 0) {
                columnsWidths.put(column, width);
            }
        }
    }

    private void addRowHeightIfNecessary(final SpreadsheetRowReference row,
                                         final Map<SpreadsheetRowReference, Double> rowsHeights,
                                         final SpreadsheetEngineContext context) {
        if (false == rowsHeights.containsKey(row)) {
            final double height = this.rowHeight(row, context);
            if (height > 0) {
                rowsHeights.put(row, height);
            }
        }
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

    private static void addLabels(final SpreadsheetExpressionReference reference,
                                  final SpreadsheetLabelStore store,
                                  final Set<SpreadsheetLabelMapping> all) {
        all.addAll(store.labels(reference));
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
        final SpreadsheetCell result = evaluation.parseFormulaEvaluateAndStyle(cell, this, context);
        context.storeRepository()
                .cells()
                .save(result); // update cells enabling caching of parsing and value and errors.
        return result;
    }

    // Visible for SpreadsheetEngineEvaluation only called by COMPUTE_IF_NECESSARY & FORCE_RECOMPUTE
    SpreadsheetCell parseFormulaEvaluateAndStyle(final SpreadsheetCell cell,
                                                 final SpreadsheetEngineEvaluation evaluation,
                                                 final SpreadsheetEngineContext context) {
        final SpreadsheetCell afterParse = this.parseFormulaIfNecessary(
                cell,
                Function.identity(),
                context
        );

        return afterParse.formula().error().isPresent() ?
                afterParse :
                this.evaluateAndStyle(
                        afterParse,
                        evaluation,
                        context
                );
    }

    // PARSE .........................................................................................................

    /**
     * Parsers the formula for this cell, and sets its expression or error if parsing fails.
     */
    SpreadsheetCell parseFormulaIfNecessary(final SpreadsheetCell cell,
                                            final Function<SpreadsheetParserToken, SpreadsheetParserToken> parsed,
                                            final SpreadsheetEngineContext context) {
        SpreadsheetCell result;
        SpreadsheetFormula formula = cell.formula();

        try {
            // if a token is NOT present parse the formula text
            SpreadsheetParserToken token = formula.token()
                    .orElse(null);
            if (null == token) {
                final SpreadsheetMetadata metadata = context.spreadsheetMetadata();

                formula = SpreadsheetFormula.parse(
                        TextCursors.charSequence(
                                cell.formula()
                                        .text()
                        ),
                        cell.parsePattern()
                                .map(p -> p.parser())
                                .orElseGet(() -> SpreadsheetParsers.valueOrExpression(
                                                metadata.parser()
                                        )
                                ),
                        metadata.parserContext(context::now)
                );

                token = formula.token()
                        .orElse(null);
            }
            if (null != token) {
                token = parsed.apply(token);
                formula = formula.setToken(
                        Optional.of(token)
                );
            }
            // if expression is absent, convert token into expression
            if (null != token && false == formula.expression().isPresent()) {
                formula = formula.setExpression(
                        context.toExpression(token)
                );
            }

            result = cell.setFormula(
                    formula
            );

            //if error formatAndApplyStyle
            if (formula.error().isPresent()) {
                result = this.formatAndApplyStyle(
                        result,
                        Optional.empty(), // no formatter
                        context
                );
            }

        } catch (final Exception failed) {
            result = this.setError(
                    failed,
                    cell.setFormula(formula),
                    context
            );
        }

        return result;
    }

    // EVAL .........................................................................................................

    /**
     * This is only called if the formula was parsed an {@link Expression} exists ready for evaluation.
     */
    private SpreadsheetCell evaluateAndStyle(final SpreadsheetCell cell,
                                             final SpreadsheetEngineEvaluation evaluation,
                                             final SpreadsheetEngineContext context) {
        SpreadsheetFormula formula = cell.formula();
        SpreadsheetCell result = cell;

        try {
            // ask enum to dispatch
            final Optional<Expression> maybeExpression = formula.expression();
            if (maybeExpression.isPresent()) {
                result = cell.setFormula(
                        formula.setValue(
                                evaluation.evaluate(
                                        this,
                                        maybeExpression.get(),
                                        formula,
                                        cell,
                                        context
                                )
                        ).replaceErrorWithValueIfPossible(context)
                );
            }

            result = this.formatAndApplyStyle(
                    result,
                    cell.formatPattern()
                            .map(SpreadsheetFormatPattern::formatter),
                    context
            );
        } catch (final Exception cause) {
            result = this.setError(
                    cause,
                    cell,
                    context
            );
        }

        return result;
    }

    private SpreadsheetCell setError(final Throwable cause,
                                     final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        return this.formatAndApplyStyle(
                cell.setFormula(
                        cell.formula()
                                .setValue(
                                        Optional.of(
                                                SpreadsheetErrorKind.translate(cause)
                                                        .replaceWithValueIfPossible(context)
                                        )
                                )
                ),
                Optional.empty(), // ignore cell formatter
                context
        );
    }

    /**
     * If a formatted value is present and the {@link Expression#isPure(ExpressionPurityContext)} then return
     * the current {@link SpreadsheetFormula#value()} otherwise evaluate the expression again.
     */
    // SpreadsheetEngineEvaluation#COMPUTE_IF_NECESSARY
    Optional<Object> evaluateIfNecessary(final Expression expression,
                                         final SpreadsheetFormula formula,
                                         final SpreadsheetCell cell,
                                         final SpreadsheetEngineContext context) {
        return cell.formatted().isPresent() && expression.isPure(context) ?
                formula.value() :
                this.evaluate(
                        expression,
                        cell,
                        context
                );
    }

    /**
     * Unconditionally evaluate the {@link Expression} returning the value.
     */
    // SpreadsheetEngineEvaluation#FORCE_RECOMPUTE
    Optional<Object> evaluate(final Expression expression,
                              final SpreadsheetCell cell,
                              final SpreadsheetEngineContext context) {

        return Optional.ofNullable(
                context.evaluate(
                        expression,
                        Optional.of(cell)
                )
        );
    }

    // FORMAT .........................................................................................................

    /**
     * If a value is present use the {@link SpreadsheetFormatter} and apply the styling.
     */
    private SpreadsheetCell formatAndApplyStyle(final SpreadsheetCell cell,
                                                final Optional<SpreadsheetFormatter> formatter,
                                                final SpreadsheetEngineContext context) {
        final SpreadsheetFormula formula = cell
                .formula();
        final Optional<Object> value = formula.value();

        return value.isPresent() ?
                this.locateAndApplyConditionalFormattingRule(
                        cell.setFormatted(
                                Optional.of(
                                        context.format(
                                                        value.get(),
                                                        formatter.orElse(
                                                                context.spreadsheetMetadata()
                                                                        .formatter()
                                                        )
                                                )
                                                .map(
                                                        f -> cell.style()
                                                                .replace(f.toTextNode())
                                                )
                                                .orElse(TextNode.EMPTY_TEXT)
                                )
                        ),
                        context
                ) :
                cell;
    }

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
        for (final SpreadsheetConditionalFormattingRule rule : rules) {
            final Boolean booleanResult = context.evaluateAsBoolean(
                    rule.formula()
                            .expression()
                            .get(),
                    Optional.of(
                            cell
                    )
            );
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
                columnWidth = columnWidthOrRowHeight(
                        TextStylePropertyName.WIDTH,
                        context
                );
            }
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
                rowHeight = columnWidthOrRowHeight(
                        TextStylePropertyName.HEIGHT,
                        context
                );
            }
        }

        return rowHeight;
    }

    /**
     * Gets the double value for the given {@link TextStylePropertyName} which is either WIDTH or HEIGHT>
     */
    private double columnWidthOrRowHeight(final TextStylePropertyName<Length<?>> propertyName,
                                          final SpreadsheetEngineContext context) {
        return context.spreadsheetMetadata()
                .getEffectiveStylePropertyOrFail(propertyName)
                .pixelValue();
    }

    @Override
    public int columnCount(final SpreadsheetEngineContext context) {
        checkContext(context);

        return context.storeRepository()
                .cells()
                .columnCount();
    }

    @Override
    public int rowCount(final SpreadsheetEngineContext context) {
        checkContext(context);

        return context.storeRepository()
                .cells()
                .rowCount();
    }

    // WINDOW...........................................................................................................

    @Override
    public SpreadsheetViewportWindows window(final SpreadsheetViewportRectangle viewportRectangle,
                                             final boolean includeFrozenColumnsRows,
                                             final Optional<SpreadsheetSelection> selection,
                                             final SpreadsheetEngineContext context) {
        Objects.requireNonNull(viewportRectangle, "viewportRectangle");
        Objects.requireNonNull(selection, "selection");
        checkContext(context);

        return this.window0(
                viewportRectangle,
                includeFrozenColumnsRows,
                selection.map(context::resolveIfLabel),
                context
        );
    }

    private SpreadsheetViewportWindows window0(final SpreadsheetViewportRectangle viewportRectangle,
                                               final boolean includeFrozenColumnsRows,
                                               final Optional<SpreadsheetSelection> selection,
                                               final SpreadsheetEngineContext context) {
        double width = viewportRectangle.width();
        double height = viewportRectangle.height();

        SpreadsheetColumnReferenceRange frozenColumns = null;
        SpreadsheetRowReferenceRange frozenRows = null;

        if (includeFrozenColumnsRows) {
            final SpreadsheetMetadata metadata = context.spreadsheetMetadata();

            // compute actual frozenColumns, metadata.FROZEN_COLUMNS might be higher than requested width...............

            final Optional<SpreadsheetColumnReferenceRange> maybeFrozenColumns = metadata.get(SpreadsheetMetadataPropertyName.FROZEN_COLUMNS);
            if (maybeFrozenColumns.isPresent()) {
                final SpreadsheetColumnReference lastFrozenColumn = maybeFrozenColumns.get()
                        .end();

                SpreadsheetColumnReference leftColumn = SpreadsheetReferenceKind.RELATIVE.firstColumn();
                SpreadsheetColumnReference rightColumn = leftColumn;

                do {
                    final double columnWidth = this.columnWidth(rightColumn, context);
                    width = width - columnWidth;
                    if (width <= 0) {
                        break;
                    }
                    if (rightColumn.equalsIgnoreReferenceKind(lastFrozenColumn)) {
                        break;
                    }
                    rightColumn = rightColumn.addSaturated(1);
                } while (!rightColumn.isLast());

                frozenColumns = leftColumn.columnRange(rightColumn);
            }

            // compute actual frozenRows, metadata.FROZEN_ROWS might be higher than requested height....................
            final Optional<SpreadsheetRowReferenceRange> maybeFrozenRows = metadata.get(SpreadsheetMetadataPropertyName.FROZEN_ROWS);

            if (maybeFrozenRows.isPresent()) {

                final SpreadsheetRowReference lastFrozenRow = maybeFrozenRows.get()
                        .end();

                SpreadsheetRowReference topRow = SpreadsheetReferenceKind.RELATIVE.firstRow();
                SpreadsheetRowReference bottomRow = topRow;

                do {
                    final double rowHeight = this.rowHeight(bottomRow, context);
                    height = height - rowHeight;
                    if (height <= 0) {
                        break;
                    }
                    if (bottomRow.equalsIgnoreReferenceKind(lastFrozenRow)) {
                        break;
                    }
                    bottomRow = bottomRow.addSaturated(1);
                } while (!bottomRow.isLast());

                frozenRows = topRow.rowRange(bottomRow);
            }
        }

        // non frozen viewport
        SpreadsheetCellReference nonFrozenHome = context.resolveIfLabel(
                viewportRectangle.home()
        ).toCell();
        if (null != frozenColumns) {
            final SpreadsheetColumnReference right = frozenColumns.end();
            if (right.compareTo(nonFrozenHome.column()) >= 0) {
                nonFrozenHome = nonFrozenHome.setColumn(right.addSaturated(+1));
            }
        }
        if (null != frozenRows) {
            final SpreadsheetRowReference right = frozenRows.end();
            if (right.compareTo(nonFrozenHome.row()) >= 0) {
                nonFrozenHome = nonFrozenHome.setRow(right.addSaturated(+1));
            }
        }

        final SpreadsheetColumnReferenceRange nonFrozenColumns = width > 0 ?
                this.columnRange(
                        nonFrozenHome.column(),
                        0,
                        width,
                        selection,
                        context
                ) : null;

        final SpreadsheetRowReferenceRange nonFrozenRows = height > 0 ?
                this.rowRange(
                        nonFrozenHome.row(),
                        0,
                        height,
                        selection,
                        context
                ) : null;

        final Set<SpreadsheetCellRange> window = Sets.ordered();

        SpreadsheetCellRange nonFrozenCells = null;
        if (null != nonFrozenColumns && null != nonFrozenRows) {
            nonFrozenCells = nonFrozenColumns.setRowReferenceRange(nonFrozenRows);
        }

        // compute other ranges parse frozenColumns/frozenRows .........................................................

        boolean skipPan = false;

        if (null != frozenColumns && null != frozenRows) {
            // FCR fr fr fr
            // fc  n  n  n
            // fc  n  n  n
            final SpreadsheetCellRange frozenColumnsRowsCells = frozenColumns.setRowReferenceRange(frozenRows);

            window.add(frozenColumnsRowsCells);

            skipPan = null != frozenColumnsRowsCells &&
                    selection.map(s -> s.testCellRange(frozenColumnsRowsCells)).orElse(false);
        }

        if (null != frozenRows && null != nonFrozenColumns) {
            // fcr FR FR FR
            // fc  n  n  n
            // fc  n  n  n
            final SpreadsheetCellRange frozenRowsCells = frozenRows.setColumnReferenceRange(nonFrozenColumns);

            window.add(frozenRowsCells);

            skipPan = skipPan ||
                    selection.map(s -> s.testCellRange(frozenRowsCells)).orElse(false);
        }

        if (null != frozenColumns && null != nonFrozenRows) {
            // fcr fr fr fr
            // FC  n  n  n
            // FC  n  n  n
            final SpreadsheetCellRange frozenColumnCells = frozenColumns.setRowReferenceRange(nonFrozenRows);
            window.add(frozenColumnCells);

            skipPan = skipPan ||
                    selection.map(s -> s.testCellRange(frozenColumnCells)).orElse(false);
        }

        if (null != nonFrozenCells) {
            // selection is not in any of the frozen ranges therefore nonFrozenCells may need panning.
            if (selection.isPresent() && !skipPan) {
                final SpreadsheetSelection spreadsheetSelection = selection.get();

                if (!spreadsheetSelection.testCellRange(nonFrozenCells)) {
                    nonFrozenCells = BasicSpreadsheetEngineWindowSpreadsheetSelectionVisitor.pan(
                            nonFrozenCells,
                            nonFrozenHome.viewportRectangle(
                                    width,
                                    height
                            ),
                            spreadsheetSelection,
                            this,
                            context
                    );
                }
            }
            window.add(nonFrozenCells);
        }

        return SpreadsheetViewportWindows.with(window);
    }

    /**
     * Uses the given home cell of the viewport and a X offset and width to compute the start and end columns.
     * Note if the selection matches the left or right columns incompletely then that will advance left/right.
     */
    SpreadsheetColumnReferenceRange columnRange(final SpreadsheetColumnReference column,
                                                final double xOffset,
                                                final double width,
                                                final Optional<SpreadsheetSelection> selection,
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
                    if (0 == x) {
                        break;
                    }
                    if (x < 0) {
                        if (!selection.isPresent() || !selection.get().testColumn(leftColumn)) {
                            break;
                        }

                        x = x + this.columnWidth(rightColumn, context);
                        rightColumn = rightColumn.addSaturated(-1);
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
            if (0 == x) {
                break;
            }
            if (x < 0) {
                if (!selection.isPresent() || !selection.get().testColumn(rightColumn)) {
                    break;
                }

                x = x + this.columnWidth(leftColumn, context);
                leftColumn = leftColumn.addSaturated(+1);
            }
            rightColumn = rightColumn.addSaturated(+1);
        }

        return leftColumn.columnRange(rightColumn);
    }

    /**
     * Uses the given home cell of the viewport and a Y offset and height to compute the start and end rows.
     * Note if the selection matches the top or bottom rows incompletely then that will advance up/down.
     */
    SpreadsheetRowReferenceRange rowRange(final SpreadsheetRowReference row,
                                          final double yOffset,
                                          final double height,
                                          final Optional<SpreadsheetSelection> selection,
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
                    if (0 == y) {
                        break;
                    }
                    if (y < 0) {
                        if (!selection.isPresent() || !selection.get().testRow(topRow)) {
                            break;
                        }

                        y = y + this.rowHeight(bottomRow, context);
                        bottomRow = bottomRow.addSaturated(-1);
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
            if (0 == y) {
                break;
            }
            if (y < 0) {
                if (!selection.isPresent() || !selection.get().testRow(bottomRow)) {
                    break;
                }

                y = y + this.rowHeight(topRow, context);
                topRow = topRow.addSaturated(+1);
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

    // navigate.........................................................................................................

    @Override
    public Optional<SpreadsheetViewport> navigate(final SpreadsheetViewport viewport,
                                                  final SpreadsheetEngineContext context) {
        Objects.requireNonNull(viewport, "viewport");
        checkContext(context);

        Optional<SpreadsheetViewport> result = Optional.empty();

        SpreadsheetViewport notLabelViewport = viewport;

        final Optional<AnchoredSpreadsheetSelection> maybeAnchored = viewport.selection();
        if (maybeAnchored.isPresent()) {
            final AnchoredSpreadsheetSelection anchoredBefore = maybeAnchored.get();
            final SpreadsheetSelection selection = anchoredBefore.selection();

            if (selection.isLabelName()) {
                final SpreadsheetSelection selectionNotLabel = context.resolveIfLabel(selection);
                notLabelViewport = notLabelViewport.setSelection(
                        Optional.of(
                                selectionNotLabel.setAnchor(
                                        anchoredBefore.anchor()
                                )
                        )
                );

                result = this.navigate0(
                        notLabelViewport,
                        context
                );

                if (result.isPresent()) {
                    SpreadsheetViewport viewportResult = result.get();
                    final Optional<AnchoredSpreadsheetSelection> resultMaybeAnchored = viewportResult.selection();
                    if (resultMaybeAnchored.isPresent()) {
                        final AnchoredSpreadsheetSelection resultAnchored = resultMaybeAnchored.get();
                        final SpreadsheetSelection resultSelection = resultAnchored.selection();
                        if (resultSelection.equalsIgnoreReferenceKind(selectionNotLabel)) {
                            result = Optional.of(
                                    // restore the original label
                                    viewportResult.setSelection(maybeAnchored)
                            );
                        }
                    }
                }
            } else {
                result = this.navigate0(
                        notLabelViewport,
                        context
                );
            }
        } else {
            result = this.navigate0(
                    notLabelViewport,
                    context
            );
        }

        return result;
    }

    private Optional<SpreadsheetViewport> navigate0(final SpreadsheetViewport viewport,
                                                    final SpreadsheetEngineContext context) {
        final SpreadsheetStoreRepository repository = context.storeRepository();

        return this.navigate1(
                viewport,
                SpreadsheetViewportNavigationContexts.basic(
                        repository.columns()::isHidden,
                        (c) -> this.columnWidth(c, context),
                        repository.rows()::isHidden,
                        (r) -> this.rowHeight(r, context),
                        (r, i, s) -> this.window(r, i, s, context)
                )
        );
    }

    private Optional<SpreadsheetViewport> navigate1(final SpreadsheetViewport viewport,
                                                    final SpreadsheetViewportNavigationContext context) {
        Optional<SpreadsheetViewport> result;

        final List<SpreadsheetViewportNavigation> navigations = SpreadsheetViewportNavigation.compact(
                viewport.navigations()
        );
        if (navigations.isEmpty()) {
            result = this.navigateWithoutNavigation(
                    viewport,
                    context
            );
        } else {
            SpreadsheetViewport navigating = viewport;

            for (final SpreadsheetViewportNavigation navigation : navigations) {
                navigating = navigation.update(
                        navigating,
                        context
                );
            }

            result = Optional.of(
                    navigating.setNavigations(SpreadsheetViewport.NO_NAVIGATION)
            );
        }

        return result;
    }

    /**
     * Tests if the home is hidden, returning {@link SpreadsheetViewport#NO_NAVIGATION} then also tests the selection
     * and if that is hidden clears it.
     */
    private Optional<SpreadsheetViewport> navigateWithoutNavigation(final SpreadsheetViewport viewport,
                                                                    final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = null;

        final SpreadsheetCellReference home = viewport.rectangle()
                .home();
        if (context.isColumnHidden(home.column()) || context.isRowHidden(home.row())) {
            // home is hidden clear viewport
            result = null;
        } else {
            final Optional<AnchoredSpreadsheetSelection> maybeAnchored = viewport.selection();
            if (maybeAnchored.isPresent()) {
                final AnchoredSpreadsheetSelection anchored = maybeAnchored.get();
                final SpreadsheetSelection selection = anchored.selection();
                if (selection.isHidden(context::isColumnHidden, context::isRowHidden)) {
                    // selection is hidden clear it.
                    result = viewport.setSelection(SpreadsheetViewport.NO_SELECTION);
                } else {
                    result = viewport;
                }
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                            final String valueType,
                                            final Expression expression,
                                            final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cells, "cells");
        checkValueType(valueType);
        Objects.requireNonNull(expression, "expression");
        checkContext(context);

        return cells.stream()
                .filter(
                        BasicSpreadsheetEngineFilterPredicate.with(
                                valueType,
                                expression,
                                context
                        )
                ).collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public Set<SpreadsheetCell> findCells(final SpreadsheetCellRange range,
                                          final SpreadsheetCellRangePath path,
                                          final int offset,
                                          final int max,
                                          final String valueType,
                                          final Expression expression,
                                          final SpreadsheetEngineContext context) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(path, "path");
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset " + offset + " < 0");
        }
        if (max < 0) {
            throw new IllegalArgumentException("Invalid max " + max + " < 0");
        }
        checkValueType(valueType);
        checkExpression(expression);
        checkContext(context);

        final Set<SpreadsheetCell> found = Sets.sorted(
                SpreadsheetCellReference.cellComparator(
                        path.comparator()
                )
        );

        final SpreadsheetCellStore store = context.storeRepository()
                .cells();
        final Predicate<SpreadsheetCell> filterPredicate = BasicSpreadsheetEngineFilterPredicate.with(
                valueType,
                expression,
                context
        );

        int loadOffset = 0;
        int skipOffset = 0;

        for (; ; ) {
            final int maxLeft = max - found.size();
            if (maxLeft <= 0) {
                break;
            }

            final Collection<SpreadsheetCell> loaded = store.loadCells(
                    range,
                    path,
                    loadOffset,
                    maxLeft
            );
            if (loaded.isEmpty()) {
                break;
            }

            for (final SpreadsheetCell possible : loaded) {
                loadOffset++;

                final SpreadsheetCell loadedAndEval = this.evaluateAndStyle(
                        possible,
                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                        context
                );

                if(filterPredicate.test(loadedAndEval)) {
                    if (skipOffset >= offset) {
                        found.add(loadedAndEval);
                    }
                    skipOffset++;
                }
            }
        }

        return Sets.readOnly(found);
    }

    // checkers.........................................................................................................

    private static String checkValueType(final String valueType) {
        return CharSequences.failIfNullOrEmpty(valueType, "valueType");
    }

    private static void checkExpression(final Expression expression) {
        Objects.requireNonNull(expression, "expression");
    }

    private static void checkLabel(final SpreadsheetLabelName name) {
        Objects.requireNonNull(name, "name");
    }

    private static void checkMapping(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");
    }

    private static void checkColumn(SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");
    }

    private static void checkRow(SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");
    }

    private static void checkSelection(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");
    }

    private static void checkEvaluation(final SpreadsheetEngineEvaluation evaluation) {
        Objects.requireNonNull(evaluation, "evaluation");
    }

    private static void checkDeltaProperties(final Set<SpreadsheetDeltaProperties> deltaProperties) {
        Objects.requireNonNull(deltaProperties, "deltaProperties");
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
        return this.getClass().getSimpleName();
    }
}
