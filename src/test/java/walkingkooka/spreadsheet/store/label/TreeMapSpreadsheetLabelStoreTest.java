package walkingkooka.spreadsheet.store.label;

public final class TreeMapSpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<TreeMapSpreadsheetLabelStore> {

    @Override
    public TreeMapSpreadsheetLabelStore createStore() {
        return TreeMapSpreadsheetLabelStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetLabelStore> type() {
        return TreeMapSpreadsheetLabelStore.class;
    }
}
