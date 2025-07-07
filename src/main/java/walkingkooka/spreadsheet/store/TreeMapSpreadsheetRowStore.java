
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

import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * A {@link SpreadsheetRowStore} that uses a {@link TreeMap}.
 */
final class TreeMapSpreadsheetRowStore implements SpreadsheetRowStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetRowStore}
     */
    static TreeMapSpreadsheetRowStore create() {
        return new TreeMapSpreadsheetRowStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetRowStore() {
        super();
        this.store = Stores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetRowStore::idSetter);
    }

    private static SpreadsheetRow idSetter(final SpreadsheetRowReference id,
                                           final SpreadsheetRow SpreadsheetRow) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRow> load(final SpreadsheetRowReference id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetRow save(final SpreadsheetRow SpreadsheetRow) {
        return this.store.save(SpreadsheetRow);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetRow> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final SpreadsheetRowReference id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetRowReference> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetRowReference> ids(final int offset,
                                            final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetRow> values(final int offset,
                                       final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetRow> between(final SpreadsheetRowReference from,
                                        final SpreadsheetRowReference to) {
        return this.store.between(
            from,
            to
        );
    }

    // VisibleForTesting
    private final Store<SpreadsheetRowReference, SpreadsheetRow> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
