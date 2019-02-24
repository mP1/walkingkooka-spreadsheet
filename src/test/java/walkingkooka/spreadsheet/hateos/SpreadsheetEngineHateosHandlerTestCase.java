package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.header.Link;
import walkingkooka.net.http.server.hateos.FakeHateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetEngineHateosHandlerTestCase<H extends HateosHandler<I, JsonNode>, I extends Comparable<I>>
        extends SpreadsheetHateosHandlerTestCase2<H, I> {

    SpreadsheetEngineHateosHandlerTestCase() {
        super();
    }

    @Test
    public void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(this.engine(), null, this.engineContextSupplier());
        });
    }

    @Test
    public void testWithNullEngineContextSupplierFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(this.engine(), this.contentType(), null);
        });
    }

    @Override
    final H createHandler(final HateosContentType<JsonNode, HasJsonNode> contentType) {
        return this.createHandler(this.engine(), contentType, this.engineContextSupplier());
    }

    abstract H createHandler(final SpreadsheetEngine engine,
                             final HateosContentType<JsonNode, HasJsonNode> contentType,
                             final Supplier<SpreadsheetEngineContext> context);

    @Override
    public HateosHandlerContext<JsonNode> createContext() {
        return new FakeHateosHandlerContext<JsonNode>() {
            @Override
            public JsonNode addLinks(final HateosResourceName name,
                                     final Comparable<?> id,
                                     final JsonNode node) {
                return SpreadsheetEngineHateosHandlerTestCase.this.addLinks(name,
                        id,
                        JsonObjectNode.class.cast(node));
            }
        };
    }

    private JsonNode addLinks(final HateosResourceName name,
                              final Comparable<?> id,
                              final JsonObjectNode node) {
        return node.set(JsonNodeName.with("_links"),
                Link.parse("<http://example.com/" + name + "/" + id + "/self>;type=text/plain").get(0).toJsonNode());
    }

    abstract SpreadsheetEngine engine();

    final Supplier<SpreadsheetEngineContext> engineContextSupplier() {
        return this::engineContext;
    }

    abstract SpreadsheetEngineContext engineContext();
}
