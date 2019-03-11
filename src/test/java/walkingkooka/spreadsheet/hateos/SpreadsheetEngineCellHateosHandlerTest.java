package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;

public final class SpreadsheetEngineCellHateosHandlerTest extends SpreadsheetEngineHateosHandlerTestCase<SpreadsheetEngineCellHateosHandler> {

    @Override
    public Class<SpreadsheetEngineCellHateosHandler> type() {
        return SpreadsheetEngineCellHateosHandler.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName();
    }
}
