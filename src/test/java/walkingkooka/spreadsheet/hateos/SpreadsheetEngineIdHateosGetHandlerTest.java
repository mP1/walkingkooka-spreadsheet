package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.FakeHateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosGetHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineLoading;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.JsonNode;

import java.util.Map;
import java.util.function.Supplier;

public final class SpreadsheetEngineIdHateosGetHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineIdHateosGetHandler<JsonNode>, SpreadsheetId, SpreadsheetId>
        implements HateosGetHandlerTesting<SpreadsheetEngineIdHateosGetHandler<JsonNode>, SpreadsheetId, JsonNode>,
        ToStringTesting<SpreadsheetEngineIdHateosGetHandler<JsonNode>> {

    @Test
    public void testId() {
        final SpreadsheetId id = this.id();
        this.getAndCheck(this.createHandler(),
                id,
                this.parameters(),
                this.createContext(),
                id.toJsonNode());
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
        this.toStringAndCheck(this.createHandler(), "SpreadsheetEngine.id");
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
    SpreadsheetEngineIdHateosGetHandler<JsonNode> createHandler(final SpreadsheetEngine engine,
                                                                final HateosContentType<JsonNode, SpreadsheetId> contentType,
                                                                final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineIdHateosGetHandler.with(engine, contentType, context);
    }

    @Override
    public SpreadsheetId id() {
        return SpreadsheetId.with(123456);
    }

    @Override
    public Range<SpreadsheetId> collection() {
        return Range.all();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final SpreadsheetEngineLoading loading) {
        return Maps.one(UrlParameterName.with("loading"), Lists.of(loading.toString()));
    }

    @Override
    public HateosHandlerContext<JsonNode> createContext() {
        return new FakeHateosHandlerContext<JsonNode>();
    }


    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine(){
            @Override
            public SpreadsheetId id() {
                return SpreadsheetEngineIdHateosGetHandlerTest.this.id();
            }
        };
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineIdHateosGetHandler<JsonNode>> type() {
        return Cast.to(SpreadsheetEngineIdHateosGetHandler.class);
    }
}
