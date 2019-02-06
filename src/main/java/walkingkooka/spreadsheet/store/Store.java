package walkingkooka.spreadsheet.store;

import java.util.Optional;

/**
 * A store that holds a value with an id (K).
 */
public interface Store<K, V> {

    /**
     * Fetches the value using the reference.
     */
    Optional<V> load(final K id);

    /**
     * Fetches the value with the id or throws a {@link StoreException}.
     */
    default V loadOrFail(final K id) {
        final Optional<V> value = this.load(id);
        if (false == value.isPresent()) {
            throw new LoadStoreException("Value with id " + id + " is absent");
        }
        return value.get();
    }

    /**
     * Saves or updates a value.
     */
    void save(final V value);

    /**
     * Deletes a single value by id.
     */
    void delete(final K id);

    /**
     * Returns the total number of records in the store.
     */
    int count();
}
