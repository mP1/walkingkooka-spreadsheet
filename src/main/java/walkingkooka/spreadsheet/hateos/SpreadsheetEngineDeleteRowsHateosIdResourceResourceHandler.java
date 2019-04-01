package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosIdResourceResourceHandler} that handles deleting a single or range of rows.
 */
final class SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler extends SpreadsheetEngineRowHateosHandler
        implements HateosIdResourceResourceHandler<SpreadsheetRowReference,SpreadsheetRow, SpreadsheetCell> {

    static SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler with(final SpreadsheetEngine engine,
                                                                           final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteRowsHateosIdResourceResourceHandler(final SpreadsheetEngine engine,
                                                                       final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetCell> handle(final SpreadsheetRowReference row,
                                            final Optional<SpreadsheetRow> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(row, "row");
        this.checkResourceEmpty(resource);
        checkParameters(parameters);

        this.engine.deleteRows(row, 1, this.context.get());

        return Optional.empty();
    }

    @Override
    String operation() {
        return "deleteRows";
    }
}
