package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineDeleteColumnsHateosHandler extends SpreadsheetEngineColumnHateosHandler {

    static SpreadsheetEngineDeleteColumnsHateosHandler with(final SpreadsheetEngine engine,
                                                            final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteColumnsHateosHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteColumnsHateosHandler(final SpreadsheetEngine engine,
                                                        final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetColumn> handle(final SpreadsheetColumnReference column,
                                              final Optional<SpreadsheetColumn> resource,
                                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(column, "column");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        this.engine.deleteColumns(column, 1, this.context.get());

        return Optional.empty();
    }

    @Override
    public List<SpreadsheetColumn> handleCollection(final Range<SpreadsheetColumnReference> columns,
                                                    final List<SpreadsheetColumn> resources,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkInclusiveRange(columns, "columns");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        final SpreadsheetColumnReference lower = columns.lowerBound().value().get();
        final SpreadsheetColumnReference upper = columns.upperBound().value().get();

        this.engine.deleteColumns(lower, upper.value() - lower.value() + 1, this.context.get());

        return Lists.empty();
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
