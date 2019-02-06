package walkingkooka.spreadsheet.engine;

import walkingkooka.test.PublicStaticHelperTestCase;

import java.lang.reflect.Method;

public final class SpreadsheetEnginesTest extends PublicStaticHelperTestCase<SpreadsheetEngines> {
    @Override
    public Class<SpreadsheetEngines> type() {
        return SpreadsheetEngines.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
