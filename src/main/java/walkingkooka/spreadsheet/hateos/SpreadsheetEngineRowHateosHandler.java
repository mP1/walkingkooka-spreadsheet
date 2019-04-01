package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.function.Supplier;

/**
 * A handler for rows.
 */
abstract class SpreadsheetEngineRowHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetRowReference, SpreadsheetRow, SpreadsheetCell> {

    SpreadsheetEngineRowHateosHandler(final SpreadsheetEngine engine,
                                      final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }
}
