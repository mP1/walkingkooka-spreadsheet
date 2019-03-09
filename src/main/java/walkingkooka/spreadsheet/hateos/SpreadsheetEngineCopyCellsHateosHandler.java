package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#copy(Collection, SpreadsheetRange, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineCopyCellsHateosHandler extends SpreadsheetEngineCellHateosHandler {

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
    public Optional<SpreadsheetCell> handle(final SpreadsheetCellReference cellReference,
                                            final Optional<SpreadsheetCell> cell,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(cellReference, "cellReference");
        checkResourceEmpty(cell);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public List<SpreadsheetCell> handleCollection(final Range<SpreadsheetCellReference> ids,
                                                  final List<SpreadsheetCell> cells,
                                                  final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        checkResources(cells);
        checkParameters(parameters);

        this.engine.copy(cells,
                this.parameterValueOrFail(parameters, TO, SpreadsheetRange::parse),
                this.context.get());

        return Lists.empty();
    }

    // @VisibleForTesting
    final static UrlParameterName TO = UrlParameterName.with("to");

    @Override
    String operation() {
        return "copy"; // SpreadsheetEngine#copy
    }
}
