package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SpreadsheetEngineDeleteRowsHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineDeleteRowsHateosHandler, SpreadsheetRowReference, SpreadsheetRow> {

    @Test
    public void testDeleteRow() {
        final Latch deleted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final Optional<SpreadsheetRow> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

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
                Optional.empty()
        );

        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteSeveralRows() {
        final Latch deleted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final List<SpreadsheetRow> resources = this.resourceCollection();

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

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
                resources,
                HateosHandler.NO_PARAMETERS,
                Lists.empty());
        assertTrue(deleted.value());
    }

    @Test
    public void testDeleteAllRowsFails() {
        this.handleCollectionFails2(Range.all());
    }

    @Test
    public void testDeleteOpenRangeBeginFails() {
        this.handleCollectionFails2(Range.lessThanEquals(SpreadsheetRowReference.parse("2")));
    }

    @Test
    public void testDeleteOpenRangeEndFails() {
        this.handleCollectionFails2(Range.greaterThanEquals(SpreadsheetRowReference.parse("2")));
    }

    private void handleCollectionFails2(final Range<SpreadsheetRowReference> rows) {
        assertEquals("Range of rows required=" + rows,
                this.handleCollectionFails(rows,
                        this.resourceCollection(),
                        HateosHandler.NO_PARAMETERS,
                        IllegalArgumentException.class).getMessage(),
                "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteRows");
    }

    @Override
    public Class<SpreadsheetEngineDeleteRowsHateosHandler> type() {
        return Cast.to(SpreadsheetEngineDeleteRowsHateosHandler.class);
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
    }

    @Override
    public Optional<SpreadsheetRow> resource() {
        return Optional.empty();
    }

    @Override
    public List<SpreadsheetRow> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Range<SpreadsheetRowReference> collection() {
        return Range.greaterThanEquals(SpreadsheetRowReference.parse("2"))
                .and(Range.lessThanEquals(SpreadsheetRowReference.parse("4"))); // 2, 3, 4
    }

    private SpreadsheetEngineDeleteRowsHateosHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    SpreadsheetEngineDeleteRowsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                           final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteRowsHateosHandler.with(engine, context);
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
