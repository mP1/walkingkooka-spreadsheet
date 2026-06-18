
/*
 * Copyright 2025 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableList;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageName;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.StorageWatcher;
import walkingkooka.store.StoreWatcher;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Storage} that maps {@link Form} to paths, assuming the context handles the parent {@link walkingkooka.spreadsheet.meta.SpreadsheetId}.
 * <pre>
 * /form/{@link FormName}
 * </pre>
 * for the {@link StorageValue}.
 */
final class SpreadsheetStorageForm extends SpreadsheetStorage {

    /**
     * Singleton
     */
    final static SpreadsheetStorageForm INSTANCE = new SpreadsheetStorageForm();

    private final static Optional<MediaType> MEDIA_TYPE = Optional.of(SpreadsheetMediaTypes.MEMORY_FORM);

    private SpreadsheetStorageForm() {
        super();
    }

    @Override
    boolean canWriteNonNull(final StoragePath path,
                            final SpreadsheetStorageContext context) {
        boolean writeable;

        try {
            writeable = null != this.toFormName(path);
        } catch (final IllegalArgumentException ignore) {
            writeable = false;
        }

        return writeable;
    }

    @Override
    Optional<StorageValue> loadNonRoot(final StoragePath path,
                                       final SpreadsheetStorageContext context) {
        final FormName formName = toFormName(path);

        StorageValue value = null;

        if (null != formName) {
            final Optional<Form<SpreadsheetValidationReference>> form = context.loadForm(formName);
            if (false == form.isEmpty()) {
                value = StorageValue.with(path)
                    .setValue(
                        Cast.to(form)
                    ).setContentType(MEDIA_TYPE);
            }
        }

        return Optional.ofNullable(value);
    }

    private FormName toFormName(final StoragePath storagePath) {
        final List<StorageName> names = storagePath.namesList();

        final FormName formName;

        switch (names.size()) {
            case 0:
            case 1:
                formName = null;
                break;
            case 2:
                formName = parseFormName(
                    names.get(1)
                );
                break;
            default:
                throw storagePath.invalidStoragePathException("Invalid extra path after FormName");
        }

        return formName;
    }

    @Override
    StorageValue saveNonNull(final StorageValue value,
                             final SpreadsheetStorageContext context) {
        final StoragePath path = value.path();

        switch (path.namesList()
            .size()) {
            case 0:
            case 1:
            case 2:
                break;
            default:
                throw path.invalidStoragePathException("Invalid path after FormName");
        }

        final Form<SpreadsheetValidationReference> form = context.convertOrFail(
            value.value()
                .orElseThrow(
                    () -> value.path().invalidStoragePathException("Missing Form name")
                ),
            Form.class
        );

        return value.setValue(
            Optional.of(
                context.saveForm(form)
            )
        ).setContentType(MEDIA_TYPE);
    }

    /**
     * Deletes the given forms. Note if the path contains additional path components a {@link IllegalArgumentException}
     * will be thrown.
     */
    @Override
    void deleteNonNull(final StoragePath path,
                       final SpreadsheetStorageContext context) {
        final List<StorageName> storageNames = path.namesList();
        switch (storageNames.size()) {
            case 0:
            case 1:
                throw path.invalidStoragePathException("Missing FormName");
            case 2:
                context.deleteForm(
                    parseFormName(
                        storageNames.get(1)
                    )
                );
                break;
            default:
                throw path.invalidStoragePathException("Invalid path after FormName");
        }
    }

    @Override
    List<StorageValueInfo> listNonNull(final StoragePath path,
                                       final int offset,
                                       final int count,
                                       final SpreadsheetStorageContext context) {
        final List<StorageName> storageNames = path.namesList();

        final String formName;

        switch (storageNames.size()) {
            case 0:
            case 1:
                formName = "";
                break;
            case 2:
                formName = storageNames.get(1)
                    .value();
                break;
            default:
                throw path.invalidStoragePathException("Invalid path after FormName");
        }

        return context.findFormsByName(
                formName,
                offset,
                count
            ).stream()
            .map(
                (Form<SpreadsheetValidationReference> f) -> StorageValueInfo.with(
                    StoragePath.ROOT.append(
                        StorageName.with(
                            f.name()
                                .text()
                        )
                    ),
                    context.createdAuditInfo()
                )
            ).collect(ImmutableList.collector());
    }

    // addWatcher.......................................................................................................

    @Override
    Runnable addWatcher0(final StorageWatcher watcher,
                         final SpreadsheetStorageContext context) {
        return context.addFormStoreWatcher(
            this.formStoreWatcher(watcher)
        );
    }

    @Override
    Runnable addWatcherOnce0(final StorageWatcher watcher,
                             final SpreadsheetStorageContext context) {
        return context.addFormStoreWatcherOnce(
            this.formStoreWatcher(watcher)
        );
    }

    private StoreWatcher<Form<SpreadsheetValidationReference>> formStoreWatcher(final StorageWatcher watcher) {
        return new StoreWatcher<>() {
            @Override
            public void onValueChange(final Optional<Form<SpreadsheetValidationReference>> oldValue,
                                      final Optional<Form<SpreadsheetValidationReference>> newValue) {
                watcher.onValueChange(
                    toStorageValue(oldValue),
                    toStorageValue(newValue)
                );
            }
        };
    }

    private static Optional<StorageValue> toStorageValue(final Optional<Form<SpreadsheetValidationReference>> form) {
        return form.map(
            (Form<SpreadsheetValidationReference> f) -> StorageValue.with(
                StoragePath.parse(
                    StoragePath.SEPARATOR.string() +
                        f.name()
                            .text()
                )
            ).setValue(
                Optional.of(f)
            ).setContentType(MEDIA_TYPE)
        );
    }

    // helper...........................................................................................................

    private static FormName parseFormName(final StorageName name) {
        return FormName.with(
            name.withoutFileExtension()
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetStorageForm.class.getSimpleName();
    }
}
