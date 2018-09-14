package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetCellStore implements SpreadsheetCellStore, Fake {

    @Override
    public Optional<SpreadsheetCell> load(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
        throw new UnsupportedOperationException();
    }

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
