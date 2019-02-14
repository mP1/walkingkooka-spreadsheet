package walkingkooka.spreadsheet.store.cell;

public final class GuavaTableSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<GuavaTableSpreadsheetCellStore> {
    @Override
    public GuavaTableSpreadsheetCellStore createStore() {
        return GuavaTableSpreadsheetCellStore.create();
    }

    @Override
    public Class<GuavaTableSpreadsheetCellStore> type() {
        return GuavaTableSpreadsheetCellStore.class;
    }
}
