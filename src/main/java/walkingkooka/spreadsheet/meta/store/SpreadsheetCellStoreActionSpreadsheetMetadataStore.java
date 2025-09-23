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
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStoreAction;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link SpreadsheetMetadataStore} that watches each save {@link SpreadsheetMetadata} and determines if the cells
 * belonging to the matching spreadsheet need to be cleared in some way. For example if the {@link SpreadsheetMetadataPropertyName#NUMBER_FORMATTER}
 * changes, then all cells need to be reformatted.
 */
final class SpreadsheetCellStoreActionSpreadsheetMetadataStore implements SpreadsheetMetadataStore {

    static SpreadsheetCellStoreActionSpreadsheetMetadataStore with(final SpreadsheetMetadataStore metadataStore,
                                                                   final Function<SpreadsheetId, SpreadsheetCellStore> cellStore) {
        Objects.requireNonNull(metadataStore, "metadataStore");
        Objects.requireNonNull(cellStore, "cellStore");

        return new SpreadsheetCellStoreActionSpreadsheetMetadataStore(
            metadataStore,
            cellStore
        );
    }

    private SpreadsheetCellStoreActionSpreadsheetMetadataStore(final SpreadsheetMetadataStore metadataStore,
                                                               final Function<SpreadsheetId, SpreadsheetCellStore> cellStore) {
        super();
        this.metadataStore = metadataStore;
        this.cellStore = cellStore;
    }

    @Override
    public Optional<SpreadsheetMetadata> load(final SpreadsheetId spreadsheetId) {
        return this.metadataStore.load(spreadsheetId);
    }

    @Override
    public SpreadsheetMetadata save(final SpreadsheetMetadata metadata) {
        final SpreadsheetMetadataStore metadataStore = this.metadataStore;
        final Optional<SpreadsheetId> maybeId = metadata.id();

        final SpreadsheetMetadata saved;
        if (maybeId.isPresent()) {
            final SpreadsheetId id = maybeId.get();
            final Optional<SpreadsheetMetadata> maybeBefore = metadataStore.load(id);

            saved = metadataStore.save(metadata);

            if (maybeBefore.isPresent()) {
                final Map<SpreadsheetMetadataPropertyName<?>, Object> beforeMap = maybeBefore.get()
                    .value();
                final Map<SpreadsheetMetadataPropertyName<?>, Object> afterMap = metadata.value();

                SpreadsheetCellStoreAction action = SpreadsheetCellStoreAction.NONE;

                for (final Map.Entry<SpreadsheetMetadataPropertyName<?>, Object> nameAndValue : beforeMap.entrySet()) {
                    final SpreadsheetMetadataPropertyName<?> name = nameAndValue.getKey();
                    final Object value = nameAndValue.getValue();

                    final Object otherValue = afterMap.get(name);
                    if (false == value.equals(otherValue)) {
                        action = action.max(name.spreadsheetCellStoreAction());

                        if (action == SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT) {
                            break;
                        }
                    }
                }

                if (action != SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT) {
                    for (final Map.Entry<SpreadsheetMetadataPropertyName<?>, Object> nameAndValue : afterMap.entrySet()) {
                        final SpreadsheetMetadataPropertyName<?> name = nameAndValue.getKey();
                        final Object value = nameAndValue.getValue();

                        final Object otherValue = beforeMap.get(name);
                        if (false == value.equals(otherValue)) {
                            action = action.max(name.spreadsheetCellStoreAction());

                            if (action == SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT) {
                                break;
                            }
                        }
                    }
                }

                switch (action) {
                    case NONE:
                        break;
                    case PARSE_FORMULA:
                        this.cellStore.apply(id)
                            .clearParsedFormulaExpressions();
                        break;
                    case EVALUATE_AND_FORMAT:
                        this.cellStore.apply(id)
                            .clearFormatted();
                        break;
                }
            }

        } else {
            saved = metadataStore.save(metadata);
        }

        return saved;
    }

    /**
     * Holds the cells for the same spreadsheet.
     */
    private final Function<SpreadsheetId, SpreadsheetCellStore> cellStore;

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetMetadata> watcher) {
        return this.metadataStore.addSaveWatcher(watcher);
    }

    @Override
    public void delete(final SpreadsheetId spreadsheetId) {
        this.metadataStore.delete(spreadsheetId);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetId> watcher) {
        return this.metadataStore.addDeleteWatcher(watcher);
    }

    @Override
    public int count() {
        return this.metadataStore.count();
    }

    @Override
    public Set<SpreadsheetId> ids(final int offset,
                                  final int count) {
        return this.metadataStore.ids(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetMetadata> values(final int offset,
                                            final int count) {
        return this.metadataStore.values(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetMetadata> between(final SpreadsheetId from,
                                             final SpreadsheetId to) {
        return this.metadataStore.between(
            from,
            to
        );
    }

    @Override
    public List<SpreadsheetMetadata> findByName(final String name,
                                                final int offset,
                                                final int count) {
        return this.metadataStore.findByName(
            name,
            offset,
            count
        );
    }

    private final SpreadsheetMetadataStore metadataStore;

    @Override
    public String toString() {
        return this.metadataStore.toString();
    }
}
