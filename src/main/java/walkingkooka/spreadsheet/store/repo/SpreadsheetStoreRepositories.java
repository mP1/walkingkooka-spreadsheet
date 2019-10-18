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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;

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
                                                   final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences,
                                                   final SpreadsheetGroupStore groups,
                                                   final SpreadsheetLabelStore labels,
                                                   final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences,
                                                   final SpreadsheetMetadataStore metadatas,
                                                   final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells,
                                                   final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                                                   final SpreadsheetUserStore users) {
        return BasicSpreadsheetStoreRepository.with(cells,
                cellReferences,
                groups,
                labels,
                labelReferences,
                metadatas,
                rangeToCells,
                rangeToConditionalFormattingRules,
                users);
    }

    /**
     * Stop creation
     */
    private SpreadsheetStoreRepositories() {
        throw new UnsupportedOperationException();
    }
}
