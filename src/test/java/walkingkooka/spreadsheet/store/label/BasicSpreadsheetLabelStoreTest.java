package walkingkooka.spreadsheet.store.label;

public final class BasicSpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<BasicSpreadsheetLabelStore> {

    @Override
    protected BasicSpreadsheetLabelStore createStore() {
        return BasicSpreadsheetLabelStore.create();
    }

    @Override
    public Class<BasicSpreadsheetLabelStore> type() {
        return BasicSpreadsheetLabelStore.class;
    }
}
