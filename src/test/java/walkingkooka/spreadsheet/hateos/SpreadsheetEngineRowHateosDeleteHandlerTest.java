package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosDeleteHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosHandler;
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

public final class SpreadsheetEngineRowHateosDeleteHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineRowHateosDeleteHandler<JsonNode>, SpreadsheetRowReference>
        implements HateosDeleteHandlerTesting<SpreadsheetEngineRowHateosDeleteHandler<JsonNode>, SpreadsheetRowReference, JsonNode>,
        ToStringTesting<SpreadsheetEngineRowHateosDeleteHandler<JsonNode>> {

    @Test
    public void testDeleteRow() {
        final Latch deleted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final Optional<JsonNode> resource = this.resource();

        this.deleteAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public void deleteRows(final SpreadsheetRowReference r,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(1, count, "count");
                        deleted.set("Deleted");
                    }
                }),
                row,
                resource,
                HateosHandler.NO_PARAMETERS,
                this.createContext(),
                Optional.empty()
        );

        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteSeveralRows() {
        final Latch deleted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final Optional<JsonNode> resource = this.resource();

        this.deleteCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public void deleteRows(final SpreadsheetRowReference r,
                                           final int count,
                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(3, count, "count");
                        deleted.set("Deleted");
                    }
                }),
                Range.greaterThanEquals(row).and(Range.lessThanEquals(SpreadsheetRowReference.parse("4"))), // 3 rows inclusive
                resource,
                HateosHandler.NO_PARAMETERS,
                this.createContext(),
                Optional.empty());
        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteAllRowsFails() {
        this.deleteCollectionFails2(Range.all());
    }

    @Test
    public void testDeleteOpenRangeBeginFails() {
        this.deleteCollectionFails2(Range.lessThanEquals(SpreadsheetRowReference.parse("2")));
    }

    @Test
    public void testDeleteOpenRangeEndFails() {
        this.deleteCollectionFails2(Range.greaterThanEquals(SpreadsheetRowReference.parse("2")));
    }

    private void deleteCollectionFails2(final Range<SpreadsheetRowReference> rows) {
        assertEquals("Range of rows required=" + rows,
                this.deleteCollectionFails(rows,
                        this.resource(),
                        HateosHandler.NO_PARAMETERS,
                        this.createContext(),
                        IllegalArgumentException.class).getMessage(),
                "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteRows");
    }

    @Override
    public Class<SpreadsheetEngineRowHateosDeleteHandler<JsonNode>> type() {
        return Cast.to(SpreadsheetEngineRowHateosDeleteHandler.class);
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
    }

    @Override
    public Optional<JsonNode> resource() {
        return Optional.empty();
    }

    @Override
    public Range<SpreadsheetRowReference> collection() {
        return Range.greaterThanEquals(SpreadsheetRowReference.parse("2"))
                .and(Range.lessThanEquals(SpreadsheetRowReference.parse("4"))); // 2, 3, 4
    }

    private SpreadsheetEngineRowHateosDeleteHandler<JsonNode> createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.contentType(),
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    SpreadsheetEngineRowHateosDeleteHandler<JsonNode> createHandler(final SpreadsheetEngine engine,
                                                                       final HateosContentType<JsonNode, HasJsonNode> contentType,
                                                                       final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineRowHateosDeleteHandler.with(engine, contentType, context);
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
