package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.Set;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosCollectionHandler} for {@link SpreadsheetCellStore#column}
 */
final class SpreadsheetCellStoreColumnHateosCollectionHandler extends SpreadsheetCellStoreHateosCollectionHandler<SpreadsheetColumnReference, SpreadsheetColumn> {

    static SpreadsheetCellStoreColumnHateosCollectionHandler with(final SpreadsheetCellStore store) {
        check(store);
        return new SpreadsheetCellStoreColumnHateosCollectionHandler(store);
    }

    private SpreadsheetCellStoreColumnHateosCollectionHandler(final SpreadsheetCellStore store) {
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
