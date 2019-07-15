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

package walkingkooka.spreadsheet.meta.store;

import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.SpreadsheetStores;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link SpreadsheetMetadataStore} that uses a {@link Map}.
 */
final class TreeMapSpreadsheetMetadataStore implements SpreadsheetMetadataStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetMetadataStore}
     */
    static TreeMapSpreadsheetMetadataStore create() {
        return new TreeMapSpreadsheetMetadataStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetMetadataStore() {
        super();

        this.store = SpreadsheetStores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetMetadataStore::idSetter);
    }

    private static SpreadsheetMetadata idSetter(final SpreadsheetId id,
                                                final SpreadsheetMetadata spreadsheetMetadata) {
        return spreadsheetMetadata.set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SpreadsheetId.with(null == id ? 1 : id.value() + 1));
    }

    @Override
    public Optional<SpreadsheetMetadata> load(final SpreadsheetId id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetMetadata save(final SpreadsheetMetadata spreadsheetMetadata) {
        return this.store.save(spreadsheetMetadata);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetMetadata> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final SpreadsheetId id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetId> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetId> ids(final int from,
                                  final int count) {
        return this.store.ids(from, count);
    }

    @Override
    public List<SpreadsheetMetadata> values(final SpreadsheetId from,
                                            final int count) {
        return this.store.values(from, count);
    }

    final SpreadsheetStore<SpreadsheetId, SpreadsheetMetadata> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
