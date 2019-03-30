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
 * A {@link HateosHandler} that deletes a value from a {@link Store}.
 */
final class SpreadsheetStoreDeleteHateosHandler<I extends Comparable<I>,
        R extends HateosResource<I>,
        S extends Store<I, R>>
        extends SpreadsheetStoreHateosHandler<I, R, S>
        implements HateosHandler<I, R, R>{

    static <I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> SpreadsheetStoreDeleteHateosHandler<I, R, S> with(final S store) {
        check(store);
        return new SpreadsheetStoreDeleteHateosHandler<>(store);
    }

    private SpreadsheetStoreDeleteHateosHandler(final S store) {
        super(store);
    }

    @Override
    public Optional<R> handle(final I id,
                              final Optional<R> value,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(id);
        checkValueEmpty(value);
        checkParameters(parameters);

        this.store.delete(id);
        return Optional.empty();
    }

    @Override
    public List<R> handleCollection(final Range<I> ids,
                                    final List<R> values,
                                    final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIds(ids);
        checkValues(values);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return Store.class.getSimpleName()+ ".delete";
    }
}
