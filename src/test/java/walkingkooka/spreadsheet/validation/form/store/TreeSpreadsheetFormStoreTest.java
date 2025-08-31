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

package walkingkooka.spreadsheet.validation.form.store;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.form.store.FormStoreTesting;

public final class TreeSpreadsheetFormStoreTest implements FormStoreTesting<TreeSpreadsheetFormStore, SpreadsheetExpressionReference> {

    @Test
    public void testSaveAndLoad() {
        final TreeSpreadsheetFormStore store = this.createStore();

        final Form<SpreadsheetExpressionReference> form = this.value();
        store.save(form);

        this.loadAndCheck(
            store,
            form.name(),
            form
        );
    }

    @Override
    public TreeSpreadsheetFormStore createStore() {
        return TreeSpreadsheetFormStore.empty();
    }

    @Override
    public FormName id() {
        return FormName.with("Hello");
    }

    @Override
    public Form<SpreadsheetExpressionReference> value() {
        return Form.with(this.id());
    }

    // class............................................................................................................

    @Override
    public Class<TreeSpreadsheetFormStore> type() {
        return TreeSpreadsheetFormStore.class;
    }
}
