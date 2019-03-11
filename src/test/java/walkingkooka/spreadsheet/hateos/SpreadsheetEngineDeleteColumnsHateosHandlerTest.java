package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SpreadsheetEngineDeleteColumnsHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineDeleteColumnsHateosHandler, SpreadsheetColumnReference, SpreadsheetColumn> {

    @Test
    public void testDeleteColumn() {
        final Latch deleted = Latch.create();

        final SpreadsheetColumnReference column = this.id();
        final Optional<SpreadsheetColumn> resource = this.resource();

        this.createHandler(new FakeSpreadsheetEngine() {

            @Override
            public void deleteColumns(final SpreadsheetColumnReference c,
                                      final int count,
                                      final SpreadsheetEngineContext context) {
                assertEquals(column, c, "column");
                assertEquals(1, count, "count");
                deleted.set("Deleted");
            }
        }).handle(column,
                resource,
                HateosHandler.NO_PARAMETERS);
        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteSeveralColumns() {
        final Latch deleted = Latch.create();

        final SpreadsheetColumnReference column = this.id();
        final List<SpreadsheetColumn> resources = this.resourceCollection();

        this.createHandler(new FakeSpreadsheetEngine() {

            @Override
            public void deleteColumns(final SpreadsheetColumnReference c,
                                      final int count,
                                      final SpreadsheetEngineContext context) {
                assertEquals(column, c, "column");
                assertEquals(3, count, "count");
                deleted.set("Deleted");
            }
        }).handleCollection(
                Range.greaterThanEquals(column).and(Range.lessThanEquals(SpreadsheetColumnReference.parse("D"))), // 3 inclusive
                resources,
                HateosHandler.NO_PARAMETERS);
        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteAllColumnsFails() {
        this.handleCollectionFails2(Range.all());
    }

    @Test
    public void testDeleteOpenRangeBeginFails() {
        this.handleCollectionFails2(Range.lessThanEquals(SpreadsheetColumnReference.parse("A")));
    }

    @Test
    public void testDeleteOpenRangeEndFails() {
        this.handleCollectionFails2(Range.greaterThanEquals(SpreadsheetColumnReference.parse("A")));
    }

    private void handleCollectionFails2(final Range<SpreadsheetColumnReference> columns) {
        assertEquals("Range of columns required=" + columns,
                this.handleCollectionFails(columns,
                        this.resourceCollection(),
                        HateosHandler.NO_PARAMETERS,
                        IllegalArgumentException.class).getMessage(),
                "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteColumns");
    }

    @Override
    public Class<SpreadsheetEngineDeleteColumnsHateosHandler> type() {
        return Cast.to(SpreadsheetEngineDeleteColumnsHateosHandler.class);
    }

    @Override
    public SpreadsheetColumnReference id() {
        return SpreadsheetColumnReference.parse("B");
    }

    @Override
    public Optional<SpreadsheetColumn> resource() {
        return Optional.empty();
    }

    @Override
    public List<SpreadsheetColumn> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Range<SpreadsheetColumnReference> collection() {
        return Range.greaterThanEquals(SpreadsheetColumnReference.parse("C"))
                .and(Range.lessThanEquals(SpreadsheetColumnReference.parse("E"))); // C, D, E
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private SpreadsheetEngineDeleteColumnsHateosHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    SpreadsheetEngineDeleteColumnsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteColumnsHateosHandler.with(engine, context);
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
