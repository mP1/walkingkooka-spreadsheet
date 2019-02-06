package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTestCase;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetEngineLoadingTest extends ClassTestCase<SpreadsheetEngineLoading> {
    @Override
    public Class<SpreadsheetEngineLoading> type() {
        return SpreadsheetEngineLoading.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
