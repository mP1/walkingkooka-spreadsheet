package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosGetHandler;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetEngineCellHateosGetHandlerTest extends SpreadsheetHateosHandlerTestCase<SpreadsheetEngineCellHateosGetHandler> {

    @Override
    public Class<SpreadsheetEngineCellHateosGetHandler> type() {
        return SpreadsheetEngineCellHateosGetHandler.class;
    }

    @Override
    public String typeNameSuffix() {
        return HateosGetHandler.class.getSimpleName();
    }
}
