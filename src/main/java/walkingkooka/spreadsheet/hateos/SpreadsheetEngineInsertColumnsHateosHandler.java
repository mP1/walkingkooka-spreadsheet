package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineInsertColumnsHateosHandler extends SpreadsheetEngineColumnHateosHandler {

    static SpreadsheetEngineInsertColumnsHateosHandler with(final SpreadsheetEngine engine,
                                                            final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineInsertColumnsHateosHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineInsertColumnsHateosHandler(final SpreadsheetEngine engine,
                                                        final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }


    @Override
    public Optional<SpreadsheetCell> handle(final SpreadsheetColumnReference column,
                                            final Optional<SpreadsheetColumn> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(column, "column");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        this.engine.insertColumns(column,
                this.count(parameters),
                this.context.get());

        return Optional.empty();
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
    public List<SpreadsheetCell> handleCollection(final Range<SpreadsheetColumnReference> columns,
                                                  final List<SpreadsheetColumn> resources,
                                                  final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(columns, "columns");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "insertColumns";
    }
}
