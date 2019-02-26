package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for all handlers.
 */
abstract class SpreadsheetHateosHandler<I extends Comparable<I>, R extends HateosResource<I>> implements HateosHandler<I, R> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetHateosHandler() {
        super();
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
