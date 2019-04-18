package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetEngineLoadCellHateosHandlerTest
        extends SpreadsheetEngineHateosHandlerTestCase2<SpreadsheetEngineLoadCellHateosHandler,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCell> {

    private final static SpreadsheetEngineEvaluation EVALUATION = SpreadsheetEngineEvaluation.FORCE_RECOMPUTE;

    @Test
    public void testLoadCellInvalidEvaluationParameterFails() {
        this.handleFails(this.id(),
                this.resource(),
                HateosHandler.NO_PARAMETERS,
                IllegalArgumentException.class);
    }

    @Test
    public void testLoadCellInvalidEvaluationParameterFails2() {
        this.handleFails(this.id(),
                this.resource(),
                Maps.of(HttpRequestParameterName.with("evaluation"), Lists.of("a", "b")),
                IllegalArgumentException.class);
    }

    @Test
    public void testLoadCell() {
        this.handleAndCheck(this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(this.cell()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler().toString(), "SpreadsheetEngine.loadCell");
    }

    @Override
    SpreadsheetEngineLoadCellHateosHandler createHandler(final SpreadsheetEngine engine,
                                                         final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineLoadCellHateosHandler.with(engine, context);
    }

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return Range.greaterThanEquals(SpreadsheetCellReference.parse("B2"))
                .and(Range.lessThanEquals(SpreadsheetCellReference.parse("D4")));
    }

    @Override
    public Optional<SpreadsheetCell> resource() {
        return Optional.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return this.parameters(EVALUATION);
    }

    private Map<HttpRequestAttribute<?>, Object> parameters(final SpreadsheetEngineEvaluation evaluation) {
        return Maps.of(UrlParameterName.with("evaluation"), Lists.of(evaluation.toString()));
    }

    @Override
    SpreadsheetEngine engine() {
        return new FakeSpreadsheetEngine() {
            @Override
            public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference id,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final SpreadsheetEngineContext context) {
                Objects.requireNonNull(id, "id");
                Objects.requireNonNull(evaluation, "evaluation");
                Objects.requireNonNull(context, "context");


                assertEquals(SpreadsheetEngineLoadCellHateosHandlerTest.this.id(), id, "id");
                assertEquals(EVALUATION, evaluation, "evaluation");
                assertNotEquals(null, context, "context");

                return Optional.of(cell());
            }
        };
    }

    @Override
    SpreadsheetEngineContext engineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    public Class<SpreadsheetEngineLoadCellHateosHandler> type() {
        return Cast.to(SpreadsheetEngineLoadCellHateosHandler.class);
    }
}
