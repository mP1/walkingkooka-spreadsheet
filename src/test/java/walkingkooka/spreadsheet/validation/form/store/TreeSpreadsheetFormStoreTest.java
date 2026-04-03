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
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

public final class TreeSpreadsheetFormStoreTest implements SpreadsheetFormStoreTesting<TreeSpreadsheetFormStore> {

    @Test
    public void testSaveAndLoad() {
        final TreeSpreadsheetFormStore store = this.createStore();

        final Form<SpreadsheetValidationReference> form = this.value();
        store.save(form);

        this.loadAndCheck(
            store,
            form.name(),
            form
        );
    }

    @Test
    public void testFindFormsByName() {
        final TreeSpreadsheetFormStore store = this.createStore();

        final Form<SpreadsheetValidationReference> form1 = Form.with(
            FormName.with("Form1")
        );
        store.save(form1);

        final Form<SpreadsheetValidationReference> form2 = Form.with(
            FormName.with("Form2")
        );
        store.save(form2);

        final Form<SpreadsheetValidationReference> form3 = Form.with(
            FormName.with("Form3")
        );
        store.save(form3);

        final Form<SpreadsheetValidationReference> form4 = Form.with(
            FormName.with("Form4")
        );
        store.save(form4);

        this.findFormsByNameAndCheck(
            store,
            "Form",
            0,
            4,
            form1,
            form2,
            form3,
            form4
        );
    }

    @Test
    public void testFindFormsByNameSkip() {
        final TreeSpreadsheetFormStore store = this.createStore();

        final Form<SpreadsheetValidationReference> form1 = Form.with(
            FormName.with("Form1")
        );
        store.save(form1);

        final Form<SpreadsheetValidationReference> form2 = Form.with(
            FormName.with("Form2")
        );
        store.save(form2);

        final Form<SpreadsheetValidationReference> form3 = Form.with(
            FormName.with("Form3")
        );
        store.save(form3);

        final Form<SpreadsheetValidationReference> form4 = Form.with(
            FormName.with("Form4")
        );
        store.save(form4);

        this.findFormsByNameAndCheck(
            store,
            "Form",
            1,
            4,
            form2,
            form3,
            form4
        );
    }

    @Test
    public void testFindFormsByNameCount() {
        final TreeSpreadsheetFormStore store = this.createStore();

        final Form<SpreadsheetValidationReference> form1 = Form.with(
            FormName.with("Form1")
        );
        store.save(form1);

        final Form<SpreadsheetValidationReference> form2 = Form.with(
            FormName.with("Form2")
        );
        store.save(form2);

        final Form<SpreadsheetValidationReference> form3 = Form.with(
            FormName.with("Form3")
        );
        store.save(form3);

        final Form<SpreadsheetValidationReference> form4 = Form.with(
            FormName.with("Form4")
        );
        store.save(form4);

        this.findFormsByNameAndCheck(
            store,
            "Form",
            0,
            3,
            form1,
            form2,
            form3
        );
    }

    @Test
    public void testFindFormsByNameOffsetAndCount() {
        final TreeSpreadsheetFormStore store = this.createStore();

        final Form<SpreadsheetValidationReference> form1 = Form.with(
            FormName.with("Form1")
        );
        store.save(form1);

        final Form<SpreadsheetValidationReference> form2 = Form.with(
            FormName.with("Form2")
        );
        store.save(form2);

        final Form<SpreadsheetValidationReference> form3 = Form.with(
            FormName.with("Form3")
        );
        store.save(form3);

        final Form<SpreadsheetValidationReference> form4 = Form.with(
            FormName.with("Form4")
        );
        store.save(form4);

        this.findFormsByNameAndCheck(
            store,
            "Form",
            1,
            2,
            form2,
            form3
        );
    }

    @Test
    public void testFindFormsByNameFiltered() {
        final TreeSpreadsheetFormStore store = this.createStore();

        final Form<SpreadsheetValidationReference> form1 = Form.with(
            FormName.with("Different1")
        );
        store.save(form1);

        final Form<SpreadsheetValidationReference> form2 = Form.with(
            FormName.with("Form2")
        );
        store.save(form2);

        final Form<SpreadsheetValidationReference> form3 = Form.with(
            FormName.with("Form3")
        );
        store.save(form3);

        final Form<SpreadsheetValidationReference> form4 = Form.with(
            FormName.with("Form4")
        );
        store.save(form4);

        this.findFormsByNameAndCheck(
            store,
            "Form",
            0,
            2,
            form2,
            form3
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
    public Form<SpreadsheetValidationReference> value() {
        return Form.with(this.id());
    }

    // class............................................................................................................

    @Override
    public Class<TreeSpreadsheetFormStore> type() {
        return TreeSpreadsheetFormStore.class;
    }
}
