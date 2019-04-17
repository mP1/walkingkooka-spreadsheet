package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosIdResourceResourceHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler extends SpreadsheetEngineHateosHandler
        implements HateosIdResourceResourceHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> {

    static SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler with(final SpreadsheetEngine engine,
                                                                              final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineInsertColumnsHateosIdResourceResourceHandler(final SpreadsheetEngine engine,
                                                                          final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta> handle(final SpreadsheetColumnReference column,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(column, "column");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return Optional.of(this.engine.insertColumns(column,
                this.count(parameters),
                this.context.get()));
    }

    private int count(final Map<HttpRequestAttribute<?>, Object> parameters) {
        final Optional<List<String>> maybeValues = COUNT.parameterValue(parameters);
        if (!maybeValues.isPresent()) {
            throw new IllegalArgumentException("Required parameter " + COUNT + " missing");
        }
        final List<String> values = maybeValues.get();
        if (1 != values.size()) {
            throw new IllegalArgumentException("Required parameter " + COUNT + " has invalid values count=" + values);
        }

        return Integer.parseInt(values.get(0));
    }

    private final UrlParameterName COUNT = UrlParameterName.with("count");

    @Override
    String operation() {
        return "insertColumns";
    }
}
