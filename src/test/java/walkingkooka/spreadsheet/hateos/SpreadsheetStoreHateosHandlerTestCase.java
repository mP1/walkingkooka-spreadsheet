package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;

public abstract class SpreadsheetStoreHateosHandlerTestCase<T>
        extends SpreadsheetHateosHandlerTestCase<T> {

    SpreadsheetStoreHateosHandlerTestCase() {
        super();
    }
}
