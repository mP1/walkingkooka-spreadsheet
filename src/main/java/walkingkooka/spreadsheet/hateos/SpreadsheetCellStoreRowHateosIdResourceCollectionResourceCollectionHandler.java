package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Set;

/**
 * A {@link HateosIdResourceCollectionResourceCollectionHandler} for {@link SpreadsheetCellStore#row}
 */
final class SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler extends SpreadsheetCellStoreHateosIdResourceCollectionResourceCollectionHandler<SpreadsheetRowReference,
        SpreadsheetRow> {

    static SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler with(final SpreadsheetCellStore store) {
        check(store);
        return new SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler(store);
    }

    private SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler(final SpreadsheetCellStore store) {
        super(store);
    }

    @Override
    Set<SpreadsheetCell> handle0(final SpreadsheetRowReference row) {
        return this.store.row(row);
    }

    @Override
    String operation() {
        return "row";
    }
}
