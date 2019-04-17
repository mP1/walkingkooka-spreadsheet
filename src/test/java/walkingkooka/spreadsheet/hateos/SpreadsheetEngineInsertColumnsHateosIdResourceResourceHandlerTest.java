package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler,
        SpreadsheetColumnReference,
        SpreadsheetDelta,
        SpreadsheetDelta>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler,
                SpreadsheetColumnReference,
                SpreadsheetDelta,
                SpreadsheetDelta> {

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
    public void testInsertOneColumn() {
        final SpreadsheetCell cell = this.cell();

        final SpreadsheetColumnReference column = this.id();
        final Optional<SpreadsheetDelta> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetId id() {
                        return spreadsheetId();
                    }

                    @Override
                    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference r,
                                                          final int count,
                                                          final SpreadsheetEngineContext context) {
                        assertEquals(column, r, "column");
                        assertEquals(1, count, "count");
                        return delta();
                    }
                }),
                column,
                resource,
                parameters("1"),
                Optional.of(delta())
        );
    }

    @Test
    public void testInsertSeveralColumns() {
        final SpreadsheetColumnReference column = this.id();
        final Optional<SpreadsheetDelta> resource = this.resource();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public SpreadsheetId id() {
                        return spreadsheetId();
                    }

                    @Override
                    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference r,
                                                          final int count,
                                                          final SpreadsheetEngineContext context) {
                        assertEquals(column, r, "column");
                        assertEquals(3, count, "count");
                        return delta();
                    }
                }),
                this.id(),
                resource,
                parameters("3"),
                Optional.of(delta()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertColumns");
    }

    @Override
    public Class<SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler> type() {
        return Cast.to(SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler.class);
    }

    @Override
    public SpreadsheetColumnReference id() {
        return SpreadsheetColumnReference.parse("D");
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        return Optional.empty();
    }

    private SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final String... count) {
        return Maps.of(UrlParameterName.with("count"), Lists.of(count));
    }

    @Override
    SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine,
                                                                                final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler.with(engine, context);
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
