package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell> {

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
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteRows");
    }

    @Override
    public Class<SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler> type() {
        return SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler.class;
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
    }

    @Override
    public Optional<SpreadsheetRow> resource() {
        return Optional.empty();
    }

    private SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine,
                                                                             final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler.with(engine, context);
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
