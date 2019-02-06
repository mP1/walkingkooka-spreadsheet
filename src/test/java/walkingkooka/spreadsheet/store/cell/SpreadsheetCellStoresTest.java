package walkingkooka.spreadsheet.store.cell;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetCellStoresTest extends PublicStaticHelperTestCase<SpreadsheetCellStores> {
    @Override
    public Class<SpreadsheetCellStores> type() {
        return SpreadsheetCellStores.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
