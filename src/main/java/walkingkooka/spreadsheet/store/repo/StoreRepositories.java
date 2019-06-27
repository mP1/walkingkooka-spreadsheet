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

import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;
import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetCellStore} implementations.
 */
public final class StoreRepositories implements PublicStaticHelper {

    /**
     * {@see FakeStoreRepository}
     */
    public static StoreRepository fake() {
        return new FakeStoreRepository();
    }

    /**
     * {@see BasicStoreRepository}
     */
    public static StoreRepository basic(final SpreadsheetCellStore cells,
                                        final SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences,
                                        final SpreadsheetGroupStore groups,
                                        final SpreadsheetLabelStore labels,
                                        final SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences,
                                        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells,
                                        final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules,
                                        final SpreadsheetUserStore users) {
        return BasicStoreRepository.with(cells,
                cellReferences,
                groups,
                labels,
                labelReferences,
                rangeToCells,
                rangeToConditionalFormattingRules,
                users);
    }

    /**
     * Stop creation
     */
    private StoreRepositories() {
        throw new UnsupportedOperationException();
    }
}
