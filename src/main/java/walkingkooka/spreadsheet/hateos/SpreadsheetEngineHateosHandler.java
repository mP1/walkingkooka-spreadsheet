package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * An abstract hateos handler that includes uses a {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext} to do things.
 */
abstract class SpreadsheetEngineHateosHandler<I extends Comparable<I>, R extends HateosResource<I>>
        extends SpreadsheetHateosHandler<I, R> {

    /**
     * Checks required factory method parameters are not null.
     */
    static void check(final SpreadsheetEngine engine,
                      final Supplier<SpreadsheetEngineContext> context) {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(context, "context");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetEngineHateosHandler(final SpreadsheetEngine engine,
                                   final Supplier<SpreadsheetEngineContext> context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    /**
     * Checks that the range bounds are not null and both are inclusive.
     */
    final void checkInclusiveRange(final Range<I> range, final String label) {
        Objects.requireNonNull(range, label);

        if (!range.lowerBound().isInclusive() || !range.upperBound().isInclusive()) {
            throw new IllegalArgumentException("Range of " + label + " required=" + range);
        }
    }

    /**
     * Complains if the resource is null.
     */
    final void checkResource(final R resource) {
        Objects.requireNonNull(resource, "resource");
    }

    /**
     * Complains if the resource is null.
     */
    final void checkResource(final Optional<R> resource) {
        Objects.requireNonNull(resource, "resource");
    }

    /**
     * Complains if the resource is null or present.
     */
    final void checkResourceEmpty(final Optional<R> resource) {
        Objects.requireNonNull(resource, "resource");
        if (resource.isPresent()) {
            throw new IllegalArgumentException("Resource not allowed=" + resource);
        }
    }

    /**
     * Complains if the resource is null.
     */
    final void checkResources(final List<R> resources) {
        Objects.requireNonNull(resources, "resources");
    }

    /**
     * Complains if the resource is null or present.
     */
    final void checkResourcesEmpty(final List<R> resources) {
        Objects.requireNonNull(resources, "resources");
        if (resources.size() > 0) {
            throw new IllegalArgumentException("Resources not allowed=" + resources);
        }
    }

    final void checkParameters(final Map<HttpRequestAttribute<?>, Object> parameters) {
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
