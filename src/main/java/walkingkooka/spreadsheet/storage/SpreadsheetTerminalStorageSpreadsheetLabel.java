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

import walkingkooka.collect.list.ImmutableList;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageName;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Storage} that maps {@link SpreadsheetLabelMapping} to a {@link Storage}, for the current spreadsheet.
 * <pre>
 * /label/SpreadsheetLabelName
 * </pre>
 * for the {@link StorageValue}.
 */
final class SpreadsheetTerminalStorageSpreadsheetLabel extends SpreadsheetTerminalStorage {

    /**
     * Singleton
     */
    final static SpreadsheetTerminalStorageSpreadsheetLabel INSTANCE = new SpreadsheetTerminalStorageSpreadsheetLabel();

    private final static MediaType MEDIA_TYPE = SpreadsheetMediaTypes.MEMORY_LABEL;

    private SpreadsheetTerminalStorageSpreadsheetLabel() {
        super();
    }

    @Override
    Optional<StorageValue> loadNonNull(final StoragePath path,
                                       final SpreadsheetStorageContext context) {
        StorageValue value = null;

        final List<StorageName> names = path.namesList();

        SpreadsheetLabelName labelName = null;

        switch (names.size()) {
            case 2:
                labelName = parseLabel(
                    names.get(1)
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid path after label name");
        }

        if (null != labelName) {
            final SpreadsheetLabelMapping mapping = context.loadLabel(labelName)
                .orElse(null);

            if (null != mapping) {
                value = StorageValue.with(
                    path,
                    Optional.of(
                        mapping
                    )
                ).setContentType(MEDIA_TYPE);
            }
        }

        return Optional.ofNullable(value);
    }

    @Override
    StorageValue saveNonNull(final StorageValue value,
                             final SpreadsheetStorageContext context) {
        final List<StorageName> names = value.path()
            .namesList();
        switch (names.size()) {
            case 0:
            case 1:
                throw new IllegalArgumentException("Missing label");
            case 2:
                SpreadsheetLabelMapping labelMapping = context.convertOrFail(
                    value.value()
                        .orElseThrow(() -> new IllegalArgumentException("Missing " + SpreadsheetLabelMapping.class.getSimpleName())),
                    SpreadsheetLabelMapping.class
                );

                final SpreadsheetLabelMapping saved = context.saveLabel(labelMapping);

                return value.setValue(
                    Optional.of(saved)
                ).setContentType(MEDIA_TYPE);
            default:
                throw new IllegalArgumentException("Invalid path after label");
        }
    }

    @Override
    void deleteNonNull(final StoragePath path,
                       final SpreadsheetStorageContext context) {
        final List<StorageName> names = path.namesList();
        switch (names.size()) {
            case 0:
            case 1:
                throw new IllegalArgumentException("Missing label");
            case 2:
                context.deleteLabel(
                    parseLabel(
                        names.get(1)
                    )
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid path after label");
        }
    }

    @Override
    List<StorageValueInfo> listNonNull(final StoragePath path,
                                       final int offset,
                                       final int count,
                                       final SpreadsheetStorageContext context) {
        final List<StorageName> names = path.namesList();

        final String labelName;

        switch (names.size()) {
            case 0:
            case 1:
                labelName = "";
                break;
            case 2:
                labelName = names.get(1)
                    .value();
                break;
            default:
                throw new IllegalArgumentException("Invalid path after label");
        }

        return context.findLabelsByName(
                labelName,
                offset,
                count
            ).stream()
            .map(
                (SpreadsheetLabelName l) -> StorageValueInfo.with(
                    StoragePath.ROOT.append(
                        StorageName.with(
                            l.text()
                        )
                    ),
                    context.createdAuditInfo()
                )
            ).collect(ImmutableList.collector());
    }

    private static SpreadsheetLabelName parseLabel(final StorageName name) {
        return SpreadsheetSelection.labelName(
            name.value()
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetCellStore.class.getSimpleName();
    }
}
