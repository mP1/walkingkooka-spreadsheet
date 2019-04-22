package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base class that decorates requests performing parameter checks and a few other extras.
 */
abstract class SpreadsheetEngineHateosHandler2<I extends Comparable<I>> extends SpreadsheetEngineHateosHandler implements HateosHandler<I,
        SpreadsheetDelta,
        SpreadsheetDelta> {

    SpreadsheetEngineHateosHandler2(final SpreadsheetEngine engine,
                                    final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public final Optional<SpreadsheetDelta> handle(final I id,
                                                   final Optional<SpreadsheetDelta> delta,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, this.id());
        checkSpreadsheetDelta(delta);
        checkParameters(parameters);

        return Optional.of(this.handle0(id, delta, parameters));
    }

    abstract String id();

    abstract SpreadsheetDelta handle0(final I id,
                                      final Optional<SpreadsheetDelta> delta,
                                      final Map<HttpRequestAttribute<?>, Object> parameters);

    @Override
    public final Optional<SpreadsheetDelta> handleCollection(final Range<I> range,
                                                             final Optional<SpreadsheetDelta> delta,
                                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkRange(range);
        checkSpreadsheetDelta(delta);
        checkParameters(parameters);

        return Optional.of(this.handleCollection0(range, delta, parameters));
    }

    abstract void checkRange(final Range<I> range);

    abstract SpreadsheetDelta handleCollection0(final Range<I> range,
                                                final Optional<SpreadsheetDelta> delta,
                                                final Map<HttpRequestAttribute<?>, Object> parameters);

    abstract void checkSpreadsheetDelta(final Optional<SpreadsheetDelta> delta);
}
