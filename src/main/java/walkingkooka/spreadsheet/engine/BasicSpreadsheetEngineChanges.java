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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
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
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.TargetAndSpreadsheetCellReference;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.watch.Watchers;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aggregates all the updated cells that result parse an operation by {@link BasicSpreadsheetEngine}.
 * <br>
 * Note that cell reference save events are not watched.
 */
final class BasicSpreadsheetEngineChanges implements SpreadsheetExpressionReferenceLoader {

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
        this.changes++;
        this.mode.onCellSaved(
                cell,
                this
        );
    }

    private void onCellDeleted(final SpreadsheetCellReference cell) {
        this.changes++;
        this.mode.onCellDeleted(
                cell,
                this
        );
    }

    private void onColumnSaved(final SpreadsheetColumn column) {
        this.changes++;
        this.mode.onColumnSaved(
                column,
                this
        );
    }

    private void onColumnDeleted(final SpreadsheetColumnReference column) {
        this.changes++;
        this.mode.onColumnDeleted(
                column,
                this
        );
    }

    private void onLabelSaved(final SpreadsheetLabelMapping mapping) {
        this.changes++;
        this.mode.onLabelSaved(
                mapping,
                this
        );
    }

    private void onLabelDeleted(final SpreadsheetLabelName label) {
        this.changes++;
        this.mode.onLabelDeleted(
                label,
                this
        );
    }

    private void onRowSaved(final SpreadsheetRow row) {
        this.changes++;
        this.mode.onRowSaved(
                row,
                this
        );
    }

    private void onRowDeleted(final SpreadsheetRowReference row) {
        this.changes++;
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
        this.getOrCreateColumnCache(column.reference())
                .save(column);
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
                //null,
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
        this.getOrCreateRowCache(row.reference())
                .save(row);
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
                this.rows
        );
    }

    final Map<SpreadsheetRowReference, BasicSpreadsheetEngineChangesCache<SpreadsheetRowReference, SpreadsheetRow>> rows = Maps.sorted();

    // CELL SAVE........................................................................................................

    /**
     * Accepts a just saved cell, parsing the formula adding external references and then batching references to this cell.
     */
    void onCellSavedImmediate(final SpreadsheetCell cell) {
        final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(cell.reference())
                .save(cell);

        if (false == cache.committed) {
            this.refreshSavedCell(
                    cell.reference(),
                    cache
            );
        }
    }

    void onCellSavedBatch(final SpreadsheetCell cell) {
        this.getOrCreateCellCache(cell.reference())
                .save(cell);
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

    private void refreshSavedCell(final SpreadsheetCellReference cell,
                                  final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache) {
        SpreadsheetCell spreadsheetCell = null;

        if (false == cache.committed) {
            spreadsheetCell = this.repository.cells()
                    .load(cell)
                    .orElse(null);
            if (null != spreadsheetCell) {
                // cache

                spreadsheetCell = this.engine.parseFormulaEvaluateFormatStyleAndSave(
                        spreadsheetCell,
                        SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                        this, // SpreadsheetExpressionReferenceLoader
                        this.spreadsheetEngineContext(
                                spreadsheetCell,
                                this.context.spreadsheetExpressionEvaluationContext(
                                        Optional.of(spreadsheetCell),
                                        this // SpreadsheetExpressionReferenceLoader
                                )
                        )
                );
            }

            if (null != spreadsheetCell) {
                this.onCellLoad(spreadsheetCell);
            }
        }

        this.removeCellExternalReferences(cell);
        if (null != spreadsheetCell) {
            this.addFormulaReferences(spreadsheetCell);
        }
        this.addCellExternalReferences(cell);

        // must be set last because #removeReferences will perform deletes which will clear cache.committed.
        cache.committed = true;
    }

    /**
     * Commits a loaded cell. References to the cell will be extracted at a later stage.
     */
    void onCellLoad(final SpreadsheetCell cell) {
        this.getOrCreateCellCache(cell.reference())
                .load(cell)
                .setCommitted(true); // assume SpreadsheetEngineEngine.loadCell "load"
    }

    /**
     * Tests if the given {@link SpreadsheetCellReference} has been already been loaded in this request.
     */
    boolean isCellLoaded(final SpreadsheetCellReference cell) {
        return this.cells.containsKey(cell);
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

    BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> getOrCreateCellCache(final SpreadsheetCellReference cell) {
        return BasicSpreadsheetEngineChangesCache.getOrCreate(
                cell,
                this.cells
        );
    }

    private void refreshDeletedCell(final SpreadsheetCellReference cell,
                                    final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache) {
        this.removeCellExternalReferences(cell);
        this.addCellExternalReferences(cell);

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
            if (cache.isLoadOrSave()) {
                final SpreadsheetLabelMapping labelMapping = cache.value();
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
        final SpreadsheetLabelName labelName = mapping.label();
        BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache = this.getOrCreateLabelCache(labelName)
                .save(mapping);
        if (false == cache.committed) {
            this.refreshSavedLabel(
                    labelName,
                    cache
            );
        }
    }

    void onLabelSavedBatch(final SpreadsheetLabelMapping mapping) {
        this.getOrCreateLabelCache(mapping.label())
                .save(mapping);
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
                .ifPresent(r -> r.forEach(this::refreshSavedCell));

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
                this.labels
        );
    }

    /**
     * Records all updated and deleted labels. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     * A null value indicates the label was deleted.
     */
    final Map<SpreadsheetLabelName, BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> labels = Maps.sorted();

    private void refreshDeletedLabel(final SpreadsheetLabelName label,
                                     final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache) {
        this.repository.labelReferences()
                .load(label)
                .ifPresent(r -> r.forEach(this::refreshSavedCell));

        cache.committed = true;
    }

    private void removeCellExternalReferences(final SpreadsheetCellReference cell) {
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
                                this.repository
                        )::accept
                );
    }

    private void addCellExternalReferences(final SpreadsheetCellReference cell) {
        final SpreadsheetStoreRepository repository = this.repository;

        repository.cellReferences()
                .loadTargets(cell)
                .forEach(this::refreshCellExternalReference);

        repository.labels()
                .labels(cell)
                .forEach(m -> this.refreshSavedLabel(m.label()));

        repository.rangeToCells()
                .findCellRangesIncludingCell(cell)
                .forEach(this::refreshRange);
    }

    private void refreshCellExternalReference(final SpreadsheetCellReference cell) {
        this.getOrCreateCellCache(cell)
                .load()
                .setCommitted(false);
    }

    // COMMIT...........................................................................................................

    /**
     * Commits any outstanding cell, label, reference type operations.
     */
    void commit() {
        boolean changed;

        do {
            final int changesBeforeCount = this.changes;
            {
                final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell>> workCells = this.cells.values()
                        .stream()
                        .filter(c -> false == c.committed)
                        .collect(Collectors.toList());

                for (final BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache : workCells) {
                    final SpreadsheetCellReference cell = cache.reference;
                    if (cache.isLoadOrSave()) {
                        this.refreshSavedCell(
                                cell,
                                cache
                        );
                    } else {
                        if (cache.isDelete()) {
                            this.refreshDeletedCell(
                                    cell,
                                    cache
                            );
                        }
                    }
                }
            }

            {
                final Collection<BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping>> workLabels = this.labels.values()
                        .stream()
                        .filter(c -> false == c.committed)
                        .collect(Collectors.toList());

                for (final BasicSpreadsheetEngineChangesCache<SpreadsheetLabelName, SpreadsheetLabelMapping> cache : workLabels) {
                    final SpreadsheetLabelName label = cache.reference;
                    if (cache.isLoadOrSave()) {
                        this.refreshSavedLabel(
                                label,
                                cache
                        );
                    } else {
                        if (cache.isDelete()) {
                            this.refreshDeletedLabel(
                                    label,
                                    cache
                            );
                        }
                    }
                }
            }

            changed = this.changes != changesBeforeCount;
        } while (changed);
    }

    /**
     * Incremented each time a change to a cell or label happens.
     */
    int changes;

    // MISC.............................................................................................................

    // VisibleFor BasicSpreadsheetEngine
    final Set<SpreadsheetDeltaProperties> deltaProperties;

    final BasicSpreadsheetEngine engine;

    private SpreadsheetEngineContext spreadsheetEngineContext(final SpreadsheetCell cell,
                                                              final SpreadsheetExpressionEvaluationContext context) {
        return BasicSpreadsheetEngineSpreadsheetEngineContext.with(
                this.context, // SpreadsheetEngineContext
                context // SpreadsheetExpressionEvaluationContext
        );
    }

    final SpreadsheetEngineContext context;

    final SpreadsheetStoreRepository repository;

    /**
     * Removes previously added watchers.
     */
    void close() {
        // some might be null if the corresponding {@link SpreadsheetDeltaProperties} was clear.
        Watchers.removeAllThenFail(
                this.onSaveCell,
                this.onDeleteCell,
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

    private final Runnable onSaveColumn;
    private final Runnable onDeleteColumn;

    private final Runnable onSaveLabel;
    private final Runnable onDeleteLabel;

    private final Runnable onSaveRow;
    private final Runnable onDeleteRow;

    // SpreadsheetExpressionReferenceLoader.............................................................................

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        // first load the cell, then parse, evaluate and format if it is present.
        BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(cell);
        SpreadsheetCell spreadsheetCell;

        if (cache.isLoadOrSave() && cache.committed) {
            spreadsheetCell = cache.value();
        } else {
            if (cache.isDeleteOrMissing()) {
                spreadsheetCell = null;
            } else {
                spreadsheetCell = this.repository.cells()
                        .load(cell)
                        .orElse(null);
                if (null != spreadsheetCell) {
                    spreadsheetCell = this.engine.loadCell(
                            spreadsheetCell,
                            SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                            this,
                            this.spreadsheetEngineContext(
                                    spreadsheetCell,
                                    context
                            )
                    );
                    cache.loadCellReference(spreadsheetCell);
                } else {
                    cache.missing();
                }
            }
        }

        return Optional.ofNullable(spreadsheetCell);
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                              final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(context, "context");

        final Set<SpreadsheetCell> all = SortedSets.tree();

        for (final SpreadsheetCellReference cell : range) {
            BasicSpreadsheetEngineChangesCache<SpreadsheetCellReference, SpreadsheetCell> cache = this.getOrCreateCellCache(cell);
            SpreadsheetCell spreadsheetCell;

            if (cache.isLoadOrSave() && cache.committed) {
                spreadsheetCell = cache.value();
            } else {
                if (cache.isDeleteOrMissing()) {
                    spreadsheetCell = null;
                } else {
                    spreadsheetCell = this.repository.cells()
                            .load(cell)
                            .orElse(null);
                    if (null != spreadsheetCell) {
                        spreadsheetCell = this.engine.loadCell(
                                spreadsheetCell,
                                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                                this,
                                this.spreadsheetEngineContext(
                                        spreadsheetCell,
                                        context
                                )
                        );
                        cache.loadCellReference(spreadsheetCell);
                    } else {
                        cache.missing();
                    }
                }
            }

            if (null != spreadsheetCell) {
                all.add(spreadsheetCell);
            }
        }

        return Sets.readOnly(all);
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        return this.repository.labels()
                .load(labelName);
    }

    // Object...........................................................................................................

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
