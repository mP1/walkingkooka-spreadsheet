package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface StoreTesting<S extends Store<K, V>, K, V> extends ClassTesting2<S> {

    @Test
    default void testLoadNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().load(null);
        });
    }

    @Test
    default void testSaveNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().save(null);
        });
    }

    @Test
    default void testDeleteNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().delete(null);
        });
    }

    S createStore();

    default void loadFailCheck(final K id) {
        this.loadFailCheck(this.createStore(), id);
    }

    default void loadFailCheck(final S store, final K id) {
        final Optional<V> value = store.load(id);
        assertEquals(Optional.empty(), value, () -> "Expected id " + id + " to fail");
    }

    default void countAndCheck(final S store, final int count) {
        assertEquals(count, store.count(), () -> "Wrong count " + store);
    }

    @Override
    default MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
