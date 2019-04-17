package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdRangeResourceCollectionResourceCollectionHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A {@link HateosIdRangeResourceCollectionResourceCollectionHandler} that handles deleting a single or range of rows.
 */
final class SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler extends SpreadsheetEngineRowHateosHandler
        implements HateosIdRangeResourceCollectionResourceCollectionHandler<SpreadsheetRowReference, SpreadsheetRow, SpreadsheetCell> {

    static SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                                    final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteRowsHateosIdRangeResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                                final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public List<SpreadsheetCell> handle(final Range<SpreadsheetRowReference> rows,
                                        final List<SpreadsheetRow> resources,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdsInclusive(rows, "rows");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        final SpreadsheetRowReference lower = rows.lowerBound().value().get();
        final SpreadsheetRowReference upper = rows.upperBound().value().get();

        final List<SpreadsheetCell> cells = Lists.array();
        cells.addAll(this.engine.deleteRows(lower, upper.value() - lower.value() + 1, this.context.get()).cells());
        return Lists.readOnly(cells);
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
