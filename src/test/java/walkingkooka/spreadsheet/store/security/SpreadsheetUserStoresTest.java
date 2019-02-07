package walkingkooka.spreadsheet.store.security;

import walkingkooka.test.ClassTestCase;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetUserStoresTest extends ClassTestCase<SpreadsheetUserStores>
        implements PublicStaticHelperTesting<SpreadsheetUserStores> {

    @Override
    public Class<SpreadsheetUserStores> type() {
        return SpreadsheetUserStores.class;
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
