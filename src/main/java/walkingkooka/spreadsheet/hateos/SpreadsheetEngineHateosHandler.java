package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.tree.Node;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * An abstract hateos handler that includes uses a {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext} to do things.
 */
abstract class SpreadsheetEngineHateosHandler<K extends Comparable<K>, V, N extends Node<N, ?, ?, ?>> extends SpreadsheetHateosHandler<K, V, N> {

    /**
     * Checks required factory method parameters are not null.
     */
    static <K extends Comparable<K>, V, N extends Node<N, ?, ?, ?>> void check(final SpreadsheetEngine engine,
                                                                               final HateosContentType<N, V> contentType,
                                                                               final Supplier<SpreadsheetEngineContext> context) {
        Objects.requireNonNull(engine, "engine");
        check(contentType);
        Objects.requireNonNull(context, "context");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetEngineHateosHandler(final SpreadsheetEngine engine,
                                   final HateosContentType<N, V> contentType,
                                   final Supplier<SpreadsheetEngineContext> context) {
        super(contentType);
        this.engine = engine;
        this.context = context;
    }

    /**
     * Checks that the range bounds are not null and both are inclusive.
     */
    final void checkInclusiveRange(final Range<K> range, final String label) {
        Objects.requireNonNull(range, label);

        if (!range.lowerBound().isInclusive() || !range.upperBound().isInclusive()) {
            throw new IllegalArgumentException("Range of " + label + " required=" + range);
        }
    }

    /**
     * Complains if the resource is null or present.
     */
    final void checkResourceEmpty(final Optional<N> resource) {
        Objects.requireNonNull(resource, "resource");
        if (resource.isPresent()) {
            throw new IllegalArgumentException("Resource not allowed=" + resource);
        }
    }

    final void checkParameters(Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(parameters, "parameters");
    }

    final SpreadsheetEngine engine;
    final Supplier<SpreadsheetEngineContext> context;

    @Override
    public final String toString() {
        return SpreadsheetEngine.class.getSimpleName() + "." + this.operation();
    }

    abstract String operation();
}
