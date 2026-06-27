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

package walkingkooka.spreadsheet.storage;

import walkingkooka.convert.ConverterLike;
import walkingkooka.convert.ConverterLikeDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageContextDelegator;
import walkingkooka.storage.StoragePath;
import walkingkooka.store.StoreWatcher;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.Optional;
import java.util.Set;

public interface SpreadsheetStorageContextDelegator extends SpreadsheetStorageContext,
    StorageContextDelegator,
    SpreadsheetEnvironmentContextDelegator,
    SpreadsheetMetadataContextDelegator,
    ConverterLikeDelegator {

    // ConverterLikeDelegator...........................................................................................

    @Override
    default ConverterLike converterLike() {
        return this.spreadsheetStorageContext();
    }

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    default SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetStorageContext();
    }

    // SpreadsheetMetadataContextDelegator..............................................................................

    @Override
    default SpreadsheetMetadataContext spreadsheetMetadataContext() {
        return this.spreadsheetStorageContext();
    }

    // SpreadsheetStorageContextDelegator...............................................................................

    @Override
    default StoragePath parseStoragePath(final String text) {
        return SpreadsheetStorageContext.super.parseStoragePath(text);
    }

    @Override
    default Set<SpreadsheetCell> loadCells(final SpreadsheetExpressionReference cellsOrLabel) {
        return this.spreadsheetStorageContext()
            .loadCells(cellsOrLabel);
    }

    @Override
    default Set<SpreadsheetCell> saveCells(final Set<SpreadsheetCell> cells) {
        return this.spreadsheetStorageContext()
            .saveCells(cells);
    }

    @Override
    default void deleteCells(final SpreadsheetExpressionReference cellsOrLabel) {
        this.spreadsheetStorageContext()
            .deleteCells(cellsOrLabel);
    }

    @Override
    default Runnable addCellWatcher(final StoreWatcher<SpreadsheetCell> watcher) {
        return this.spreadsheetStorageContext()
            .addCellWatcher(watcher);
    }

    @Override
    default Runnable addCellWatcherOnce(final StoreWatcher<SpreadsheetCell> watcher) {
        return this.spreadsheetStorageContext()
            .addCellWatcher(watcher);
    }

    @Override
    default Optional<Form<SpreadsheetValidationReference>> loadForm(final FormName formName) {
        return this.spreadsheetStorageContext()
            .loadForm(formName);
    }

    @Override
    default Form<SpreadsheetValidationReference> saveForm(final Form<SpreadsheetValidationReference> form) {
        return this.spreadsheetStorageContext()
            .saveForm(form);
    }

    @Override
    default void deleteForm(final FormName formName) {
        this.spreadsheetStorageContext()
            .deleteForm(formName);
    }

    @Override
    default Set<Form<SpreadsheetValidationReference>> findFormsByName(final String formName,
                                                                      final int offset,
                                                                      final int count) {
        return this.spreadsheetStorageContext()
            .findFormsByName(
                formName,
                offset,
                count
            );
    }

    @Override
    default Runnable addFormWatcher(final StoreWatcher<Form<SpreadsheetValidationReference>> watcher) {
        return this.spreadsheetStorageContext()
            .addFormWatcher(watcher);
    }

    @Override
    default Runnable addFormWatcherOnce(final StoreWatcher<Form<SpreadsheetValidationReference>> watcher) {
        return this.spreadsheetStorageContext()
            .addFormWatcherOnce(watcher);
    }

    @Override
    default Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetStorageContext()
            .loadLabel(labelName);
    }

    @Override
    default SpreadsheetLabelMapping saveLabel(final SpreadsheetLabelMapping label) {
        return this.spreadsheetStorageContext()
            .saveLabel(label);
    }

    @Override
    default void deleteLabel(final SpreadsheetLabelName labelName) {
        this.spreadsheetStorageContext()
            .deleteLabel(labelName);
    }

    @Override
    default Set<SpreadsheetLabelName> findLabelsByName(final String labelName,
                                                       final int offset,
                                                       final int count) {
        return this.spreadsheetStorageContext()
            .findLabelsByName(
                labelName,
                offset,
                count
            );
    }

    @Override
    default Runnable addLabelWatcher(final StoreWatcher<SpreadsheetLabelMapping> watcher) {
        return this.spreadsheetStorageContext()
            .addLabelWatcher(watcher);
    }

    @Override
    default Runnable addLabelWatcherOnce(final StoreWatcher<SpreadsheetLabelMapping> watcher) {
        return this.spreadsheetStorageContext()
            .addLabelWatcherOnce(watcher);
    }

    @Override
    default EnvironmentContext environmentContext() {
        return SpreadsheetEnvironmentContextDelegator.super.environmentContext();
    }

    @Override
    SpreadsheetStorageContext cloneEnvironment();

    @Override
    SpreadsheetStorageContext setEnvironmentContext(EnvironmentContext environmentContext);

    @Override
    default StorageContext storageContext() {
        return this.spreadsheetStorageContext();
    }

    SpreadsheetStorageContext spreadsheetStorageContext();
}
