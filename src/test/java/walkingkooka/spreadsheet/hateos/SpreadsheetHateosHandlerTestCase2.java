package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosResource;

public abstract class SpreadsheetHateosHandlerTestCase2<H extends HateosHandler<I, R>, I extends Comparable<I>, R extends HateosResource<I>>
        extends SpreadsheetHateosHandlerTestCase<H>
        implements HateosHandlerTesting<H, I, R> {

    SpreadsheetHateosHandlerTestCase2() {
        super();
    }
}
