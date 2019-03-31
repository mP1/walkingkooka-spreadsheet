package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An abstract {@link HateosHandler} that includes uses a {@link SpreadsheetEngine} and {@link SpreadsheetEngineContext} to do things.
 */
abstract class SpreadsheetEngineHateosHandler extends SpreadsheetHateosHandler {

    /**
     * Checks required factory method parameters are not null.
     */
    static void check(final SpreadsheetEngine engine,
                      final Supplier<SpreadsheetEngineContext> context) {
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(context, "context");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetEngineHateosHandler(final SpreadsheetEngine engine,
                                   final Supplier<SpreadsheetEngineContext> context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    final SpreadsheetEngine engine;
    final Supplier<SpreadsheetEngineContext> context;

    @Override
    public final String toString() {
        return SpreadsheetEngine.class.getSimpleName() + "." + this.operation();
    }

    abstract String operation();
}
