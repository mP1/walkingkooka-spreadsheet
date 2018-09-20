package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.color.Color;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

import java.util.Optional;

public final class SpreadsheetTextStyleEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetTextStyle> {

    @Test
    public void testEmptyEquals() {
        this.checkEquals(SpreadsheetTextStyle.EMPTY, SpreadsheetTextStyle.EMPTY);
    }

    @Test
    public void testEmptyDifferent() {
        this.checkNotEquals(SpreadsheetTextStyle.EMPTY,
                SpreadsheetTextStyle.with(SpreadsheetTextStyle.NO_FONT_FAMILY,
                        SpreadsheetTextStyle.NO_FONT_SIZE,
                        SpreadsheetTextStyle.NO_COLOR,
                        SpreadsheetTextStyle.NO_BACKGROUND_COLOR,
                        this.falseValue(),
                        this.falseValue(),
                        this.falseValue(),
                        this.falseValue()));
    }

    @Test
    public void testDifferentFontFamily() {
        this.checkNotEquals(SpreadsheetTextStyle.with(Optional.of(FontFamilyName.with("Different")), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentFontFamily2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(SpreadsheetTextStyle.NO_FONT_FAMILY, this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentFontSize() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), Optional.of(FontSize.with(5)), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentFontSize2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), SpreadsheetTextStyle.NO_FONT_SIZE, this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentColor() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.differentColor(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentColor2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), SpreadsheetTextStyle.NO_COLOR, this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentBackgroundColor() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.differentColor(), this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentBackgroundColor2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), SpreadsheetTextStyle.NO_BACKGROUND_COLOR, this.bold(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentBold() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.falseValue(), this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentBold2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), SpreadsheetTextStyle.NO_BOLD, this.italics(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentItalics() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.falseValue(), this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentItalics2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), SpreadsheetTextStyle.NO_ITALICS, this.underline(), this.strikethru()));
    }

    @Test
    public void testDifferentUnderline() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.falseValue(), this.strikethru()));
    }

    @Test
    public void testDifferentUnderline2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), SpreadsheetTextStyle.NO_UNDERLINE, this.strikethru()));
    }

    @Test
    public void testDifferentStrikethru() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.falseValue()));
    }

    @Test
    public void testDifferentStrikethru2() {
        this.checkNotEquals(SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), SpreadsheetTextStyle.NO_STRIKETHRU));
    }

    @Override
    protected SpreadsheetTextStyle createObject() {
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

    private Optional<Color> differentColor() {
        return Optional.of(Color.WHITE);
    }

    private Optional<Boolean> falseValue() {
        return Optional.of(false);
    }
}
