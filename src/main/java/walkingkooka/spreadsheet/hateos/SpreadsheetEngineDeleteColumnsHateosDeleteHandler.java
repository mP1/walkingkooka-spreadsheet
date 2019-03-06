package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosDeleteHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosDeleteHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineDeleteColumnsHateosDeleteHandler<N extends Node<N, ?, ?, ?>> extends SpreadsheetEngineColumnHateosHandler<N>
        implements HateosDeleteHandler<SpreadsheetColumnReference, N> {

    static <N extends Node<N, ?, ?, ?>> SpreadsheetEngineDeleteColumnsHateosDeleteHandler<N> with(final SpreadsheetEngine engine,
                                                                                                  final HateosContentType<N, SpreadsheetColumnReference> contentType,
                                                                                                  final Supplier<SpreadsheetEngineContext> context) {
        check(engine, contentType, context);
        return new SpreadsheetEngineDeleteColumnsHateosDeleteHandler<N>(engine, contentType, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteColumnsHateosDeleteHandler(final SpreadsheetEngine engine,
                                                              final HateosContentType<N, SpreadsheetColumnReference> contentType,
                                                              final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }

    @Override
    public Optional<N> delete(final SpreadsheetColumnReference column,
                              final Optional<N> resource,
                              final Map<HttpRequestAttribute<?>, Object> parameters,
                              final HateosHandlerContext<N> context) {
        Objects.requireNonNull(column, "column");
        checkResourceEmpty(resource);
        checkParameters(parameters);
        checkContext(context);

        this.engine.deleteColumns(column, 1, this.context.get());

        return Optional.empty();
    }

    @Override
    public Optional<N> deleteCollection(final Range<SpreadsheetColumnReference> columns,
                                        final Optional<N> resource,
                                        final Map<HttpRequestAttribute<?>, Object> parameters,
                                        final HateosHandlerContext<N> context) {
        checkInclusiveRange(columns, "columns");
        checkResourceEmpty(resource);
        checkParameters(parameters);
        checkContext(context);

        final SpreadsheetColumnReference lower = columns.lowerBound().value().get();
        final SpreadsheetColumnReference upper = columns.upperBound().value().get();

        this.engine.deleteColumns(lower, upper.value() - lower.value() + 1, this.context.get());

        return Optional.empty();
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
