package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
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
final class SpreadsheetEngineDeleteRowsHateosHandler extends SpreadsheetEngineRowHateosHandler {

    static SpreadsheetEngineDeleteRowsHateosHandler with(final SpreadsheetEngine engine,
                                                         final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteRowsHateosHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteRowsHateosHandler(final SpreadsheetEngine engine,
                                                     final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetCell> handle(final SpreadsheetRowReference row,
                                            final Optional<SpreadsheetRow> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(row, "row");
        this.checkResourceEmpty(resource);
        checkParameters(parameters);

        this.engine.deleteRows(row, 1, this.context.get());

        return Optional.empty();
    }

    @Override
    public List<SpreadsheetCell> handleCollection(final Range<SpreadsheetRowReference> rows,
                                                  final List<SpreadsheetRow> resources,
                                                  final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdsInclusive(rows, "rows");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        final SpreadsheetRowReference lower = rows.lowerBound().value().get();
        final SpreadsheetRowReference upper = rows.upperBound().value().get();

        this.engine.deleteRows(lower, upper.value() - lower.value() + 1, this.context.get());

        return Lists.empty();
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
