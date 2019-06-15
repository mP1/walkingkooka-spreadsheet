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
