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
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStore;
import walkingkooka.storage.Storage;

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
                                                   final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                                                   final SpreadsheetRowStore rows,
                                                   final Storage storage,
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
            rangeToConditionalFormattingRules,
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
     * Stop creation
     */
    private SpreadsheetStoreRepositories() {
        throw new UnsupportedOperationException();
    }
}
