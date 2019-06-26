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

package walkingkooka.spreadsheet;

import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.store.repo.StoreRepositories;
import walkingkooka.spreadsheet.store.repo.StoreRepository;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStores;

import java.util.Map;
import java.util.Objects;

/**
 * A {@link SpreadsheetContext} that creates a new {@link StoreRepository} for unknown {@link SpreadsheetId}.
 * There is no way to delete existing spreadsheets.
 */
final class MemorySpreadsheetContext implements SpreadsheetContext {

    /**
     * Creates a new empty {@link MemorySpreadsheetContext}
     */
    static MemorySpreadsheetContext create() {
        return new MemorySpreadsheetContext();
    }

    private MemorySpreadsheetContext() {
        super();
    }

    @Override
    public StoreRepository storeRepository(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        StoreRepository storeRepository = this.idToStoreRepository.get(id);
        if (null == storeRepository) {
            storeRepository = this.createStoreRepository();
            this.idToStoreRepository.put(id, storeRepository);
        }
        return storeRepository;
    }

    private StoreRepository createStoreRepository() {
        return StoreRepositories.basic(SpreadsheetCellStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                SpreadsheetGroupStores.treeMap(),
                SpreadsheetLabelStores.treeMap(),
                SpreadsheetReferenceStores.treeMap(),
                SpreadsheetRangeStores.treeMap(),
                SpreadsheetUserStores.treeMap());
    }

    private final Map<SpreadsheetId, StoreRepository> idToStoreRepository = Maps.sorted();

    @Override
    public String toString() {
        return this.idToStoreRepository.toString();
    }
}