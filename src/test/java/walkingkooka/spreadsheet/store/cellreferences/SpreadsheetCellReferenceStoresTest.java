package walkingkooka.spreadsheet.store.cellreferences;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetCellReferenceStoresTest implements ClassTesting2<SpreadsheetCellReferenceStores>,
        PublicStaticHelperTesting<SpreadsheetCellReferenceStores> {

    @Override
    public Class<SpreadsheetCellReferenceStores> type() {
        return SpreadsheetCellReferenceStores.class;
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
