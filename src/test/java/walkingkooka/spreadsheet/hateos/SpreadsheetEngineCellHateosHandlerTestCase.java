package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

public abstract class SpreadsheetEngineCellHateosHandlerTestCase<H extends HateosHandler<SpreadsheetCellReference, SpreadsheetCell>>
        extends SpreadsheetEngineHateosHandlerTestCase2<H, SpreadsheetCellReference, SpreadsheetCell> {

    SpreadsheetEngineCellHateosHandlerTestCase() {
        super();
    }
}
