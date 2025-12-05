
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

import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * A {@link SpreadsheetColumnStore} that uses a {@link TreeMap}.
 */
final class TreeMapSpreadsheetColumnStore implements SpreadsheetColumnStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetColumnStore}
     */
    static TreeMapSpreadsheetColumnStore create() {
        return new TreeMapSpreadsheetColumnStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetColumnStore() {
        super();
        this.store = Stores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetColumnStore::idSetter);
    }

    private static SpreadsheetColumn idSetter(final SpreadsheetColumnReference id,
                                              final SpreadsheetColumn SpreadsheetColumn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetColumn> load(final SpreadsheetColumnReference id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetColumn save(final SpreadsheetColumn SpreadsheetColumn) {
        return this.store.save(SpreadsheetColumn);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetColumn> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final SpreadsheetColumnReference id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetColumnReference> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetColumnReference> ids(final int offset,
                                               final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetColumn> values(final int offset,
                                          final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetColumn> between(final SpreadsheetColumnReference from,
                                           final SpreadsheetColumnReference to) {
        return this.store.between(
            from,
            to
        );
    }

    // VisibleForTesting
    private final Store<SpreadsheetColumnReference, SpreadsheetColumn> store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof TreeMapSpreadsheetColumnStore &&
                this.equals0((TreeMapSpreadsheetColumnStore) other));
    }

    private boolean equals0(final TreeMapSpreadsheetColumnStore other) {
        return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}
