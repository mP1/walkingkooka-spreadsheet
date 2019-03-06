package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosDeleteHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosPutHandler;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosDeleteHandler} that handles deleting a single or range of rows.
 */
final class SpreadsheetEngineInsertRowsHateosPutHandler<N extends Node<N, ?, ?, ?>> extends SpreadsheetEngineRowHateosHandler<N>
        implements HateosPutHandler<SpreadsheetRowReference, N> {

    static <N extends Node<N, ?, ?, ?>> SpreadsheetEngineInsertRowsHateosPutHandler<N> with(final SpreadsheetEngine engine,
                                                                                            final HateosContentType<N, SpreadsheetRowReference> contentType,
                                                                                            final Supplier<SpreadsheetEngineContext> context) {
        check(engine, contentType, context);
        return new SpreadsheetEngineInsertRowsHateosPutHandler<N>(engine, contentType, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineInsertRowsHateosPutHandler(final SpreadsheetEngine engine,
                                                        final HateosContentType<N, SpreadsheetRowReference> contentType,
                                                        final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }


    @Override
    public Optional<N> put(final SpreadsheetRowReference row,
                           final Optional<N> resource,
                           final Map<HttpRequestAttribute<?>, Object> parameters,
                           final HateosHandlerContext<N> context) {
        Objects.requireNonNull(row, "row");
        checkResourceEmpty(resource);
        checkParameters(parameters);
        checkContext(context);

        this.engine.insertRows(row,
                this.count(parameters),
                this.context.get());

        return Optional.empty();
    }

    private int count(final Map<HttpRequestAttribute<?>, Object> parameters) {
        final Optional<List<String>> maybeValues = COUNT.parameterValue(parameters);
        if (!maybeValues.isPresent()) {
            throw new IllegalArgumentException("Required parameter " + COUNT + " missing");
        }
        final List<String> values = maybeValues.get();
        if (1 != values.size()) {
            throw new IllegalArgumentException("Required parameter " + COUNT + " has invalid values count=" + values);
        }

        return Integer.parseInt(values.get(0));
    }

    private final UrlParameterName COUNT = UrlParameterName.with("count");

    @Override
    public Optional<N> putCollection(final Range<SpreadsheetRowReference> rows,
                                     final Optional<N> resource,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final HateosHandlerContext<N> context) {
        Objects.requireNonNull(rows, "rows");
        checkResource(resource);
        checkParameters(parameters);
        checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "insertRows";
    }
}
