package walkingkooka.spreadsheet.store;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.checkerframework.checker.units.qual.C;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Optional;

/**
 * A {@link SpreadsheetCellStore} that is backed by a Guava {@link com.google.common.collect.Table}
 */
final class GuavaTableSpreadsheetCellStore implements SpreadsheetCellStore{

    static GuavaTableSpreadsheetCellStore create() {
        return new GuavaTableSpreadsheetCellStore();
    }

    private GuavaTableSpreadsheetCellStore() {
        super();
    }

    @Override
    public Optional<SpreadsheetCell> load(final SpreadsheetCellReference reference) {
        return Optional.empty();
    }

    @Override
    public void save(final SpreadsheetCell cell) {

    }

    @Override
    public void delete(final SpreadsheetCellReference reference) {

    }

    private Table<SpreadsheetRowReference, SpreadsheetCellReference, SpreadsheetCell> cells = TreeBasedTable.create();
}
