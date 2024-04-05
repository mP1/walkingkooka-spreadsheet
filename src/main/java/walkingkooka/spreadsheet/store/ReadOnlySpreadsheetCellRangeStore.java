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

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

final class ReadOnlySpreadsheetCellRangeStore<V> implements SpreadsheetCellRangeStore<V> {

    static <V> ReadOnlySpreadsheetCellRangeStore<V> with(final SpreadsheetCellRangeStore<V> store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetCellRangeStore<>(store);
    }

    private ReadOnlySpreadsheetCellRangeStore(final SpreadsheetCellRangeStore<V> store) {
        this.store = store;
    }

    public Optional<List<V>> load(final SpreadsheetCellRangeReference id) {
        return store.load(id);
    }

    public List<V> save(final List<V> value) {
        Objects.requireNonNull(value, "value");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<List<V>> saved) {
        Objects.requireNonNull(saved, "saved");
        throw new UnsupportedOperationException();
    }

    public void delete(final SpreadsheetCellRangeReference id) {
        Objects.requireNonNull(id, "id");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetCellRangeReference> deleted) {
        Objects.requireNonNull(deleted, "saved");
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        return store.count();
    }

    @Override
    public Set<SpreadsheetCellRangeReference> ids(final int from, final int count) {
        return store.ids(from, count);
    }

    @Override
    public List<List<V>> values(final int from,
                                final int count) {
        return store.values(from, count);
    }

    @Override
    public List<List<V>> between(final SpreadsheetCellRangeReference from,
                                 final SpreadsheetCellRangeReference to) {
        return store.between(
                from,
                to
        );
    }

    @Override
    public Set<SpreadsheetCellRangeReference> loadCellRangeReferences(final SpreadsheetCellReference cell) {
        return store.loadCellRangeReferences(cell);
    }

    @Override
    public Set<V> loadCellReferenceValues(final SpreadsheetCellReference cell) {
        return store.loadCellReferenceValues(cell);
    }

    @Override
    public void addValue(final SpreadsheetCellRangeReference range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replaceValue(final SpreadsheetCellRangeReference range, final V newValue, final V oldValue) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(newValue, "newValue");
        Objects.requireNonNull(oldValue, "oldValue");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue(final SpreadsheetCellRangeReference range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCellRangeReference> rangesWithValue(final V value) {
        Objects.requireNonNull(value, "value");

        return this.store.rangesWithValue(value);
    }

    private final SpreadsheetCellRangeStore<V> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
