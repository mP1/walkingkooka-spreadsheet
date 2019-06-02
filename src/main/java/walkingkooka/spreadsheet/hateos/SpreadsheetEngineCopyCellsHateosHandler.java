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
import java.util.function.Supplier;

/**
 * A {@link HateosHandler} that calls {@link SpreadsheetEngine#copyCells(Collection, SpreadsheetRange, SpreadsheetEngineContext)}.
 */
final class SpreadsheetEngineCopyCellsHateosHandler extends SpreadsheetEngineHateosHandler2<SpreadsheetCellReference> {

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
    String id() {
        return "cellreference";
    }

    @Override
    SpreadsheetDelta handle0(final SpreadsheetCellReference id,
                             final SpreadsheetDelta resource,
                             final Map<HttpRequestAttribute<?>, Object> parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    void checkRange(final Range<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");
    }

    @Override
    SpreadsheetDelta handleCollection0(final Range<SpreadsheetCellReference> cells,
                                       final SpreadsheetDelta resource,
                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        return this.engine.copyCells(resource.cells(),
                this.parameterValueOrFail(parameters, TO, SpreadsheetRange::parse),
                this.context.get());
    }

    // @VisibleForTesting
    final static UrlParameterName TO = UrlParameterName.with("to");

    @Override
    String operation() {
        return "copyCells"; // SpreadsheetEngine#copyCells
    }
}
