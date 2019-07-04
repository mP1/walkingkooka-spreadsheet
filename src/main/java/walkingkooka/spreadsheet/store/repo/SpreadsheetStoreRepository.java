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
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.meta.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;

/**
 * Holds all {@link SpreadsheetStore stores} in the system
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
