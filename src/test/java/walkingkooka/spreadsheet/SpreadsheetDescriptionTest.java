package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public final class SpreadsheetDescriptionTest implements ClassTesting2<SpreadsheetDescription>,
        HashCodeEqualsDefinedTesting<SpreadsheetDescription>,
        ToStringTesting<SpreadsheetDescription> {

    private final static String TEXT = "description #1";

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDescription.with(null);
        });
    }

    @Test
    public void testWithEmptyValueFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetDescription.with("");
        });
    }

    @Test
    public void testWithWhitespaceValueFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetDescription.with("   ");
        });
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
        this.toStringAndCheck(this.createObject(),
                CharSequences.quote(TEXT).toString());
    }

    @Override
    public SpreadsheetDescription createObject() {
        return SpreadsheetDescription.with(TEXT);
    }

    private void checkValue(final SpreadsheetDescription description, final String value) {
        assertEquals(value, description.value(), "value");
    }

    @Override
    public Class<SpreadsheetDescription> type() {
        return SpreadsheetDescription.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
