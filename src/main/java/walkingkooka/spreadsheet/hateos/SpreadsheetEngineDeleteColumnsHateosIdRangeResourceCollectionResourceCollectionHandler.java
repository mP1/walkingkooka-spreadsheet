package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdRangeResourceCollectionResourceCollectionHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A {@link HateosIdRangeResourceCollectionResourceCollectionHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineDeleteColumnsHateosIdRangeResourceCollectionResourceCollectionHandler extends SpreadsheetEngineColumnHateosHandler
        implements HateosIdRangeResourceCollectionResourceCollectionHandler<SpreadsheetColumnReference, SpreadsheetColumn, SpreadsheetCell> {

    static SpreadsheetEngineDeleteColumnsHateosIdRangeResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                                       final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteColumnsHateosIdRangeResourceCollectionResourceCollectionHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteColumnsHateosIdRangeResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                                   final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public List<SpreadsheetCell> handle(final Range<SpreadsheetColumnReference> columns,
                                        final List<SpreadsheetColumn> resources,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdsInclusive(columns, "columns");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        final SpreadsheetColumnReference lower = columns.lowerBound().value().get();
        final SpreadsheetColumnReference upper = columns.upperBound().value().get();

        this.engine.deleteColumns(lower, upper.value() - lower.value() + 1, this.context.get());

        return Lists.empty();
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
