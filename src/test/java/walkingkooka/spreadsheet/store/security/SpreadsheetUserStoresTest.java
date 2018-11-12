package walkingkooka.spreadsheet.store.security;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetUserStoresTest extends PublicStaticHelperTestCase<SpreadsheetUserStores> {
    @Override
    protected Class<SpreadsheetUserStores> type() {
        return SpreadsheetUserStores.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
