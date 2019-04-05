package walkingkooka.spreadsheet.store.reference;

import walkingkooka.spreadsheet.store.Store;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link Store} that holds one or more references for every {@link SpreadsheetCellReference}.
 */
public interface SpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> extends Store<T, Set<SpreadsheetCellReference>> {

    @Override
    default Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> value) {
        Objects.requireNonNull(value, "value");
        throw new UnsupportedOperationException();
    }

    /**
     * Saves many references to the given id. Note any {@link #addAddReferenceWatcher(Consumer)} and {@link #addRemoveReferenceWatcher(Consumer)}
     * will be fired for all targets.
     */
    void saveReferences(final T id, final Set<SpreadsheetCellReference> targets);

    /**
     * Adds a reference to the given id.
     */
    void addReference(final TargetAndSpreadsheetCellReference<T> targetAndReference);

    /**
     * Adds a {@link Consumer watcher} which receives all added reference events.
     */
    Runnable addAddReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher);

    /**
     * Removes a reference from the given id.
     */
    void removeReference(final TargetAndSpreadsheetCellReference<T> targetAndReference);

    /**
     * Adds a {@link Consumer watcher} which receives all removed reference events.
     */
    Runnable addRemoveReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher);

    /**
     * Loads the referred id given a {@link SpreadsheetCellReference}.
     */
    Set<T> loadReferred(final SpreadsheetCellReference reference);
}
