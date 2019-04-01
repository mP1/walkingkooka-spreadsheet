package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlParameterName;
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

public final class SpreadsheetEngineInsertRowsHateosIdResourceResourceHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineInsertRowsHateosIdResourceResourceHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetEngineInsertRowsHateosIdResourceResourceHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell> {

    @Test
    public void testInsertMissingCountParametersFails() {
        this.handleFails(this.id(),
                this.resource(),
                HateosHandler.NO_PARAMETERS,
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertInvalidCountParametersFails() {
        this.handleFails(this.id(),
                this.resource(),
                this.parameters("1", "2"),
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertOneRow() {
        final Latch inserted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final Optional<SpreadsheetRow> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

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
                Optional.empty()
        );

        assertTrue(inserted.value());
    }

    @Test
    public void testInsertSeveralRows() {
        final Latch inserted = Latch.create();

        final SpreadsheetRowReference row = this.id();
        final Optional<SpreadsheetRow> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

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
                Optional.empty());
        assertTrue(inserted.value());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertRows");
    }

    @Override
    public Class<SpreadsheetEngineInsertRowsHateosIdResourceResourceHandler> type() {
        return Cast.to(SpreadsheetEngineInsertRowsHateosIdResourceResourceHandler.class);
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
    }

    @Override
    public Optional<SpreadsheetRow> resource() {
        return Optional.empty();
    }

    private SpreadsheetEngineInsertRowsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final String... count) {
        return Maps.one(UrlParameterName.with("count"), Lists.of(count));
    }

    @Override
    SpreadsheetEngineInsertRowsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine,
                                                                             final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertRowsHateosIdResourceResourceHandler.with(engine, context);
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
