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

import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.security.store.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.security.store.SpreadsheetUserStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStore;
import walkingkooka.storage.Storage;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
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
     * A {@link SpreadsheetExpressionReferenceStore} holding cell references.
     */
    SpreadsheetCellReferencesStore cellReferences();

    /**
     * A {@link SpreadsheetColumnStore} holding columns.
     */
    SpreadsheetColumnStore columns();

    /**
     * A {@link SpreadsheetFormStore} holding forms
     */
    SpreadsheetFormStore forms();

    /**
     * A {@link SpreadsheetGroupStore} holding groups.
     */
    SpreadsheetGroupStore groups();

    /**
     * A {@link SpreadsheetLabelStore} holding labels.
     */
    SpreadsheetLabelStore labels();

    /**
     * A {@link SpreadsheetLabelReferencesStore} references for a single label.
     */
    SpreadsheetLabelReferencesStore labelReferences();

    /**
     * A {@link SpreadsheetMetadataStore} holding all {@link SpreadsheetMetadataStore}.
     */
    SpreadsheetMetadataStore metadatas();

    /**
     * A {@link SpreadsheetCellRangeStore} that maps ranges to cells
     */
    SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells();

    /**
     * A {@link SpreadsheetRowStore} holding rows.
     */
    SpreadsheetRowStore rows();

    /**
     * Returns a {@link Storage}
     */
    Storage<StorageExpressionEvaluationContext> storage();

    /**
     * A {@link SpreadsheetUserStore} holding users.
     */
    SpreadsheetUserStore users();
}
