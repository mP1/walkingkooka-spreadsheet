package walkingkooka.spreadsheet;

import walkingkooka.test.StandardThrowableTesting;
import walkingkooka.test.TestCase;

public final class SpreadsheetExceptionTest extends TestCase
        implements StandardThrowableTesting<SpreadsheetException> {

    @Override
    public Class<SpreadsheetException> type() {
        return SpreadsheetException.class;
    }
}
