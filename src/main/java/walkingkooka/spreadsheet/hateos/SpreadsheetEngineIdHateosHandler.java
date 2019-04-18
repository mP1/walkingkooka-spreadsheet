package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#id()}.
 */
final class SpreadsheetEngineIdHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetId, SpreadsheetDelta, SpreadsheetDelta> {

    static SpreadsheetEngineIdHateosHandler with(final SpreadsheetEngine engine,
                                                 final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineIdHateosHandler(engine, context);
    }

    private SpreadsheetEngineIdHateosHandler(final SpreadsheetEngine engine,
                                             final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta> handle(final SpreadsheetId id,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, "id");
        this.checkResourceEmpty(resource);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetId> ids,
                                                       final Optional<SpreadsheetDelta> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIds(ids, "ids");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return Optional.of(SpreadsheetDelta.with(this.engine.id(), SpreadsheetDelta.NO_CELLS));
    }

    @Override
    String operation() {
        return "id";
    }

}
