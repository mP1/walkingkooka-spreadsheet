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

    private final static int COMPUTE_AGAIN = -1;

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
        final SpreadsheetColumnReference column = reference.column();
        this.cells.put(reference.row(), column, cell);

        if(COMPUTE_AGAIN != this.columns) {
            this.columns = Math.max(column.value(), this.columns);
        }
    }

    @Override
    public void delete(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        final SpreadsheetColumnReference column = reference.column();
        if(column.value() != this.columns) {
            this.columns = COMPUTE_AGAIN;
        }
        this.cells.remove(reference.row(), column);
    }

    @Override
    public int rows() {
        return this.cells.rowKeySet().last().value();
    }

    @Override
    public int columns() {
        if(COMPUTE_AGAIN == this.columns) {
            int columns = 0;
            for(SpreadsheetColumnReference columnReference: this.cells.columnKeySet()) {
                columns = Math.max(columns, columnReference.value());
            }
            this.columns = columns;
        }
        return this.columns;
    }

    private int columns = COMPUTE_AGAIN;

    private TreeBasedTable<SpreadsheetRowReference, SpreadsheetColumnReference, SpreadsheetCell> cells = TreeBasedTable.create();

    @Override
    public String toString() {
        return this.cells.toString();
    }
}
