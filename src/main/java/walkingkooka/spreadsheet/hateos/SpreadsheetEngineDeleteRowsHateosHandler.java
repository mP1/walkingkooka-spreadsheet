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
 * A {@link HateosHandler} for {@link SpreadsheetEngine#deleteRows(SpreadsheetRowReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineDeleteRowsHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> {

    static SpreadsheetEngineDeleteRowsHateosHandler with(final SpreadsheetEngine engine,
                                                         final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteRowsHateosHandler(engine, context);
    }

    private SpreadsheetEngineDeleteRowsHateosHandler(final SpreadsheetEngine engine,
                                                     final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta> handle(final SpreadsheetRowReference row,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(row, "row");
        this.checkResourceEmpty(resource);
        checkParameters(parameters);

        return Optional.of(this.engine.deleteRows(row, 1, this.context.get()));
    }

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetRowReference> rows,
                                                       final Optional<SpreadsheetDelta> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdsInclusive(rows, "rows");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        final SpreadsheetRowReference lower = rows.lowerBound().value().get();
        final SpreadsheetRowReference upper = rows.upperBound().value().get();

        return Optional.of(this.engine.deleteRows(lower, upper.value() - lower.value() + 1, this.context.get()));
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
