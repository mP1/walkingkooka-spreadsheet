package walkingkooka.spreadsheet.store;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetCellStoresTest extends PublicStaticHelperTestCase<SpreadsheetCellStores> {
    @Override
    protected Class<SpreadsheetCellStores> type() {
        return SpreadsheetCellStores.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
