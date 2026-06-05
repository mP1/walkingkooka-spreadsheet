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
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageName;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Storage} that supports CRUD operations for {@link SpreadsheetMetadata}.
 * <pre>
 *  /spreadsheet/SpreadsheetId
 *  /spreadsheet/123
 * </pre>
 */
final class SpreadsheetStorageSpreadsheetMetadata extends SpreadsheetStorage {

    /**
     * Singleton
     */
    final static SpreadsheetStorageSpreadsheetMetadata INSTANCE = new SpreadsheetStorageSpreadsheetMetadata();

    private final static Optional<MediaType> MEDIA_TYPE = Optional.of(
        SpreadsheetMediaTypes.MEMORY_SPREADSHEET_METADATA
    );

    private SpreadsheetStorageSpreadsheetMetadata() {
        super();
    }

    @Override
    boolean canWriteNonNull(final StoragePath path,
                            final SpreadsheetStorageContext context) {
        this.toSpreadsheetId(path);
        return true;
    }

    /**
     * Loads the {@link SpreadsheetMetadata} with the given {@link SpreadsheetId}.
     * <pre>
     * /spreadsheet/SpreadsheetId
     * </pre>
     */
    @Override
    Optional<StorageValue> loadNonRoot(final StoragePath path,
                                       final SpreadsheetStorageContext context) {
        Optional<StorageValue> loaded = Optional.empty();

        final SpreadsheetId spreadsheetId = this.toSpreadsheetId(path);
        if (null != spreadsheetId) {
            loaded = context.loadMetadata(spreadsheetId)
                .map(m -> StorageValue.with(path)
                    .setValue(
                        Optional.of(m)
                    ).setContentType(MEDIA_TYPE)
                );
        }

        return loaded;
    }

    private SpreadsheetId toSpreadsheetId(final StoragePath path) {
        final List<StorageName> storageNames = path.namesList();

        SpreadsheetId spreadsheetId = null;

        switch (storageNames.size()) {
            case 0:
            case 1:
                spreadsheetId = null;
                break;
            case 2:
                spreadsheetId = parseSpreadsheetId(
                    storageNames.get(1)
                );
                break;
            default:
                throw path.invalidStoragePathException("Invalid path");
        }

        return spreadsheetId;
    }

    @Override
    StorageValue saveNonNull(final StorageValue value,
                             final SpreadsheetStorageContext context) {
        final StoragePath path = value.path();
        final List<StorageName> storageNames = path.namesList();

        switch (storageNames.size()) {
            case 1:
                break;
            case 2:
                throw path.invalidStoragePathException("Invalid path, contains SpreadsheetId");
            default:
                throw path.invalidStoragePathException("Invalid path");
        }

        final SpreadsheetMetadata saved = context.saveMetadata(
            context.convertOrFail(
                value.value()
                    .orElseThrow(() -> path.invalidStoragePathException("Invalid path missing " + SpreadsheetMetadata.class.getSimpleName())),
                SpreadsheetMetadata.class
            )
        );

        return value.setPath(
            StoragePath.ROOT.append(
                StorageName.with(
                    saved.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID).toString()
                )
            )
        ).setValue(
            Optional.of(saved)
        ).setContentType(MEDIA_TYPE);
    }

    /**
     * Deletes the {@link SpreadsheetId} in the path.
     */
    @Override
    void deleteNonNull(final StoragePath path,
                       final SpreadsheetStorageContext context) {
        final List<StorageName> names = path.namesList();
        switch (names.size()) {
            case 0:
            case 1:
                throw path.invalidStoragePathException("Invalid path missing " + SpreadsheetId.class.getSimpleName());
            case 2:
                context.deleteMetadata(
                    parseSpreadsheetId(
                        names.get(1)
                    )
                );
                break;
            default:
                throw path.invalidStoragePathException("Invalid path");
        }
    }

    /**
     * Supports listing using the provided {@link walkingkooka.spreadsheet.meta.SpreadsheetName} pattern.
     */
    @Override
    List<StorageValueInfo> listNonNull(final StoragePath path,
                                       final int offset,
                                       final int count,
                                       final SpreadsheetStorageContext context) {
        final List<StorageName> names = path.namesList();

        final String name;

        switch (names.size()) {
            case 0:
            case 1:
                name = "";
                break;
            case 2:
                name = names.get(1)
                    .value();
                break;
            default:
                throw path.invalidStoragePathException("Invalid path");
        }

        return context.findMetadataBySpreadsheetName(
                name,
                offset,
                count
            ).stream()
            .map(m ->
                StorageValueInfo.with(
                    StoragePath.ROOT.append(
                        StorageName.with(
                            m.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                                .toString()
                        )
                    ),
                    context.createdAuditInfo()
                )
            ).collect(ImmutableList.collector());
    }

    private static SpreadsheetId parseSpreadsheetId(final StorageName name) {
        return SpreadsheetId.parse(
            name.withoutFileExtension()
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetMetadataStore.class.getSimpleName();
    }
}
