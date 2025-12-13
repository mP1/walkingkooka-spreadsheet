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
import walkingkooka.collect.set.Sets;
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
import walkingkooka.spreadsheet.store.ReferenceAndSpreadsheetCellReference;
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
 * Aggregates all the updated cells that result parse an operation by {@link BasicSpreadsheetEngine}.
 * <br>
 * Note that cell reference save events are not watched.
 */
final class BasicSpreadsheetEngineChanges implements SpreadsheetExpressionReferenceLoader {


    static BasicSpreadsheetEngineChanges with(final BasicSpreadsheetEngine engine,
                                              final SpreadsheetEngineEvaluation evaluation,
                                              final Set<SpreadsheetDeltaProperties> deltaProperties,
                                              final BasicSpreadsheetEngineChangesMode mode,
                                              final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineChanges(
            engine,
            evaluation,
            deltaProperties,
            mode,
            context
        );
    }

    private BasicSpreadsheetEngineChanges(final BasicSpreadsheetEngine engine,
                                          final SpreadsheetEngineEvaluation evaluation,
                                          final Set<SpreadsheetDeltaProperties> deltaProperties,
                                          final BasicSpreadsheetEngineChangesMode mode,
                                          final SpreadsheetEngineContext context) {
        super();

        this.engine = engine;
        this.evaluation = evaluation;
        this.deltaProperties = deltaProperties;
        this.mode = mode;
        this.context = context;

        this.scopedCells = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        final SpreadsheetStoreRepository repository = context.storeRepository();

        final SpreadsheetCellStore cellStore = repository.cells();

        final List<Runnable> watchers = Lists.array();
        if (deltaProperties.contains(SpreadsheetDeltaProperties.CELLS)) {
            watchers.add(
                cellStore.addSaveWatcher(this::onCellSaved)
            );
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_CELLS)) {
            watchers.add(
                cellStore.addDeleteWatcher(this::onCellDeleted)
            );
        }

        final SpreadsheetColumnStore columnStore = repository.columns();

        if (deltaProperties.contains(SpreadsheetDeltaProperties.COLUMNS)) {
            watchers.add(
                columnStore.addSaveWatcher(this::onColumnSaved)
            );
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_CELLS)) {
            watchers.add(
                columnStore.addDeleteWatcher(this::onColumnDeleted)
            );
        }

        final SpreadsheetLabelStore labelStore = repository.labels();
        if (deltaProperties.contains(SpreadsheetDeltaProperties.LABELS)) {
            watchers.add(
                labelStore.addSaveWatcher(this::onLabelSaved)
            );
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_LABELS)) {
            watchers.add(
                labelStore.addDeleteWatcher(this::onLabelDeleted)
            );
        }

        final SpreadsheetRowStore rowStore = repository.rows();
        if (deltaProperties.contains(SpreadsheetDeltaProperties.ROWS)) {
            watchers.add(
                rowStore.addSaveWatcher(this::onRowSaved)
            );
        }
        if (deltaProperties.contains(SpreadsheetDeltaProperties.DELETED_ROWS)) {
            watchers.add(
                rowStore.addDeleteWatcher(this::onRowDeleted)
            );
        }

        this.watchers = Watchers.runnableCollection(watchers);

        this.repository = repository;
    }

    // LABEL............................................................................................................

    void onLabelSaved(final SpreadsheetLabelMapping mapping) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(
            mapping.label(),
            BasicSpreadsheetEngineChangesCacheStatusLabel.SAVED
        ).saved(mapping);

        if (this.isImmediate()) {
            this.refreshLabelCellReferences(cache);
        }
    }

    private void onLabelDeleted(final SpreadsheetLabelName label) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(
            label,
            BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED
        ).setStatus(BasicSpreadsheetEngineChangesCacheStatusLabel.DELETED);

        if (this.isImmediate()) {
            this.refreshLabelCellReferences(cache);
        }
    }

    private void refreshAllLabels() {
        final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> labels = this.labels.values()
            .stream()
            .filter(c -> c.status().isRefreshable())
            .collect(Collectors.toList());

        for (final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : labels) {
            this.refreshLabel(cache);
        }
    }

    private void refreshLabel(final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache) {
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
        final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> caches = this.labels.values()
            .stream()
            .filter(c -> c.status().isReferenceRefreshable())
            .collect(Collectors.toList());

        for (final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : caches) {
            this.refreshLabelCellReferences(cache);
        }
    }

    private void refreshLabelCellReferences(final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> labelCache) {
        labelCache.forceReferencesRefresh();

        final SpreadsheetEngineEvaluation backup = this.setEvaluation(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE);
        try {
            for (final SpreadsheetCellReference cell : this.repository.labelReferences()
                .load(labelCache.reference).orElse(Sets.empty())) {
                this.getOrCreateCellCache(
                    cell,
                    BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED
                ).forceReferencesRefresh();
            }
        } finally {
            this.setEvaluation(backup);
        }

        labelCache.referencesRefreshed();
    }

    BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> getOrCreateLabelCache(final SpreadsheetLabelName labelName,
                                                                                                            final BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetLabelName> initialStatus) {
        return this.getOrCreate(
            labelName,
            this.labels,
            initialStatus
        );
    }

    // BasicSpreadsheetEnginePrepareResponse
    final Map<SpreadsheetLabelName, BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> labels = SpreadsheetSelectionMaps.label();

    // CELL.............................................................................................................

    /**
     * Commits a loaded cell. References to the cell will be extracted at a later stage.
     */
    void onCellLoading(final SpreadsheetCell cell) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell.reference(),
            BasicSpreadsheetEngineChangesCacheStatusCell.UNLOADED
        );

        cache.loadingOrMissing(cell);

        if (this.isImmediate()) {
            this.refreshCell(cache);
        }
    }

    void onCellSaved(final SpreadsheetCell cell) {
        // A SpreadsheetCell must have been evaluated
        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell.reference(),
            BasicSpreadsheetEngineChangesCacheStatusCell.SAVED
        ).saved(cell);

        if (this.isImmediate()) {
            this.refreshCell(cache);
        }
    }

    void onCellDeleted(final SpreadsheetCellReference cell) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell,
            BasicSpreadsheetEngineChangesCacheStatusCell.DELETED
        ).setStatus(BasicSpreadsheetEngineChangesCacheStatusCell.DELETED);

        if (this.isImmediate()) {
            this.refreshCell(cache);
        }
    }

    private void refreshAllCells() {
        final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell>> cells = this.cells.values()
            .stream()
            .map(BasicSpreadsheetEngineChangesCache::forceReferencesRefresh)
            .filter(c -> c.status().isRefreshable())
            .collect(Collectors.toList());

        for (final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cell : cells) {
            this.refreshCell(cell);
        }
    }

    private void refreshCell(final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache) {
        final SpreadsheetCellReference cell = cache.reference;

        final BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetCellReference> status = cache.status();
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
        final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell>> cellCaches = this.cells.values()
            .stream()
            .map(BasicSpreadsheetEngineChangesCache::forceReferencesRefresh)
            .filter(c -> c.status().isReferenceRefreshable())
            .collect(Collectors.toList());

        for (final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache : cellCaches) {
            final SpreadsheetCellReference cell = cache.reference;

            this.removeCellExternalReferences(cell);
            this.removeFormulaReferences(cell);

            final SpreadsheetCell spreadsheetCell = cache.valueOrNull();
            if (null != spreadsheetCell) {
                this.addFormulaReferences(spreadsheetCell);
            }
            this.addCellExternalReferences(
                cell,
                BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED
            );

            cache.referencesRefreshed();
        }
    }

    // CELL EXTERNAL REFERENCES.........................................................................................

    private void removeCellExternalReferences(final SpreadsheetCellReference cell) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
            .delete(cell);

        repository.labelReferences()
            .findReferencesWithCell(
                cell,
                0, // offset
                BasicSpreadsheetEngine.FIND_REFERENCES_COUNT
            ).forEach(l -> this.repository.labelReferences()
                .removeCell(
                    ReferenceAndSpreadsheetCellReference.with(
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

    private void addCellExternalReferences(final SpreadsheetCellReference cell,
                                           final BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetCellReference> initialStatus) {
        final SpreadsheetStoreRepository repository = this.repository;

        for (final SpreadsheetCellReference cellReference : repository.cellReferences()
            .load(cell)
            .orElse(Sets.empty())) {
            final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
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
                BasicSpreadsheetEngine.FIND_LABELS_WITH_REFERENCE_COUNT
            )
        ) {
            this.refreshLabel(
                this.getOrCreateLabelCache(
                    labelMapping.label(),
                    BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED
                )
            );
        }

        for (final SpreadsheetCellRangeReference cellRange : repository.rangeToCells()
            .findCellRangesIncludingCell(cell)) {
            for (final SpreadsheetCellReference cellInRange : this.repository.rangeToCells()
                .load(cellRange)
                .orElse(Lists.empty())) {

                final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
                    cellInRange,
                    BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED
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
            .removeReferencesWithCell(cell);

        this.repository.labelReferences()
            .removeReferencesWithCell(cell);

        final SpreadsheetCellRangeStore<SpreadsheetCellReference> cellRangeStore = this.repository.rangeToCells();

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
                BasicSpreadsheetEngineChangesAddFormulaReferenceSpreadsheetSelectionVisitor.with(
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

        for (final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : this.labels.values()) {
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

    BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> getOrCreateCellCache(final SpreadsheetCellReference cell,
                                                                                                       final BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetCellReference> initialStatus) {
        return this.getOrCreate(
            cell,
            this.cells,
            initialStatus
        );
    }

    // BasicSpreadsheetEnginePrepareResponse
    final Map<SpreadsheetCellReference, BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell>> cells = SpreadsheetSelectionMaps.cell();

    // COLUMN...........................................................................................................

    void onColumnSaved(final SpreadsheetColumn column) {
        this.getOrCreateColumnCache(
            column.reference(),
            BasicSpreadsheetEngineChangesCacheStatusColumn.SAVED
        ).saved(column);
    }

    void onColumnDeleted(final SpreadsheetColumnReference column) {
        this.getOrCreateColumnCache(column, BasicSpreadsheetEngineChangesCacheStatusColumn.DELETED)
            .deleted();
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetColumnReference, SpreadsheetColumn> getOrCreateColumnCache(final SpreadsheetColumnReference column,
                                                                                                                     final BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetColumnReference> initialStatus) {
        return this.getOrCreate(
            column,
            this.columns,
            initialStatus
        );
    }

    // BasicSpreadsheetEnginePrepareResponse
    final Map<SpreadsheetColumnReference, BasicSpreadsheetEngineChangesCache<SpreadsheetColumnReference, SpreadsheetColumn>> columns = SpreadsheetSelectionMaps.column();

    // ROW..............................................................................................................

    void onRowSaved(final SpreadsheetRow row) {
        this.getOrCreateRowCache(
            row.reference(),
            BasicSpreadsheetEngineChangesCacheStatusRow.SAVED
        ).saved(row);
    }

    void onRowDeleted(final SpreadsheetRowReference row) {
        this.getOrCreateRowCache(
            row,
            BasicSpreadsheetEngineChangesCacheStatusRow.DELETED
        ).setStatus(BasicSpreadsheetEngineChangesCacheStatusRow.DELETED);
    }

    private BasicSpreadsheetEngineChangesCache<SpreadsheetRowReference, SpreadsheetRow> getOrCreateRowCache(final SpreadsheetRowReference row,
                                                                                                            final BasicSpreadsheetEngineChangesCacheStatus<SpreadsheetRowReference> initialStatus) {
        return this.getOrCreate(
            row,
            this.rows,
            initialStatus
        );
    }

    // BasicSpreadsheetEnginePrepareResponse
    final Map<SpreadsheetRowReference, BasicSpreadsheetEngineChangesCache<SpreadsheetRowReference, SpreadsheetRow>> rows = SpreadsheetSelectionMaps.row();

    // shared...........................................................................................................

    private <S extends SpreadsheetSelection, V> BasicSpreadsheetEngineChangesCache<S, V> getOrCreate(final S selection,
                                                                                                     final Map<S, BasicSpreadsheetEngineChangesCache<S, V>> map,
                                                                                                     final BasicSpreadsheetEngineChangesCacheStatus<S> initialStatus) {
        final int before = map.size();

        final BasicSpreadsheetEngineChangesCache<S, V> cache = BasicSpreadsheetEngineChangesCache.getOrCreate(
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

    final BasicSpreadsheetEngine engine;

    private SpreadsheetEngineEvaluation setEvaluation(final SpreadsheetEngineEvaluation evaluation) {
        final SpreadsheetEngineEvaluation backup = this.evaluation;
        if (backup != evaluation) {
            this.evaluation = evaluation;
        }
        return backup;
    }

    private SpreadsheetEngineEvaluation evaluation;

    // VisibleFor BasicSpreadsheetEngine
    final Set<SpreadsheetDeltaProperties> deltaProperties;

    // mode.............................................................................................................

    boolean isBatch() {
        return BasicSpreadsheetEngineChangesMode.BATCH == this.mode;
    }

    BasicSpreadsheetEngineChangesMode setMode(final BasicSpreadsheetEngineChangesMode mode) {
        BasicSpreadsheetEngineChangesMode backup = this.mode;

        this.mode = mode;
        return backup;
    }

    BasicSpreadsheetEngineChangesMode setImmediate() {
        return this.setMode(BasicSpreadsheetEngineChangesMode.IMMEDIATE);
    }

    boolean isImmediate() {
        return BasicSpreadsheetEngineChangesMode.IMMEDIATE == this.mode;
    }

    BasicSpreadsheetEngineChangesMode mode;

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

        final BasicSpreadsheetEngineChangesMode backup = this.setImmediate();
        final SpreadsheetEngineEvaluation backupEvaluation = this.setEvaluation(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE);

        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
            cell,
            BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED
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

        final BasicSpreadsheetEngineChangesMode backupMode = this.setImmediate();
        final SpreadsheetEngineEvaluation backupEvaluation = this.setEvaluation(SpreadsheetEngineEvaluation.FORCE_RECOMPUTE);
        try {
            for (final SpreadsheetCell cell : this.context.storeRepository()
                .cells()
                .loadCellRange(range)) {

                BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(
                    cell.reference(),
                    BasicSpreadsheetEngineChangesCacheStatusCell.REFERENCE_UNLOADED
                );

                cache.loaded(cell);
            }

            final SortedSet<SpreadsheetCell> all = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

            for (final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache : this.cells.values()) {
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

        final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(
            labelName,
            BasicSpreadsheetEngineChangesCacheStatusLabel.REFERENCE_UNLOADED
        );

        final BasicSpreadsheetEngineChangesMode backup = this.setImmediate();

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
