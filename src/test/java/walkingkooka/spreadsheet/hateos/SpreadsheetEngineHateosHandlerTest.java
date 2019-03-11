package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;

public final class SpreadsheetEngineHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineHateosHandler> {

    @Override
    public Class<SpreadsheetEngineHateosHandler> type() {
        return SpreadsheetEngineHateosHandler.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName();
    }
}
