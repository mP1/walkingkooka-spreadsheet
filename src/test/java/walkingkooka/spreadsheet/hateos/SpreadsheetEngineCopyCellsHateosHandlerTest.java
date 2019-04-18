package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineCopyCellsHateosHandlerTest extends
        SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineCopyCellsHateosHandler, SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta>
        implements HateosHandlerTesting<SpreadsheetEngineCopyCellsHateosHandler,
        SpreadsheetCellReference,
        SpreadsheetDelta,
        SpreadsheetDelta> {

    @Test
    public void testHandleUnsupported() {
        this.handleUnsupported(this.createHandler());
    }

    @Test
    public void testCopy() {
        this.handleCollectionAndCheck(this.collection(),
                this.resource(),
                this.parameters(),
                Optional.of(this.delta()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), SpreadsheetEngine.class.getSimpleName() + ".copyCells");
    }

    @Override
    SpreadsheetEngineCopyCellsHateosHandler createHandler(final SpreadsheetEngine engine,
                                                          final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineCopyCellsHateosHandler.with(engine, context);
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {

            @Override
            public SpreadsheetId id() {
                return spreadsheetId();
            }

            @Override
            public SpreadsheetDelta copyCells(final Collection<SpreadsheetCell> from,
                                              final SpreadsheetRange to,
                                              final SpreadsheetEngineContext context) {
                assertEquals(resource().get().cells(), from, "from");
                assertEquals(SpreadsheetRange.parse(TO), to, "to");
                return delta();
            }
        };
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return null;
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.of(SpreadsheetEngineCopyCellsHateosHandler.TO, Lists.of(TO));
    }

    private final static String TO = "E1:F2";

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return SpreadsheetCellReference.parseRange("C1:D2");
    }

    @Override
    public Optional<SpreadsheetDelta> resource() {
        return Optional.of(this.delta());
    }

    @Override
    public Class<SpreadsheetEngineCopyCellsHateosHandler> type() {
        return SpreadsheetEngineCopyCellsHateosHandler.class;
    }
}
