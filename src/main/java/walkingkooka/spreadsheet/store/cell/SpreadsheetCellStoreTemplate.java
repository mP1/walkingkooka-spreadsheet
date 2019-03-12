package walkingkooka.spreadsheet.store.cell;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Template for all {@link SpreadsheetCellStore}
 */
abstract class SpreadsheetCellStoreTemplate implements SpreadsheetCellStore {

    SpreadsheetCellStoreTemplate() {
        super();
    }

    @Override
    public final Optional<SpreadsheetCell> load(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "references");

        return this.load0(reference);
    }

    abstract Optional<SpreadsheetCell> load0(final SpreadsheetCellReference reference);

    @Override
    public final SpreadsheetCell save(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");

        return this.save0(cell);
    }

    abstract SpreadsheetCell save0(final SpreadsheetCell cell);

    @Override
    public final void delete(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
        this.delete0(reference);
    }

    abstract void delete0(final SpreadsheetCellReference reference);

    @Override
    public final Set<SpreadsheetCell> row(final int row) {
        if (row < 0) {
            throw new IllegalArgumentException("Row " + row + " must be >= 0");
        }
        return this.row0(row);
    }

    abstract Set<SpreadsheetCell> row0(final int row);

    @Override
    public final Set<SpreadsheetCell> column(final int column) {
        if (column < 0) {
            throw new IllegalArgumentException("Column " + column + " must be >= 0");
        }
        return this.column0(column);
    }

    abstract Set<SpreadsheetCell> column0(final int column);

    @Override
    abstract public String toString();
}
