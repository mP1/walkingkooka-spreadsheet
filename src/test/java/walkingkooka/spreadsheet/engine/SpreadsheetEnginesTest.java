package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetEnginesTest implements ClassTesting2<SpreadsheetEngines>,
        PublicStaticHelperTesting<SpreadsheetEngines> {

    @Override
    public Class<SpreadsheetEngines> type() {
        return SpreadsheetEngines.class;
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
