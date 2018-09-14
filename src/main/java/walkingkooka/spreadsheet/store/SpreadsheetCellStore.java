package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A store that holds all cells for a spreadsheet. No additional actions are supported.
 */
public interface SpreadsheetCellStore {

    /**
     * Fetches the cell using the reference.
     */
    Optional<SpreadsheetCell> load(final SpreadsheetCellReference reference);

    /**
     * Updates a single cell.
     */
    void save(final SpreadsheetCell cell);

    /**
     * Deletes a single cell.
     */
    void delete(final SpreadsheetCellReference reference);

    /**
     * The highest row number
     */
    int rows();

    /**
     * The highest column number
     */
    int columns();

    /**
     * Returns a view of all cells in the given row.
     */
    Collection<SpreadsheetCell> row(int row);

    /**
     * Returns a view of all cells in the given column.
     */
    Collection<SpreadsheetCell> column( int column);
}
