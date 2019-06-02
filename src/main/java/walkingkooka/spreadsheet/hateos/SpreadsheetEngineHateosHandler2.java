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
        final SpreadsheetDelta delta2 = checkSpreadsheetDelta(delta);
        checkParameters(parameters);

        return Optional.of(this.handle0(id, delta2, parameters)
                .setWindow(delta2.window()));
    }

    abstract String id();

    abstract SpreadsheetDelta handle0(final I id,
                                      final SpreadsheetDelta delta,
                                      final Map<HttpRequestAttribute<?>, Object> parameters);

    @Override
    public final Optional<SpreadsheetDelta> handleCollection(final Range<I> range,
                                                             final Optional<SpreadsheetDelta> delta,
                                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkRange(range);
        final SpreadsheetDelta delta2 = checkSpreadsheetDelta(delta);
        checkParameters(parameters);

        return Optional.of(this.handleCollection0(range, delta2, parameters)
                .setWindow(delta2.window()));
    }

    abstract void checkRange(final Range<I> range);

    abstract SpreadsheetDelta handleCollection0(final Range<I> range,
                                                final SpreadsheetDelta delta,
                                                final Map<HttpRequestAttribute<?>, Object> parameters);

    final SpreadsheetDelta checkSpreadsheetDelta(final Optional<SpreadsheetDelta> delta) {
        Objects.requireNonNull(delta, "delta");
        return delta.orElseThrow(() -> new IllegalArgumentException("Required " + SpreadsheetDelta.class.getSimpleName() + " missing."));
    }
}
