package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.naming.NameTestCase;
import walkingkooka.text.CaseSensitivity;

import static org.junit.Assert.assertEquals;

public final class FontFamilyNameTest extends NameTestCase<FontFamilyName, FontFamilyName> {

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
    protected CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    protected String nameText() {
        return "Times New Roman";
    }

    @Override
    protected String differentNameText() {
        return "Different";
    }

    @Override
    protected String nameTextLess() {
        return "Antiqua";
    }

    @Override
    protected Class<FontFamilyName> type() {
        return FontFamilyName.class;
    }
}
