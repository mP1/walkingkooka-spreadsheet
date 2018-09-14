package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Objects;
import java.util.Optional;

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
}
