package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandlerContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineLoading;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosGetHandler} that calls {@link SpreadsheetEngine#loadCell(SpreadsheetCellReference, SpreadsheetEngineLoading, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineLoadCellHateosGetHandler<N extends Node<N, ?, ?, ?>> extends SpreadsheetEngineCellHateosGetHandler<N> {

    static <N extends Node<N, ?, ?, ?>> SpreadsheetEngineLoadCellHateosGetHandler<N> with(final SpreadsheetEngine engine,
                                                                                          final HateosContentType<N, HasJsonNode> contentType,
                                                                                          final Supplier<SpreadsheetEngineContext> context) {
        check(engine, contentType, context);
        return new SpreadsheetEngineLoadCellHateosGetHandler<N>(engine, contentType, context);
    }

    private SpreadsheetEngineLoadCellHateosGetHandler(final SpreadsheetEngine engine,
                                                      final HateosContentType<N, HasJsonNode> contentType,
                                                      final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }

    @Override
    Optional<N> get0(final SpreadsheetCellReference id,
                     final Map<HttpRequestAttribute<?>, Object> parameters,
                     final HateosHandlerContext<N> hateosHandlerContext,
                     final SpreadsheetEngine engine,
                     final SpreadsheetEngineContext engineContext) {

        return this.engine.loadCell(id,
                this.loading(parameters),
                engineContext).map(cell -> this.addLinks(id, cell, hateosHandlerContext));
    }

    /**
     * Loads the {@link SpreadsheetEngineLoading} from a request parameter.
     */
    private SpreadsheetEngineLoading loading(final Map<HttpRequestAttribute<?>, Object> parameters) {
        final Optional<List<String>> maybeValues = LOADING.parameterValue(parameters);
        if (!maybeValues.isPresent()) {
            throw new IllegalArgumentException("Required parameter " + LOADING + " missing");
        }
        final List<String> values = maybeValues.get();
        if (values.size() != 1) {
            throw new IllegalArgumentException("Required parameter " + LOADING + " incorrect=" + values);
        }
        return SpreadsheetEngineLoading.valueOf(values.get(0));
    }

    private final static UrlParameterName LOADING = UrlParameterName.with("loading");

    @Override
    Optional<N> getCollection0(final Range<SpreadsheetCellReference> ids,
                               final Map<HttpRequestAttribute<?>, Object> parameters,
                               final HateosHandlerContext<N> hateosHandlerContext,
                               final SpreadsheetEngine engine,
                               final SpreadsheetEngineContext engineContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "loadCell";
    }
}
