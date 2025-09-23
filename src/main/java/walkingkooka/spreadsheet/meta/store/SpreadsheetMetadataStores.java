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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;

import java.util.function.Function;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetMetadataStore} implementations.
 */
public final class SpreadsheetMetadataStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetMetadataStore}
     */
    public static SpreadsheetMetadataStore fake() {
        return new FakeSpreadsheetMetadataStore();
    }

    /**
     * {@see ReadOnlySpreadsheetMetadataStore}
     */
    public static SpreadsheetMetadataStore readOnly(final SpreadsheetMetadataStore store) {
        return ReadOnlySpreadsheetMetadataStore.with(store);
    }

    /**
     * {@see SpreadsheetCellStoreActionSpreadsheetMetadataStore}
     */
    public static SpreadsheetMetadataStore spreadsheetCellStoreAction(final SpreadsheetMetadataStore metadataStore,
                                                                      final Function<SpreadsheetId, SpreadsheetCellStore> cellStore) {
        return SpreadsheetCellStoreActionSpreadsheetMetadataStore.with(
            metadataStore,
            cellStore
        );
    }

    /**
     * {@see TreeMapSpreadsheetMetadataStore}
     */
    public static SpreadsheetMetadataStore treeMap() {
        return TreeMapSpreadsheetMetadataStore.empty();
    }

    /**
     * Stop creation
     */
    private SpreadsheetMetadataStores() {
        throw new UnsupportedOperationException();
    }
}
