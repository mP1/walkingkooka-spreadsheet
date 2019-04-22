package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineEvaluation;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#loadCell(SpreadsheetCellReference, SpreadsheetEngineEvaluation, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineLoadCellHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetCell> {

    static SpreadsheetEngineLoadCellHateosHandler with(final SpreadsheetEngine engine,
                                                       final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineLoadCellHateosHandler(engine, context);
    }

    private SpreadsheetEngineLoadCellHateosHandler(final SpreadsheetEngine engine,
                                                   final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetCell> handle(final SpreadsheetCellReference cellReference,
                                            final Optional<SpreadsheetCell> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(cellReference, "cellReference");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return this.engine.loadCell(cellReference,
                this.parameterValueOrFail(parameters, EVALUATION, SpreadsheetEngineEvaluation::valueOf),
                this.context.get());
    }

    @Override
    public Optional<SpreadsheetCell> handleCollection(final Range<SpreadsheetCellReference> ids,
                                                      final Optional<SpreadsheetCell> resource,
                                                      final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        checkResource(resource);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    private final static UrlParameterName EVALUATION = UrlParameterName.with("evaluation");

    @Override
    String operation() {
        return "loadCell";
    }
}
