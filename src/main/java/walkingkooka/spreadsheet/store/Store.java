package walkingkooka.spreadsheet.store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
    V save(final V value);

    /**
     * Deletes a single value by id.
     */
    void delete(final K id);

    /**
     * Returns the total number of records in the store.
     */
    int count();

    /**
     * Returns a view of all ids between the positional range.
     */
    Set<K> ids(final int from, final int count);

    /**
     * Returns a view of all values between the range of ids.
     */
    List<V> values(final K from, final int count);

    /**
     * Useful parameter checking for both {@link #ids}
     */
    static void checkFromAndTo(final int from, final int count) {
        if (from < 0) {
            throw new IllegalArgumentException("From " + from + " < 0");
        }
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }

    /**
     * Useful parameter checking for both {@link #ids}
     */
    static <K> void checkFromAndToIds(final K from, final int count) {
        Objects.requireNonNull(from, "from");
        if (count < 0) {
            throw new IllegalArgumentException("Count " + count + " < 0");
        }
    }
}
