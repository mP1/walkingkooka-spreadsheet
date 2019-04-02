package walkingkooka.spreadsheet.store.reference;

import walkingkooka.spreadsheet.store.Store;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Set;

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
     * Saves many references to the given id.
     */
    void saveReferences(final T id, final Set<SpreadsheetCellReference> targets);

    /**
     * Adds a reference to the given id.
     */
    void addReference(final T id, final SpreadsheetCellReference target);

    /**
     * Removes a reference from the given id.
     */
    void removeReference(final T id, final SpreadsheetCellReference target);

    /**
     * Loads the referred id given a {@link SpreadsheetCellReference}.
     */
    Set<T> loadReferred(final SpreadsheetCellReference reference);
}
