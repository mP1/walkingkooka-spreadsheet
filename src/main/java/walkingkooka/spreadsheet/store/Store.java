package walkingkooka.spreadsheet.store;

import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

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
     * Adds a watcher that will receive values after a save.<br>
     * Note the watcher will only be fired when new or different values are saved. Saving the same value twice in succession
     * should only fire the first time, ignoring the second because it is identical.
     */
    Runnable addSaveWatcher(final Consumer<V> saved);

    /**
     * Deletes a single value by id.
     */
    void delete(final K id);

    /**
     * Adds a watcher that will receive values after a deleted.
     */
    Runnable addDeleteWatcher(final Consumer<K> deleted);

    /**
     * Returns the total number of records in the store.
     */
    int count();

    /**
     * Returns a view of all ids between the positional range.
     */
    Set<K> ids(final int from, final int count);

    /**
     * Returns the first id or an {@link Optional#empty()}.
     */
    default Optional<K> firstId() {
        final Set<K> first = this.ids(0, 1);
        return first.stream().findFirst();
    }

    /**
     * Returns a view of all values between the range of ids.
     */
    List<V> values(final K from, final int count);

    /**
     * Fetches the first value if one is present.
     */
    default Optional<V> firstValue() {
        V value = null;

        final Optional<K> id = this.firstId();
        if (id.isPresent()) {

            final List<V> values = this.values(id.get(), 1);
            if (values.size() > 0) {
                value = values.iterator().next();
            }
        }

        return Optional.ofNullable(value);
    }

    /**
     * Returns all values in this store.
     */
    default List<V> all() {
        return this.firstId()
                .map((i) -> this.values(i, Integer.MAX_VALUE))
                .orElse(Lists.empty());
    }

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
