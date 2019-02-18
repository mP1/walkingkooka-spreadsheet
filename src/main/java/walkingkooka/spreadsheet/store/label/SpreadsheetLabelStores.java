package walkingkooka.spreadsheet.store.label;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetLabelStore} implementations.
 */
public final class SpreadsheetLabelStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetLabelStore}
     */
    public static SpreadsheetLabelStore fake() {
        return new FakeSpreadsheetLabelStore();
    }

    /**
     * {@see TreeMapSpreadsheetLabelStore}
     */
    public static SpreadsheetLabelStore treeMap() {
        return TreeMapSpreadsheetLabelStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetLabelStores() {
        throw new UnsupportedOperationException();
    }
}
