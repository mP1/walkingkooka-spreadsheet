package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Set;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosCollectionHandler} for {@link SpreadsheetCellStore#row}
 */
final class SpreadsheetCellStoreRowHateosCollectionHandler extends SpreadsheetCellStoreHateosCollectionHandler<SpreadsheetRowReference, SpreadsheetRow> {

    static SpreadsheetCellStoreRowHateosCollectionHandler with(final SpreadsheetCellStore store) {
        check(store);
        return new SpreadsheetCellStoreRowHateosCollectionHandler(store);
    }

    private SpreadsheetCellStoreRowHateosCollectionHandler(final SpreadsheetCellStore store) {
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
