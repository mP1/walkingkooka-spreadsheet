package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetFormattedCell;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public final class SpreadsheetCellStyleTest extends ClassTestCase<SpreadsheetCellStyle>
        implements HashCodeEqualsDefinedTesting<SpreadsheetCellStyle> {

    @Test(expected = NullPointerException.class)
    public void testWithNullTextFails() {
        SpreadsheetCellStyle.with(null);
    }

    @Test
    public void testWith() {
        this.checkText(this.createObject(), this.text());
    }

    @Test
    public void testEmpty() {
        final SpreadsheetCellStyle style = SpreadsheetCellStyle.EMPTY;
        this.checkText(style, SpreadsheetTextStyle.EMPTY);
    }

    // setCellFormattedText....................................................................................

    @Test
    public void testSetCellFormattedText() {
        final SpreadsheetCellStyle style = this.createObject();
        final String text = "abc123";
        final SpreadsheetFormattedCell formatted = style.setCellFormattedText(text);
        assertEquals("text", text, formatted.text());
        assertEquals("style", style, formatted.style());
    }

    // setText......................................................................................................

    @Test
    public void testSetTextSame() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, style.setText(this.text()));
    }

    @Test(expected = NullPointerException.class)
    public void testSetTextNullFails() {
        this.createObject().setText(null);
    }

    @Test
    public void testSetTextDifferent() {
        final SpreadsheetTextStyle different = SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS);
        final SpreadsheetCellStyle style = this.createObject().setText(different);

        this.checkText(style, different);
    }

    // merge.........................................................................................................

    @Test(expected = NullPointerException.class)
    public void testMergeNullFails() {
        this.createObject().merge(null);
    }

    @Test
    public void testMergeSelf() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, style.merge(style));
    }

    @Test
    public void testMergeWithEmpty() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, style.merge(SpreadsheetCellStyle.EMPTY));
    }

    @Test
    public void testMerge1() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, SpreadsheetCellStyle.EMPTY.merge(style));
    }

    @Test
    public void testMerge2() {
        final SpreadsheetCellStyle style = SpreadsheetCellStyle.EMPTY.setText(
                SpreadsheetTextStyle.EMPTY
                        .setBold(SpreadsheetTextStyle.BOLD)
                        .setItalics(SpreadsheetTextStyle.ITALICS));
        final SpreadsheetCellStyle other = SpreadsheetCellStyle.EMPTY.setText(
                SpreadsheetTextStyle.EMPTY
                        .setBold(SpreadsheetTextStyle.NO_BOLD)
                        .setUnderline(SpreadsheetTextStyle.UNDERLINE));

        this.mergeAndCheck(style, other, SpreadsheetCellStyle.EMPTY.setText(
                SpreadsheetTextStyle.EMPTY
                        .setBold(SpreadsheetTextStyle.BOLD)
                        .setItalics(SpreadsheetTextStyle.ITALICS)
                        .setUnderline(SpreadsheetTextStyle.UNDERLINE)));
    }

    private void mergeAndCheck(final SpreadsheetCellStyle style, final SpreadsheetCellStyle other, final SpreadsheetCellStyle expected) {
        assertEquals(style + " merge " + other + " failed", expected, style.merge(other));
    }

    // toString.........................................................................................................

    @Test
    public void testToStringEmpty() {
        assertEquals("",
                SpreadsheetCellStyle.EMPTY.toString());
    }

    @Test
    public void testToString() {
        assertEquals("Times New Roman #01E240 bold", SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY
                .setBold(Optional.of(true))
                .setFontFamily(Optional.of(FontFamilyName.with("Times New Roman")))
                .setColor(Optional.of(Color.fromRgb(123456))))
                .toString());
    }


    // helpers.........................................................................................................

    @Override
    public SpreadsheetCellStyle createObject() {
        return SpreadsheetCellStyle.with(this.text());
    }

    private SpreadsheetTextStyle text() {
        return SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD);
    }

    private void checkText(final SpreadsheetCellStyle style, final SpreadsheetTextStyle text) {
        assertEquals("text", text, style.text());
    }

    @Override
    protected Class<SpreadsheetCellStyle> type() {
        return SpreadsheetCellStyle.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
