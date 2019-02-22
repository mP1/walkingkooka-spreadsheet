package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosGetHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A{@link HateosGetHandler} with abstract methods that receive the store, engine and engine context.
 */
abstract class SpreadsheetEngineCellHateosGetHandler<N extends Node<N, ?, ?, ?>> extends SpreadsheetEngineCellHateosHandler<N>
        implements HateosGetHandler<SpreadsheetCellReference, N> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetEngineCellHateosGetHandler(final SpreadsheetEngine engine,
                                          final HateosContentType<N, HasJsonNode> contentType,
                                          final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }

    @Override
    public final Optional<N> get(final SpreadsheetCellReference id,
                                 final Map<HttpRequestParameterName, List<String>> parameters,
                                 final HateosHandlerContext<N> context) {
        return this.get0(id, parameters, context, this.engine, this.context.get());
    }

    abstract Optional<N> get0(final SpreadsheetCellReference id,
                              final Map<HttpRequestParameterName, List<String>> parameters,
                              final HateosHandlerContext<N> hateosHandlerContext,
                              final SpreadsheetEngine engine,
                              final SpreadsheetEngineContext engineContext);

    @Override
    public final Optional<N> getCollection(final Range<SpreadsheetCellReference> ids,
                                           final Map<HttpRequestParameterName, List<String>> parameters,
                                           final HateosHandlerContext<N> context) {
        return this.getCollection0(ids, parameters, context, this.engine, this.context.get());
    }

    abstract Optional<N> getCollection0(final Range<SpreadsheetCellReference> ids,
                                        final Map<HttpRequestParameterName, List<String>> parameters,
                                        final HateosHandlerContext<N> hateosHandlerContext,
                                        final SpreadsheetEngine engine,
                                        final SpreadsheetEngineContext engineContext);
}
