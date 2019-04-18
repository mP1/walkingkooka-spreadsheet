package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#copyCells(Collection, SpreadsheetRange, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineCopyCellsHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> {

    static SpreadsheetEngineCopyCellsHateosHandler with(final SpreadsheetEngine engine,
                                                        final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineCopyCellsHateosHandler(engine, context);
    }

    private SpreadsheetEngineCopyCellsHateosHandler(final SpreadsheetEngine engine,
                                                    final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetDelta> handle(final SpreadsheetCellReference id,
                                             final Optional<SpreadsheetDelta> resource,
                                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, "id");
        checkResource(resource);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetDelta> handleCollection(final Range<SpreadsheetCellReference> ids,
                                                       final Optional<SpreadsheetDelta> resource,
                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        checkResource(resource);
        checkParameters(parameters);

        return Optional.of(this.engine.copyCells(resource.get().cells(),
                this.parameterValueOrFail(parameters, TO, SpreadsheetRange::parse),
                this.context.get()));
    }

    // @VisibleForTesting
    final static UrlParameterName TO = UrlParameterName.with("to");

    @Override
    String operation() {
        return "copyCells"; // SpreadsheetEngine#copyCells
    }
}
