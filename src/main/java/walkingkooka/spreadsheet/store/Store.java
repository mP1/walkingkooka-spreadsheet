package walkingkooka.spreadsheet.store;

import java.util.Optional;

/**
 * A store that holds a value with an id (K).
 */
public interface Store<K, V> {

    /**
     * Fetches the cell using the reference.
     */
    Optional<V> load(final K id);

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
