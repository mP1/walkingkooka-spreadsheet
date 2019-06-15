/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
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
