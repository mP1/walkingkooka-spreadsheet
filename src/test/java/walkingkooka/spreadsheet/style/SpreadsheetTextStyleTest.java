package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.color.Color;
import walkingkooka.test.PublicClassTestCase;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public final class SpreadsheetTextStyleTest extends PublicClassTestCase<SpreadsheetTextStyle> {

    @Test(expected = NullPointerException.class)
    public void testWithNullFontFamilyFails() {
        SpreadsheetTextStyle.with(null, this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullFontSizeFails() {
        SpreadsheetTextStyle.with(this.fontFamily(), null, this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullColorFails() {
        SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), null, this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullBackgroundColorFails() {
        SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), null, this.bold(), this.italics(), this.underline(), this.strikethru());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullBoldFails() {
        SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), null, this.italics(), this.underline(), this.strikethru());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullItalicsFails() {
        SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), null, this.underline(), this.strikethru());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullUnderlineFails() {
        SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), null, this.strikethru());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullStrikethruFails() {
        SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), null);
    }

    @Test
    public void testWith() {
        final SpreadsheetTextStyle style = this.create();

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    @Test
    public void testEmpty() {
        final SpreadsheetTextStyle style = SpreadsheetTextStyle.EMPTY;

        this.checkFontFamily(style, SpreadsheetTextStyle.NO_FONT_FAMILY);
        this.checkFontSize(style, SpreadsheetTextStyle.NO_FONT_SIZE);
        this.checkColor(style, SpreadsheetTextStyle.NO_COLOR);
        this.checkBackgroundColor(style, SpreadsheetTextStyle.NO_BACKGROUND_COLOR);
        this.checkBold(style, SpreadsheetTextStyle.NO_BOLD);
        this.checkItalics(style, SpreadsheetTextStyle.NO_ITALICS);
        this.checkUnderline(style, SpreadsheetTextStyle.NO_UNDERLINE);
        this.checkStrikethru(style, SpreadsheetTextStyle.NO_STRIKETHRU);
    }
    
    // setFontFamily......................................................................................................
    
    @Test
    public void testSetFontFamilySame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setFontFamily(this.fontFamily()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetFontFamilyNullFails() {
        this.create().setFontFamily(null);
    }
    
    @Test
    public void testSetFontFamilyDifferent() {
        final Optional<FontFamilyName> different = SpreadsheetTextStyle.NO_FONT_FAMILY;
        final SpreadsheetTextStyle style = this.create().setFontFamily(different);

        this.checkFontFamily(style, different);
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru()); 
    }

    @Test
    public void testSetFontFamilyDifferent2() {
        final Optional<FontFamilyName> different = Optional.of(FontFamilyName.with("Different"));
        final SpreadsheetTextStyle style = this.create().setFontFamily(different);

        this.checkFontFamily(style, different);
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }


    // setFontSize......................................................................................................

    @Test
    public void testSetFontSizeSame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setFontSize(this.fontSize()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetFontSizeNullFails() {
        this.create().setFontSize(null);
    }

    @Test
    public void testSetFontSizeDifferent() {
        final Optional<FontSize> different = SpreadsheetTextStyle.NO_FONT_SIZE;
        final SpreadsheetTextStyle style = this.create().setFontSize(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, different);
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    @Test
    public void testSetFontSizeDifferent2() {
        final Optional<FontSize> different = Optional.of(FontSize.with(5));
        final SpreadsheetTextStyle style = this.create().setFontSize(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, different);
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    // setColor......................................................................................................

    @Test
    public void testSetColorSame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setColor(this.color()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetColorNullFails() {
        this.create().setColor(null);
    }

    @Test
    public void testSetColorDifferent() {
        final Optional<Color> different = SpreadsheetTextStyle.NO_COLOR;
        final SpreadsheetTextStyle style = this.create().setColor(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, different);
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    @Test
    public void testSetColorDifferent2() {
        final Optional<Color> different = Optional.of(Color.WHITE);
        final SpreadsheetTextStyle style = this.create().setColor(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, different);
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }


    // setBackgroundColor......................................................................................................

    @Test
    public void testSetBackgroundColorSame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setBackgroundColor(this.backgroundColor()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetBackgroundColorNullFails() {
        this.create().setBackgroundColor(null);
    }

    @Test
    public void testSetBackgroundColorDifferent() {
        final Optional<Color> different = SpreadsheetTextStyle.NO_BACKGROUND_COLOR;
        final SpreadsheetTextStyle style = this.create().setBackgroundColor(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, different);
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    @Test
    public void testSetBackgroundColorDifferent2() {
        final Optional<Color> different = Optional.of(Color.WHITE);
        final SpreadsheetTextStyle style = this.create().setBackgroundColor(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, different);
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    // setBold......................................................................................................

    @Test
    public void testSetBoldSame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setBold(this.bold()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetBoldNullFails() {
        this.create().setBold(null);
    }

    @Test
    public void testSetBoldDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_BOLD;
        final SpreadsheetTextStyle style = this.create().setBold(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, different);
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    @Test
    public void testSetBoldDifferent2() {
        final Optional<Boolean> different = Optional.of(false);
        final SpreadsheetTextStyle style = this.create().setBold(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, different);
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    // setItalics......................................................................................................

    @Test
    public void testSetItalicsSame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setItalics(this.bold()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetItalicsNullFails() {
        this.create().setItalics(null);
    }

    @Test
    public void testSetItalicsDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_ITALICS;
        final SpreadsheetTextStyle style = this.create().setItalics(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, different);
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    @Test
    public void testSetItalicsDifferent2() {
        final Optional<Boolean> different = Optional.of(false);
        final SpreadsheetTextStyle style = this.create().setItalics(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, different);
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, this.strikethru());
    }

    // setUnderline......................................................................................................

    @Test
    public void testSetUnderlineSame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setUnderline(this.bold()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetUnderlineNullFails() {
        this.create().setUnderline(null);
    }

    @Test
    public void testSetUnderlineDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_UNDERLINE;
        final SpreadsheetTextStyle style = this.create().setUnderline(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, different);
        this.checkStrikethru(style, this.strikethru());
    }

    @Test
    public void testSetUnderlineDifferent2() {
        final Optional<Boolean> different = Optional.of(false);
        final SpreadsheetTextStyle style = this.create().setUnderline(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, different);
        this.checkStrikethru(style, this.strikethru());
    }
    
    // setStrikethru......................................................................................................

    @Test
    public void testSetStrikethruSame() {
        final SpreadsheetTextStyle style = this.create();
        assertSame(style, style.setStrikethru(this.bold()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetStrikethruNullFails() {
        this.create().setStrikethru(null);
    }

    @Test
    public void testSetStrikethruDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_STRIKETHRU;
        final SpreadsheetTextStyle style = this.create().setStrikethru(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, different);
    }

    @Test
    public void testSetStrikethruDifferent2() {
        final Optional<Boolean> different = Optional.of(false);
        final SpreadsheetTextStyle style = this.create().setStrikethru(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, different);
    }

    // toString.........................................................................................................

    @Test
    public void testToStringAll() {
        assertEquals("Times New Roman 12 #11FF33 #11FF33 bold italics underline strikethru",
                this.create().toString());
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("",
                SpreadsheetTextStyle.EMPTY.toString());
    }

    @Test
    public void testToString() {
        assertEquals("Times New Roman #01E240 bold", SpreadsheetTextStyle.EMPTY
                .setBold(Optional.of(true))
                .setFontFamily(this.fontFamily())
                .setColor(Optional.of(Color.fromRgb(123456)))
                .toString());
    }

    @Test
    public void testToString2() {
        assertEquals("Times New Roman #01E240 bold italics", SpreadsheetTextStyle.EMPTY
                .setBold(Optional.of(true))
                .setItalics(Optional.of(true))
                .setStrikethru(Optional.of(false))
                .setUnderline(Optional.of(false))
                .setFontFamily(this.fontFamily())
                .setColor(Optional.of(Color.fromRgb(123456)))
                .toString());
    }

    // helpers.........................................................................................................

    private SpreadsheetTextStyle create() {
        return SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru());
    }

    private Optional<FontFamilyName> fontFamily() {
        return Optional.of(FontFamilyName.with("Times New Roman"));
    }

    private Optional<FontSize> fontSize() {
        return Optional.of(FontSize.with(12));
    }

    private Optional<Color> color() {
        return Optional.of(Color.fromRgb(0x11FF33));
    }

    private Optional<Color> backgroundColor() {
        return Optional.of(Color.fromRgb(0x11FF33));
    }

    private Optional<Boolean> bold() {
        return Optional.of(true);
    }

    private Optional<Boolean> italics() {
        return Optional.of(true);
    }

    private Optional<Boolean> underline() {
        return Optional.of(true);
    }

    private Optional<Boolean> strikethru() {
        return Optional.of(true);
    }
    
    private void checkFontFamily(final SpreadsheetTextStyle style, final Optional<FontFamilyName> fontFamily) {
        assertEquals("fontFamily", fontFamily, style.fontFamily());
    }

    private void checkFontSize(final SpreadsheetTextStyle style, final Optional<FontSize> fontSize) {
        assertEquals("fontSize", fontSize, style.fontSize());
    }

    private void checkColor(final SpreadsheetTextStyle style, final Optional<Color> color) {
        assertEquals("color", color, style.color());
    }

    private void checkBackgroundColor(final SpreadsheetTextStyle style, final Optional<Color> backgroundColor) {
        assertEquals("backgroundColor", backgroundColor, style.backgroundColor());
    }

    private void checkBold(final SpreadsheetTextStyle style, final Optional<Boolean> bold) {
        assertEquals("bold", bold, style.bold());
    }

    private void checkItalics(final SpreadsheetTextStyle style, final Optional<Boolean> italics) {
        assertEquals("italics", italics, style.italics());
    }

    private void checkUnderline(final SpreadsheetTextStyle style, final Optional<Boolean> underline) {
        assertEquals("underline", underline, style.underline());
    }

    private void checkStrikethru(final SpreadsheetTextStyle style, final Optional<Boolean> strikethru) {
        assertEquals("strikethru", strikethru, style.strikethru());
    }

    @Override
    protected Class<SpreadsheetTextStyle> type() {
        return SpreadsheetTextStyle.class;
    }
}
