package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;

public abstract class SpreadsheetEngineHateosHandlerTestCase<T> extends SpreadsheetHateosHandlerTestCase<T> {

    SpreadsheetEngineHateosHandlerTestCase() {
        super();
    }

    final SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(123);
    }

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetEngine.class.getSimpleName();
    }
}
