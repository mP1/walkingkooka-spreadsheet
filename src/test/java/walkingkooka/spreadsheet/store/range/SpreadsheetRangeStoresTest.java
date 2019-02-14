package walkingkooka.spreadsheet.store.range;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetRangeStoresTest implements ClassTesting2<SpreadsheetRangeStores>,
        PublicStaticHelperTesting<SpreadsheetRangeStores> {

    @Override
    public Class<SpreadsheetRangeStores> type() {
        return SpreadsheetRangeStores.class;
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
