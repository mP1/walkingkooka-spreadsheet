package walkingkooka.spreadsheet.store.cellreferences;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetCellReferenceStore} implementations.
 */
public final class SpreadsheetCellReferenceStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetCellReferenceStore}
     */
    public static SpreadsheetCellReferenceStore fake() {
        return new FakeSpreadsheetCellReferenceStore();
    }

    /**
     * {@see TreeMapSpreadsheetCellReferenceStore}
     */
    public static SpreadsheetCellReferenceStore treeMap() {
        return TreeMapSpreadsheetCellReferenceStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetCellReferenceStores() {
        throw new UnsupportedOperationException();
    }
}
