package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosIdResourceResourceHandler} that saves a value from a {@link Store}.
 */
final class SpreadsheetStoreSaveHateosIdResourceResourceHandler<I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> extends SpreadsheetStoreHateosHandler<I, R, S>
        implements HateosIdResourceResourceHandler<I, R, R>{

    static <I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> SpreadsheetStoreSaveHateosIdResourceResourceHandler<I, R, S> with(final S store) {
        check(store);
        return new SpreadsheetStoreSaveHateosIdResourceResourceHandler<>(store);
    }

    private SpreadsheetStoreSaveHateosIdResourceResourceHandler(final S store) {
        super(store);
    }

    @Override
    public Optional<R> handle(final I id,
                              final Optional<R> value,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(id);
        checkResourcePresent(value);
        checkParameters(parameters);

        return Optional.of(this.store.save(value.get()));
    }

    @Override
    public String toString() {
        return Store.class.getSimpleName()+ ".save";
    }
}
