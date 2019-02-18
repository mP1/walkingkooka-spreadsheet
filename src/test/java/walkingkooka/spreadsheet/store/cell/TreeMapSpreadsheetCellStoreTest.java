package walkingkooka.spreadsheet.store.cell;

public final class TreeMapSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<TreeMapSpreadsheetCellStore> {

    @Override
    public TreeMapSpreadsheetCellStore createStore() {
        return TreeMapSpreadsheetCellStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetCellStore> type() {
        return TreeMapSpreadsheetCellStore.class;
    }
}
