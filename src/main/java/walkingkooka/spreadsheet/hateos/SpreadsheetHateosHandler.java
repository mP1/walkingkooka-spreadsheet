package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;

/**
 * Base class for all handlers.
 */
abstract class SpreadsheetHateosHandler<I extends Comparable<I>, R extends HateosResource<I>> implements HateosHandler<I, R> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetHateosHandler() {
        super();
    }
}
