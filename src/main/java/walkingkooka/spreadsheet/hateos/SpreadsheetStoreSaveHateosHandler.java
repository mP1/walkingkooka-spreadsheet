package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.store.Store;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link walkingkooka.net.http.server.hateos.HateosHandler} that saves a value from a {@link Store}.
 */
final class SpreadsheetStoreSaveHateosHandler<I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> extends SpreadsheetStoreHateosHandler<I, R, S> {

    static <I extends Comparable<I>, R extends HateosResource<I>, S extends Store<I, R>> SpreadsheetStoreSaveHateosHandler<I, R, S> with(final S store) {
        check(store);
        return new SpreadsheetStoreSaveHateosHandler<>(store);
    }

    private SpreadsheetStoreSaveHateosHandler(final S store) {
        super(store);
    }

    @Override
    public Optional<R> handle(final I id,
                              final Optional<R> value,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(id);
        checkValuePresent(value);
        checkParameters(parameters);

        return Optional.of(this.store.save(value.get()));
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
        return Store.class.getSimpleName()+ ".save";
    }
}
