package walkingkooka.spreadsheet.store.label;

import walkingkooka.test.ClassTestCase;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetLabelStoresTest extends ClassTestCase<SpreadsheetLabelStores>
        implements PublicStaticHelperTesting<SpreadsheetLabelStores> {

    @Override
    public Class<SpreadsheetLabelStores> type() {
        return SpreadsheetLabelStores.class;
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
