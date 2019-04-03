package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Aggregates all the updated cells that result from an operation by {@link BasicSpreadsheetEngine}.
 */
final class BasicSpreadsheetEngineUpdatedCells implements Closeable {

    static BasicSpreadsheetEngineUpdatedCells with(final BasicSpreadsheetEngine engine,
                                                   final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineUpdatedCells(engine, context);
    }

    private BasicSpreadsheetEngineUpdatedCells(final BasicSpreadsheetEngine engine,
                                               final SpreadsheetEngineContext context) {
        super();

        final SpreadsheetCellStore cellStore = engine.cellStore;
        this.save = cellStore.addSaveWatcher(this::saved);
        this.delete = cellStore.addDeleteWatcher(this::deleted);

        this.engine = engine;
        this.context = context;
    }

    private void saved(final SpreadsheetCell saved) {
        final SpreadsheetCellReference reference = saved.reference();
        if (null == this.updated.put(reference, saved)) {
            this.batchReferrers(reference);
        }
    }

    private void deleted(final SpreadsheetCellReference deleted) {
        this.batchReferrers(deleted);
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

            final Optional<SpreadsheetCell> c = this.engine.loadCell(potential,
                    SpreadsheetEngineLoading.FORCE_RECOMPUTE,
                    this.context);
        }

        final Set<SpreadsheetCell> updated = Sets.sorted();
        updated.addAll(this.updated.values());
        return updated;
    }

    private void batchCell(final SpreadsheetCellReference reference) {
        if (false == this.updated.containsKey(reference)) {
            this.queue.add(reference);
            this.batchReferrers(reference);
        }
    }

    private void batchLabel(final SpreadsheetLabelName label) {
        this.engine.labelReferencesStore.load(label).ifPresent(r -> r.forEach(this::batchCell));
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
     * Tracks all the seen cells, this helps avoided cycles and unnecessary re-computations.
     */
//    private final Set<SpreadsheetCellReference> seen = Sets.sorted();

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
            this.save.run();
        } finally {
            this.delete.run();
        }
    }

    private final Runnable save;
    private final Runnable delete;

    @Override
    public String toString() {
        return this.updated.toString();
    }
}
