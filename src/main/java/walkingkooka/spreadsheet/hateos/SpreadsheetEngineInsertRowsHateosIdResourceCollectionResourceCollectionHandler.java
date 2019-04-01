package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandler;
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
 * A {@link HateosIdResourceCollectionResourceCollectionHandler} that handles inserting rows.
 */
final class SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler extends SpreadsheetEngineHateosHandler
        implements HateosIdResourceCollectionResourceCollectionHandler<SpreadsheetRowReference, SpreadsheetRow, SpreadsheetCell> {

    static SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                               final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineInsertRowsHateosIdResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                           final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }


    @Override
    public List<SpreadsheetCell> handle(final SpreadsheetRowReference row,
                                        final List<SpreadsheetRow> resources,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(row, "row");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        final List<SpreadsheetCell> cells = Lists.array();
        cells.addAll(this.engine.insertRows(row,
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
        return "insertRows";
    }
}
