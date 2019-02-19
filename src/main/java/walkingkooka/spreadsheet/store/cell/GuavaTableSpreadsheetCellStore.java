package walkingkooka.spreadsheet.store.cell;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * A {@link SpreadsheetCellStore} that is backed by a Guava {@link com.google.common.collect.Table}
 */
final class GuavaTableSpreadsheetCellStore extends SpreadsheetCellStoreTemplate {

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
    Optional<SpreadsheetCell> load0(final SpreadsheetCellReference reference) {
        return Optional.ofNullable(this.cells.get(reference.row().value(), reference.column().value()));
    }

    @Override
    SpreadsheetCellReference save0(final SpreadsheetCell cell) {
        final SpreadsheetCellReference reference = cell.reference();
        final int column = reference.column().value();
        this.cells.put(reference.row().value(), column, cell);

        if (COMPUTE_AGAIN != this.columns) {
            this.columns = Math.max(column, this.columns);
        }
        return reference;
    }

    @Override
    void delete0(final SpreadsheetCellReference reference) {
        final int column = reference.column().value();
        if (column != this.columns) {
            this.columns = COMPUTE_AGAIN;
        }
        this.cells.remove(reference.row().value(), column);
    }

    @Override
    public int count() {
        return this.cells.size();
    }

    @Override
    public int rows() {
        return this.cells.isEmpty() ?
                0 :
                this.cells.rowKeySet().last();
    }

    @Override
    public int columns() {
        if (COMPUTE_AGAIN == this.columns) {
            int columns = 0;
            for (int c : this.cells.columnKeySet()) {
                columns = Math.max(columns, c);
            }
            this.columns = columns;
        }
        return this.columns;
    }

    private int columns = COMPUTE_AGAIN;

    @Override
    Collection<SpreadsheetCell> row0(final int row) {
        return Collections.unmodifiableCollection(this.cells.row(row)
                .values());
    }

    @Override
    Collection<SpreadsheetCell> column0(final int column) {
        return Collections.unmodifiableCollection(this.cells.column(column)
                .values());
    }

    // row, column
    private TreeBasedTable<Integer, Integer, SpreadsheetCell> cells = TreeBasedTable.create();

    @Override
    public String toString() {
        return this.cells.toString();
    }
}
