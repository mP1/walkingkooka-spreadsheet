package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetEngineCopyCellsHateosHandlerTest extends SpreadsheetEngineCellHateosHandlerTestCase<SpreadsheetEngineCopyCellsHateosHandler> {

    @Test
    public void testCopy() {
        this.handleCollectionAndCheck(this.collection(),
                this.resourceCollection(),
                this.parameters(),
                Lists.empty());
        assertEquals(true, this.copied, "BasicEngine.copy not invoked");
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
            public void copy(final Collection<SpreadsheetCell> from,
                             final SpreadsheetRange to,
                             final SpreadsheetEngineContext context) {
                assertEquals(SpreadsheetEngineCopyCellsHateosHandlerTest.this.resourceCollection(), from, "from");
                assertEquals(SpreadsheetRange.parse(TO), to, "to");

                SpreadsheetEngineCopyCellsHateosHandlerTest.this.copied = true;
            }
        };
    }

    private boolean copied = false;

    @Override
    SpreadsheetEngineContext engineContext() {
        return null;
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.one(SpreadsheetEngineCopyCellsHateosHandler.TO, Lists.of(TO));
    }

    private final static String TO = "E1:F2";

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Optional<SpreadsheetCell> resource() {
        return Optional.empty();
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
    public Class<SpreadsheetEngineCopyCellsHateosHandler> type() {
        return SpreadsheetEngineCopyCellsHateosHandler.class;
    }
}
