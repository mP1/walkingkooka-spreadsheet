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

import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableList;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;
import walkingkooka.text.CaseSensitivity;

import java.util.Comparator;
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
    static TreeMapSpreadsheetMetadataStore empty() {
        return new TreeMapSpreadsheetMetadataStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetMetadataStore() {
        super();

        this.store = Stores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetMetadataStore::idSetter);

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
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");

        final Set<SpreadsheetMetadataPropertyName<?>> missing = spreadsheetMetadata.missingRequiredProperties();
        if (false == missing.isEmpty()) {
            throw new IllegalArgumentException(
                "Metadata missing required properties: " +
                    missing.stream()
                        .map(SpreadsheetMetadataPropertyName::toString)
                        .collect(Collectors.joining(", "))
            );
        }

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
    public Set<SpreadsheetId> ids(final int offset,
                                  final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetMetadata> values(final int offset,
                                            final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetMetadata> between(final SpreadsheetId from,
                                             final SpreadsheetId to) {
        return this.store.between(
            from,
            to
        );
    }

    @Override
    public List<SpreadsheetMetadata> findByName(final String name,
                                                final int offset,
                                                final int count) {
        Objects.requireNonNull(name, "name");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        return this.store.all()
            .stream()
            .filter(m -> m.name()
                .map(n -> CaseSensitivity.INSENSITIVE.startsWith(
                        n.value(),
                        name
                    )
                ).orElse(false)
            ).skip(offset)
            .limit(count)
            .collect(ImmutableList.collector());
    }

    private final Store<SpreadsheetId, SpreadsheetMetadata> store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof TreeMapSpreadsheetMetadataStore &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final TreeMapSpreadsheetMetadataStore other) {
        return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}
