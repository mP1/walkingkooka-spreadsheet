package walkingkooka.spreadsheet.store.cell;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Set;

public class FakeSpreadsheetCellStore extends FakeStore<SpreadsheetCellReference, SpreadsheetCell> implements SpreadsheetCellStore, Fake {

    @Override
    public int rows() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int columns() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a view of all cells in the given row.
     */
    @Override
    public Set<SpreadsheetCell> row(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a view of all cells in the given column.
     */
    @Override
    public Set<SpreadsheetCell> column(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }
}
