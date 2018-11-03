package walkingkooka.spreadsheet.store.range;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetRangeStore} implementations.
 */
public final class SpreadsheetRangeStores implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetRangeStore}
     */
    public static <V> SpreadsheetRangeStore<V> basic() {
        return BasicSpreadsheetRangeStore.create();
    }

    /**
     * {@see FakeSpreadsheetRangeStore}
     */
    public static <V> SpreadsheetRangeStore<V> fake() {
        return new FakeSpreadsheetRangeStore<V>();
    }

    /**
     * Stop creation
     */
    private SpreadsheetRangeStores() {
        throw new UnsupportedOperationException();
    }
}
