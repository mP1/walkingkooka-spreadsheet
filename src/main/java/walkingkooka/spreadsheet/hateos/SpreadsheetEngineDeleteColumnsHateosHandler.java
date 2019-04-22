package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} for {@link SpreadsheetEngine#deleteColumns(SpreadsheetColumnReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineDeleteColumnsHateosHandler extends SpreadsheetEngineHateosHandler2<SpreadsheetColumnReference> {

    static SpreadsheetEngineDeleteColumnsHateosHandler with(final SpreadsheetEngine engine,
                                                            final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteColumnsHateosHandler(engine, context);
    }

    private SpreadsheetEngineDeleteColumnsHateosHandler(final SpreadsheetEngine engine,
                                                        final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    String id() {
        return "column";
    }

    @Override
    SpreadsheetDelta handle0(final SpreadsheetColumnReference column,
                             final SpreadsheetDelta resource,
                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        return this.engine.deleteColumns(column, 1, this.context.get());
    }

    @Override
    void checkRange(final Range<SpreadsheetColumnReference> columns) {
        checkIdsInclusive(columns, "columns");
    }

    @Override
    SpreadsheetDelta handleCollection0(final Range<SpreadsheetColumnReference> columns,
                                       final SpreadsheetDelta resource,
                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        final SpreadsheetColumnReference lower = columns.lowerBound().value().get();
        final SpreadsheetColumnReference upper = columns.upperBound().value().get();

        return this.engine.deleteColumns(lower, upper.value() - lower.value() + 1, this.context.get());
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
