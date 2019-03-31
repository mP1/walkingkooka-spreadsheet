package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.function.Supplier;

/**
 * A {@link HateosHandler} for columns.
 */
abstract class SpreadsheetEngineColumnHateosHandler extends SpreadsheetEngineHateosHandler
        implements HateosHandler<SpreadsheetColumnReference, SpreadsheetColumn, SpreadsheetCell> {

    SpreadsheetEngineColumnHateosHandler(final SpreadsheetEngine engine,
                                         final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }
}
