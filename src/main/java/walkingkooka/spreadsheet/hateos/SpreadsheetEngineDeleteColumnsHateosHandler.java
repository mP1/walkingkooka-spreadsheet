package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} for {@link SpreadsheetEngine#deleteColumns(SpreadsheetColumnReference, int, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineDeleteColumnsHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> {

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
    public Optional<SpreadsheetDelta> handle(final SpreadsheetColumnReference column,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(column, "column");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return Optional.of(this.engine.deleteColumns(column, 1, this.context.get()));
    }

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetColumnReference> columns,
                                                       final Optional<SpreadsheetDelta> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdsInclusive(columns, "columns");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        final SpreadsheetColumnReference lower = columns.lowerBound().value().get();
        final SpreadsheetColumnReference upper = columns.upperBound().value().get();

        return Optional.of(this.engine.deleteColumns(lower, upper.value() - lower.value() + 1, this.context.get()));
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
