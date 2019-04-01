package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;

public final class SpreadsheetEngineRowHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineRowHateosHandler> {

    @Override
    public Class<SpreadsheetEngineRowHateosHandler> type() {
        return SpreadsheetEngineRowHateosHandler.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName();
    }
}
