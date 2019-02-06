package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetDataValidatorContextsTest extends PublicStaticHelperTestCase<SpreadsheetDataValidatorContexts> {
    @Override
    public Class<SpreadsheetDataValidatorContexts> type() {
        return SpreadsheetDataValidatorContexts.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
