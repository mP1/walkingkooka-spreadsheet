package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.type.MemberVisibility;

import static org.junit.Assert.assertEquals;


public final class SpreadsheetIdTest extends ClassTestCase<SpreadsheetId>
        implements HashCodeEqualsDefinedTesting<SpreadsheetId> {

    private final static Long VALUE = 123L;

    @Test
    public void testWith() {
        final SpreadsheetId id = SpreadsheetId.with(VALUE);
        assertEquals("id", VALUE, id.value());
    }

    @Test
    public void testDifferentSpreadsheetId() {
        this.checkNotEquals(SpreadsheetId.with(999));
    }

    @Test
    public void testToString() {
        assertEquals("" + VALUE, SpreadsheetId.with(VALUE).toString());
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    @Override
    public SpreadsheetId createObject() {
        return SpreadsheetId.with(VALUE);
    }

    @Override
    protected Class<SpreadsheetId> type() {
        return SpreadsheetId.class;
    }
}
