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
import walkingkooka.spreadsheet.store.SpreadsheetLabelReferencesStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.validation.form.store.SpreadsheetFormStore;
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
    public SpreadsheetCellReferencesStore cellReferences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetColumnStore columns() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormStore forms() {
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
    public SpreadsheetLabelReferencesStore labelReferences() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadataStore metadatas() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetRowStore rows() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetUserStore users() {
        throw new UnsupportedOperationException();
    }
}
