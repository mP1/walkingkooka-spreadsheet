package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.header.Link;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.hateos.FakeHateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosGetHandler;
import walkingkooka.net.http.server.hateos.HateosGetHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineLoading;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetEngineLoadCellHateosGetHandlerTest extends SpreadsheetEngineCellHateosHandlerTestCase<SpreadsheetEngineLoadCellHateosGetHandler<JsonNode>>
        implements HateosGetHandlerTesting<SpreadsheetEngineLoadCellHateosGetHandler<JsonNode>, SpreadsheetCellReference, JsonNode>,
        ToStringTesting<SpreadsheetEngineLoadCellHateosGetHandler<JsonNode>> {

    private final static SpreadsheetEngineLoading LOADING = SpreadsheetEngineLoading.FORCE_RECOMPUTE;

    @Test
    public void testLoadCellInvalidLoadingParameterFails() {
        this.getFails(this.createHandler(),
                this.id(),
                HateosGetHandler.NO_PARAMETERS,
                this.createContext(),
                IllegalArgumentException.class);
    }

    @Test
    public void testLoadCellInvalidLoadingParameterFails2() {
        this.getFails(this.createHandler(),
                this.id(),
                Maps.one(HttpRequestParameterName.with("loading"), Lists.of("a", "b")),
                this.createContext(),
                IllegalArgumentException.class);
    }

    @Test
    public void testLoadCell() {
        this.getAndCheck(this.createHandler(),
                this.id(),
                this.parameters(),
                this.createContext(),
                JsonNode.parse("{\n" +
                        "  \"reference\": \"A1\",\n" +
                        "  \"formula\": {\n" +
                        "    \"text\": \"1+2\"\n" +
                        "  },\n" +
                        "  \"style\": {},\n" +
                        "  \"_links\": {\n" +
                        "    \"href\": \"http://example.com/cell/A1/self\",\n" +
                        "    \"type\": \"text/plain\"\n" +
                        "  }\n" +
                        "}"));
    }

    @Test
    public void testGetCollectionUnsupportedOperationException() {
        this.getCollectionFails(Range.all(),
                this.parameters(),
                this.createContext(),
                UnsupportedOperationException.class);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.loadCell");
    }

    @Override
    public void testGetCollectionNullCollectionFails() {
        // nop
    }

    @Override
    public void testGetCollectionNullParametCollectionersFails() {
        // nop
    }

    @Override
    public void testGetCollectionNullContextFails() {
        // nop
    }

    @Override
    SpreadsheetEngineLoadCellHateosGetHandler<JsonNode> createHandler(final SpreadsheetEngine engine,
                                                                      final HateosContentType<JsonNode, SpreadsheetCell> contentType,
                                                                      final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineLoadCellHateosGetHandler.with(engine, contentType, context);
    }

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return Range.all();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return this.parameters(LOADING);
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final SpreadsheetEngineLoading loading) {
        return Maps.one(UrlParameterName.with("loading"), Lists.of(loading.toString()));
    }

    @Override
    public HateosHandlerContext<JsonNode> createContext() {
        return new FakeHateosHandlerContext<JsonNode>() {
            @Override
            public JsonNode addLinks(final HateosResourceName name,
                                     final Comparable<?> id,
                                     final JsonNode node) {
                return SpreadsheetEngineLoadCellHateosGetHandlerTest.this.addLinks(name,
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

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine(){
            @Override
            public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference id,
                                                      final SpreadsheetEngineLoading loading,
                                                      final SpreadsheetEngineContext context) {
                Objects.requireNonNull(id, "id");
                Objects.requireNonNull(loading, "loading");
                Objects.requireNonNull(context, "context");


                assertEquals(SpreadsheetEngineLoadCellHateosGetHandlerTest.this.id(), id, "id");
                assertEquals(LOADING, loading, "loading");
                assertNotEquals(null, context, "context");

                return Optional.of(SpreadsheetEngineLoadCellHateosGetHandlerTest.this.cell());
            }
        };
    }

    private SpreadsheetCell cell() {
        return SpreadsheetCell.with(this.id(), SpreadsheetFormula.with("1+2"), SpreadsheetCellStyle.EMPTY);
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineLoadCellHateosGetHandler<JsonNode>> type() {
        return Cast.to(SpreadsheetEngineLoadCellHateosGetHandler.class);
    }
}
