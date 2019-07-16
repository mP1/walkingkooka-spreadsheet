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
import walkingkooka.store.Store;

/**
 * Holds all {@link Store stores} in the system
 */
public interface SpreadsheetStoreRepository {

    /**
     * A {@link SpreadsheetCellStore} holding cells.
     */
    SpreadsheetCellStore cells();

    /**
     * A {@link SpreadsheetReferenceStore} holding cell references.
     */
    SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences();

    /**
     * A {@link SpreadsheetGroupStore} holding groups.
     */
    SpreadsheetGroupStore groups();

    /**
     * A {@link SpreadsheetLabelStore} holding labels.
     */
    SpreadsheetLabelStore labels();

    /**
     * A {@link SpreadsheetReferenceStore} holding label references.
     */
    SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences();

    /**
     * A {@link SpreadsheetMetadataStore} holding all {@link SpreadsheetMetadataStore}.
     */
    SpreadsheetMetadataStore metadatas();

    /**
     * A {@lin SpreadsheetRangeStore} that maps ranges to cells
     */
    SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells();

    /**
     * A {@lin SpreadsheetRangeStore} that maps ranges to {@link SpreadsheetConditionalFormattingRule}
     */
    SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules();

    /**
     * A {@link SpreadsheetUserStore} holding users.
     */
    SpreadsheetUserStore users();
}
