package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} that loads a value from a {@link Store}.
 */
final class SpreadsheetStoreLoadHateosHandler<I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> extends SpreadsheetStoreHateosHandler<I, R, S>
        implements HateosHandler<I, R, R>{

    static <I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> SpreadsheetStoreLoadHateosHandler<I, R, S> with(final S store) {
        check(store);
        return new SpreadsheetStoreLoadHateosHandler<>(store);
    }

    private SpreadsheetStoreLoadHateosHandler(final S store) {
        super(store);
    }

    @Override
    public Optional<R> handle(final I id,
                              final Optional<R> value,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(id);
        checkResourceEmpty(value);
        checkParameters(parameters);

        return this.store.load(id);
    }

    @Override
    public List<R> handleCollection(final Range<I> ids,
                                    final List<R> values,
                                    final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIds(ids, "ids");
        checkResourcesEmpty(values);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return Store.class.getSimpleName()+ ".load";
    }
}
