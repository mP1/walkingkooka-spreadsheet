package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import static org.junit.Assert.assertEquals;


public final class SpreadsheetErrorTest extends ClassTestCase<SpreadsheetError> 
        implements HashCodeEqualsDefinedTesting<SpreadsheetError>, HasJsonNodeTesting<SpreadsheetError> {

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
    
    // equals...............................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(SpreadsheetError.with("different"));
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(SpreadsheetError.with(MESSAGE.toUpperCase()));
    }

    // HasJsonNode...............................................................................................

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(), JsonNode.string(MESSAGE));
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        assertEquals(MESSAGE, this.createObject().toString());
    }

    @Override
    public SpreadsheetError createObject() {
        return SpreadsheetError.with(MESSAGE);
    }

    private void checkValue(final SpreadsheetError error, final String value) {
        assertEquals("formula", value, error.value());
    }

    @Override
    protected Class<SpreadsheetError> type() {
        return SpreadsheetError.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
