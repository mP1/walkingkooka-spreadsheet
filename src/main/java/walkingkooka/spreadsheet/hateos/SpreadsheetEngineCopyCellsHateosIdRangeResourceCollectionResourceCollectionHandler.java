package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.UrlParameterName;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdRangeResourceCollectionResourceCollectionHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A {@link HateosIdRangeResourceCollectionResourceCollectionHandler} that calls {@link SpreadsheetEngine#copyCells(Collection, SpreadsheetRange, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler extends SpreadsheetEngineHateosHandler
        implements HateosIdRangeResourceCollectionResourceCollectionHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetCell> {

    static SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler with(final SpreadsheetEngine engine,
                                                                                                   final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler(engine, context);
    }

    private SpreadsheetEngineCopyCellsHateosIdRangeResourceCollectionResourceCollectionHandler(final SpreadsheetEngine engine,
                                                                                               final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public List<SpreadsheetCell> handle(final Range<SpreadsheetCellReference> ids,
                                        final List<SpreadsheetCell> cells,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        checkResources(cells);
        checkParameters(parameters);

        this.engine.copyCells(cells,
                this.parameterValueOrFail(parameters, TO, SpreadsheetRange::parse),
                this.context.get());

        return Lists.empty();
    }

    // @VisibleForTesting
    final static UrlParameterName TO = UrlParameterName.with("to");

    @Override
    String operation() {
        return "copyCells"; // SpreadsheetEngine#copyCells
    }
}
