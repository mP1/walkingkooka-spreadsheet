package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;

import static org.junit.Assert.assertEquals;

public final class FontSizeTest extends PublicClassTestCase<FontSize> {

    @Test(expected = IllegalArgumentException.class)
    public void testWithNegativeValueFails() {
        FontSize.with(-1);
    }

    @Test
    public void testWith() {
        final Integer value = 10;
        final FontSize size = FontSize.with(value);
        assertEquals("value", value, size.value());
    }

    @Test
    public void testToString() {
        assertEquals("10", FontSize.with(10).toString());
    }

    @Override
    protected Class<FontSize> type() {
        return FontSize.class;
    }
}
