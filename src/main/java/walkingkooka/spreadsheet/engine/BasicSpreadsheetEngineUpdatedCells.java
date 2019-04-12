package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.Watchers;
import walkingkooka.spreadsheet.store.reference.TargetAndSpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Aggregates all the updated cells that result from an operation by {@link BasicSpreadsheetEngine}.
 */
final class BasicSpreadsheetEngineUpdatedCells implements AutoCloseable {

    static BasicSpreadsheetEngineUpdatedCells with(final BasicSpreadsheetEngine engine,
                                                   final SpreadsheetEngineContext context,
                                                   final BasicSpreadsheetEngineUpdatedCellsMode mode) {
        return new BasicSpreadsheetEngineUpdatedCells(engine, context, mode);
    }

    private BasicSpreadsheetEngineUpdatedCells(final BasicSpreadsheetEngine engine,
                                               final SpreadsheetEngineContext context,
                                               final BasicSpreadsheetEngineUpdatedCellsMode mode) {
        super();

        this.mode = mode;

        this.onSaveCell = engine.cellStore.addSaveWatcher(this::onCellSaved);
        this.onDeleteCell = engine.cellStore.addDeleteWatcher(this::onCellDeleted);
        this.onDeleteCellReferences = engine.cellReferencesStore.addRemoveReferenceWatcher(this::onCellReferenceDeleted);

        this.engine = engine;
        this.context = context;
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

    // IMMEDIATE.......................................................................................................

    /**
     * Accepts a just saved cell, parsing the formula adding external references and then batching references to this cell.
     */
    void onCellSavedImmediate(final SpreadsheetCell cell) {
        final SpreadsheetCellReference reference = cell.reference();
        if (null == this.updated.put(reference, cell)) {
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
                .ifPresent(e -> BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor.processReferences(e,
                        cell,
                        this.engine));
    }

    /**
     * Invoked whenever a cell is deleted or replaced.
     */
    void onCellDeletedImmediate(final SpreadsheetCellReference cell) {
        this.removePreviousExpressionReferences(cell);
        this.batchReferrers(cell);
    }

    private void removePreviousExpressionReferences(final SpreadsheetCellReference cell) {
        final BasicSpreadsheetEngine engine = this.engine;

        engine.cellReferencesStore.delete(cell);
        engine.labelReferencesStore.loadReferred(cell)
                .forEach(l -> engine.labelReferencesStore.removeReference(TargetAndSpreadsheetCellReference.with(l, cell)));
        engine.rangeToCellStore.rangesWithValue(cell)
                .forEach(r -> engine.rangeToCellStore.removeValue(r, cell));
    }

    void onCellReferenceDeletedImmediate(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.batchReferrers(targetAndReference.target());
    }

    // BATCH MODE....................................................................................................

    void onCellSavedBatch(final SpreadsheetCell cell) {
        this.batchCell(cell.reference());
    }

    void onCellDeletedBatch(final SpreadsheetCellReference cell) {
        this.batchCell(cell);
    }

    void onCellReferenceDeletedBatch(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        throw new UnsupportedOperationException();
    }

    // REFRESH UPDATED ................................................................................................

    /**
     * Completes any outstanding refreshes.
     */
    Set<SpreadsheetCell> refreshUpdated() {
        this.mode = BasicSpreadsheetEngineUpdatedCellsMode.IMMEDIATE;

        for (; ; ) {
            final SpreadsheetCellReference potential = this.queue.poll();
            if (null == potential) {
                break;
            }
            if (this.updated.containsKey(potential)) {
                continue;
            }

            this.engine.loadCell(potential,
                    SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                    this.context);
        }

        final Set<SpreadsheetCell> updated = Sets.sorted();
        updated.addAll(this.updated.values());
        return Sets.readOnly(updated);
    }

    private void batchCell(final SpreadsheetCellReference reference) {
        if (false == this.updated.containsKey(reference)) {
            this.queue.add(reference);
            this.batchReferrers(reference);
        }
    }

    private void batchLabel(final SpreadsheetLabelName label) {
        this.engine.labelReferencesStore.load(label)
                .ifPresent(r -> r.forEach(this::batchCell));
    }

    private void batchRange(final SpreadsheetRange range) {
        this.engine.rangeToCellStore.load(range)
                .ifPresent(c -> c.forEach(this::batchCell));
    }

    private void batchReferrers(final SpreadsheetCellReference reference) {
        final BasicSpreadsheetEngine engine = this.engine;

        engine.cellReferencesStore
                .loadReferred(reference)
                .forEach(this::batchCell);

        engine.labelStore.labels(reference)
                .forEach(this::batchLabel);

        engine.rangeToCellStore.loadCellReferenceRanges(reference)
                .forEach(this::batchRange);
    }

    /**
     * The current mode.
     */
    private volatile BasicSpreadsheetEngineUpdatedCellsMode mode;

    /**
     * Holds a queue of cell references that need to be updated.
     */
    private final Queue<SpreadsheetCellReference> queue = new ConcurrentLinkedQueue<>();

    /**
     * Records all updated cells. This can then be returned by the {@link BasicSpreadsheetEngine} method.
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> updated = Maps.sorted();

    /**
     * Mostly used to load cells and access stores.
     */
    private final BasicSpreadsheetEngine engine;

    private final SpreadsheetEngineContext context;

    /**
     * Removes previously added watchers.
     */
    @Override
    public void close() {
        Watchers.removeAllThenFail(this.onSaveCell, this.onDeleteCell, this.onDeleteCellReferences);
    }

    private final Runnable onSaveCell;
    private final Runnable onDeleteCell;
    private final Runnable onDeleteCellReferences;

    @Override
    public String toString() {
        return this.updated.toString();
    }
}
