package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;

import static org.junit.Assert.assertEquals;


public final class SpreadsheetErrorTest extends PublicClassTestCase<SpreadsheetError> {

    private final static String MESSAGE = "message #1";

    @Test(expected = NullPointerException.class)
    public void testWithNullValueFails() {
        SpreadsheetError.with(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithEmptyValueFails() {
        SpreadsheetError.with("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithWhitespaceValueFails() {
        SpreadsheetError.with("   ");
    }

    @Test
    public void testWith() {
        final SpreadsheetError error = SpreadsheetError.with(MESSAGE);
        this.checkValue(error, MESSAGE);
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        assertEquals(MESSAGE, this.createError().toString());
    }

    private SpreadsheetError createError() {
        return SpreadsheetError.with(MESSAGE);
    }

    private void checkValue(final SpreadsheetError error, final String value) {
        assertEquals("formula", value, error.value());
    }

    @Override
    protected Class<SpreadsheetError> type() {
        return SpreadsheetError.class;
    }
}
