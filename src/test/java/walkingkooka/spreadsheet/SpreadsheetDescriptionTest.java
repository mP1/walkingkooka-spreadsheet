package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.type.MemberVisibility;

import static org.junit.Assert.assertEquals;


public final class SpreadsheetDescriptionTest extends ClassTestCase<SpreadsheetDescription>
        implements HashCodeEqualsDefinedTesting<SpreadsheetDescription> {

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

    // equals...............................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(SpreadsheetDescription.with("different"));
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(SpreadsheetDescription.with(TEXT.toUpperCase()));
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        assertEquals(CharSequences.quote(TEXT).toString(), this.createObject().toString());
    }

    @Override
    public SpreadsheetDescription createObject() {
        return SpreadsheetDescription.with(TEXT);
    }

    private void checkValue(final SpreadsheetDescription description, final String value) {
        assertEquals("value", value, description.value());
    }

    @Override
    protected Class<SpreadsheetDescription> type() {
        return SpreadsheetDescription.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
