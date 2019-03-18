package walkingkooka.spreadsheet.store.cellreferences;

import walkingkooka.spreadsheet.store.Store;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Set;

/**
 * A {@link Store} that holds one or more references for every {@link SpreadsheetCellReference}.
 */
public interface SpreadsheetCellReferenceStore extends Store<SpreadsheetCellReference, Set<SpreadsheetCellReference>> {

    /**
     * Saves many references to the given id.
     */
    void saveReferences(final SpreadsheetCellReference id, final Set<SpreadsheetCellReference> targets);

    /**
     * Adds a reference to the given id.
     */
    void addReference(final SpreadsheetCellReference id, final SpreadsheetCellReference target);

    /**
     * Removes a reference from the given id.
     */
    void removeReference(final SpreadsheetCellReference id, final SpreadsheetCellReference target);
}
