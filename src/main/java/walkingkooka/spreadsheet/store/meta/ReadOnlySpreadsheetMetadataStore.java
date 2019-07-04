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

package walkingkooka.spreadsheet.store.meta;

import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Wraps another {@link SpreadsheetMetadataStore} and presents a readonly view.
 */
final class ReadOnlySpreadsheetMetadataStore implements SpreadsheetMetadataStore {

    static ReadOnlySpreadsheetMetadataStore with(final SpreadsheetMetadataStore store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetMetadataStore(store);
    }

    private ReadOnlySpreadsheetMetadataStore(SpreadsheetMetadataStore store) {
        this.store = store;
    }

    @Override
    public Optional<SpreadsheetMetadata> load(final SpreadsheetId id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetMetadata save(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetMetadata> saved) {
        Objects.requireNonNull(saved, "saved");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetId> deleted) {
        Objects.requireNonNull(deleted, "deleted");
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetId> ids(final int from, final int count) {
        return this.store.ids(from, count);
    }

    @Override
    public List<SpreadsheetMetadata> values(final SpreadsheetId from, final int count) {
        return this.store.values(from, count);
    }

    private final SpreadsheetMetadataStore store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
