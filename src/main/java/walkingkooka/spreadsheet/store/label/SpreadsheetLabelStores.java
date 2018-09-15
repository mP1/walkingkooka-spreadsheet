package walkingkooka.spreadsheet.store.label;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetLabelStore} implementations.
 */
public final class SpreadsheetLabelStores implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetLabelStore}
     */
    public static SpreadsheetLabelStore basic() {
        return BasicSpreadsheetLabelStore.create();
    }

    /**
     * {@see FakeSpreadsheetLabelStore}
     */
    public static SpreadsheetLabelStore fake() {
        return new FakeSpreadsheetLabelStore();
    }

    /**
     * Stop creation
     */
    private SpreadsheetLabelStores() {
        throw new UnsupportedOperationException();
    }
}
