package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTestCase;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public final class SpreadsheetEnginesTest extends ClassTestCase<SpreadsheetEngines>
        implements PublicStaticHelperTesting<SpreadsheetEngines> {

    @Override
    public Class<SpreadsheetEngines> type() {
        return SpreadsheetEngines.class;
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
