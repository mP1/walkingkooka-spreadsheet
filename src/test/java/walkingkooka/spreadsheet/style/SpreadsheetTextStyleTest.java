package walkingkooka.spreadsheet.style;

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTextStyleTest implements ClassTesting2<SpreadsheetTextStyle>,
        HashCodeEqualsDefinedTesting<SpreadsheetTextStyle>,
        HasJsonNodeTesting<SpreadsheetTextStyle>,
        ToStringTesting<SpreadsheetTextStyle> {

    @Test
    public void testWithNullFontFamilyFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(null, this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru());
        });
    }

    @Test
    public void testWithNullFontSizeFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(this.fontFamily(), null, this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru());
        });
    }

    @Test
    public void testWithNullColorFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), null, this.backgroundColor(), this.bold(), this.italics(), this.underline(), this.strikethru());
        });
    }

    @Test
    public void testWithNullBackgroundColorFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), null, this.bold(), this.italics(), this.underline(), this.strikethru());
        });
    }

    @Test
    public void testWithNullBoldFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), null, this.italics(), this.underline(), this.strikethru());
        });
    }

    @Test
    public void testWithNullItalicsFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), null, this.underline(), this.strikethru());
        });
    }

    @Test
    public void testWithNullUnderlineFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), null, this.strikethru());
        });
    }

    @Test
    public void testWithNullStrikethruFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetTextStyle.with(this.fontFamily(), this.fontSize(), this.color(), this.backgroundColor(), this.bold(), this.italics(), this.underline(), null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetTextStyle style = this.createObject();

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setFontFamily(this.fontFamily()));
    }

    @Test
    public void testSetFontFamilyNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setFontFamily(null);
        });
    }

    @Test
    public void testSetFontFamilyDifferent() {
        final Optional<FontFamilyName> different = SpreadsheetTextStyle.NO_FONT_FAMILY;
        final SpreadsheetTextStyle style = this.createObject().setFontFamily(different);

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
        final SpreadsheetTextStyle style = this.createObject().setFontFamily(different);

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setFontSize(this.fontSize()));
    }

    @Test
    public void testSetFontSizeNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setFontSize(null);
        });
    }

    @Test
    public void testSetFontSizeDifferent() {
        final Optional<FontSize> different = SpreadsheetTextStyle.NO_FONT_SIZE;
        final SpreadsheetTextStyle style = this.createObject().setFontSize(different);

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
        final SpreadsheetTextStyle style = this.createObject().setFontSize(different);

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setColor(this.color()));
    }

    @Test
    public void testSetColorNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setColor(null);
        });
    }

    @Test
    public void testSetColorDifferent() {
        final Optional<Color> different = SpreadsheetTextStyle.NO_COLOR;
        final SpreadsheetTextStyle style = this.createObject().setColor(different);

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
        final Optional<Color> different = this.differentColor();
        final SpreadsheetTextStyle style = this.createObject().setColor(different);

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setBackgroundColor(this.backgroundColor()));
    }

    @Test
    public void testSetBackgroundColorNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setBackgroundColor(null);
        });
    }

    @Test
    public void testSetBackgroundColorDifferent() {
        final Optional<Color> different = SpreadsheetTextStyle.NO_BACKGROUND_COLOR;
        final SpreadsheetTextStyle style = this.createObject().setBackgroundColor(different);

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
        final Optional<Color> different = this.differentColor();
        final SpreadsheetTextStyle style = this.createObject().setBackgroundColor(different);

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setBold(this.bold()));
    }

    @Test
    public void testSetBoldNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setBold(null);
        });
    }

    @Test
    public void testSetBoldDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_BOLD;
        final SpreadsheetTextStyle style = this.createObject().setBold(different);

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
        final Optional<Boolean> different = this.falseValue();
        final SpreadsheetTextStyle style = this.createObject().setBold(different);

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setItalics(this.bold()));
    }

    @Test
    public void testSetItalicsNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setItalics(null);
        });
    }

    @Test
    public void testSetItalicsDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_ITALICS;
        final SpreadsheetTextStyle style = this.createObject().setItalics(different);

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
        final Optional<Boolean> different = this.falseValue();
        final SpreadsheetTextStyle style = this.createObject().setItalics(different);

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setUnderline(this.bold()));
    }

    @Test
    public void testSetUnderlineNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setUnderline(null);
        });
    }

    @Test
    public void testSetUnderlineDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_UNDERLINE;
        final SpreadsheetTextStyle style = this.createObject().setUnderline(different);

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
        final Optional<Boolean> different = this.falseValue();
        final SpreadsheetTextStyle style = this.createObject().setUnderline(different);

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
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.setStrikethru(this.bold()));
    }

    @Test
    public void testSetStrikethruNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setStrikethru(null);
        });
    }

    @Test
    public void testSetStrikethruDifferent() {
        final Optional<Boolean> different = SpreadsheetTextStyle.NO_STRIKETHRU;
        final SpreadsheetTextStyle style = this.createObject().setStrikethru(different);

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
        final Optional<Boolean> different = this.falseValue();
        final SpreadsheetTextStyle style = this.createObject().setStrikethru(different);

        this.checkFontFamily(style, this.fontFamily());
        this.checkFontSize(style, this.fontSize());
        this.checkColor(style, this.color());
        this.checkBackgroundColor(style, this.backgroundColor());
        this.checkBold(style, this.bold());
        this.checkItalics(style, this.italics());
        this.checkUnderline(style, this.underline());
        this.checkStrikethru(style, different);
    }

    // merge.........................................................................................................

    @Test
    public void testMergeNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().merge(null);
        });
    }

    @Test
    public void testMergeSelf() {
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.merge(style));
    }

    @Test
    public void testMergeWithEmpty() {
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, style.merge(SpreadsheetTextStyle.EMPTY));
    }

    @Test
    public void testMerge1() {
        final SpreadsheetTextStyle style = this.createObject();
        assertSame(style, SpreadsheetTextStyle.EMPTY.merge(style));
    }

    @Test
    public void testMerge2() {
        final SpreadsheetTextStyle style = SpreadsheetTextStyle.EMPTY
                .setBold(this.bold())
                .setItalics(this.falseValue());
        final SpreadsheetTextStyle other = SpreadsheetTextStyle.EMPTY
                .setBold(this.falseValue())
                .setUnderline(this.underline());

        this.mergeAndCheck(style, other, SpreadsheetTextStyle.EMPTY
                .setBold(this.bold())
                .setItalics(this.falseValue())
                .setUnderline(this.underline()));
    }

    @Test
    public void testMerge3() {
        final SpreadsheetTextStyle style = SpreadsheetTextStyle.EMPTY
                .setColor(this.differentColor())
                .setBold(this.falseValue())
                .setItalics(this.falseValue());
        final SpreadsheetTextStyle other = this.createObject();

        this.mergeAndCheck(style, other, this.createObject()
                .setColor(this.differentColor())
                .setBold(this.falseValue())
                .setItalics(this.falseValue())
                .setUnderline(this.underline()));
    }

    private void mergeAndCheck(final SpreadsheetTextStyle style, final SpreadsheetTextStyle other, final SpreadsheetTextStyle expected) {
        assertEquals(expected,
                style.merge(other),
                () -> style + " merge " + other + " failed");
    }

    // equals ........................................................................................................

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

    // isEmpty.........................................................................................................

    @Test
    public void testIsEmptyEmpty() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY,
                true);
    }

    @Test
    public void testIsEmptyEmptyFontFamily() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setFontFamily(SpreadsheetTextStyle.NO_FONT_FAMILY),
                true);
    }

    @Test
    public void testIsEmptyEmptyFontSize() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setFontSize(SpreadsheetTextStyle.NO_FONT_SIZE),
                true);
    }

    @Test
    public void testIsEmptyEmptyBold() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.NO_BOLD),
                true);
    }

    @Test
    public void testIsEmptyEmptyItalics() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.NO_ITALICS),
                true);
    }

    @Test
    public void testIsEmptyEmptyUnderline() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setUnderline(SpreadsheetTextStyle.NO_UNDERLINE),
                true);
    }

    @Test
    public void testIsEmptyEmptyStrikethru() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setStrikethru(SpreadsheetTextStyle.NO_STRIKETHRU),
                true);
    }

    @Test
    public void testIsEmptyNotEmpty() {
        this.isEmptyAndCheck(this.createObject(), false);
    }

    @Test
    public void testIsEmptyNotEmptyFontFamily() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setFontFamily(Optional.of(FontFamilyName.with("Times New Roman"))),
                false);
    }

    @Test
    public void testIsEmptyNotEmptyFontSize() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setFontSize(Optional.of(FontSize.with(10))),
                false);
    }

    @Test
    public void testIsEmptyNotEmptyBold() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD),
                false);
    }

    @Test
    public void testIsEmptyNotEmptyItalics() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS),
                false);
    }

    @Test
    public void testIsEmptyNotEmptyUnderline() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setUnderline(SpreadsheetTextStyle.UNDERLINE),
                false);
    }

    @Test
    public void testIsEmptyNotEmptyStrikethru() {
        this.isEmptyAndCheck(SpreadsheetTextStyle.EMPTY.setStrikethru(SpreadsheetTextStyle.STRIKETHRU),
                false);
    }

    private void isEmptyAndCheck(final SpreadsheetTextStyle style, final boolean empty) {
        assertEquals(empty,
                style.isEmpty(),
                style + " is empty");
    }

    // HasJsonNode ........................................................................................................

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(),
                "{\n" +
                        "\t\"font-family\": \"Times New Roman\",\n" +
                        "\t\"font-size\": 12,\n" +
                        "\t\"color\": \"#11ff33\",\n" +
                        "\t\"background-color\": \"#11ff33\",\n" +
                        "\t\"bold\": true,\n" +
                        "\t\"italics\": true,\n" +
                        "\t\"underline\": true,\n" +
                        "\t\"strikethru\": true\n" +
                        "}");
    }

    @Test
    public void testToJsonNodeMissingSome() {
        this.toJsonNodeAndCheck(SpreadsheetTextStyle.EMPTY.setFontFamily(this.fontFamily())
                        .setColor(this.color())
                        .setBold(this.bold())
                        .setItalics(this.italics()),
                "{\n" +
                        "\t\"font-family\": \"Times New Roman\",\n" +
                        "\t\"color\": \"#11ff33\",\n" +
                        "\t\"bold\": true,\n" +
                        "\t\"italics\": true\n" +
                        "}");
    }

    @Test
    public void testToJsonNodeFalse() {
        this.toJsonNodeAndCheck(SpreadsheetTextStyle.EMPTY.setFontFamily(this.fontFamily())
                        .setBold(Optional.of(Boolean.FALSE)),
                "{\n" +
                        "\t\"font-family\": \"Times New Roman\",\n" +
                        "\t\"bold\": false\n" +
                        "}");
    }

    // toString.........................................................................................................

    @Test
    public void testToStringAll() {
        this.toStringAndCheck(this.createObject(),
                "Times New Roman 12 #11ff33 #11ff33 bold italics underline strikethru");
    }

    @Test
    public void testToStringEmpty() {
        this.toStringAndCheck(SpreadsheetTextStyle.EMPTY, "");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetTextStyle.EMPTY
                        .setBold(Optional.of(true))
                        .setFontFamily(this.fontFamily())
                        .setColor(Optional.of(Color.fromRgb(0x123456))),
                "Times New Roman #123456 bold");
    }

    @Test
    public void testToString2() {
        this.toStringAndCheck(SpreadsheetTextStyle.EMPTY
                        .setBold(Optional.of(true))
                        .setItalics(Optional.of(true))
                        .setStrikethru(this.falseValue())
                        .setUnderline(this.falseValue())
                        .setFontFamily(this.fontFamily())
                        .setColor(Optional.of(Color.fromRgb(0x123456))),
                "Times New Roman #123456 bold italics");
    }

    // helpers.........................................................................................................

    @Override
    public SpreadsheetTextStyle createObject() {
        return SpreadsheetTextStyle.with(
                this.fontFamily(),
                this.fontSize(),
                this.color(),
                this.backgroundColor(),
                this.bold(),
                this.italics(),
                this.underline(),
                this.strikethru());
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

    private void checkFontFamily(final SpreadsheetTextStyle style, final Optional<FontFamilyName> fontFamily) {
        assertEquals(fontFamily, style.fontFamily(), "fontFamily");
    }

    private void checkFontSize(final SpreadsheetTextStyle style, final Optional<FontSize> fontSize) {
        assertEquals(fontSize, style.fontSize(), "fontSize");
    }

    private void checkColor(final SpreadsheetTextStyle style, final Optional<Color> color) {
        assertEquals(color, style.color(), "color");
    }

    private void checkBackgroundColor(final SpreadsheetTextStyle style, final Optional<Color> backgroundColor) {
        assertEquals(backgroundColor, style.backgroundColor(), "backgroundColor");
    }

    private void checkBold(final SpreadsheetTextStyle style, final Optional<Boolean> bold) {
        assertEquals(bold, style.bold(), "bold");
    }

    private void checkItalics(final SpreadsheetTextStyle style, final Optional<Boolean> italics) {
        assertEquals(italics, style.italics(), "italics");
    }

    private void checkUnderline(final SpreadsheetTextStyle style, final Optional<Boolean> underline) {
        assertEquals(underline, style.underline(), "underline");
    }

    private void checkStrikethru(final SpreadsheetTextStyle style, final Optional<Boolean> strikethru) {
        assertEquals(strikethru, style.strikethru(), "strikethru");
    }

    @Override
    public Class<SpreadsheetTextStyle> type() {
        return SpreadsheetTextStyle.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
