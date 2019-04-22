package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;

public final class SpreadsheetEngineHateosHandler3Test extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineHateosHandler3> {

    @Override
    public Class<SpreadsheetEngineHateosHandler3> type() {
        return SpreadsheetEngineHateosHandler3.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName() + "3";
    }
}
