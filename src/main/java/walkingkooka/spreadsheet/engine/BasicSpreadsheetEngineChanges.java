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
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.TargetAndSpreadsheetCellReference;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.watch.Watchers;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Aggregates all the updated cells that result from an operation by {@link BasicSpreadsheetEngine}.
 */
final class BasicSpreadsheetEngineChanges implements AutoCloseable {

    static BasicSpreadsheetEngineChanges with(final BasicSpreadsheetEngine engine,
                                              final SpreadsheetEngineContext context,
                                              final Set<SpreadsheetDeltaProperties> deltaProperties,
                                              final BasicSpreadsheetEngineChangesMode mode) {
        return new BasicSpreadsheetEngineChanges(
                engine,
                context,
                deltaProperties,
                mode
        );
    }

    private BasicSpreadsheetEngineChanges(final BasicSpreadsheetEngine engine,
                                          final SpreadsheetEngineContext context,
                                          final Set<SpreadsheetDeltaProperties> deltaProperties,
                                          final BasicSpreadsheetEngineChangesMode mode) {
        super();

        this.mode = mode;
        this.deltaProperties = deltaProperties;

        this.engine = engine;
        this.context = context;

        // test $deltaProperties for each watcher registration.

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetCellStore cellStore = repository.cells();

        this.onSaveCell = deltaProperties.contains(SpreadsheetDeltaProperties.CELLS) ?
                cellStore.addSaveWatcher(this::onCellSaved) :
                null;

        this.onDeleteCell = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_CELLS) ?
                cellStore.addDeleteWatcher(this::onCellDeleted) :
                null;

        // ???
        this.onDeleteCellReferences = repository.cellReferences()
                .addRemoveReferenceWatcher(this::onCellReferenceDeleted);

        final SpreadsheetColumnStore columnStore = repository.columns();
        this.onSaveColumn = deltaProperties.contains(SpreadsheetDeltaProperties.COLUMNS) ?
                columnStore.addSaveWatcher(this::onColumnSaved) :
                null;
        this.onDeleteColumn = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_CELLS) ?
                columnStore.addDeleteWatcher(this::onColumnDeleted) :
                null;

        final SpreadsheetLabelStore labelStore = repository.labels();

        this.onSaveLabel = deltaProperties.contains(SpreadsheetDeltaProperties.LABELS) ?
                labelStore.addSaveWatcher(this::onLabelSaved) :
                null;
        this.onDeleteLabel = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_LABELS) ?
                labelStore.addDeleteWatcher(this::onLabelDeleted) :
                null;

        final SpreadsheetRowStore rowStore = repository.rows();
        this.onSaveRow = deltaProperties.contains(SpreadsheetDeltaProperties.ROWS) ?
                rowStore.addSaveWatcher(this::onRowSaved) :
                null;
        this.onDeleteRow = deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_ROWS) ?
                rowStore.addDeleteWatcher(this::onRowDeleted) :
                null;

        this.repository = repository;
    }

    // dispatch using mode to the final target.

    private void onCellSaved(final SpreadsheetCell cell) {
        this.mode.onCellSaved(cell, this);
    }

    private void onCellDeleted(final SpreadsheetCellReference cell) {
        this.mode.onCellDeleted(cell, this);
    }

    private void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.mode.onCellReferenceDeleted(targetAndReference, this);
    }

    private void onColumnSaved(final SpreadsheetColumn column) {
        this.mode.onColumnSaved(column, this);
    }

    private void onColumnDeleted(final SpreadsheetColumnReference column) {
        this.mode.onColumnDeleted(column, this);
    }

    private void onLabelSaved(final SpreadsheetLabelMapping mapping) {
        this.mode.onLabelSaved(mapping, this);
    }

    private void onLabelDeleted(final SpreadsheetLabelName label) {
        this.mode.onLabelDeleted(label, this);
    }

    private void onRowSaved(final SpreadsheetRow row) {
        this.mode.onRowSaved(row, this);
    }

    private void onRowDeleted(final SpreadsheetRowReference row) {
        this.mode.onRowDeleted(row, this);
    }

    // IMMEDIATE.......................................................................................................

    /**
     * Accepts a just saved cell, parsing the formula adding external references and then batching references to this cell.
     */
    void onCellSavedImmediate(final SpreadsheetCell cell) {
        final SpreadsheetCellReference reference = cell.reference();

        final Map<SpreadsheetCellReference, SpreadsheetCell> updatedAndDeleted = this.updatedAndDeletedCells;
        final SpreadsheetCell previous = updatedAndDeleted.get(reference);

        // save replaces deletes
        if (null == previous) {
            updatedAndDeleted.put(reference, cell);

            this.removePreviousExpressionReferences(reference);
            this.addNewExpressionReferences(reference, cell.formula());
            this.batchReferrers(reference);
        }
    }

    /**
     * Removes any existing references by this cell and replaces them with new references if any are present.
     */
    private void addNewExpressionReferences(final SpreadsheetCellReference cell,
                                            final SpreadsheetFormula formula) {
        formula.expression()
                .ifPresent(e -> BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor.processReferences(e,
                        cell,
                        this.context));
    }

    /**
     * Invoked whenever a cell is deleted.
     */
    void onCellDeletedImmediate(final SpreadsheetCellReference cell) {
        this.deletedCellImmediate(cell);
    }

    private void deletedCellImmediate(final SpreadsheetCellReference cell) {
        final Map<SpreadsheetCellReference, SpreadsheetCell> updatedAndDeleted = this.updatedAndDeletedCells;
        final SpreadsheetCell previous = updatedAndDeleted.get(cell);

        // delete does not overwrite save/updated
        if (null == previous) {
            updatedAndDeleted.put(cell, null);
            this.removePreviousExpressionReferences(cell);
            this.batchReferrers(cell);
        }
    }

    private void removePreviousExpressionReferences(final SpreadsheetCellReference cell) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
                .delete(cell);
        repository.labelReferences()
                .loadReferred(cell)
                .forEach(l -> repository.labelReferences().removeReference(TargetAndSpreadsheetCellReference.with(l, cell)));
        repository.rangeToCells()
                .rangesWithValue(cell)
                .forEach(r -> repository.rangeToCells().removeValue(r, cell));
    }

    void onCellReferenceDeletedImmediate(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.batchReferrers(targetAndReference.target());
    }

    void onColumnSavedImmediate(final SpreadsheetColumn column) {
        this.onColumnSave(column);
    }

    void onColumnDeletedImmediate(final SpreadsheetColumnReference column) {
        this.deletedColumnImmediate(column);
    }

    private void deletedColumnImmediate(final SpreadsheetColumnReference column) {
        final Map<SpreadsheetColumnReference, SpreadsheetColumn> updatedAndDeleted = this.updatedAndDeletedColumns;
        final SpreadsheetColumn previous = updatedAndDeleted.get(column);

        // delete does not overwrite save/updated
        if (null == previous) {
            updatedAndDeleted.put(column, null);
        }
    }

    void onLabelSavedImmediate(final SpreadsheetLabelMapping mapping) {
        this.batchLabel(mapping.label());
    }

    void onLabelDeletedImmediate(final SpreadsheetLabelName label) {
        this.batchLabel(label);
    }

    void onRowSavedImmediate(final SpreadsheetRow row) {
        this.onRowSave(row);
    }

    void onRowDeletedImmediate(final SpreadsheetRowReference row) {
        this.deletedRowImmediate(row);
    }

    private void deletedRowImmediate(final SpreadsheetRowReference row) {
        final Map<SpreadsheetRowReference, SpreadsheetRow> updatedAndDeleted = this.updatedAndDeletedRows;
        final SpreadsheetRow previous = updatedAndDeleted.get(row);

        // delete does not overwrite save/updated
        if (null == previous) {
            updatedAndDeleted.put(row, null);
        }
    }

    // BATCH MODE....................................................................................................

    void onCellSavedBatch(final SpreadsheetCell cell) {
        final SpreadsheetCellReference reference = cell.reference();
        this.unsavedCells.add(reference);

        this.removePreviousExpressionReferences(reference);
        this.addNewExpressionReferences(reference, cell.formula());
        this.batchReferrers(reference);
    }

    void onCellDeletedBatch(final SpreadsheetCellReference cell) {
        this.deletedCellImmediate(cell);
    }

    @SuppressWarnings("unused")
    void onCellReferenceDeletedBatch(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.batchReferrers(targetAndReference.target());
    }

    void onColumnSavedBatch(final SpreadsheetColumn column) {
        this.onColumnSaved(column);
    }

    private void onColumnSave(final SpreadsheetColumn column) {
        final SpreadsheetColumnReference reference = column.reference();

        final Map<SpreadsheetColumnReference, SpreadsheetColumn> updatedAndDeleted = this.updatedAndDeletedColumns;
        final SpreadsheetColumn previous = updatedAndDeleted.get(reference);

        // save replaces deletes
        if (null == previous) {
            updatedAndDeleted.put(reference, column);
        }
    }

    void onColumnDeletedBatch(final SpreadsheetColumnReference column) {
        this.deletedColumnImmediate(column);
    }

    void onLabelSavedBatch(final SpreadsheetLabelMapping mapping) {
        this.batchLabel(mapping.label());
    }

    void onLabelDeletedBatch(final SpreadsheetLabelName label) {
        this.batchLabel(label);
    }

    void onRowSavedBatch(final SpreadsheetRow row) {
        this.onRowSave(row);
    }

    private void onRowSave(final SpreadsheetRow row) {
        final SpreadsheetRowReference reference = row.reference();

        final Map<SpreadsheetRowReference, SpreadsheetRow> updatedAndDeleted = this.updatedAndDeletedRows;
        final SpreadsheetRow previous = updatedAndDeleted.get(reference);

        // save replaces deletes
        if (null == previous) {
            updatedAndDeleted.put(reference, row);
        }
    }

    void onRowDeletedBatch(final SpreadsheetRowReference row) {
        this.deletedRowImmediate(row);
    }

    // REFRESH UPDATED ................................................................................................

    /**
     * Completes any outstanding refreshes.
     */
    void refreshUpdated() {
        this.mode = BasicSpreadsheetEngineChangesMode.IMMEDIATE;

        for (; ; ) {
            final SpreadsheetCellReference potential = this.unsavedCells.poll();
            if (null == potential) {
                break;
            }
            // saves will have a value of null for the given $potential (reference).
            if (null != this.updatedAndDeletedCells.get(potential)) {
                continue;
            }

            this.engine.loadCell0(
                    potential,
                    SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                    this,
                    this.context
            );
        }
    }

    /**
     * Unconditionally adds the {@link SpreadsheetCell} to the updated cells. This is used to add a cell that was loaded
     * but not changed.
     */
    void onLoad(final SpreadsheetCell cell) {
        this.updatedAndDeletedCells.put(cell.reference(), cell);
    }

    /**
     * Tests if the given {@link SpreadsheetCellReference} has been already been loaded in this request.
     */
    boolean isLoaded(final SpreadsheetCellReference reference) {
        return this.updatedAndDeletedCells.containsKey(reference);
    }

    // cells............................................................................................................

    /**
     * Returns all the updated {@link SpreadsheetCell}.
     */
    Set<SpreadsheetCell> updatedCells() {
        if (null == this.updatedCells) {
            this.extractUpdatedCellsDeletedCells();
        }
        return Sets.readOnly(this.updatedCells);
    }

    private Set<SpreadsheetCell> updatedCells;

    /**
     * Returns a {@link SpreadsheetCellRange} that includes all the {@link #deletedCells()} and {@link #updatedCells()}
     */
    Optional<SpreadsheetCellRange> deletedAndUpdatedCellRange() {
        SpreadsheetColumnReference left = null;
        SpreadsheetColumnReference right = null;

        SpreadsheetRowReference top = null;
        SpreadsheetRowReference bottom = null;

        for (final SpreadsheetCell cell : this.updatedCells()) {
            final SpreadsheetCellReference cellReference = cell.reference();
            final SpreadsheetColumnReference column = cellReference.column();
            final SpreadsheetRowReference row = cellReference.row();

            if (null == left) {
                left = column;
                right = column;
                top = row;
                bottom = row;
            } else {
                left = left.min(column);
                right = right.max(column);
                top = top.min(row);
                bottom = bottom.max(row);
            }
        }

        for (final SpreadsheetCellReference cellReference : this.deletedCells()) {
            final SpreadsheetColumnReference column = cellReference.column();
            final SpreadsheetRowReference row = cellReference.row();

            if (null == left) {
                left = column;
                right = column;
                top = row;
                bottom = row;
            } else {
                left = left.min(column);
                right = right.max(column);
                top = top.min(row);
                bottom = bottom.max(row);
            }
        }

        return null != left ?
                Optional.of(
                        left.columnRange(right)
                                .setRowReferenceRange(
                                        top.rowRange(bottom)
                                )
                ) :
                Optional.empty();
    }

    /**
     * Returns all cells that were deleted for any reason.
     */
    Set<SpreadsheetCellReference> deletedCells() {
        if (null == this.deletedCells) {
            this.extractUpdatedCellsDeletedCells();
        }
        return Sets.readOnly(this.deletedCells);
    }

    private Set<SpreadsheetCellReference> deletedCells;

    private void extractUpdatedCellsDeletedCells() {
        final Set<SpreadsheetCell> updatedCells = Sets.ordered();
        final Set<SpreadsheetCellReference> deletedCells = Sets.ordered();

        for (final Map.Entry<SpreadsheetCellReference, SpreadsheetCell> referenceToCell : this.updatedAndDeletedCells.entrySet()) {
            final SpreadsheetCell cell = referenceToCell.getValue();
            if (null != cell) {
                updatedCells.add(cell);
            } else {
                deletedCells.add(referenceToCell.getKey());
            }
        }

        this.updatedCells = updatedCells;
        this.deletedCells = deletedCells;
    }

    // columns............................................................................................................

    /**
     * Returns all the updated {@link SpreadsheetColumn}.
     */
    Set<SpreadsheetColumn> updatedColumns() {
        if(null == this.updatedColumns) {
            this.extractUpdatedColumnsDeletedColumns();
        }
        return Sets.readOnly(this.updatedColumns);
    }

    private Set<SpreadsheetColumn> updatedColumns;

    /**
     * Returns all columns that were deleted for any reason.
     */
    Set<SpreadsheetColumnReference> deletedColumns() {
        if(null == this.deletedColumns) {
            this.extractUpdatedColumnsDeletedColumns();
        }
        return Sets.readOnly(this.deletedColumns);
    }

    private Set<SpreadsheetColumnReference> deletedColumns;

    private void extractUpdatedColumnsDeletedColumns() {
        final Set<SpreadsheetColumn> updatedColumns = Sets.ordered();
        final Set<SpreadsheetColumnReference> deletedColumns = Sets.ordered();

        for (final Map.Entry<SpreadsheetColumnReference, SpreadsheetColumn> referenceToColumn : this.updatedAndDeletedColumns.entrySet()) {
            final SpreadsheetColumn column = referenceToColumn.getValue();
            if (null != column) {
                updatedColumns.add(column);
            } else {
                deletedColumns.add(referenceToColumn.getKey());
            }
        }

        this.updatedColumns = updatedColumns;
        this.deletedColumns = deletedColumns;
    }

    // rows............................................................................................................

    /**
     * Returns all the updated {@link SpreadsheetRow}.
     */
    Set<SpreadsheetRow> updatedRows() {
        if(null == this.updatedRows) {
            this.extractUpdatedRowsDeletedRows();
        }
        return Sets.readOnly(this.updatedRows);
    }

    private Set<SpreadsheetRow> updatedRows;

    /**
     * Returns all rows that were deleted for any reason.
     */
    Set<SpreadsheetRowReference> deletedRows() {
        if(null == this.deletedRows) {
            this.extractUpdatedRowsDeletedRows();
        }
        return Sets.readOnly(this.deletedRows);
    }

    private Set<SpreadsheetRowReference> deletedRows;

    private void extractUpdatedRowsDeletedRows() {
        final Set<SpreadsheetRow> updatedRows = Sets.ordered();
        final Set<SpreadsheetRowReference> deletedRows = Sets.ordered();

        for (final Map.Entry<SpreadsheetRowReference, SpreadsheetRow> referenceToRow : this.updatedAndDeletedRows.entrySet()) {
            final SpreadsheetRow row = referenceToRow.getValue();
            if (null != row) {
                updatedRows.add(row);
            } else {
                deletedRows.add(referenceToRow.getKey());
            }
        }

        this.updatedRows = updatedRows;
        this.deletedRows = deletedRows;
    }

    // batch...........................................................................................................

    private void batchCell(final SpreadsheetCellReference reference) {
        // saves replace delete, but dont replace a previous save
        if (null == this.updatedAndDeletedCells.get(reference)) {
            this.unsavedCells.add(reference);
            this.batchReferrers(reference);
        }
    }

    private void batchLabel(final SpreadsheetLabelName label) {
        this.repository.labelReferences()
                .load(label)
                .ifPresent(r -> r.forEach(this::batchCell));
    }

    private void batchRange(final SpreadsheetCellRange range) {
        this.repository.rangeToCells()
                .load(range)
                .ifPresent(c -> c.forEach(this::batchCell));
    }

    private void batchReferrers(final SpreadsheetCellReference reference) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
                .loadReferred(reference)
                .forEach(this::batchCell);

        repository.labels()
                .labels(reference)
                .forEach(m -> this.batchLabel(m.label()));

        repository.rangeToCells()
                .loadCellReferenceRanges(reference)
                .forEach(this::batchRange);
    }

    /**
     * The current mode.
     */
    private volatile BasicSpreadsheetEngineChangesMode mode;

    /**
     * Holds a queue of cell references that need to be updated.
     */
    private final Queue<SpreadsheetCellReference> unsavedCells = new ConcurrentLinkedQueue<>();

    /**
     * Records all updated which includes deleted cells. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the cell was deleted.
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> updatedAndDeletedCells = Maps.sorted();

    /**
     * Records all updated which includes deleted columns. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the column was deleted.
     */
    private final Map<SpreadsheetColumnReference, SpreadsheetColumn> updatedAndDeletedColumns = Maps.sorted();

    /**
     * Records all updated which includes deleted rows. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the row was deleted.
     */
    private final Map<SpreadsheetRowReference, SpreadsheetRow> updatedAndDeletedRows = Maps.sorted();

    // VisibleFor BasicSpreadsheetEngine
    final Set<SpreadsheetDeltaProperties> deltaProperties;

    private final BasicSpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;
    private final SpreadsheetStoreRepository repository;

    /**
     * Removes previously added watchers.
     */
    @Override
    public void close() {
        // some might be null if the corresponding {@link SpreadsheetDeltaProperties} was clear.
        Watchers.removeAllThenFail(
                this.onSaveCell,
                this.onDeleteCell,
                this.onDeleteCellReferences,
                this.onSaveColumn,
                this.onDeleteColumn,
                this.onSaveLabel,
                this.onDeleteLabel,
                this.onSaveRow,
                this.onDeleteRow
        );
    }

    private final Runnable onSaveCell;
    private final Runnable onDeleteCell;
    private final Runnable onDeleteCellReferences;

    private final Runnable onSaveColumn;
    private final Runnable onDeleteColumn;

    private final Runnable onSaveLabel;
    private final Runnable onDeleteLabel;

    private final Runnable onSaveRow;
    private final Runnable onDeleteRow;

    @Override
    public String toString() {
        return this.updatedAndDeletedCells + " " + this.updatedAndDeletedColumns + " " + this.updatedAndDeletedRows;
    }
}
