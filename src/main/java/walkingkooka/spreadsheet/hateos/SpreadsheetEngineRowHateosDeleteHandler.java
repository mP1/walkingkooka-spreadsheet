package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosDeleteHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosDeleteHandler} that handles deleting a single or range of rows.
 */
final class SpreadsheetEngineRowHateosDeleteHandler<N extends Node<N, ?, ?, ?>> extends SpreadsheetEngineRowHateosHandler<N>
        implements HateosDeleteHandler<SpreadsheetRowReference, N> {

    static <N extends Node<N, ?, ?, ?>> SpreadsheetEngineRowHateosDeleteHandler<N> with(final SpreadsheetEngine engine,
                                                                                        final HateosContentType<N, HasJsonNode> contentType,
                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        check(engine, contentType, context);
        return new SpreadsheetEngineRowHateosDeleteHandler<N>(engine, contentType, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineRowHateosDeleteHandler(final SpreadsheetEngine engine,
                                                    final HateosContentType<N, HasJsonNode> contentType,
                                                    final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }

    @Override
    public void delete(final SpreadsheetRowReference row,
                       final Optional<N> resource,
                       final HateosHandlerContext<N> context) {
        Objects.requireNonNull(row, "row");
        checkResourceEmpty(resource);
        Objects.requireNonNull(context, "context");

        this.engine.deleteRows(row, 1, this.context.get());
    }

    @Override
    public void deleteCollection(final Range<SpreadsheetRowReference> rows,
                                 final Optional<N> resource,
                                 final HateosHandlerContext<N> context) {
        checkInclusiveRange(rows, "rows");
        checkResourceEmpty(resource);
        Objects.requireNonNull(context, "context");

        final SpreadsheetRowReference lower = rows.lowerBound().value().get();
        final SpreadsheetRowReference upper = rows.upperBound().value().get();

        this.engine.deleteRows(lower, upper.value() - lower.value() + 1, this.context.get());
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
