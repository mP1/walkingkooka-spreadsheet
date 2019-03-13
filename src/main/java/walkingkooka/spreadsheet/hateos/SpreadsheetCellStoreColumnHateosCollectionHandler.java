package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;

import java.util.Set;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosCollectionHandler} for {@link SpreadsheetCellStore#column(int)}
 */
final class SpreadsheetCellStoreColumnHateosCollectionHandler extends SpreadsheetCellStoreHateosCollectionHandler {

    static SpreadsheetCellStoreColumnHateosCollectionHandler with(final SpreadsheetCellStore store) {
        check(store);
        return new SpreadsheetCellStoreColumnHateosCollectionHandler(store);
    }

    private SpreadsheetCellStoreColumnHateosCollectionHandler(final SpreadsheetCellStore store) {
        super(store);
    }

    @Override
    Set<SpreadsheetCell> handle0(final Integer column) {
        return this.store.row(column);
    }

    @Override
    String operation() {
        return "column";
    }
}
