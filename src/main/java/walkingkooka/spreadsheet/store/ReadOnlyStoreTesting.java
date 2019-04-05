package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStore;
import walkingkooka.test.TypeNameTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Stores tests that are readonly must also implement this interface(mixin).
 */
public interface ReadOnlyStoreTesting<S extends Store<K, V>, K, V> extends StoreTesting<S, K, V>,
        TypeNameTesting<S> {

    @Test
    default void testSaveFails() {
        final V value = this.value();

        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().save(value);
        });
    }

    @Test
    default void testAddSaveWatcherAndRemoveFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().addSaveWatcher((a) -> {
            });
        });
    }

    @Test
    default void testDeleteFails() {
        final K id = this.id();

        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().delete(id);
        });
    }

    @Test
    default void testAddDeleteWatcherAndRemoveFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().addDeleteWatcher((a) -> {
            });
        });
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNamePrefix() {
        return "ReadOnly";
    }
}
