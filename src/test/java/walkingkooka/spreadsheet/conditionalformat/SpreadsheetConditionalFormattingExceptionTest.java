package walkingkooka.spreadsheet.conditionalformat;

import walkingkooka.test.StandardThrowableTesting;
import walkingkooka.test.TestCase;

public final class SpreadsheetConditionalFormattingExceptionTest extends TestCase
        implements StandardThrowableTesting<SpreadsheetConditionalFormattingException> {

    @Override
    public Class<SpreadsheetConditionalFormattingException> type() {
        return SpreadsheetConditionalFormattingException.class;
    }
}
