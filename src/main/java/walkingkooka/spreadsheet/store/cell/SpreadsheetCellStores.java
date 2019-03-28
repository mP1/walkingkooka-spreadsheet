package walkingkooka.spreadsheet.store.cell;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetCellStore} implementations.
 */
public final class SpreadsheetCellStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore fake() {
        return new FakeSpreadsheetCellStore();
    }

    /**
     * {@see TreeMapSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore treeMap() {
        return TreeMapSpreadsheetCellStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetCellStores() {
        throw new UnsupportedOperationException();
    }
}
