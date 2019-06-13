package walkingkooka.spreadsheet.store.label;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetLabelStoresTest implements ClassTesting2<SpreadsheetLabelStores>,
        PublicStaticHelperTesting<SpreadsheetLabelStores> {

    @Override
    public Class<SpreadsheetLabelStores> type() {
        return SpreadsheetLabelStores.class;
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
