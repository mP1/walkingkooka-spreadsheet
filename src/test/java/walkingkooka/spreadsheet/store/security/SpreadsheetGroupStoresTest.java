package walkingkooka.spreadsheet.store.security;

import walkingkooka.test.ClassTestCase;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetGroupStoresTest extends ClassTestCase<SpreadsheetGroupStores>
        implements PublicStaticHelperTesting<SpreadsheetGroupStores> {

    @Override
    public Class<SpreadsheetGroupStores> type() {
        return SpreadsheetGroupStores.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
