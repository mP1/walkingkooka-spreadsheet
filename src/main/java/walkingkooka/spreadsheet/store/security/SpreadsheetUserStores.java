package walkingkooka.spreadsheet.store.security;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetUserStore} implementations.
 */
public final class SpreadsheetUserStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetUserStore}
     */
    public static SpreadsheetUserStore fake() {
        return new FakeSpreadsheetUserStore();
    }

    /**
     * {@see TreeMapSpreadsheetUserStore}
     */
    public static SpreadsheetUserStore treeMap() {
        return TreeMapSpreadsheetUserStore.with();
    }

    /**
     * Stop creation
     */
    private SpreadsheetUserStores() {
        throw new UnsupportedOperationException();
    }
}
