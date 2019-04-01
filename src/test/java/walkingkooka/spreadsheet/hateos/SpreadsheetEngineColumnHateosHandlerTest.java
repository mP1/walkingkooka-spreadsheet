package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;

public final class SpreadsheetEngineColumnHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineColumnHateosHandler> {

    @Override
    public Class<SpreadsheetEngineColumnHateosHandler> type() {
        return SpreadsheetEngineColumnHateosHandler.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName();
    }
}
