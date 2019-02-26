package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.test.TestSuiteNameTesting;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetHateosHandlerTestCase2<H extends HateosHandler<I, JsonNode>, I extends Comparable<I>, V extends HasJsonNode>
        extends SpreadsheetHateosHandlerTestCase<H>
        implements HateosHandlerTesting<H, I, JsonNode> {

    SpreadsheetHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testWithNullContentTypeFails() {
        assertThrows(NullPointerException.class, () -> {
           this.createHandler(null);
        });
    }

    @Override
    public final H createHandler() {
        return this.createHandler(this.contentType());
    }

    final HateosContentType<JsonNode, V> contentType() {
        return HateosContentType.json();
    }

    abstract H createHandler(final HateosContentType<JsonNode, V> type);
}
