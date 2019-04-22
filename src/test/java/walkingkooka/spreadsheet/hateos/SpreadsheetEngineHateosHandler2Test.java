package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;

public final class SpreadsheetEngineHateosHandler2Test extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineHateosHandler2> {

    @Override
    public Class<SpreadsheetEngineHateosHandler2> type() {
        return SpreadsheetEngineHateosHandler2.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName() + "2";
    }
}
