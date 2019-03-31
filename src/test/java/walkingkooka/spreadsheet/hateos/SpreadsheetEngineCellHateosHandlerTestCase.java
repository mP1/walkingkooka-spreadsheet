package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

public abstract class SpreadsheetEngineCellHateosHandlerTestCase<H extends HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetCell>>
        extends SpreadsheetEngineHateosTestCase2<H, SpreadsheetCellReference, SpreadsheetCell, SpreadsheetCell> {

    SpreadsheetEngineCellHateosHandlerTestCase() {
        super();
    }
}
