
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

    private final static MediaType MEDIA_TYPE = SpreadsheetMediaTypes.MEMORY_FORM;

    private SpreadsheetStorageForm() {
        super();
    }

    @Override
    Optional<StorageValue> loadNonNull(final StoragePath path,
                                       final SpreadsheetStorageContext context) {
        final List<StorageName> names = path.namesList();

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
                throw path.invalidStoragePathException("Invalid extra path after FormName");
        }

        StorageValue value = null;

        if (null != formName) {
            final Optional<Form<SpreadsheetValidationReference>> form = context.loadForm(formName);
            if (false == form.isEmpty()) {
                value = StorageValue.with(
                    path,
                    Cast.to(form)
                ).setContentType(MEDIA_TYPE);
            }
        }

        return Optional.ofNullable(value);
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
                .orElseThrow(() -> new IllegalArgumentException("Missing Form")),
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
