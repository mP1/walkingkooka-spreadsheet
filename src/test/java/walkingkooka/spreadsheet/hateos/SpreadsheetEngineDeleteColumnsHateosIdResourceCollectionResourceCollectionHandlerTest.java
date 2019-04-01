package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCell>
        implements HateosIdResourceCollectionResourceCollectionHandlerTesting<SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCell> {

    @Test
    public void testDeleteColumn() {
        final SpreadsheetColumnReference column = this.id();
        final List<SpreadsheetColumn> resources = this.resourceCollection();

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetEngine() {

                    @Override
                    public Set<SpreadsheetCell> deleteColumns(final SpreadsheetColumnReference c,
                                                              final int count,
                                                              final SpreadsheetEngineContext context) {
                        assertEquals(column, c, "column");
                        assertEquals(1, count, "count");
                        return Sets.of(cell());
                    }
                }),
                column,
                resources,
                HateosHandler.NO_PARAMETERS,
                Lists.of(this.cell()));
    }

    private SpreadsheetCell cell() {
        return this.cell("A99", "1+2");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.deleteColumns");
    }

    @Override
    public Class<SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler> type() {
        return SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler.class;
    }

    @Override
    public SpreadsheetColumnReference id() {
        return SpreadsheetColumnReference.parse("B");
    }

    @Override
    public List<SpreadsheetColumn> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine) {
        return this.createHandler(engine,
                this.engineContextSupplier());
    }

    @Override
    SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine,
                                                                                                    final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler.with(engine, context);
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
