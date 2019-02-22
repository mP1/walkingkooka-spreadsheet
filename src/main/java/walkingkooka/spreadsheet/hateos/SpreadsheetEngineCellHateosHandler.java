package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.function.Supplier;

/**
 * A handler for cells.
 */
abstract class SpreadsheetEngineCellHateosHandler<N extends Node<N, ?, ?, ?>> extends
        SpreadsheetEngineHateosHandler<SpreadsheetCellReference, HasJsonNode, N> {

    SpreadsheetEngineCellHateosHandler(final SpreadsheetEngine engine,
                                       final HateosContentType<N, HasJsonNode> contentType,
                                       final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }

    @Override
    final HateosResourceName resourceName() {
        return RESOURCE_NAME;
    }

    final static HateosResourceName RESOURCE_NAME = HateosResourceName.with("cell");
}
