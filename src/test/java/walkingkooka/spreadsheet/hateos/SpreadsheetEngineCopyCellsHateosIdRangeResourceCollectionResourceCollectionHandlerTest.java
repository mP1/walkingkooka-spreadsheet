package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdRangeResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandlerTest extends
        SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler, SpreadsheetCellReference, SpreadsheetCell, SpreadsheetCell>
        implements HateosIdRangeResourceCollectionResourceCollectionHandlerTesting<SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCell> {

    @Test
    public void testCopy() {
        this.handleAndCheck(this.collection(),
                this.resourceCollection(),
                this.parameters(),
                Lists.of(this.cell()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), SpreadsheetEngine.class.getSimpleName() + ".copyCells");
    }

    @Override
    SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine,
                                                                                                     final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler.with(engine, context);
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
                assertEquals(SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandlerTest.this.resourceCollection(), from, "from");
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
        return Maps.of(SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler.TO, Lists.of(TO));
    }

    private final static String TO = "E1:F2";

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return Range.greaterThanEquals(SpreadsheetCellReference.parse("C1")).and(
                Range.lessThanEquals(SpreadsheetCellReference.parse("D2")));
    }

    @Override
    public List<SpreadsheetCell> resourceCollection() {
        return Lists.of(SpreadsheetCell.with(SpreadsheetCellReference.parse("A1"),
                SpreadsheetFormula.with("1+2"),
                SpreadsheetCellStyle.EMPTY));
    }

    @Override
    public Class<SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler> type() {
        return SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler.class;
    }
}
