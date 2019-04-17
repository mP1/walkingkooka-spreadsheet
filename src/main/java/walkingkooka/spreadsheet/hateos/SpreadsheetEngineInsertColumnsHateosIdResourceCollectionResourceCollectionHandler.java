package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandler;
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
 * A {@link HateosIdResourceCollectionResourceCollectionHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler extends SpreadsheetEngineHateosHandler
        implements HateosIdResourceCollectionResourceCollectionHandler<SpreadsheetColumnReference, SpreadsheetColumn, SpreadsheetCell> {

    static SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                                  final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineInsertColumnsHateosIdResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                              final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public List<SpreadsheetCell> handle(final SpreadsheetColumnReference column,
                                        final List<SpreadsheetColumn> resource,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(column, "column");
        checkResourcesEmpty(resource);
        checkParameters(parameters);

        final List<SpreadsheetCell> cells = Lists.array();
        cells.addAll(this.engine.insertColumns(column,
                this.count(parameters),
                this.context.get()));
        return Lists.readOnly(cells);
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
