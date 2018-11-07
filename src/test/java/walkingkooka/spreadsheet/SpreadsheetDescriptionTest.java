package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.text.CharSequences;

import static org.junit.Assert.assertEquals;


public final class SpreadsheetDescriptionTest extends PublicClassTestCase<SpreadsheetDescription> {

    private final static String TEXT = "description #1";

    @Test(expected = NullPointerException.class)
    public void testWithNullValueFails() {
        SpreadsheetDescription.with(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithEmptyValueFails() {
        SpreadsheetDescription.with("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithWhitespaceValueFails() {
        SpreadsheetDescription.with("   ");
    }

    @Test
    public void testWith() {
        final SpreadsheetDescription description = SpreadsheetDescription.with(TEXT);
        this.checkValue(description, TEXT);
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        assertEquals(CharSequences.quote(TEXT).toString(), this.createDescription().toString());
    }

    private SpreadsheetDescription createDescription() {
        return SpreadsheetDescription.with(TEXT);
    }

    private void checkValue(final SpreadsheetDescription description, final String value) {
        assertEquals("value", value, description.value());
    }

    @Override
    protected Class<SpreadsheetDescription> type() {
        return SpreadsheetDescription.class;
    }
}
