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
import walkingkooka.test.Fake;

public class FakeSpreadsheetStoreRepository implements SpreadsheetStoreRepository, Fake {

    protected FakeSpreadsheetStoreRepository() {
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
