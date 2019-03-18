package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.engine.SpreadsheetEngine;

public abstract class SpreadsheetEngineHateosHandlerTestCase<T>
        extends SpreadsheetHateosHandlerTestCase<T> {

    SpreadsheetEngineHateosHandlerTestCase() {
        super();
    }

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetEngine.class.getSimpleName();
    }
}
