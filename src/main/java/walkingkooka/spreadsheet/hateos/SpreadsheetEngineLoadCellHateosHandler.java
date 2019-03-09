package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineLoading;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosHandler} that calls {@link SpreadsheetEngine#loadCell(SpreadsheetCellReference, SpreadsheetEngineLoading, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineLoadCellHateosHandler extends SpreadsheetEngineCellHateosHandler {

    static SpreadsheetEngineLoadCellHateosHandler with(final SpreadsheetEngine engine,
                                                       final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineLoadCellHateosHandler(engine, context);
    }

    private SpreadsheetEngineLoadCellHateosHandler(final SpreadsheetEngine engine,
                                                   final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetCell> handle(final SpreadsheetCellReference cellReference,
                                            final Optional<SpreadsheetCell> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(cellReference, "cellReference");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return this.engine.loadCell(cellReference,
                this.loading(parameters),
                this.context.get());
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
    public final List<SpreadsheetCell> handleCollection(final Range<SpreadsheetCellReference> cellReferences,
                                                        final List<SpreadsheetCell> resources,
                                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(cellReferences, "cellReferences");
        checkResourcesEmpty(resources);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "loadCell";
    }
}
