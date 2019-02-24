package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.Node;
import walkingkooka.tree.json.HasJsonNode;

import java.util.function.Supplier;

/**
 * A handler for rows.
 */
abstract class SpreadsheetEngineRowHateosHandler<N extends Node<N, ?, ?, ?>> extends
        SpreadsheetEngineHateosHandler<SpreadsheetRowReference, HasJsonNode, N> {

    SpreadsheetEngineRowHateosHandler(final SpreadsheetEngine engine,
                                      final HateosContentType<N, HasJsonNode> contentType,
                                      final Supplier<SpreadsheetEngineContext> context) {
        super(engine, contentType, context);
    }

    @Override
    final HateosResourceName resourceName() {
        return RESOURCE_NAME;
    }

    final static HateosResourceName RESOURCE_NAME = HateosResourceName.with("row");
}
