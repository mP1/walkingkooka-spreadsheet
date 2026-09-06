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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetColumn;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetRow;
import walkingkooka.store.StoreWatcher;
import walkingkooka.watch.Watchers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

/**
 * Aggregates all the updated cells that result parse an operation by {@link SpreadsheetEngineBasic}.
 * <br>
 * Note that cell reference save events are not watched.
 */
final class SpreadsheetEngineBasicChanges implements SpreadsheetExpressionReferenceLoader {


    static SpreadsheetEngineBasicChanges with(final SpreadsheetEngineBasic engine,
                                              final SpreadsheetEngineEvaluation evaluation,
                                              final Set<SpreadsheetDeltaProperties> deltaProperties,
                                              final SpreadsheetEngineBasicChangesMode mode,
                                              final SpreadsheetEngineContext context) {
        return new SpreadsheetEngineBasicChanges(
            engine,
            evaluation,
            deltaProperties,
            mode,
            context
        );
    }

    private SpreadsheetEngineBasicChanges(final SpreadsheetEngineBasic engine,
                                          final SpreadsheetEngineEvaluation evaluation,
                                          final Set<SpreadsheetDeltaProperties> deltaProperties,
                                          final SpreadsheetEngineBasicChangesMode mode,
                                          final SpreadsheetEngineContext context) {
        super();

        this.engine = engine;
        this.evaluation = evaluation;
        this.deltaProperties = deltaProperties;
        this.mode = mode;
        this.context = context.setSpreadsheetMetadataMode(SpreadsheetMetadataMode.FORMULA);

        this.scopedCells = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetCellStore cellStore = repository.cells();

        final List<Runnable> watchers = Lists.array();
        if (deltaProperties.contains(SpreadsheetDeltaProperties.CELLS)) {
            watchers.add(
                cellStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetCell> previous,
                                                  final Optional<SpreadsheetCell> next) {
                            if (next.isPresent()) {
                                SpreadsheetEngineBasicChanges.this.onCellSaved(
                                    next.get()
                                );
                            }
                        }
                    }
                )
            );
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_CELLS)) {
            watchers.add(
                cellStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetCell> previous,
                                                  final Optional<SpreadsheetCell> next) {
                            if (next.isEmpty()) {
                                SpreadsheetEngineBasicChanges.this.onCellDeleted(
                                    previous.get()
                                        .reference()
                                );
                            }
                        }
                    }
                )
            );
        }

        final SpreadsheetColumnStore columnStore = repository.columns();

        if (deltaProperties.contains(SpreadsheetDeltaProperties.COLUMNS)) {
            watchers.add(
                columnStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetColumn> previous,
                                                  final Optional<SpreadsheetColumn> next) {
                            if (next.isPresent()) {
                                SpreadsheetEngineBasicChanges.this.onColumnSaved(
                                    next.get()
                                );
                            }
                        }
                    }
                )
            );
        }

        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_COLUMNS)) {
            watchers.add(
                columnStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetColumn> previous,
                                                  final Optional<SpreadsheetColumn> next) {
                            if (next.isEmpty()) {
                                SpreadsheetEngineBasicChanges.this.onColumnDeleted(
                                    previous.get()
                                        .reference()
                                );
                            }
                        }
                    }
                )
            );
        }

        final SpreadsheetLabelStore labelStore = repository.labels();
        if (deltaProperties.contains(SpreadsheetDeltaProperties.LABELS)) {
            watchers.add(
                labelStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetLabelMapping> previous,
                                                  final Optional<SpreadsheetLabelMapping> next) {
                            if (next.isPresent()) {
                                SpreadsheetEngineBasicChanges.this.onLabelSaved(
                                    next.get()
                                );
                            }
                        }
                    }
                )
            );
        }

        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_LABELS)) {
            watchers.add(
                labelStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetLabelMapping> previous,
                                                  final Optional<SpreadsheetLabelMapping> next) {
                            if (next.isEmpty()) {
                                SpreadsheetEngineBasicChanges.this.onLabelDeleted(
                                    previous.get()
                                        .label()
                                );
                            }
                        }
                    }
                )
            );
        }

        final SpreadsheetRowStore rowStore = repository.rows();
        if (deltaProperties.contains(SpreadsheetDeltaProperties.ROWS)) {
            watchers.add(
                rowStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetRow> previous,
                                                  final Optional<SpreadsheetRow> next) {
                            if (next.isPresent()) {
                                SpreadsheetEngineBasicChanges.this.onRowSaved(
                                    next.get()
                                );
                            }
                        }
                    }
                )
            );
        }

        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_ROWS)) {
            watchers.add(
                rowStore.addStoreWatcher(
                    new StoreWatcher<>() {
                        @Override
                        public void onValueChange(final Optional<SpreadsheetRow> previous,
                                                  final Optional<SpreadsheetRow> next) {
                            if (next.isEmpty()) {
                                SpreadsheetEngineBasicChanges.this.onRowDeleted(
                                    previous.get()
                                        .reference()
                                );
                            }
                        }
                    }
                )
            );
        }

        this.watchers = Watchers.runnableCollection(watchers);

        this.repository = repository;
    }

    // LABEL............................................................................................................

    void onLabelSaved(final SpreadsheetLabelMapping mapping) {
        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(
            mapping.label(),
            SpreadsheetEngineBasicChangesCacheStatusLabel.SAVED
        ).saved(mapping);

        if (this.isImmediate()) {
            this.refreshLabelCellReferences(cache);
        }
    }

    private void onLabelDeleted(final SpreadsheetLabelName label) {
        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(
            label,
            SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED
        ).setStatus(SpreadsheetEngineBasicChangesCacheStatusLabel.DELETED);

        if (this.isImmediate()) {
            this.refreshLabelCellReferences(cache);
        }
    }

    private void refreshAllLabels() {
        final Collection<SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> labels = this.labels.values()
            .stream()
            .filter(c -> c.status().isRefreshable())
            .collect(Collectors.toList());

        for (final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : labels) {
            this.refreshLabel(cache);
        }
    }

    private void refreshLabel(final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache) {
        final SpreadsheetLabelName label = cache.reference;

        // lazy load label
        if (cache.status().isUnloaded()) {
            cache.loadedOrMissing(
                this.context.storeRepository()
                    .labels()
                    .load(label)
                    .orElse(null)
            );
        }

        if (this.isImmediate()) {
            this.refreshLabelCellReferences(cache);
        }
    }

    private void refreshAllLabelsCellReferences() {
        final Collection<SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> caches = this.labels.values()
            .stream()
            .filter(c -> c.status().isReferenceRefreshable())
            .collect(Collectors.toList());

        for (final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : caches) {
            this.refreshLabelCellReferences(cache);
        }
    }

    private void refreshLabelCellReferences(final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> labelCache) {
        labelCache.forceReferencesRefresh();

        final SpreadsheetEngineEvaluation backup = this.setEvaluation(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE);
        try {
            for (final SpreadsheetCellReference cell : this.repository.labelReferences().findValuesById(labelCache.reference, 0, Integer.MAX_VALUE)) {
                this.getOrCreateCellCache(
                    cell,
                    SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_UNLOADED
                ).forceReferencesRefresh();
            }
        } finally {
            this.setEvaluation(backup);
        }

        labelCache.referencesRefreshed();
    }

    SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> getOrCreateLabelCache(final SpreadsheetLabelName labelName,
                                                                                                            final SpreadsheetEngineBasicChangesCacheStatus<SpreadsheetLabelName> initialStatus) {
        return this.getOrCreate(
            labelName,
            this.labels,
            initialStatus
        );
    }

    // SpreadsheetEngineBasicPrepareResponse
    final Map<SpreadsheetLabelName, SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> labels = SpreadsheetSelectionMaps.label();

    // CELL.............................................................................................................

    /**
     * Commits a loaded cell. References to the cell will be extracted at a later stage.
     */
    void onCellLoading(final SpreadsheetCell cell) {
        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell.reference(),
            SpreadsheetEngineBasicChangesCacheStatusCell.UNLOADED
        );

        cache.loadingOrMissing(cell);

        if (this.isImmediate()) {
            this.refreshCell(cache);
        }
    }

    void onCellSaved(final SpreadsheetCell cell) {
        // A SpreadsheetCell must have been evaluated
        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell.reference(),
            SpreadsheetEngineBasicChangesCacheStatusCell.SAVED
        ).saved(cell);

        if (this.isImmediate()) {
            this.refreshCell(cache);
        }
    }

    void onCellDeleted(final SpreadsheetCellReference cell) {
        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell,
            SpreadsheetEngineBasicChangesCacheStatusCell.DELETED
        ).setStatus(SpreadsheetEngineBasicChangesCacheStatusCell.DELETED);

        if (this.isImmediate()) {
            this.refreshCell(cache);
        }
    }

    private void refreshAllCells() {
        final Collection<SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell>> cells = this.cells.values()
            .stream()
            .map(SpreadsheetEngineBasicChangesCache::forceReferencesRefresh)
            .filter(c -> c.status().isRefreshable())
            .collect(Collectors.toList());

        for (final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cell : cells) {
            this.refreshCell(cell);
        }
    }

    private void refreshCell(final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache) {
        final SpreadsheetCellReference cell = cache.reference;

        final SpreadsheetEngineBasicChangesCacheStatus<SpreadsheetCellReference> status = cache.status();
        SpreadsheetCell spreadsheetCell;

        if (status.isUnloaded()) {
            spreadsheetCell = this.context.storeRepository()
                .cells()
                .load(cell)
                .orElse(null);
            cache.loadedOrMissing(spreadsheetCell);
        }

        spreadsheetCell = cache.valueOrNull();

        if (null != spreadsheetCell) {
            try {
                this.pushCell(cell);

                final SpreadsheetCell saved = this.engine.parseFormulaEvaluateValidateFormatStyleAndSave(
                    spreadsheetCell,
                    this.evaluation,
                    this, // SpreadsheetExpressionReferenceLoader
                    this.context
                );

                if (cache.status().isLoading()) {
                    cache.loaded(saved);
                }

                // might be stuck saving! because value did not change and save did not fire saved.
                if (cache.status().isSaving()) {
                    cache.saved(saved);
                }
            } finally {
                this.popCell(cell);
            }
        }
    }

    private void refreshAllCellExternalReferences() {
        final Collection<SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell>> cellCaches = this.cells.values()
            .stream()
            .map(SpreadsheetEngineBasicChangesCache::forceReferencesRefresh)
            .filter(c -> c.status().isReferenceRefreshable())
            .collect(Collectors.toList());

        for (final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache : cellCaches) {
            final SpreadsheetCellReference cell = cache.reference;

            this.removeCellExternalReferences(cell);
            this.removeFormulaReferences(cell);

            final SpreadsheetCell spreadsheetCell = cache.valueOrNull();
            if (null != spreadsheetCell) {
                this.addFormulaReferences(spreadsheetCell);
            }
            this.addCellExternalReferences(
                cell,
                SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_UNLOADED
            );

            cache.referencesRefreshed();
        }
    }

    // CELL EXTERNAL REFERENCES.........................................................................................

    private void removeCellExternalReferences(final SpreadsheetCellReference cell) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
            .removeByValue(cell);

        repository.labelReferences()
            .findIdsByValue(
                cell,
                0, // offset
                SpreadsheetEngineBasic.FIND_REFERENCES_COUNT
            ).forEach(l -> this.repository.labelReferences()
                .removeValue(
                    l,
                    cell
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

    private void addCellExternalReferences(final SpreadsheetCellReference cell,
                                           final SpreadsheetEngineBasicChangesCacheStatus<SpreadsheetCellReference> initialStatus) {
        final SpreadsheetStoreRepository repository = this.repository;

        for (final SpreadsheetCellReference cellReference : repository.cellReferences()
            .findValuesById(cell, 0, Integer.MAX_VALUE)) {
            final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
                cellReference,
                initialStatus
            );
            if (this.isImmediate()) {
                this.refreshCell(cache);
            }
        }

        for (final SpreadsheetLabelMapping labelMapping : repository.labels()
            .findLabelsWithReference(
                cell,
                0,
                SpreadsheetEngineBasic.FIND_LABELS_WITH_REFERENCE_COUNT
            )
        ) {
            this.refreshLabel(
                this.getOrCreateLabelCache(
                    labelMapping.label(),
                    SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED
                )
            );
        }

        for (final SpreadsheetCellRangeReference cellRange : repository.rangeToCells()
            .findCellRangesIncludingCell(cell)) {
            for (final SpreadsheetCellReference cellInRange : this.repository.rangeToCells().findValuesById(cellRange, 0, Integer.MAX_VALUE)) {

                final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
                    cellInRange,
                    SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_UNLOADED
                );
                if (this.isImmediate()) {
                    this.refreshCell(cache);
                }
            }
        }
    }

    // FORMULA REFERENCES...............................................................................................

    /**
     * Removes any cell references to cell references in the cell {@link SpreadsheetFormula}.
     */
    private void removeFormulaReferences(final SpreadsheetCellReference cell) {
        this.repository.cellReferences()
            .removeByValue(cell);

        this.repository.labelReferences()
            .removeByValue(cell);

        final SpreadsheetCellRangeStore cellRangeStore = this.repository.rangeToCells();

        for (final SpreadsheetCellRangeReference cellRange : cellRangeStore.findCellRangesIncludingCell(cell)) {
            cellRangeStore.removeValue(
                cellRange,
                cell
            );
        }
    }

    /**
     * Records any {@link walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference} within the given {@link SpreadsheetFormula}.
     */
    private void addFormulaReferences(final SpreadsheetCell spreadsheetCell) {
        final SpreadsheetCellReference cell = spreadsheetCell.reference();

        spreadsheetCell.formula()
            .consumeSpreadsheetExpressionReferences(
                SpreadsheetEngineBasicChangesAddFormulaReferenceSpreadsheetSelectionVisitor.with(
                    cell,
                    this.repository
                )::accept
            );
    }

    /**
     * Returns a {@link SpreadsheetCellRangeReference} that includes all saved & deleted {@link SpreadsheetCellReference} and {@link SpreadsheetLabelName}.
     */
    Optional<SpreadsheetCellRangeReference> changesCellRange() {
        final Set<SpreadsheetCellReference> cells = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        cells.addAll(
            this.cells.keySet()
        );

        for (final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : this.labels.values()) {
            if (false == cache.status().isUnloaded()) {
                final SpreadsheetLabelMapping labelMapping = cache.valueOrNull();
                if (null != labelMapping) {
                    final SpreadsheetCellReferenceOrRange cellOrRange = this.repository.labels()
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

    SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> getOrCreateCellCache(final SpreadsheetCellReference cell,
                                                                                                       final SpreadsheetEngineBasicChangesCacheStatus<SpreadsheetCellReference> initialStatus) {
        return this.getOrCreate(
            cell,
            this.cells,
            initialStatus
        );
    }

    // SpreadsheetEngineBasicPrepareResponse
    final Map<SpreadsheetCellReference, SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell>> cells = SpreadsheetSelectionMaps.cell();

    // COLUMN...........................................................................................................

    void onColumnSaved(final SpreadsheetColumn column) {
        this.getOrCreateColumnCache(
            column.reference(),
            SpreadsheetEngineBasicChangesCacheStatusColumn.SAVED
        ).saved(column);
    }

    void onColumnDeleted(final SpreadsheetColumnReference column) {
        this.getOrCreateColumnCache(column, SpreadsheetEngineBasicChangesCacheStatusColumn.DELETED)
            .deleted();
    }

    private SpreadsheetEngineBasicChangesCache<SpreadsheetColumnReference, SpreadsheetColumn> getOrCreateColumnCache(final SpreadsheetColumnReference column,
                                                                                                                     final SpreadsheetEngineBasicChangesCacheStatus<SpreadsheetColumnReference> initialStatus) {
        return this.getOrCreate(
            column,
            this.columns,
            initialStatus
        );
    }

    // SpreadsheetEngineBasicPrepareResponse
    final Map<SpreadsheetColumnReference, SpreadsheetEngineBasicChangesCache<SpreadsheetColumnReference, SpreadsheetColumn>> columns = SpreadsheetSelectionMaps.column();

    // ROW..............................................................................................................

    void onRowSaved(final SpreadsheetRow row) {
        this.getOrCreateRowCache(
            row.reference(),
            SpreadsheetEngineBasicChangesCacheStatusRow.SAVED
        ).saved(row);
    }

    void onRowDeleted(final SpreadsheetRowReference row) {
        this.getOrCreateRowCache(
            row,
            SpreadsheetEngineBasicChangesCacheStatusRow.DELETED
        ).setStatus(SpreadsheetEngineBasicChangesCacheStatusRow.DELETED);
    }

    private SpreadsheetEngineBasicChangesCache<SpreadsheetRowReference, SpreadsheetRow> getOrCreateRowCache(final SpreadsheetRowReference row,
                                                                                                            final SpreadsheetEngineBasicChangesCacheStatus<SpreadsheetRowReference> initialStatus) {
        return this.getOrCreate(
            row,
            this.rows,
            initialStatus
        );
    }

    // SpreadsheetEngineBasicPrepareResponse
    final Map<SpreadsheetRowReference, SpreadsheetEngineBasicChangesCache<SpreadsheetRowReference, SpreadsheetRow>> rows = SpreadsheetSelectionMaps.row();

    // shared...........................................................................................................

    private <S extends SpreadsheetSelection, V> SpreadsheetEngineBasicChangesCache<S, V> getOrCreate(final S selection,
                                                                                                     final Map<S, SpreadsheetEngineBasicChangesCache<S, V>> map,
                                                                                                     final SpreadsheetEngineBasicChangesCacheStatus<S> initialStatus) {
        final int before = map.size();

        final SpreadsheetEngineBasicChangesCache<S, V> cache = SpreadsheetEngineBasicChangesCache.getOrCreate(
            selection,
            map,
            initialStatus
        );

        // increment the change counter if a new load was attempted.
        //if (cache.status() == initialStatus) {

        // if a new cache entry was added increment changes
        if (map.size() > before) {
            this.incrementChanges();
        }

        return cache;
    }

    // COMMIT...........................................................................................................

    /**
     * Commits any outstanding cell, label, reference type operations.
     */
    void commit() {
        boolean changed;

        do {
            final int changesBeforeCount = this.changes;

            this.refreshAllLabels();
            this.refreshAllCells();
            this.refreshAllLabelsCellReferences();
            this.refreshAllCellExternalReferences();

            changed = this.changes != changesBeforeCount;
        } while (changed);
    }

    void incrementChanges() {
        this.changes++;
    }

    /**
     * Incremented each time a change to a cell or label happens.
     */
    int changes;

    // MISC.............................................................................................................

    final SpreadsheetEngineBasic engine;

    private SpreadsheetEngineEvaluation setEvaluation(final SpreadsheetEngineEvaluation evaluation) {
        final SpreadsheetEngineEvaluation backup = this.evaluation;
        if (backup != evaluation) {
            this.evaluation = evaluation;
        }
        return backup;
    }

    private SpreadsheetEngineEvaluation evaluation;

    // VisibleFor SpreadsheetEngineBasic
    final Set<SpreadsheetDeltaProperties> deltaProperties;

    // mode.............................................................................................................

    boolean isBatch() {
        return SpreadsheetEngineBasicChangesMode.BATCH == this.mode;
    }

    SpreadsheetEngineBasicChangesMode setMode(final SpreadsheetEngineBasicChangesMode mode) {
        SpreadsheetEngineBasicChangesMode backup = this.mode;

        this.mode = mode;
        return backup;
    }

    SpreadsheetEngineBasicChangesMode setImmediate() {
        return this.setMode(SpreadsheetEngineBasicChangesMode.IMMEDIATE);
    }

    boolean isImmediate() {
        return SpreadsheetEngineBasicChangesMode.IMMEDIATE == this.mode;
    }

    SpreadsheetEngineBasicChangesMode mode;

    // SpreadsheetEngineContext.........................................................................................

    final SpreadsheetEngineContext context;

    final SpreadsheetStoreRepository repository;

    private void pushCell(final SpreadsheetCellReference cell) {
        if (false == this.scopedCells.add(cell)) {
            throw SpreadsheetError.cycle(cell)
                .exception();
        }
    }

    private void popCell(final SpreadsheetCellReference cell) {
        this.scopedCells.remove(cell);
    }

    /**
     * Accumulates {@link SpreadsheetCellReference} supporting the detection of cycles.
     */
    private final Set<SpreadsheetCellReference> scopedCells;

    /**
     * Removes previously added watchers.
     */
    void close() {
        this.watchers.run();
    }

    private final Runnable watchers;

    // SpreadsheetExpressionReferenceLoader.............................................................................

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        final SpreadsheetEngineBasicChangesMode backup = this.setImmediate();
        final SpreadsheetEngineEvaluation backupEvaluation = this.setEvaluation(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE);

        final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell,
            SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_UNLOADED
        );
        try {
            this.refreshCell(cache);
        } finally {
            this.setEvaluation(backupEvaluation);
            this.setMode(backup);
        }

        return Optional.ofNullable(
            cache.valueOrNull()
        );
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(context, "context");

        final SpreadsheetEngineBasicChangesMode backupMode = this.setImmediate();
        final SpreadsheetEngineEvaluation backupEvaluation = this.setEvaluation(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE);
        try {
            for (final SpreadsheetCell cell : this.context.storeRepository()
                .cells()
                .loadCellRange(range)) {

                SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
                    cell.reference(),
                    SpreadsheetEngineBasicChangesCacheStatusCell.REFERENCE_UNLOADED
                );

                cache.loaded(cell);
            }

            final SortedSet<SpreadsheetCell> all = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

            for (final SpreadsheetEngineBasicChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache : this.cells.values()) {
                final SpreadsheetCell spreadsheetCell = cache.valueOrNull();
                if (null != spreadsheetCell) {
                    all.add(spreadsheetCell);
                }
            }

            return SortedSets.immutable(all);
        } finally {
            this.setEvaluation(backupEvaluation);
            this.setMode(backupMode);
        }
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        final SpreadsheetEngineBasicChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(
            labelName,
            SpreadsheetEngineBasicChangesCacheStatusLabel.REFERENCE_UNLOADED
        );

        final SpreadsheetEngineBasicChangesMode backup = this.setImmediate();

        try {
            this.refreshLabel(cache);

            return Optional.ofNullable(
                cache.valueOrNull()
            );

        } finally {
            this.setMode(backup);
        }
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .labelSeparator(": ")
            .value(this.evaluation)
            .label("scopedCells")
            .value(this.scopedCells)
            .label("cells")
            .value(this.cells)
            .label("columns")
            .value(this.columns)
            .label("rows")
            .value(this.rows)
            .build();
    }
}
