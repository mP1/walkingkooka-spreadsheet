package walkingkooka.spreadsheet.store.cell;

public final class BasicSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<BasicSpreadsheetCellStore> {

    @Override
    public BasicSpreadsheetCellStore createStore() {
        return BasicSpreadsheetCellStore.create();
    }

    @Override
    public Class<BasicSpreadsheetCellStore> type() {
        return BasicSpreadsheetCellStore.class;
    }
}
