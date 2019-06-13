package walkingkooka.spreadsheet.store.reference;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetReferenceStoresTest implements ClassTesting2<SpreadsheetReferenceStores>,
        PublicStaticHelperTesting<SpreadsheetReferenceStores> {

    @Override
    public Class<SpreadsheetReferenceStores> type() {
        return SpreadsheetReferenceStores.class;
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
