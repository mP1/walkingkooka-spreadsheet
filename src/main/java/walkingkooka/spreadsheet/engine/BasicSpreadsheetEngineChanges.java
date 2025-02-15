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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.TargetAndSpreadsheetCellReference;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.watch.Watchers;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aggregates all the updated cells that result parse an operation by {@link BasicSpreadsheetEngine}.
 * <br>
 * Note that cell reference save events are not watched.
 */
final class BasicSpreadsheetEngineChanges {

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

    // double dispatch using mode to the final target method............................................................

    private void onCellSaved(final SpreadsheetCell cell) {
        this.mode.onCellSaved(
                cell,
                this
        );
    }

    private void onCellDeleted(final SpreadsheetCellReference cell) {
        this.mode.onCellDeleted(
                cell,
                this
        );
    }

    private void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.mode.onCellReferenceDeleted(
                targetAndReference,
                this
        );
    }

    private void onColumnSaved(final SpreadsheetColumn column) {
        this.mode.onColumnSaved(
                column,
                this
        );
    }

    private void onColumnDeleted(final SpreadsheetColumnReference column) {
        this.mode.onColumnDeleted(
                column,
                this
        );
    }

    private void onLabelSaved(final SpreadsheetLabelMapping mapping) {
        this.mode.onLabelSaved(
                mapping,
                this
        );
    }

    private void onLabelDeleted(final SpreadsheetLabelName label) {
        this.mode.onLabelDeleted(
                label,
                this
        );
    }

    private void onRowSaved(final SpreadsheetRow row) {
        this.mode.onRowSaved(
                row,
                this
        );
    }

    private void onRowDeleted(final SpreadsheetRowReference row) {
        this.mode.onRowDeleted(
                row,
                this
        );
    }

    /**
     * The current mode, which never changes.
     */
    private final BasicSpreadsheetEngineChangesMode mode;

    // COLUMN SAVE......................................................................................................

    void onColumnSavedImmediate(final SpreadsheetColumn column) {
        this.savedColumn(column);
    }

    void onColumnSavedBatch(final SpreadsheetColumn column) {
        this.savedColumn(column);
    }

    private void savedColumn(final SpreadsheetColumn column) {
        this.getOrCreateColumnCache(column)
                .save();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetColumnReference, SpreadsheetColumn> getOrCreateColumnCache(final SpreadsheetColumn column) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                column.reference(),
                column,
                this.columns
        );
    }

    // COLUMN DELETE....................................................................................................

    void onColumnDeletedImmediate(final SpreadsheetColumnReference column) {
        this.deletedColumn(column);
    }

    void onColumnDeletedBatch(final SpreadsheetColumnReference column) {
        this.deletedColumn(column);
    }

    private void deletedColumn(final SpreadsheetColumnReference column) {
        this.getOrCreateColumnCache(column)
                .delete();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetColumnReference, SpreadsheetColumn> getOrCreateColumnCache(final SpreadsheetColumnReference column) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                column,
                null,
                this.columns
        );
    }

    final Map<SpreadsheetColumnReference, BasicSpreadsheetEngineChangesCache<SpreadsheetColumnReference, SpreadsheetColumn>> columns = Maps.sorted();

    // ROW SAVE.........................................................................................................

    void onRowSavedImmediate(final SpreadsheetRow row) {
        this.savedRow(row);
    }

    void onRowSavedBatch(final SpreadsheetRow row) {
        this.savedRow(row);
    }

    private void savedRow(final SpreadsheetRow row) {
        this.getOrCreateRowCache(row)
                .save();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetRowReference, SpreadsheetRow> getOrCreateRowCache(final SpreadsheetRow row) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                row.reference(),
                row,
                this.rows
        );
    }

    // ROW DELETE.......................................................................................................

    void onRowDeletedImmediate(final SpreadsheetRowReference row) {
        this.deletedRow(row);
    }

    void onRowDeletedBatch(final SpreadsheetRowReference row) {
        this.deletedRow(row);
    }

    private void deletedRow(final SpreadsheetRowReference row) {
        this.getOrCreateRowCache(row)
                .delete();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetRowReference, SpreadsheetRow> getOrCreateRowCache(final SpreadsheetRowReference row) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                row,
                null,
                this.rows
        );
    }

    final Map<SpreadsheetRowReference, BasicSpreadsheetEngineChangesCache<SpreadsheetRowReference, SpreadsheetRow>> rows = Maps.sorted();

    // CELL SAVE........................................................................................................

    /**
     * Accepts a just saved cell, parsing the formula adding external references and then batching references to this cell.
     */
    void onCellSavedImmediate(final SpreadsheetCell cell) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(cell)
                .save();

        if (false == cache.committed) {
            this.refreshSavedCell(
                    cell.reference(),
                    cache
            );
        }
    }

    void onCellSavedBatch(final SpreadsheetCell cell) {
        this.getOrCreateCellCache(cell)
                .save();
    }

    private void forceRefreshSavedCell(final SpreadsheetCellReference cell) {
        this.getOrCreateCellCache(cell)
                .save()
                .setCommitted(false);
    }

    private void refreshSavedCell(final SpreadsheetCellReference cell) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(cell)
                .save();

        if (false == cache.committed) {
            this.refreshSavedCell(
                    cell,
                    cache
            );
        }
    }

    private void refreshSavedCell(final SpreadsheetCellReference reference,
                                  final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache) {
        SpreadsheetCell cell = null;
        if (cache.save) {
            cell = this.repository.cells()
                    .load(reference)
                    .orElse(null);
            if (null != cell) {
                cell = this.engine.parseFormulaEvaluateFormatStyleAndSave(
                        cell,
                        SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                        this.context
                );
            }
            cache.value = cell;

            if (null != cell) {
                this.onCellLoad(cell);
            }
        }

        this.removeReferences(reference);
        if (null != cell) {
            this.addFormulaReferences(cell);
        }
        this.refreshCellExternalReferences(reference);

        // must be set last because #removeReferences will perform deletes which will clear cache.committed.
        cache.committed = true;
    }

    /**
     * Commits a loaded cell. but records it so any references are updated.
     */
    void onCellLoad(final SpreadsheetCell cell) {
        this.getOrCreateCellCache(cell)
                .setCommitted(true); // assume SpreadsheetEngineEngine.loadCell "load"
    }

    /**
     * Tests if the given {@link SpreadsheetCellReference} has been already been loaded in this request.
     */
    boolean isLoaded(final SpreadsheetCellReference cell) {
        return this.cells.containsKey(cell);
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> getOrCreateCellCache(final SpreadsheetCell cell) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                cell.reference(),
                cell,
                this.cells
        );
    }

    // CELL DELETE .....................................................................................................

    void onCellDeletedImmediate(final SpreadsheetCellReference cell) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(cell)
                .delete();

        if (false == cache.committed) {
            this.refreshDeletedCell(
                    cell,
                    cache
            );
        }
    }

    void onCellDeletedBatch(final SpreadsheetCellReference cell) {
        this.getOrCreateCellCache(cell)
                .delete();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> getOrCreateCellCache(final SpreadsheetCellReference cell) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                cell,
                null,

                this.cells
        );
    }

    private void refreshDeletedCell(final SpreadsheetCellReference cell,
                                    final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache) {
        this.removeReferences(cell);
        this.refreshCellExternalReferences(cell);

        cache.setCommitted(true); // previous #removeReferences will clear committed
    }

    private void refreshRange(final SpreadsheetCellRangeReference range) {
        this.repository.rangeToCells()
                .load(range)
                .ifPresent(c -> c.forEach(this::refreshSavedCell));
    }

    /**
     * Returns a {@link SpreadsheetCellRangeReference} that includes all saved & deleted {@link SpreadsheetCellReference} and {@link SpreadsheetLabelName}.
     */
    Optional<SpreadsheetCellRangeReference> changesCellRange() {
        final Set<SpreadsheetCellReference> cells = SortedSets.tree();

        cells.addAll(
                this.cells.keySet()
        );

        for (final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : this.labels.values()) {
            if (cache.save) {
                final SpreadsheetLabelMapping labelMapping = cache.value;
                if (null != labelMapping) {
                    final SpreadsheetCellReferenceOrRange cellOrRange = this.context.storeRepository()
                            .labels()
                            .resolveLabel(labelMapping.label())
                            .orElse(null);

                    if (null != cellOrRange) {
                        cells.add(
                                cellOrRange.toCell()
                        );
                    }
                }
            }
        }

        return SpreadsheetSelection.boundingRange(cells);
    }

    /**
     * Records all updated which includes deleted cells. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the cell was deleted.
     */
    final Map<SpreadsheetCellReference, BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell>> cells = Maps.sorted();

    // LABELS...........................................................................................................

    /**
     * Holds a queue of labels that need to be updated.
     */
    void onLabelSavedImmediate(final SpreadsheetLabelMapping mapping) {
        BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(mapping)
                .save();
        if (false == cache.committed) {
            this.refreshSavedLabel(
                    mapping.label(),
                    cache
            );
        }
    }

    void onLabelSavedBatch(final SpreadsheetLabelMapping mapping) {
        this.getOrCreateLabelCache(mapping)
                .save();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> getOrCreateLabelCache(final SpreadsheetLabelMapping mapping) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                mapping.label(),
                mapping,
                this.labels
        );
    }

    private void refreshSavedLabel(final SpreadsheetLabelName label) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(label);
        if (false == cache.committed) {
            this.refreshSavedLabel(
                    label,
                    cache
            );
        }
    }

    // delete label references
    private void refreshSavedLabel(final SpreadsheetLabelName label,
                                   final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache) {
        this.repository.labelReferences()
                .load(label)
                .ifPresent(r -> r.forEach(this::forceRefreshSavedCell));

        cache.setCommitted(true);
    }

    // LABEL DELETE ....................................................................................................

    void onLabelDeletedImmediate(final SpreadsheetLabelName label) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(label)
                .delete();

        if (false == cache.committed) {
            this.refreshDeletedLabel(
                    label,
                    cache
            );
        }
    }

    void onLabelDeletedBatch(final SpreadsheetLabelName label) {
        this.getOrCreateLabelCache(label)
                .delete();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> getOrCreateLabelCache(final SpreadsheetLabelName label) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                label,
                null,
                this.labels
        );
    }

    /**
     * Records all updated and deleted labels. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the label was deleted.
     */
    final Map<SpreadsheetLabelName, BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> labels = Maps.sorted();

    // unused
    private void refreshDeletedLabel(final SpreadsheetLabelName label) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(label);
        if (false == cache.committed) {
            this.refreshDeletedLabel(
                    label,
                    cache
            );
        }
    }

    // delete label references
    private void refreshDeletedLabel(final SpreadsheetLabelName label,
                                     final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache) {
        this.repository.labelReferences()
                .load(label)
                .ifPresent(r -> r.forEach(this::refreshSavedCell));

        cache.committed = true;
    }

    // CELL REFERENCES DELETE...........................................................................................

    void onCellReferenceDeletedImmediate(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.refreshCellExternalReferences(
                targetAndReference.target()
        );
    }

    @SuppressWarnings("unused")
    void onCellReferenceDeletedBatch(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.getOrCreateCellCache(targetAndReference.target())
                .setCommitted(false); // force refreshing formula & external references
    }

    private void removeReferences(final SpreadsheetCellReference cell) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
                .delete(cell);

        repository.labelReferences()
                .loadTargets(cell)
                .forEach(l -> this.repository.labelReferences()
                        .removeReference(
                                TargetAndSpreadsheetCellReference.with(
                                        l,
                                        cell
                                )
                        )
                );
        repository.rangeToCells()
                .findCellRangesWithValue(cell)
                .forEach(r -> this.repository.rangeToCells()
                        .removeValue(
                                r,
                                cell
                        )
                );
    }

    /**
     * Records any {@link walkingkooka.tree.expression.ExpressionReference} within the given {@link SpreadsheetFormula}.
     */
    private void addFormulaReferences(final SpreadsheetCell cell) {
        cell.formula()
                .consumeSpreadsheetExpressionReferences(
                        BasicSpreadsheetEngineChangesAddReferencesSpreadsheetSelectionVisitor.with(
                                cell.reference(),
                                this.context
                        )::accept
                );
    }

    private void refreshCellExternalReferences(final SpreadsheetCellReference reference) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
                .loadTargets(reference)
                .forEach(this::forceRefreshSavedCell);

        repository.labels()
                .labels(reference)
                .forEach(m -> this.refreshSavedLabel(m.label()));

        repository.rangeToCells()
                .findCellRangesIncludingCell(reference)
                .forEach(this::refreshRange);
    }

    // COMMIT...........................................................................................................

    /**
     * Commits any outstanding cell, label, reference type operations.
     */
    void commit() {
        boolean changed;

        do {
            changed = false;

            {
                final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell>> workCells = this.cells.values()
                        .stream()
                        .filter(c -> false == c.committed)
                        .collect(Collectors.toList());

                for (final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache : workCells) {
                    changed = true;

                    final SpreadsheetCellReference reference = cache.reference;
                    if (cache.save) {
                        this.refreshSavedCell(
                                reference,
                                cache
                        );
                    } else {
                        this.refreshDeletedCell(
                                reference,
                                cache
                        );
                    }
                }
            }

            {
                final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> workLabels = this.labels.values()
                        .stream()
                        .filter(c -> false == c.committed)
                        .collect(Collectors.toList());

                for (final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : workLabels) {
                    changed = true;

                    final SpreadsheetLabelName labelName = cache.reference;
                    if (cache.save) {
                        this.refreshSavedLabel(
                                labelName,
                                cache
                        );

                    } else {
                        this.refreshDeletedLabel(
                                labelName,
                                cache
                        );
                    }
                }
            }
        } while (changed);
    }

    // MISC.............................................................................................................

    // VisibleFor BasicSpreadsheetEngine
    final Set<SpreadsheetDeltaProperties> deltaProperties;

    private final BasicSpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;
    private final SpreadsheetStoreRepository repository;

    /**
     * Removes previously added watchers.
     */
    void close() {
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
        return this.cells +
                " " +
                this.labels +
                " " +
                this.columns +
                " " +
                this.rows;
    }
}
