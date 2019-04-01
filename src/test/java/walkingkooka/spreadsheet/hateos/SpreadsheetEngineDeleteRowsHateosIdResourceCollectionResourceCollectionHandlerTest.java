package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell>
        implements HateosIdResourceCollectionResourceCollectionHandlerTesting<SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell> {

    @Test
    public void testDeleteRow() {
        final Latch deleted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final List<SpreadsheetRow> resources = this.resourceCollection();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public Set<SpreadsheetCell> deleteRows(final SpreadsheetRowReference r,
                                                           final int count,
                                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(1, count, "count");

                        return Sets.of(cell());
                    }
                }),
                row,
                resources,
                HateosHandler.NO_PARAMETERS,
                Lists.of(this.cell())
        );
    }

    private SpreadsheetCell cell() {
        return this.cell("A99", "1+2");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteRows");
    }

    @Override
    public Class<SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler> type() {
        return SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler.class;
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
    }

    @Override
    public List<SpreadsheetRow> resourceCollection() {
        return Lists.empty();
    }

    private SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine,
                                                                                                 final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler.with(engine, context);
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
