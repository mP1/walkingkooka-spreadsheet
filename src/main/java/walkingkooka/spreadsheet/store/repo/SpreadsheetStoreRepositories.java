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

package walkingkooka.spreadsheet.store.repo;

import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStores;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStore;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStores;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetCellStore} implementations.
 */
public final class SpreadsheetStoreRepositories implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetStoreRepository}
     */
    public static SpreadsheetStoreRepository fake() {
        return new FakeSpreadsheetStoreRepository();
    }

    /**
     * {@see BasicSpreadsheetStoreRepository}
     */
    public static SpreadsheetStoreRepository basic(final SpreadsheetCellStore cells,
                                                   final SpreadsheetCellReferencesStore cellReferences,
                                                   final SpreadsheetColumnStore columns,
                                                   final SpreadsheetFormStore forms,
                                                   final SpreadsheetGroupStore groups,
                                                   final SpreadsheetLabelStore labels,
                                                   final SpreadsheetLabelReferencesStore labelReferences,
                                                   final SpreadsheetMetadataStore metadatas,
                                                   final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells,
                                                   final SpreadsheetRowStore rows,
                                                   final Storage<StorageExpressionEvaluationContext> storage,
                                                   final SpreadsheetUserStore users) {
        return BasicSpreadsheetStoreRepository.with(
            cells,
            cellReferences,
            columns,
            forms,
            groups,
            labels,
            labelReferences,
            metadatas,
            rangeToCells,
            rows,
            storage,
            users
        );
    }

    /**
     * {@see SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository}
     */
    public static SpreadsheetStoreRepository spreadsheetMetadataAwareSpreadsheetCellStore(final SpreadsheetId id,
                                                                                          final SpreadsheetStoreRepository repository,
                                                                                          final SpreadsheetParserProvider spreadsheetParserProvider,
                                                                                          final LocaleContext localeContext,
                                                                                          final ProviderContext providerContext) {
        return SpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetStoreRepository.with(
            id,
            repository,
            spreadsheetParserProvider,
            localeContext,
            providerContext
        );
    }

    /**
     * {@see BasicSpreadsheetStoreRepository}
     */
    public static SpreadsheetStoreRepository treeMap(final SpreadsheetMetadataStore metadatas) {
        return basic(
            SpreadsheetCellStores.treeMap(),
            SpreadsheetCellReferencesStores.treeMap(),
            SpreadsheetColumnStores.treeMap(),
            SpreadsheetFormStores.treeMap(),
            SpreadsheetGroupStores.treeMap(),
            SpreadsheetLabelStores.treeMap(),
            SpreadsheetLabelReferencesStores.treeMap(),
            metadatas,
            SpreadsheetCellRangeStores.treeMap(),
            SpreadsheetRowStores.treeMap(),
            Storages.tree(),
            SpreadsheetUserStores.treeMap()
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetStoreRepositories() {
        throw new UnsupportedOperationException();
    }
}
