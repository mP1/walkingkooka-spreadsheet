package walkingkooka.spreadsheet.engine;

import walkingkooka.test.StandardThrowableTesting;
import walkingkooka.test.TestCase;

public final class SpreadsheetEngineExceptionTest extends TestCase
        implements StandardThrowableTesting<SpreadsheetEngineException> {
    @Override
    public Class<SpreadsheetEngineException> type() {
        return SpreadsheetEngineException.class;
    }
}
