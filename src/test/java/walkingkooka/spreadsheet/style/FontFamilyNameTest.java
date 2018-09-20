package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.naming.NameTestCase;

import static org.junit.Assert.assertEquals;

public final class FontFamilyNameTest extends NameTestCase<FontFamilyName> {

    @Test
    public void testWith() {
        this.createNameAndCheck("Times New Roman");
    }

    @Test
    public void testToString() {
        assertEquals("abc", this.createName("abc").toString());
    }

    @Override
    protected FontFamilyName createName(final String name) {
        return FontFamilyName.with(name);
    }

    @Override
    protected Class<FontFamilyName> type() {
        return FontFamilyName.class;
    }
}
