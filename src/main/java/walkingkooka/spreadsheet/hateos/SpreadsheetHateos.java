package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for all handlers, holding numerous utility methods and the like.
 */
abstract class SpreadsheetHateos {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetHateos() {
        super();
    }

    /**
     * Complains if the id is null.
     */
    final void checkId(final Comparable<?> id) {
        Objects.requireNonNull(id, "id");
    }

    /**
     * Complains if the ids is null.
     */
    final void checkIds(final Range<?> ids) {
        checkIds(ids, "ids");
    }

    /**
     * Complains if the ids is null.
     */
    final void checkIds(final Range<?> ids, final String label) {
        Objects.requireNonNull(ids, label);
    }

    /**
     * Checks that the range bounds are not null and both are inclusive.
     */
    final void checkIdsInclusive(final Range<?> range, final String label) {
        checkIds(range, label);

        if (!range.lowerBound().isInclusive() || !range.upperBound().isInclusive()) {
            throw new IllegalArgumentException("Range of " + label + " required=" + range);
        }
    }

    /**
     * Complains if the resource is null.
     */
    final void checkResource(final Optional<? extends HateosResource<?>> resource) {
        Objects.requireNonNull(resource, "resource");
    }

    /**
     * Complains if the resource is null or present.
     */
    final void checkResourceEmpty(final Optional<? extends HateosResource<?>> resource) {
        checkResource(resource);
        if (resource.isPresent()) {
            throw new IllegalArgumentException("Resource not allowed=" + resource);
        }
    }

    /**
     * Complains if the resource is null.
     */
    final void checkResources(final List<? extends HateosResource<?>> resources) {
        Objects.requireNonNull(resources, "resources");
    }

    /**
     * Complains if the resource is null or present.
     */
    final void checkResourcesEmpty(final List<? extends HateosResource<?>> resources) {
        Objects.requireNonNull(resources, "resources");
        if (resources.size() > 0) {
            throw new IllegalArgumentException("Resources not allowed=" + resources);
        }
    }

    /**
     * Complains if the resource is NOT present.
     */
    final void checkResourcePresent(final Optional<? extends HateosResource<?>> resource) {
        this.checkResource(resource);
        if (!resource.isPresent()) {
            throw new IllegalArgumentException("Required resource not present=" + resource);
        }
    }

    /**
     * Checks parameters are present.
     */
    final void checkParameters(final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(parameters, "parameters");
    }

    /**
     * Fetches a required single parameter value and converts or fails.
     */
    final <T> T parameterValueOrFail(final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final UrlParameterName parameter,
                                     final Function<String, T> converter) {
        final Optional<List<String>> maybeValues = parameter.parameterValue(parameters);
        if (!maybeValues.isPresent()) {
            throw new IllegalArgumentException("Required parameter " + parameter + " missing");
        }
        final List<String> values = maybeValues.get();
        if (values.size() != 1) {
            throw new IllegalArgumentException("Required parameter " + parameter + " incorrect=" + values);
        }
        final String value = values.get(0);
        try {
            return converter.apply(value);
        } catch (final NullPointerException | IllegalArgumentException cause) {
            throw cause;
        } catch (final Exception cause) {
            throw new IllegalArgumentException("Invalid parameter " + parameter + " value " + CharSequences.quoteIfChars(value));
        }
    }
}
