package walkingkooka.spreadsheet.store;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.checkerframework.checker.units.qual.C;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetCellStore} that is backed by a Guava {@link com.google.common.collect.Table}
 */
final class GuavaTableSpreadsheetCellStore implements SpreadsheetCellStore{

    /**
     * Creates an empty {@link SpreadsheetCellStore} which uses a {@link Table}
     */
    static GuavaTableSpreadsheetCellStore create() {
        return new GuavaTableSpreadsheetCellStore();
    }

    /**
     * Private ctor.
     */
    private GuavaTableSpreadsheetCellStore() {
        super();
    }

    @Override
    public Optional<SpreadsheetCell> load(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        return Optional.ofNullable(this.cells.get(reference.row(), reference.column()));
    }

    @Override
    public void save(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");

        final SpreadsheetCellReference reference = cell.reference();
        this.cells.put(reference.row(), reference.column(), cell);
    }

    @Override
    public void delete(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        this.cells.remove(reference.row(), reference.column());
    }

    private Table<SpreadsheetRowReference, SpreadsheetColumnReference, SpreadsheetCell> cells = TreeBasedTable.create();

    @Override
    public String toString() {
        return this.cells.toString();
    }
}
