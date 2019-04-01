package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link HateosIdResourceResourceHandler} that handles deleting a single or range of columns.
 */
final class SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler extends SpreadsheetEngineColumnHateosHandler
        implements HateosIdResourceResourceHandler<SpreadsheetColumnReference, SpreadsheetColumn, SpreadsheetCell> {

    static SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler with(final SpreadsheetEngine engine,
                                                                              final Supplier<SpreadsheetEngineContext> context) {
        check(engine, context);
        return new SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler(engine, context);
    }

    /**
     * Private ctor
     */
    private SpreadsheetEngineDeleteColumnsHateosIdResourceResourceHandler(final SpreadsheetEngine engine,
                                                                          final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    public Optional<SpreadsheetCell> handle(final SpreadsheetColumnReference column,
                                            final Optional<SpreadsheetColumn> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(column, "column");
        checkResourceEmpty(resource);
        checkParameters(parameters);

        this.engine.deleteColumns(column, 1, this.context.get());

        return Optional.empty();
    }

    @Override
    String operation() {
        return "deleteColumns";
    }
}
