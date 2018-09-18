package walkingkooka.spreadsheet.store.cell;

import walkingkooka.spreadsheet.store.cell.BasicSpreadsheetCellStore;

public final class BasicSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<BasicSpreadsheetCellStore> {

    @Override
    protected BasicSpreadsheetCellStore createStore() {
        return BasicSpreadsheetCellStore.create();
    }

    @Override
    protected Class<BasicSpreadsheetCellStore> type() {
        return BasicSpreadsheetCellStore.class;
    }
}
