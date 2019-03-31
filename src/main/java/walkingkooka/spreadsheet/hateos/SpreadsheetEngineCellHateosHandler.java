package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.function.Supplier;

/**
 * A handler for cells.
 */
abstract class SpreadsheetEngineCellHateosHandler extends SpreadsheetEngineHateos
        implements HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetCell> {

    SpreadsheetEngineCellHateosHandler(final SpreadsheetEngine engine,
                                       final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }
}
