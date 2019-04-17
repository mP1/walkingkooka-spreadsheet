package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdRangeResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler,
        Long,
        SpreadsheetId,
        SpreadsheetId>
        implements HateosIdRangeResourceCollectionResourceCollectionHandlerTesting<SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler, Long, SpreadsheetId, SpreadsheetId> {

    @Test
    public void testHandleCollectionUnsupportedOperationException() {
        this.handleAndCheck(Range.all(),
                this.resourceCollection(),
                this.parameters(),
                Lists.of(this.spreadsheetId()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), "SpreadsheetEngine.id");
    }

    @Override
    SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetEngine engine,
                                                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler.with(engine, context);
    }

    @Override
    public Long id() {
        return 123L;
    }

    @Override
    public Range<Long> collection() {
        return Range.all();
    }

    @Override
    public List<SpreadsheetId> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final SpreadsheetEngineEvaluation evaluation) {
        return Maps.of(UrlParameterName.with("evaluation"), Lists.of(evaluation.toString()));
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {
            @Override
            public SpreadsheetId id() {
                return SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandlerTest.this.spreadsheetId();
            }
        };
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler> type() {
        return Cast.to(SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler.class);
    }
}
