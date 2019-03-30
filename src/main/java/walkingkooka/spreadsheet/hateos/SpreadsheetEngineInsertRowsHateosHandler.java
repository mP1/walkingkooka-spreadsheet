package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that handles deleting a single or range of rows.
 */
final class SpreadsheetEngineInsertRowsHateosHandler extends SpreadsheetEngineRowHateosHandler {

    static SpreadsheetEngineInsertRowsHateosHandler with(final SpreadsheetEngine engine,
                                                         final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineInsertRowsHateosHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineInsertRowsHateosHandler(final SpreadsheetEngine engine,
                                                     final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }


    @Override
    public Optional<SpreadsheetCell> handle(final SpreadsheetRowReference row,
                                            final Optional<SpreadsheetRow> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(row, "row");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        this.engine.insertRows(row,
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
    public List<SpreadsheetCell> handleCollection(final Range<SpreadsheetRowReference> rows,
                                                  final List<SpreadsheetRow> resources,
                                                  final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(rows, "rows");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "insertRows";
    }
}
