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

public final class SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCell>
        implements HateosIdResourceCollectionResourceCollectionHandlerTesting<SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
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
    public void testInsertOneRow() {
        final SpreadsheetRowReference row = this.id();
        final List<SpreadsheetRow> resources = this.resourceCollection();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public Set<SpreadsheetCell> insertRows(final SpreadsheetRowReference r,
                                                           final int count,
                                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(1, count, "count");
                        return Sets.of(cell());
                    }
                }),
                row,
                resources,
                parameters("1"),
                Lists.of(cell())
        );
    }

    @Test
    public void testInsertSeveralRows() {
        final SpreadsheetRowReference row = this.id();
        final List<SpreadsheetRow> resources = this.resourceCollection();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public Set<SpreadsheetCell> insertRows(final SpreadsheetRowReference r,
                                                           final int count,
                                                           final SpreadsheetEngineContext context) {
                        assertEquals(row, r, "row");
                        assertEquals(3, count, "count");
                        return Sets.of(cell());
                    }
                }),
                this.id(),
                resources,
                parameters("3"),
                Lists.of(this.cell()));
    }

    private SpreadsheetCell cell() {
        return this.cell("A99", "1+2");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.insertRows");
    }

    @Override
    public Class<SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler> type() {
        return Cast.to(SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler.class);
    }

    @Override
    public SpreadsheetRowReference id() {
        return SpreadsheetRowReference.parse("2");
    }

    @Override
    public List<SpreadsheetRow> resourceCollection() {
        return Lists.empty();
    }

    private SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine) {
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
    SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine,
                                                                                                 final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler.with(engine, context);
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
