package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that handles inserting a single or range of rows.
 */
final class SpreadsheetEngineInsertRowsHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> {

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
    public Optional<SpreadsheetDelta> handle(final SpreadsheetRowReference row,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(row, "row");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return Optional.of(this.engine.insertRows(row,
                this.count(parameters),
                this.context.get()));
    }

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetRowReference> rows,
                                                       final Optional<SpreadsheetDelta> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdsInclusive(rows, "rows");
        checkResourceEmpty(resource);
        checkParameters(parameters);
        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "insertRows";
    }
}

