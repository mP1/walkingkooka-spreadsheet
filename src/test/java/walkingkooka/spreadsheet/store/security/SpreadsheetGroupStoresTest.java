package walkingkooka.spreadsheet.store.security;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetGroupStoresTest implements ClassTesting2<SpreadsheetGroupStores>,
        PublicStaticHelperTesting<SpreadsheetGroupStores> {

    @Override
    public Class<SpreadsheetGroupStores> type() {
        return SpreadsheetGroupStores.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
