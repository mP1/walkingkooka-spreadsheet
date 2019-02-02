package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class StoreTestCase<S extends Store<K, V>, K, V> extends ClassTestCase<S> {
    
    @Test
    public final void testLoadNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().load(null);
        });
    }

    @Test
    public final void testSaveNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().save(null);
        });
    }

    @Test
    public final void testDeleteNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().delete(null);
        });
    }

    abstract protected S createStore();

    protected final void loadFailCheck(final K id) {
        this.loadFailCheck(this.createStore(), id);
    }

    protected final void loadFailCheck(final S store, final K id) {
        final Optional<V> value = store.load(id);
        assertEquals(Optional.empty(), value, ()-> "Expected id " + id + " to fail");
    }

    protected final void countAndCheck(final S store, final int count) {
        assertEquals(count, store.count(), ()-> "Wrong count " + store);
    }

    @Override
    protected final MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
