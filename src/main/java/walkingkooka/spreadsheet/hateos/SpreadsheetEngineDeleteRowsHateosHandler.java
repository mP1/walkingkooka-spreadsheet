package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} for {@link SpreadsheetEngine#deleteRows(SpreadsheetRowReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineDeleteRowsHateosHandler extends SpreadsheetEngineHateosHandler3<SpreadsheetRowReference> {

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
    String id() {
        return "row";
    }

    @Override
    SpreadsheetDelta handle0(final SpreadsheetRowReference row,
                             final Optional<SpreadsheetDelta> resource,
                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        return this.engine.deleteRows(row, 1, this.context.get());
    }

    @Override
    void checkRange(final Range<SpreadsheetRowReference> rows) {
        checkIdsInclusive(rows, "rows");
    }

    @Override
    SpreadsheetDelta handleCollection0(final Range<SpreadsheetRowReference> rows,
                                       final Optional<SpreadsheetDelta> resource,
                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        final SpreadsheetRowReference lower = rows.lowerBound().value().get();
        final SpreadsheetRowReference upper = rows.upperBound().value().get();

        return this.engine.deleteRows(lower, upper.value() - lower.value() + 1, this.context.get());
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
