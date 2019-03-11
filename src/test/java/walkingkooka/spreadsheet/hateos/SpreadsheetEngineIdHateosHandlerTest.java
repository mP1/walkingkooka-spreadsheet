package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineLoading;
import walkingkooka.test.ToStringTesting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class SpreadsheetEngineIdHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineIdHateosHandler, Long, SpreadsheetId>
        implements HateosHandlerTesting<SpreadsheetEngineIdHateosHandler, Long, SpreadsheetId>,
        ToStringTesting<SpreadsheetEngineIdHateosHandler> {

    @Test
    public void testId() {
        this.handleFails(this.id(),
                this.resource(),
                this.parameters(),
                UnsupportedOperationException.class);
    }

    @Test
    public void testHandleCollectionUnsupportedOperationException() {
        this.handleCollectionAndCheck(Range.all(),
                this.resourceCollection(),
                this.parameters(),
                Lists.of(this.spreadsheetId()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), "SpreadsheetEngine.id");
    }

    @Override
    SpreadsheetEngineIdHateosHandler createHandler(final SpreadsheetEngine engine,
                                                   final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineIdHateosHandler.with(engine, context);
    }

    @Override
    public Long id() {
        return 123L;
    }

    private SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(this.id());
    }

    @Override
    public Range<Long> collection() {
        return Range.all();
    }

    @Override
    public Optional<SpreadsheetId> resource() {
        return Optional.empty();
    }

    @Override
    public List<SpreadsheetId> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final SpreadsheetEngineLoading loading) {
        return Maps.one(UrlParameterName.with("loading"), Lists.of(loading.toString()));
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {
            @Override
            public SpreadsheetId id() {
                return SpreadsheetEngineIdHateosHandlerTest.this.spreadsheetId();
            }
        };
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineIdHateosHandler> type() {
        return Cast.to(SpreadsheetEngineIdHateosHandler.class);
    }
}
