package walkingkooka.spreadsheet.store.cell;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;

/**
 * A store that holds all cells for a spreadsheet. No additional actions are supported.
 */
public interface SpreadsheetCellStore extends Store<SpreadsheetCellReference, SpreadsheetCell> {

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
