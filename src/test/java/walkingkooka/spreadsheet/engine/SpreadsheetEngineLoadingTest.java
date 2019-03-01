package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.net.Url;
import walkingkooka.net.header.Link;
import walkingkooka.test.ClassTesting2;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetEngineLoadingTest implements ClassTesting2<SpreadsheetEngineLoading>,
        HasJsonNodeTesting<SpreadsheetEngineLoading> {

    // HasJsonNode..........................................................................

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
        this.fromJsonNodeFails(JsonNode.number(123));
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
    public void testFromJsonNodeStringUnknownFails() {
        this.fromJsonNodeFails(JsonNode.string("123"));
    }

    @Test
    public void testToJsonRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test
    public void testToJsonRoundtrip2() {
        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetEngineLoading.SKIP_EVALUATE);
    }

    @Override
    public Class<SpreadsheetEngineLoading> type() {
        return SpreadsheetEngineLoading.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public SpreadsheetEngineLoading createHasJsonNode() {
        return SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY;
    }

    @Override
    public SpreadsheetEngineLoading fromJsonNode(final JsonNode node) {
        return SpreadsheetEngineLoading.fromJsonNode(node);
    }
}
