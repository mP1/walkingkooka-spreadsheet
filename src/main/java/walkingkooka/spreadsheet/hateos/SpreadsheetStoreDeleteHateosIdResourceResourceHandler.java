package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosIdResourceResourceHandler} that deletes a value from a {@link Store}.
 */
final class SpreadsheetStoreDeleteHateosIdResourceResourceHandler<I extends Comparable<I>,
        R extends HateosResource<I>,
        S extends Store<I, R>>
        extends SpreadsheetStoreHateosHandler<I, R, S>
        implements HateosIdResourceResourceHandler<I, R, R>{

    static <I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> SpreadsheetStoreDeleteHateosIdResourceResourceHandler<I, R, S> with(final S store) {
        check(store);
        return new SpreadsheetStoreDeleteHateosIdResourceResourceHandler<>(store);
    }

    private SpreadsheetStoreDeleteHateosIdResourceResourceHandler(final S store) {
        super(store);
    }

    @Override
    public Optional<R> handle(final I id,
                              final Optional<R> value,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(id);
        checkResourceEmpty(value);
        checkParameters(parameters);

        this.store.delete(id);
        return Optional.empty();
    }

    @Override
    public String toString() {
        return Store.class.getSimpleName()+ ".delete";
    }
}
