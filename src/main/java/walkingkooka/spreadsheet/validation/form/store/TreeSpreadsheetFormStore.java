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

import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.form.store.FormStore;
import walkingkooka.validation.form.store.FormStores;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

final class TreeSpreadsheetFormStore implements SpreadsheetFormStore {

    static TreeSpreadsheetFormStore empty() {
        return new TreeSpreadsheetFormStore();
    }

    private TreeSpreadsheetFormStore() {
        this.store = FormStores.empty();
    }

    @Override
    public Optional<Form<SpreadsheetValidationReference>> load(final FormName formName) {
        return this.store.load(formName);
    }

    @Override
    public Form<SpreadsheetValidationReference> save(final Form<SpreadsheetValidationReference> reference) {
        return this.store.save(reference);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<Form<SpreadsheetValidationReference>> watcher) {
        return this.store.addSaveWatcher(watcher);
    }

    @Override
    public void delete(final FormName formName) {
        store.delete(formName);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<FormName> watcher) {
        return this.store.addDeleteWatcher(watcher);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<FormName> ids(final int offset,
                             final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public Optional<FormName> firstId() {
        return this.store.firstId();
    }

    @Override
    public List<Form<SpreadsheetValidationReference>> values(final int offset,
                                                             final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<Form<SpreadsheetValidationReference>> all() {
        return this.store.all();
    }

    @Override
    public List<Form<SpreadsheetValidationReference>> between(final FormName from,
                                                              final FormName to) {
        return this.store.between(
            from,
            to
        );
    }

    @Override
    public Optional<Form<SpreadsheetValidationReference>> firstValue() {
        return this.store.firstValue();
    }

    private final FormStore<SpreadsheetValidationReference> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
