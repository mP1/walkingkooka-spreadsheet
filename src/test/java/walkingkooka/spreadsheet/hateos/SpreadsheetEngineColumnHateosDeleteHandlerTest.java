package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosDeleteHandlerTesting;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SpreadsheetEngineColumnHateosDeleteHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineColumnHateosDeleteHandler<JsonNode>, SpreadsheetColumnReference>
        implements HateosDeleteHandlerTesting<SpreadsheetEngineColumnHateosDeleteHandler<JsonNode>, SpreadsheetColumnReference, JsonNode>,
        ToStringTesting<SpreadsheetEngineColumnHateosDeleteHandler<JsonNode>> {

    @Test
    public void testDeleteColumn() {
        final Latch deleted = Latch.create();

        final SpreadsheetColumnReference column = this.id();
        final Optional<JsonNode> resource = this.resource();

        this.createHandler(new FakeSpreadsheetEngine() {

            @Override
            public void deleteColumns(final SpreadsheetColumnReference c,
                                      final int count,
                                      final SpreadsheetEngineContext context) {
                assertEquals(column, c, "column");
                assertEquals(1, count, "count");
                deleted.set("Deleted");
            }
        }).delete(
                column,
                resource,
                this.createContext());
        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteSeveralColumns() {
        final Latch deleted = Latch.create();

        final SpreadsheetColumnReference column = this.id();
        final Optional<JsonNode> resource = this.resource();

        this.createHandler(new FakeSpreadsheetEngine() {

            @Override
            public void deleteColumns(final SpreadsheetColumnReference c,
                                      final int count,
                                      final SpreadsheetEngineContext context) {
                assertEquals(column, c, "column");
                assertEquals(3, count, "count");
                deleted.set("Deleted");
            }
        }).deleteCollection(
                Range.greaterThanEquals(column).and(Range.lessThanEquals(SpreadsheetColumnReference.parse("D"))), // 3 inclusive
                resource,
                this.createContext());
        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteAllColumnsFails() {
        this.deleteCollectionFails2(Range.all());
    }

    @Test
    public void testDeleteOpenRangeBeginFails() {
        this.deleteCollectionFails2(Range.lessThanEquals(SpreadsheetColumnReference.parse("A")));
    }

    @Test
    public void testDeleteOpenRangeEndFails() {
        this.deleteCollectionFails2(Range.greaterThanEquals(SpreadsheetColumnReference.parse("A")));
    }

    private void deleteCollectionFails2(final Range<SpreadsheetColumnReference> columns) {
        assertEquals("Range of columns required=" + columns,
                this.deleteCollectionFails(columns,
                        this.resource(),
                        this.createContext(),
                        IllegalArgumentException.class).getMessage(),
                "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteColumns");
    }

    @Override
    public Class<SpreadsheetEngineColumnHateosDeleteHandler<JsonNode>> type() {
        return Cast.to(SpreadsheetEngineColumnHateosDeleteHandler.class);
    }

    @Override
    public SpreadsheetColumnReference id() {
        return SpreadsheetColumnReference.parse("B");
    }

    @Override
    public Optional<JsonNode> resource() {
        return Optional.empty();
    }

    @Override
    public Range<SpreadsheetColumnReference> collection() {
        return Range.greaterThanEquals(SpreadsheetColumnReference.parse("C"))
                .and(Range.lessThanEquals(SpreadsheetColumnReference.parse("E"))); // C, D, E
    }

    private SpreadsheetEngineColumnHateosDeleteHandler<JsonNode> createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.contentType(),
                this.engineContextSupplier());
    }

    @Override
    SpreadsheetEngineColumnHateosDeleteHandler<JsonNode> createHandler(final SpreadsheetEngine engine,
                                                                       final HateosContentType<JsonNode, HasJsonNode> contentType,
                                                                       final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineColumnHateosDeleteHandler.with(engine, contentType, context);
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
