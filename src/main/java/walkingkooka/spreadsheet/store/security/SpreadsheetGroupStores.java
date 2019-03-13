package walkingkooka.spreadsheet.store.security;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetGroupStore} implementations.
 */
public final class SpreadsheetGroupStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetGroupStore}
     */
    public static SpreadsheetGroupStore fake() {
        return new FakeSpreadsheetGroupStore();
    }

    /**
     * {@see TreeMapSpreadsheetGroupStore}
     */
    public static SpreadsheetGroupStore treeMap() {
        return TreeMapSpreadsheetGroupStore.with();
    }

    /**
     * Stop creation
     */
    private SpreadsheetGroupStores() {
        throw new UnsupportedOperationException();
    }
}
