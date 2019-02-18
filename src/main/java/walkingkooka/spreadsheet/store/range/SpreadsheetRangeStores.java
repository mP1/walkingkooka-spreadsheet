package walkingkooka.spreadsheet.store.range;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetRangeStore} implementations.
 */
public final class SpreadsheetRangeStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetRangeStore}
     */
    public static <V> SpreadsheetRangeStore<V> fake() {
        return new FakeSpreadsheetRangeStore<V>();
    }

    /**
     * {@see TreeMapSpreadsheetRangeStore}
     */
    public static <V> SpreadsheetRangeStore<V> treeMap() {
        return TreeMapSpreadsheetRangeStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetRangeStores() {
        throw new UnsupportedOperationException();
    }
}
