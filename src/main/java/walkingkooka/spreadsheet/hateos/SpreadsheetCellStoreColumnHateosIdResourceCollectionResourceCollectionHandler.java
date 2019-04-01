package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.Set;

/**
 * A {@link HateosIdResourceCollectionResourceCollectionHandler} for {@link SpreadsheetCellStore#column}
 */
final class SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler extends SpreadsheetCellStoreHateosIdResourceCollectionResourceCollectionHandler<SpreadsheetColumnReference, SpreadsheetColumn> {

    static SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler with(final SpreadsheetCellStore store) {
        check(store);
        return new SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler(store);
    }

    private SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler(final SpreadsheetCellStore store) {
        super(store);
    }

    @Override
    Set<SpreadsheetCell> handle0(final SpreadsheetColumnReference column) {
        return this.store.column(column);
    }

    @Override
    String operation() {
        return "column";
    }
}
