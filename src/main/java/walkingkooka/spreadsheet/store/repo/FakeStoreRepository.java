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
import walkingkooka.spreadsheet.store.meta.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.reference.SpreadsheetReferenceStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;
import walkingkooka.test.Fake;

public class FakeStoreRepository implements StoreRepository, Fake {

    protected FakeStoreRepository() {
        super();
    }

    @Override
    public SpreadsheetCellStore cells() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetReferenceStore<SpreadsheetCellReference> cellReferences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetGroupStore groups() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetLabelStore labels() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetReferenceStore<SpreadsheetLabelName> labelReferences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadataStore metadatas() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCells() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetUserStore users() {
        throw new UnsupportedOperationException();
    }
}
