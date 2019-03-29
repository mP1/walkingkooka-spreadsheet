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
     * {@see ReadOnlySpreadsheetRangeStore}
     */
    public static <V> SpreadsheetRangeStore<V> readOnly(final SpreadsheetRangeStore<V> store) {
        return ReadOnlySpreadsheetRangeStore.with(store);
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
