package walkingkooka.spreadsheet.store;

import walkingkooka.test.Fake;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class FakeStore<K, V> implements Store<K, V>, Fake {

    @Override
    public Optional<V> load(final K id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V save(final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<V> saved) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final K id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<K> deleted) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> ids(final int from, final int count) {
        Store.checkFromAndTo(from, count);
        throw new UnsupportedOperationException();
    }

    @Override
    public List<V> values(final K from, final int count) {
        Store.checkFromAndToIds(from, count);
        throw new UnsupportedOperationException();
    }
}
