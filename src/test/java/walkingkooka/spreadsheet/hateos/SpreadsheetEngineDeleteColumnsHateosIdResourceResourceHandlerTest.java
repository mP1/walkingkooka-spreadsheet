package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.test.Latch;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCell>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCell> {

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
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteColumns");
    }

    @Override
    public Class<SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler> type() {
        return SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler.class;
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
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine,
                                                                                final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler.with(engine, context);
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
