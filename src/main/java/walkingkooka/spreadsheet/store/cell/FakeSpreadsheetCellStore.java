package walkingkooka.spreadsheet.store.cell;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;

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
    public Collection<SpreadsheetCell> row(final int row) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a view of all cells in the given column.
     */
    @Override
    public Collection<SpreadsheetCell> column(final int column) {
        throw new UnsupportedOperationException();
    }
}
