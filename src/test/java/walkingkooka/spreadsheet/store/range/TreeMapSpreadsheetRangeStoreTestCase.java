package walkingkooka.spreadsheet.store.range;

import walkingkooka.test.TypeNameTesting;

public abstract class TreeMapSpreadsheetRangeStoreTestCase<T> implements TypeNameTesting<T> {

    TreeMapSpreadsheetRangeStoreTestCase() {
        super();
    }

    @Override
    public final String typeNamePrefix() {
        return TreeMapSpreadsheetRangeStore.class.getSimpleName();
    }
}
