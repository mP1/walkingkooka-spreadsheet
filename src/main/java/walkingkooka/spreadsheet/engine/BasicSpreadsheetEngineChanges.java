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
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.TargetAndSpreadsheetCellReference;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.store.Watchers;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Aggregates all the updated cells that result from an operation by {@link BasicSpreadsheetEngine}.
 */
final class BasicSpreadsheetEngineChanges implements AutoCloseable {

    static BasicSpreadsheetEngineChanges with(final BasicSpreadsheetEngine engine,
                                              final SpreadsheetEngineContext context,
                                              final BasicSpreadsheetEngineChangesMode mode) {
        return new BasicSpreadsheetEngineChanges(engine, context, mode);
    }

    private BasicSpreadsheetEngineChanges(final BasicSpreadsheetEngine engine,
                                          final SpreadsheetEngineContext context,
                                          final BasicSpreadsheetEngineChangesMode mode) {
        super();

        this.mode = mode;

        this.engine = engine;
        this.context = context;

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetCellStore cellStore = repository.cells();
        this.onSaveCell = cellStore.addSaveWatcher(this::onCellSaved);
        this.onDeleteCell = cellStore.addDeleteWatcher(this::onCellDeleted);

        this.onDeleteCellReferences = repository.cellReferences()
                .addRemoveReferenceWatcher(this::onCellReferenceDeleted);

        final SpreadsheetLabelStore labelStore = repository.labels();
        this.onSaveLabel = labelStore.addSaveWatcher(this::onLabelSaved);
        this.onDeleteLabel = labelStore.addDeleteWatcher(this::onLabelDeleted);

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

    private void onLabelSaved(final SpreadsheetLabelMapping mapping) {
        this.mode.onLabelSaved(mapping, this);
    }

    private void onLabelDeleted(final SpreadsheetLabelName label) {
        this.mode.onLabelDeleted(label, this);
    }

    // IMMEDIATE.......................................................................................................

    /**
     * Accepts a just saved cell, parsing the formula adding external references and then batching references to this cell.
     */
    void onCellSavedImmediate(final SpreadsheetCell cell) {
        final SpreadsheetCellReference reference = cell.reference();

        final Map<SpreadsheetCellReference, SpreadsheetCell> updatedAndDeleted = this.updatedAndDeleted;
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
        this.deletedImmediate(cell);
    }

    private void deletedImmediate(final SpreadsheetCellReference cell) {
        final Map<SpreadsheetCellReference, SpreadsheetCell> updatedAndDeleted = this.updatedAndDeleted;
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

    void onLabelSavedImmediate(final SpreadsheetLabelMapping mapping) {
        this.batchLabel(mapping.label());
    }

    void onLabelDeletedImmediate(final SpreadsheetLabelName label) {
        this.batchLabel(label);
    }

    // BATCH MODE....................................................................................................

    void onCellSavedBatch(final SpreadsheetCell cell) {
        this.batchCell(cell.reference());
    }

    void onCellDeletedBatch(final SpreadsheetCellReference cell) {
        this.deletedImmediate(cell);
    }

    @SuppressWarnings("unused")
    void onCellReferenceDeletedBatch(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.batchReferrers(targetAndReference.target());
    }

    void onLabelSavedBatch(final SpreadsheetLabelMapping mapping) {
        this.batchLabel(mapping.label());
    }

    void onLabelDeletedBatch(final SpreadsheetLabelName label) {
        this.batchLabel(label);
    }

    // REFRESH UPDATED ................................................................................................

    /**
     * Completes any outstanding refreshes.
     */
    void refreshUpdated() {
        this.mode = BasicSpreadsheetEngineChangesMode.IMMEDIATE;

        for (; ; ) {
            final SpreadsheetCellReference potential = this.unsaved.poll();
            if (null == potential) {
                break;
            }
            // saves will have a value of null for the given $potential (reference).
            if (null != this.updatedAndDeleted.get(potential)) {
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
        this.updatedAndDeleted.put(cell.reference(), cell);
    }

    /**
     * Tests if the given {@link SpreadsheetCellReference} has been already been loaded in this request.
     */
    boolean isLoaded(final SpreadsheetCellReference reference) {
        return this.updatedAndDeleted.containsKey(reference);
    }

    /**
     * Returns all the updated {@link SpreadsheetCell}.
     */
    Set<SpreadsheetCell> updatedCells() {
        if(null == this.updatedCells) {
            this.extractUpdatedCellsDeletedCells();
        }
        return Sets.readOnly(this.updatedCells);
    }

    private Set<SpreadsheetCell> updatedCells;

    /**
     * Returns all cells that were deleted for any reason.
     */
    Set<SpreadsheetCellReference> deletedCells() {
        if(null == this.deletedCells) {
            this.extractUpdatedCellsDeletedCells();
        }
        return Sets.readOnly(this.deletedCells);
    }

    private Set<SpreadsheetCellReference> deletedCells;

    private void extractUpdatedCellsDeletedCells() {
        final Set<SpreadsheetCell> updatedCells = Sets.ordered();
        final Set<SpreadsheetCellReference> deletedCells = Sets.ordered();

        for (final Map.Entry<SpreadsheetCellReference, SpreadsheetCell> referenceToCell : this.updatedAndDeleted.entrySet()) {
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

    private void batchCell(final SpreadsheetCellReference reference) {
        // saves replace delete, but dont replace a previous save
        if (null == this.updatedAndDeleted.get(reference)) {
            this.unsaved.add(reference);
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
                .forEach(this::batchLabel);

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
    private final Queue<SpreadsheetCellReference> unsaved = new ConcurrentLinkedQueue<>();

    /**
     * Records all updated which includes deleted cells. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the cell was deleted.
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> updatedAndDeleted = Maps.sorted();

    private final BasicSpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;
    private final SpreadsheetStoreRepository repository;

    /**
     * Removes previously added watchers.
     */
    @Override
    public void close() {
        Watchers.removeAllThenFail(this.onSaveCell,
                this.onDeleteCell,
                this.onDeleteCellReferences,
                this.onSaveLabel,
                this.onDeleteLabel);
    }

    private final Runnable onSaveCell;
    private final Runnable onDeleteCell;
    private final Runnable onDeleteCellReferences;
    private final Runnable onSaveLabel;
    private final Runnable onDeleteLabel;

    @Override
    public String toString() {
        return this.updatedAndDeleted.toString();
    }
}
