package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.function.Supplier;

public abstract class SpreadsheetEngineCellHateosHandlerTestCase<H extends HateosHandler<SpreadsheetCellReference, JsonNode>>
        extends SpreadsheetEngineHateosHandlerTestCase<H, SpreadsheetCellReference, SpreadsheetCell> {

    SpreadsheetEngineCellHateosHandlerTestCase() {
        super();
    }
}
