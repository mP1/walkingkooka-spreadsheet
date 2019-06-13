package walkingkooka.spreadsheet.store.cell;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetCellStoresTest implements ClassTesting2<SpreadsheetCellStores>,
        PublicStaticHelperTesting<SpreadsheetCellStores> {

    @Override
    public Class<SpreadsheetCellStores> type() {
        return SpreadsheetCellStores.class;
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
