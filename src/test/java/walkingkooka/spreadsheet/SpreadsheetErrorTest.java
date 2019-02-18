package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public final class SpreadsheetErrorTest implements ClassTesting2<SpreadsheetError>,
        HashCodeEqualsDefinedTesting<SpreadsheetError>,
        HasJsonNodeTesting<SpreadsheetError>,
        ToStringTesting<SpreadsheetError> {

    private final static String MESSAGE = "message #1";

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetError.with(null);
        });
    }

    @Test
    public void testWithEmptyValueFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetError.with("");
        });
    }

    @Test
    public void testWithWhitespaceValueFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetError.with(" \t");
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetError error = SpreadsheetError.with(MESSAGE);
        this.checkValue(error, MESSAGE);
    }

    // equals...............................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(SpreadsheetError.with("different"));
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(SpreadsheetError.with(MESSAGE.toUpperCase()));
    }

    // HasJsonNode...............................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testFromJsonNodeNullFails() {
        this.fromJsonNodeFails(JsonNode.nullNode());
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(12));
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array());
    }

    @Test
    public void testFromJsonNodeObjectFails() {
        this.fromJsonNodeFails(JsonNode.object());
    }

    @Test
    public void testFromJsonNodeStringInvalidFails() {
        this.fromJsonNodeFails(JsonNode.string(""));
    }

    @Test
    public void testFromJsonNodeString() {
        this.fromJsonNodeAndCheck(JsonNode.string(MESSAGE),
                SpreadsheetError.with(MESSAGE));
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(), JsonNode.string(MESSAGE));
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), MESSAGE);
    }

    @Override
    public SpreadsheetError createObject() {
        return SpreadsheetError.with(MESSAGE);
    }

    private void checkValue(final SpreadsheetError error, final String value) {
        assertEquals(value, error.value(), "error");
    }

    @Override
    public Class<SpreadsheetError> type() {
        return SpreadsheetError.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    // HasJsonNodeTesting............................................................

    @Override
    public SpreadsheetError fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetError.fromJsonNode(jsonNode);
    }
}
