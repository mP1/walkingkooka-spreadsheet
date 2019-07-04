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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.Watchers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    }

    @Override
    public Optional<SpreadsheetMetadata> load(final SpreadsheetId id) {
        Objects.requireNonNull(id, "ids");
        return Optional.ofNullable(this.metadatas.get(id));
    }

    @Override
    public SpreadsheetMetadata save(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        final SpreadsheetId key = metadata.get(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                .orElseThrow(() -> new SpreadsheetMetadataStoreException("Metadata missing required " + SpreadsheetMetadataPropertyName.SPREADSHEET_ID + "=" + metadata));
        if (false == metadata.equals(this.metadatas.put(key, metadata))) {
            this.saveWatchers.accept(metadata);
        }

        return metadata;
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetMetadata> saved) {
        return this.saveWatchers.addWatcher(saved);
    }

    private final Watchers<SpreadsheetMetadata> saveWatchers = Watchers.create();

    @Override
    public void delete(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        if (null != this.metadatas.remove(id)) {
            this.deleteWatchers.accept(id);
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetId> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<SpreadsheetId> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.metadatas.size();
    }

    @Override
    public Set<SpreadsheetId> ids(final int from,
                                  final int count) {
        Store.checkFromAndTo(from, count);

        return this.metadatas.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    /**
     * Find the first metadata at or after the from {@link SpreadsheetId} and then gather the required count.
     */
    @Override
    public List<SpreadsheetMetadata> values(final SpreadsheetId from,
                                            final int count) {
        Store.checkFromAndToIds(from, count);

        return this.metadatas.entrySet()
                .stream()
                .filter(e -> e.getKey().compareTo(from) >= 0)
                .map(e -> e.getValue())
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    /**
     * All metadatas present in this spreadsheet
     */
    private final Map<SpreadsheetId, SpreadsheetMetadata> metadatas = Maps.sorted();

    @Override
    public String toString() {
        return this.metadatas.values().toString();
    }
}
