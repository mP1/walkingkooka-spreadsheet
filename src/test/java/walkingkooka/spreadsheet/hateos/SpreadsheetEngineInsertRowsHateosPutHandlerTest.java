package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosPutHandlerTesting;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SpreadsheetEngineInsertRowsHateosPutHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineInsertRowsHateosPutHandler<JsonNode>, SpreadsheetRowReference>
        implements HateosPutHandlerTesting<SpreadsheetEngineInsertRowsHateosPutHandler<JsonNode>, SpreadsheetRowReference, JsonNode>,
        ToStringTesting<SpreadsheetEngineInsertRowsHateosPutHandler<JsonNode>> {

    @Test
    public void testInsertMissingCountParametersFails() {
        this.putFails(this.id(),
                this.resource(),
                HateosHandler.NO_PARAMETERS,
                this.createContext(),
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertInvalidCountParametersFails() {
        this.putFails(this.id(),
                this.resource(),
                this.parameters("1", "2"),
                this.createContext(),
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertOneRow() {
        final Latch inserted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final Optional<JsonNode> resource = this.resource();

        this.putAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public void insertRows(final SpreadsheetRowReference r,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(1, count, "count");
                        inserted.set("inserted");
                    }
                }),
                row,
                resource,
                parameters("1"),
                this.createContext(),
                Optional.empty()
        );

        assertTrue(inserted.value());
    }

    @Test
    public void testInsertSeveralRows() {
        final Latch inserted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final Optional<JsonNode> resource = this.resource();

        this.putAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public void insertRows(final SpreadsheetRowReference r,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(3, count, "count");
                        inserted.set("Deleted");
                    }
                }),
                this.id(),
                resource,
                parameters("3"),
                this.createContext(),
                Optional.empty());
        assertTrue(inserted.value());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertRows");
    }

    @Override
    public Class<SpreadsheetEngineInsertRowsHateosPutHandler<JsonNode>> type() {
        return Cast.to(SpreadsheetEngineInsertRowsHateosPutHandler.class);
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
    }

    @Override
    public Optional<JsonNode> resource() {
        return Optional.empty();
    }

    private SpreadsheetEngineInsertRowsHateosPutHandler<JsonNode> createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.contentType(),
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final String...count) {
        return Maps.one(UrlParameterName.with("count"), Lists.of(count));
    }

    @Override
    SpreadsheetEngineInsertRowsHateosPutHandler<JsonNode> createHandler(final SpreadsheetEngine engine,
                                                                        final HateosContentType<JsonNode, HasJsonNode> contentType,
                                                                        final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertRowsHateosPutHandler.with(engine, contentType, context);
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine();
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }
}
