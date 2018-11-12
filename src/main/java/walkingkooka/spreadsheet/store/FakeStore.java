package walkingkooka.spreadsheet.store;

import walkingkooka.test.Fake;

import java.util.Optional;

public class FakeStore<K, V> implements Store<K, V>, Fake {

    @Override
    public Optional<V> load(final K id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final K id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        throw new UnsupportedOperationException();
    }
}
