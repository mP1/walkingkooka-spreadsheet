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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.TargetAndSpreadsheetCellReference;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.watch.Watchers;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Aggregates all the updated cells that result parse an operation by {@link BasicSpreadsheetEngine}.
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
            this.addReferences(cell);
            this.batchReferrers(reference);
        }
    }

    /**
     * Records any {@link walkingkooka.tree.expression.ExpressionReference} within the given {@link SpreadsheetFormula}.
     */
    private void addReferences(final SpreadsheetCell cell) {
        cell.formula()
                .consumeSpreadsheetExpressionReferences(
                        BasicSpreadsheetEngineChangesAddReferencesSpreadsheetSelectionVisitor.with(
                                cell.reference(),
                                this.context
                        )::accept
        );
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
                .loadTargets(cell)
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
        final SpreadsheetLabelName label = mapping.label();

        final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> updatedAndDeleted = this.updatedAndDeletedLabels;
        final SpreadsheetLabelMapping previous = updatedAndDeleted.get(label);

        // save replaces deletes
        if (null == previous) {
            updatedAndDeleted.put(
                    label,
                    mapping
            );
        }

        this.batchReferences(label);
    }

    void onLabelDeletedImmediate(final SpreadsheetLabelName label) {
        this.updatedAndDeletedLabels.put(
                label,
                null
        );
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
        this.addReferences(cell);
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

            this.context.storeRepository()
                    .cells()
                    .load(potential)
                    .ifPresent(
                            c -> {
                                final SpreadsheetCell evaluated = this.engine.parseFormulaEvaluateFormatStyleAndSave(
                                        c,
                                        SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                                        this.context
                                );
                                onLoad(evaluated); // might have just loaded a cell without any updates but want to record cell.
                            }
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
     * Returns a {@link SpreadsheetCellRangeReference} that includes all deleted and updated cells.
     */
    Optional<SpreadsheetCellRangeReference> deletedAndUpdatedCellRange() {
        SpreadsheetColumnReference left = null;
        SpreadsheetColumnReference right = null;

        SpreadsheetRowReference top = null;
        SpreadsheetRowReference bottom = null;

        for(final SpreadsheetCellReference cell : this.updatedAndDeletedCells.keySet()) {
            final SpreadsheetColumnReference column = cell.column();
            final SpreadsheetRowReference row = cell.row();

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

        for(final SpreadsheetLabelMapping labelMapping : this.updatedAndDeletedLabels.values()) {
            if(null != labelMapping) {
                SpreadsheetExpressionReference target;
                do {
                    target = labelMapping.target();
                    if (target instanceof SpreadsheetLabelName) {
                        target = this.context.storeRepository().labels().load(
                                        target.toLabelName()
                                ).map(SpreadsheetLabelMapping::target)
                                .orElse(null);
                    }
                } while (target instanceof SpreadsheetLabelName);

                if (target instanceof SpreadsheetCellReference) {
                    final SpreadsheetCellReference cell = target.toCell();
                    final SpreadsheetColumnReference column = cell.column();
                    final SpreadsheetRowReference row = cell.row();

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
            }
        }

        return null != left ?
                Optional.of(
                        left.columnRange(right)
                                .setRowRange(
                                        top.rowRange(bottom)
                                )
                ) :
                Optional.empty();
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
        // saves replace delete, but dont replace a previous save
        if (null == this.updatedAndDeletedLabels.get(label)) {
            this.unsavedLabels.add(label);
            this.batchReferences(label);
        }
    }

    private void batchRange(final SpreadsheetCellRangeReference range) {
        this.repository.rangeToCells()
                .load(range)
                .ifPresent(c -> c.forEach(this::batchCell));
    }

    private void batchReferences(final SpreadsheetLabelName label) {
        this.repository.labelReferences()
                .load(label)
                .ifPresent(r -> r.forEach(this::batchCell));
    }

    private void batchReferrers(final SpreadsheetCellReference reference) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
                .loadTargets(reference)
                .forEach(this::batchCell);

        repository.labels()
                .labels(reference)
                .forEach(m -> this.batchLabel(m.label()));

        repository.rangeToCells()
                .loadCellRangeReferences(reference)
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
    final Map<SpreadsheetCellReference, SpreadsheetCell> updatedAndDeletedCells = Maps.sorted();

    /**
     * Records all updated which includes deleted columns. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the column was deleted.
     */
    final Map<SpreadsheetColumnReference, SpreadsheetColumn> updatedAndDeletedColumns = Maps.sorted();

    /**
     * Records all updated which includes deleted rows. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the row was deleted.
     */
    final Map<SpreadsheetRowReference, SpreadsheetRow> updatedAndDeletedRows = Maps.sorted();

    /**
     * Holds a queue of labels that need to be updated.
     */
    private final Queue<SpreadsheetLabelName> unsavedLabels = new ConcurrentLinkedQueue<>();

    /**
     * Records all updated and deleted labels. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the label was deleted.
     */
    final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> updatedAndDeletedLabels = Maps.sorted();

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
