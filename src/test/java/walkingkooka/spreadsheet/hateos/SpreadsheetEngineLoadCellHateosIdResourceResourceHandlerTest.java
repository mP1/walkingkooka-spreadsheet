package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineLoading;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetEngineLoadCellHateosIdResourceResourceHandlerTest
        extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineLoadCellHateosIdResourceResourceHandler,
                        SpreadsheetCellReference,
                        SpreadsheetCell,
                        SpreadsheetCell>
implements HateosIdResourceResourceHandlerTesting<SpreadsheetEngineLoadCellHateosIdResourceResourceHandler,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCell> {

    private final static SpreadsheetEngineLoading LOADING = SpreadsheetEngineLoading.FORCE_RECOMPUTE;

    @Test
    public void testLoadCellInvalidLoadingParameterFails() {
        this.handleFails(this.id(),
                this.resource(),
                HateosHandler.NO_PARAMETERS,
                IllegalArgumentException.class);
    }

    @Test
    public void testLoadCellInvalidLoadingParameterFails2() {
        this.handleFails(this.id(),
                this.resource(),
                Maps.one(HttpRequestParameterName.with("loading"), Lists.of("a", "b")),
                IllegalArgumentException.class);
    }

    @Test
    public void testLoadCell() {
        this.handleAndCheck(this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(SpreadsheetCell.with(SpreadsheetCellReference.parse("A1"),
                        SpreadsheetFormula.with("1+2"),
                        SpreadsheetCellStyle.EMPTY)));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.loadCell");
    }

    @Override
    SpreadsheetEngineLoadCellHateosIdResourceResourceHandler createHandler(final SpreadsheetEngine engine,
                                                                           final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineLoadCellHateosIdResourceResourceHandler.with(engine, context);
    }

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Optional<SpreadsheetCell> resource() {
        return Optional.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return this.parameters(LOADING);
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final SpreadsheetEngineLoading loading) {
        return Maps.one(UrlParameterName.with("loading"), Lists.of(loading.toString()));
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {
            @Override
            public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference id,
                                                      final SpreadsheetEngineLoading loading,
                                                      final SpreadsheetEngineContext context) {
                Objects.requireNonNull(id, "id");
                Objects.requireNonNull(loading, "loading");
                Objects.requireNonNull(context, "context");


                assertEquals(SpreadsheetEngineLoadCellHateosIdResourceResourceHandlerTest.this.id(), id, "id");
                assertEquals(LOADING, loading, "loading");
                assertNotEquals(null, context, "context");

                return Optional.of(SpreadsheetEngineLoadCellHateosIdResourceResourceHandlerTest.this.cell());
            }
        };
    }

    private SpreadsheetCell cell() {
        return SpreadsheetCell.with(this.id(), SpreadsheetFormula.with("1+2"), SpreadsheetCellStyle.EMPTY);
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineLoadCellHateosIdResourceResourceHandler> type() {
        return Cast.to(SpreadsheetEngineLoadCellHateosIdResourceResourceHandler.class);
    }
}
