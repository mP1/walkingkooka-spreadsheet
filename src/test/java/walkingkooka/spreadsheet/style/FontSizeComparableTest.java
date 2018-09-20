package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.ComparableTestCase;

import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public final class FontSizeComparableTest extends ComparableTestCase<FontSize> {

    @Test
    public void testDifferent() {
        this.checkNotEquals(FontSize.with(20));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(FontSize.with(15));
    }

    @Test
    public void testInSortedSet() {
        final FontSize one = FontSize.with(1);
        final FontSize two = FontSize.with(2);
        final FontSize three = FontSize.with(3);

        final Set<FontSize> set = Sets.sorted();
        set.add(one);
        set.add(two);
        set.add(three);

        final Iterator<FontSize> values = set.iterator();
        assertEquals(one, values.next());
        assertEquals(two, values.next());
        assertEquals(three, values.next());
    }

    @Override
    protected FontSize createComparable() {
        return FontSize.with(10);
    }
}
