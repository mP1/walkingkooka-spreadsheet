package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.engine.SpreadsheetEngine;

public abstract class SpreadsheetEngineHateosTestCase<T>
        extends SpreadsheetHateosTestCase<T> {

    SpreadsheetEngineHateosTestCase() {
        super();
    }

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetEngine.class.getSimpleName();
    }
}
