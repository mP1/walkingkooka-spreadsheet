package walkingkooka.spreadsheet.store;

import org.junit.Test;
import walkingkooka.test.PackagePrivateClassTestCase;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public abstract class StoreTestCase<S extends Store<K, V>, K, V> extends PackagePrivateClassTestCase<S> {
    
    @Test(expected = NullPointerException.class)
    public final void testLoadNullIdFails() {
        this.createStore().load(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testSaveNullFails() {
        this.createStore().save(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteNullFails() {
        this.createStore().delete(null);
    }

    abstract protected S createStore();

    protected final void loadFailCheck(final K id) {
        this.loadFailCheck(this.createStore(), id);
    }

    protected final void loadFailCheck(final S store, final K id) {
        final Optional<V> value = store.load(id);
        assertEquals("Expected id " + id + " to fail", Optional.empty(), value);
    }

    protected final void countAndCheck(final S store, final int count) {
        assertEquals("Wrong count " + store, count, store.count());
    }
}
