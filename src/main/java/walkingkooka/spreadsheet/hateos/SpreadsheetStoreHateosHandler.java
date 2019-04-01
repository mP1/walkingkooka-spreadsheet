package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An abstract {@link HateosHandler} that includes uses a {@link Store}
 */
abstract class SpreadsheetStoreHateosHandler<I extends Comparable<I>, R extends HateosResource<I>, S extends Store<?, ?>>
        extends SpreadsheetHateos {

    /**
     * Checks required factory method parameters are not null.
     */
    static void check(final Store<?, ?> store) {
        Objects.requireNonNull(store, "store");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetStoreHateosHandler(final S store) {
        super();
        this.store = store;
    }

    final S store;
}
