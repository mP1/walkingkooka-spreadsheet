package walkingkooka.spreadsheet.store.range;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetRangeStoresTest extends PublicStaticHelperTestCase<SpreadsheetRangeStores> {
    @Override
    public Class<SpreadsheetRangeStores> type() {
        return SpreadsheetRangeStores.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
