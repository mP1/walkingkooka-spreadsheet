package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.naming.NameTesting;
import walkingkooka.test.ClassTestCase;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.type.MemberVisibility;

import static org.junit.Assert.assertEquals;

public final class FontFamilyNameTest extends ClassTestCase<FontFamilyName>
        implements NameTesting<FontFamilyName, FontFamilyName> {

    @Test
    public void testWith() {
        this.createNameAndCheck("Times New Roman");
    }

    @Test
    public void testToString() {
        assertEquals("abc", this.createName("abc").toString());
    }

    @Override
    public FontFamilyName createName(final String name) {
        return FontFamilyName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "Times New Roman";
    }

    @Override
    public String differentNameText() {
        return "Different";
    }

    @Override
    public String nameTextLess() {
        return "Antiqua";
    }

    @Override
    protected Class<FontFamilyName> type() {
        return FontFamilyName.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
