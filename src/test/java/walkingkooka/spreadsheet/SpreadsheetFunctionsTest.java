package walkingkooka.spreadsheet;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;

public class SpreadsheetFunctionsTest implements ClassTesting2<SpreadsheetFunctions>,
        PublicStaticHelperTesting<SpreadsheetFunctions> {

    @Override
    public Class<SpreadsheetFunctions> type() {
        return SpreadsheetFunctions.class;
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

