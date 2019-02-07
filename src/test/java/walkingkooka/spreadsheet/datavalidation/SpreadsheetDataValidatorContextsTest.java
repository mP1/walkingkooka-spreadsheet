package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.test.ClassTestCase;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetDataValidatorContextsTest extends ClassTestCase<SpreadsheetDataValidatorContexts>
        implements PublicStaticHelperTesting<SpreadsheetDataValidatorContexts> {

    @Override
    public Class<SpreadsheetDataValidatorContexts> type() {
        return SpreadsheetDataValidatorContexts.class;
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
