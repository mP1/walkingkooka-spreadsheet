package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosGetHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosGetHandler} that calls {@link SpreadsheetEngine#id()}.
 */
final class SpreadsheetEngineIdHateosGetHandler<N extends Node<N, ?, ?, ?>> extends SpreadsheetEngineHateosHandler<SpreadsheetId, SpreadsheetId, N>
        implements HateosGetHandler<SpreadsheetId, N> {

    static <N extends Node<N, ?, ?, ?>> SpreadsheetEngineIdHateosGetHandler<N> with(final SpreadsheetEngine engine,
                                                                                    final HateosContentType<N, SpreadsheetId> contentType,
                                                                                    final Supplier<SpreadsheetEngineContext> context) {
        check(engine, contentType, context);
        return new SpreadsheetEngineIdHateosGetHandler<N>(engine, contentType, context);
    }

    private SpreadsheetEngineIdHateosGetHandler(final SpreadsheetEngine engine,
                                                final HateosContentType<N, SpreadsheetId> contentType,
                                                final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }

    @Override
    public Optional<N> get(final SpreadsheetId id,
                           final Map<HttpRequestAttribute<?>, Object> parameters,
                           final HateosHandlerContext<N> context) {
        Objects.requireNonNull(id, "id");
        checkParameters(parameters);
        Objects.requireNonNull(context, "context");

        return Optional.of(this.contentType.toNode(this.engine.id()));
    }

    @Override
    public Optional<N> getCollection(final Range<SpreadsheetId> ids,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final HateosHandlerContext<N> context) {
        Objects.requireNonNull(ids, "ids");
        checkParameters(parameters);
        Objects.requireNonNull(context, "context");

        throw new UnsupportedOperationException();
    }

    @Override
    final HateosResourceName resourceName() {
        return RESOURCE_NAME;
    }

    final static HateosResourceName RESOURCE_NAME = HateosResourceName.with("id");

    @Override
    String operation() {
        return "id";
    }
}
