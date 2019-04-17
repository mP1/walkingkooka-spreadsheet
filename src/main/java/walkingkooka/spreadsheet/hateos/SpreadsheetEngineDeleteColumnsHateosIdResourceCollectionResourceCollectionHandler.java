package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
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
import java.util.function.Supplier;

/**
 * A {@link HateosIdResourceCollectionResourceCollectionHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler extends SpreadsheetEngineColumnHateosHandler
        implements HateosIdResourceCollectionResourceCollectionHandler<SpreadsheetColumnReference, SpreadsheetColumn, SpreadsheetCell> {

    static SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                                  final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteColumnsHateosIdResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                              final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public List<SpreadsheetCell> handle(final SpreadsheetColumnReference column,
                                        final List<SpreadsheetColumn> resources,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(column, "column");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        final List<SpreadsheetCell> cells = Lists.array();
        cells.addAll(this.engine.deleteColumns(column, 1, this.context.get())
                .cells());
        return Lists.readOnly(cells);
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
