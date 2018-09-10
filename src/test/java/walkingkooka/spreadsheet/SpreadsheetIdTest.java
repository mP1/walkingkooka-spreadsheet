package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;

import static org.junit.Assert.assertEquals;


public final class SpreadsheetIdTest extends PublicClassTestCase<SpreadsheetId> {

    private final static Long VALUE = 123L;

    @Test
    public void testWith() {
        final SpreadsheetId id = SpreadsheetId.with(VALUE);
        assertEquals("id", VALUE, id.value());
    }

    @Test
    public void testToString() {
        assertEquals("" + VALUE, SpreadsheetId.with(VALUE).toString());
    }

    @Override
    protected Class<SpreadsheetId> type() {
        return SpreadsheetId.class;
    }
}
