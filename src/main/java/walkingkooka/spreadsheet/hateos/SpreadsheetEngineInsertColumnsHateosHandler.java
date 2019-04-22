package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that handles inserting a single or range of columns.
 */
final class SpreadsheetEngineInsertColumnsHateosHandler extends SpreadsheetEngineHateosHandler3<SpreadsheetColumnReference> {

    static SpreadsheetEngineInsertColumnsHateosHandler with(final SpreadsheetEngine engine,
                                                            final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineInsertColumnsHateosHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineInsertColumnsHateosHandler(final SpreadsheetEngine engine,
                                                        final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    String id() {
        return "column";
    }

    @Override
    SpreadsheetDelta handle0(final SpreadsheetColumnReference column,
                             final Optional<SpreadsheetDelta> resource,
                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        return this.engine.insertColumns(column,
                this.count(parameters),
                this.context.get());
    }

    @Override
    void checkRange(final Range<SpreadsheetColumnReference> rows) {
        checkIdsInclusive(rows, "columns");
    }

    @Override
    SpreadsheetDelta handleCollection0(final Range<SpreadsheetColumnReference> columns,
                                       final Optional<SpreadsheetDelta> resource,
                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdsInclusive(columns, "columns");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "insertColumns";
    }
}
