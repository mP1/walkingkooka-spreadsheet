package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetDataValidatorContextsTest implements ClassTesting2<SpreadsheetDataValidatorContexts>,
        PublicStaticHelperTesting<SpreadsheetDataValidatorContexts> {

    @Override
    public Class<SpreadsheetDataValidatorContexts> type() {
        return SpreadsheetDataValidatorContexts.class;
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
