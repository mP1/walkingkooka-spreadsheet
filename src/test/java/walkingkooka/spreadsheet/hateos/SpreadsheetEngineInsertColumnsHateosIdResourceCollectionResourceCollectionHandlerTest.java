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
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCell>
        implements HateosIdResourceCollectionResourceCollectionHandlerTesting<SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCell> {

    @Test
    public void testInsertMissingCountParametersFails() {
        this.handleFails(this.id(),
                this.resourceCollection(),
                HateosHandler.NO_PARAMETERS,
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertInvalidCountParametersFails() {
        this.handleFails(this.id(),
                this.resourceCollection(),
                this.parameters("1", "2"),
                IllegalArgumentException.class);
    }

    @Test
    public void testInsertOneColumn() {
        final SpreadsheetCell cell = this.cell();

        final SpreadsheetColumnReference column = this.id();
        final List<SpreadsheetColumn> resources = this.resourceCollection();

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
                        return SpreadsheetDelta.with(id(), Sets.of(cell));
                    }
                }),
                column,
                resources,
                parameters("1"),
                Lists.of(cell)
        );
    }

    @Test
    public void testInsertSeveralColumns() {
        final SpreadsheetColumnReference column = this.id();
        final List<SpreadsheetColumn> resources = this.resourceCollection();

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
                        return SpreadsheetDelta.with(id(), Sets.of(cell()));
                    }
                }),
                this.id(),
                resources,
                parameters("3"),
                Lists.of(cell()));
    }

    private SpreadsheetCell cell() {
        return this.cell("A99", "1+2");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertColumns");
    }

    @Override
    public Class<SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler> type() {
        return Cast.to(SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler.class);
    }

    @Override
    public SpreadsheetColumnReference id() {
        return SpreadsheetColumnReference.parse("D");
    }

    @Override
    public List<SpreadsheetColumn> resourceCollection() {
        return Lists.empty();
    }

    private SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine) {
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
    SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine,
                                                                                                    final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler.with(engine, context);
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
