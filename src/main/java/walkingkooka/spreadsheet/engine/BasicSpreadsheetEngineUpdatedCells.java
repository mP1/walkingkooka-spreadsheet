package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRange;
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
                                                   final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineUpdatedCells(engine, context);
    }

    private BasicSpreadsheetEngineUpdatedCells(final BasicSpreadsheetEngine engine,
                                               final SpreadsheetEngineContext context) {
        super();

        this.saveCell = engine.cellStore.addSaveWatcher(this::onCellSaved);
        this.deleteCell = engine.cellStore.addDeleteWatcher(this::onCellDeleted);
        this.deleteCellReferences = engine.cellReferencesStore.addRemoveReferenceWatcher(this::onCellReferenceDeleted);

        this.engine = engine;
        this.context = context;
    }

    /**
     * Accepts a just saved cell, parsing the formula adding external references and then batching references to this cell.
     */
    private void onCellSaved(final SpreadsheetCell cell) {
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
    private void onCellDeleted(final SpreadsheetCellReference cell) {
        final BasicSpreadsheetEngine engine = this.engine;

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

    private void onCellReferenceDeleted(final TargetAndSpreadsheetCellReference<SpreadsheetCellReference> targetAndReference) {
        this.batchReferrers(targetAndReference.target());
    }

    Set<SpreadsheetCell> refreshUpdated() {
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
        try {
            this.saveCell.run();
        } finally {
            try {
                this.deleteCell.run();
            } finally {
                this.deleteCellReferences.run();
            }
        }
    }

    private final Runnable saveCell;
    private final Runnable deleteCell;
    private final Runnable deleteCellReferences;

    @Override
    public String toString() {
        return this.updated.toString();
    }
}
