package walkingkooka.spreadsheet.store.cell;

import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetCellStore} implementations.
 */
public final class SpreadsheetCellStores implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore basic() {
        return BasicSpreadsheetCellStore.create();
    }

    /**
     * {@see FakeSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore fake() {
        return new FakeSpreadsheetCellStore();
    }

    /**
     * {@see GuavaTableSpreadsheetCellStore}
     */
    public static SpreadsheetCellStore guavaTable() {
        return GuavaTableSpreadsheetCellStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetCellStores() {
        throw new UnsupportedOperationException();
    }
}
