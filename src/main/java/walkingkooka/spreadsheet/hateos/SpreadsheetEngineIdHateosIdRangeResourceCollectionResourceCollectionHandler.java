package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdRangeResourceCollectionResourceCollectionHandler;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A {@link HateosIdRangeResourceCollectionResourceCollectionHandler} that calls {@link SpreadsheetEngine#id()}.
 */
final class SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler extends SpreadsheetEngineHateosHandler
        implements HateosIdRangeResourceCollectionResourceCollectionHandler<Long, SpreadsheetId, SpreadsheetId> {

    static SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                            final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler(engine, context);
    }

    private SpreadsheetEngineIdHateosIdRangeResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public List<SpreadsheetId> handle(final Range<Long> ids,
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
