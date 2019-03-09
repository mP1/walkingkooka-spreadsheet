package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.function.Supplier;

/**
 * A handler for columns.
 */
abstract class SpreadsheetEngineColumnHateosHandler extends SpreadsheetEngineHateosHandler<SpreadsheetColumnReference, SpreadsheetColumn> {

    SpreadsheetEngineColumnHateosHandler(final SpreadsheetEngine engine,
                                         final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }
}
