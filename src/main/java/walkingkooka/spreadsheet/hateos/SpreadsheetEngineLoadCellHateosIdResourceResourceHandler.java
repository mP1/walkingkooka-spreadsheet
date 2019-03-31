package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineLoading;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosIdResourceResourceHandler} that calls {@link SpreadsheetEngine#loadCell(SpreadsheetCellReference, SpreadsheetEngineLoading, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineLoadCellHateosIdResourceResourceHandler extends SpreadsheetEngineHateosHandler
        implements HateosIdResourceResourceHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetCell> {

    static SpreadsheetEngineLoadCellHateosIdResourceResourceHandler with(final SpreadsheetEngine engine,
                                                                         final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineLoadCellHateosIdResourceResourceHandler(engine, context);
    }

    private SpreadsheetEngineLoadCellHateosIdResourceResourceHandler(final SpreadsheetEngine engine,
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
                this.parameterValueOrFail(parameters, LOADING, SpreadsheetEngineLoading::valueOf),
                this.context.get());
    }

    private final static UrlParameterName LOADING = UrlParameterName.with("loading");

    @Override
    String operation() {
        return "loadCell";
    }
}
