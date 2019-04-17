package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
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
import java.util.function.Supplier;

/**
 * A {@link HateosIdResourceCollectionResourceCollectionHandler} that handles deleting a single or range of rows.
 */
final class SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler extends SpreadsheetEngineRowHateosHandler
        implements HateosIdResourceCollectionResourceCollectionHandler<SpreadsheetRowReference, SpreadsheetRow, SpreadsheetCell> {

    static SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                               final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteRowsHateosIdResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                           final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public List<SpreadsheetCell> handle(final SpreadsheetRowReference row,
                                        final List<SpreadsheetRow> resources,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(row, "row");
        this.checkResourcesEmpty(resources);
        checkParameters(parameters);

        final List<SpreadsheetCell> cells = Lists.array();
        cells.addAll(this.engine.deleteRows(row, 1, this.context.get())
                .cells());
        return Lists.readOnly(cells);
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
