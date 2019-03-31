package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;

public final class SpreadsheetEngineHateosTest extends SpreadsheetEngineHateosTestCase<SpreadsheetEngineHateos> {

    @Override
    public Class<SpreadsheetEngineHateos> type() {
        return SpreadsheetEngineHateos.class;
    }

    @Override
    public String typeNameSuffix() {
        return "Hateos";
    }
}
