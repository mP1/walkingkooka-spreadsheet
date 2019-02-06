package walkingkooka.spreadsheet.store.label;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetLabelStoresTest extends PublicStaticHelperTestCase<SpreadsheetLabelStores> {
    @Override
    public Class<SpreadsheetLabelStores> type() {
        return SpreadsheetLabelStores.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
