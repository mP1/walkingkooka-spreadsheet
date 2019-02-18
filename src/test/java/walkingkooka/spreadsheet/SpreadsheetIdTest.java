package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;


public final class SpreadsheetIdTest implements ClassTesting2<SpreadsheetId>,
        HashCodeEqualsDefinedTesting<SpreadsheetId>,
        HasJsonNodeTesting<SpreadsheetId>,
        ToStringTesting<SpreadsheetId> {

    private final static Long VALUE = 123L;

    @Test
    public void testWith() {
        final SpreadsheetId id = SpreadsheetId.with(VALUE);
        assertEquals(VALUE, id.value(), "id");
    }

    @Test
    public void testDifferentSpreadsheetId() {
        this.checkNotEquals(SpreadsheetId.with(999));
    }

    @Test
    public void testToJsonNodeFromJsonNodeRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetId.with(VALUE));
    }

    @Test
    public void testToJsonNodeFromJsonNodeRoundtrip2() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetId.with(0xabcd));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetId.with(VALUE),
                "" + VALUE);
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    @Override
    public SpreadsheetId createObject() {
        return SpreadsheetId.with(VALUE);
    }

    @Override
    public Class<SpreadsheetId> type() {
        return SpreadsheetId.class;
    }

    // HasJsonNodeTesting..............................................................................

    @Override
    public SpreadsheetId fromJsonNode(final JsonNode node) {
        return SpreadsheetId.fromJsonNode(node);
    }
}
