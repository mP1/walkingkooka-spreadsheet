package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base class that decorates requests performing parameter checks and a few other extras.
 */
abstract class SpreadsheetEngineHateosHandler3<I extends Comparable<I>> extends SpreadsheetEngineHateosHandler2<I> {

    SpreadsheetEngineHateosHandler3(final SpreadsheetEngine engine,
                                    final Supplier<SpreadsheetEngineContext> context) {
        super(engine, context);
    }

    @Override
    final void checkSpreadsheetDelta(final Optional<SpreadsheetDelta> delta) {
        this.checkResourceEmpty(delta);
    }
}
