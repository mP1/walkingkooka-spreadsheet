package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosHandler} that calls {@link SpreadsheetEngine#id()}.
 */
final class SpreadsheetEngineIdHateosHandler extends SpreadsheetEngineHateos
        implements HateosHandler<Long, SpreadsheetId, SpreadsheetId> {

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
    public Optional<SpreadsheetId> handle(final Long id,
                                          final Optional<SpreadsheetId> resource,
                                          final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, "id");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public List<SpreadsheetId> handleCollection(final Range<Long> ids,
                                                final List<SpreadsheetId> resources,
                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        return Lists.of(this.engine.id());
    }

    @Override
    String operation() {
        return "id";
    }
}
