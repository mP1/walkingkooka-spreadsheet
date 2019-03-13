package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;

import java.util.Set;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosCollectionHandler} for {@link SpreadsheetCellStore#row(int)}
 */
final class SpreadsheetCellStoreRowHateosCollectionHandler extends SpreadsheetCellStoreHateosCollectionHandler {

    static SpreadsheetCellStoreRowHateosCollectionHandler with(final SpreadsheetCellStore store) {
        check(store);
        return new SpreadsheetCellStoreRowHateosCollectionHandler(store);
    }

    private SpreadsheetCellStoreRowHateosCollectionHandler(final SpreadsheetCellStore store) {
        super(store);
    }

    @Override
    Set<SpreadsheetCell> handle0(final Integer row) {
        return this.store.row(row);
    }

    @Override
    String operation() {
        return "row";
    }
}
