package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.test.ClassTesting;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetEngineHateosHandlerTest extends SpreadsheetHateosHandlerTestCase<SpreadsheetEngineHateosHandler> {

    @Override
    public Class<SpreadsheetEngineHateosHandler> type() {
        return SpreadsheetEngineHateosHandler.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosHandler.class.getSimpleName();
    }
}
