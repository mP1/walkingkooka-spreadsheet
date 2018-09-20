package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.compare.ComparableTestCase;

public final class FontFamilyComparableTest extends ComparableTestCase<FontFamilyName> {

    @Test
    public void testLess() {
        this.compareToAndCheckLess(FontFamilyName.with("Zebra"));
    }

    @Override
    protected FontFamilyName createComparable() {
        return FontFamilyName.with("Times New Roman");
    }
}
