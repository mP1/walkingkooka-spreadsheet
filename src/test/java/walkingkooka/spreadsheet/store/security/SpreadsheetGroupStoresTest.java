package walkingkooka.spreadsheet.store.security;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetGroupStoresTest extends PublicStaticHelperTestCase<SpreadsheetGroupStores> {
    @Override
    public Class<SpreadsheetGroupStores> type() {
        return SpreadsheetGroupStores.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
