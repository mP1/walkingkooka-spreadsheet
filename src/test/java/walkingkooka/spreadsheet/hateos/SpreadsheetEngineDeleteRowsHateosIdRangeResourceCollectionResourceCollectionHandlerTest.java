package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdRangeResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell>
        implements HateosIdRangeResourceCollectionResourceCollectionHandlerTesting<SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell> {

    @Test
    public void testDeleteSeveralRows() {
        final SpreadsheetRowReference row = this.id();
        final List<SpreadsheetRow> resources = this.resourceCollection();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public Set<SpreadsheetCell> deleteRows(final SpreadsheetRowReference r,
                                                           final int count,
                                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(3, count, "count");
                        return Sets.of(cell());
                    }
                }),
                Range.greaterThanEquals(row).and(Range.lessThanEquals(SpreadsheetRowReference.parse("4"))), // 3 rows inclusive
                resources,
                HateosHandler.NO_PARAMETERS,
                Lists.of(this.cell()));
    }

    private SpreadsheetCell cell() {
        return this.cell("A99", "1+2");
    }

    @Test
    public void testDeleteAllRowsFails() {
        this.handleFails2(Range.all());
    }

    @Test
    public void testDeleteOpenRangeBeginFails() {
        this.handleFails2(Range.lessThanEquals(SpreadsheetRowReference.parse("2")));
    }

    @Test
    public void testDeleteOpenRangeEndFails() {
        this.handleFails2(Range.greaterThanEquals(SpreadsheetRowReference.parse("2")));
    }

    private void handleFails2(final Range<SpreadsheetRowReference> rows) {
        assertEquals("Range of rows required=" + rows,
                this.handleFails(rows,
                        this.resourceCollection(),
                        HateosHandler.NO_PARAMETERS,
                        IllegalArgumentException.class).getMessage(),
                "message");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteRows");
    }

    private SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine, this.engineContextSupplier());
    }

    @Override
    public Class<SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler> type() {
        return SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler.class;
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
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

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine,
                                                                                                      final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler.with(engine, context);
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
