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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.Storages;

import java.nio.file.Path;

/**
 * A collection of {@link Storage} for a spreadsheet terminal.
 */
public final class SpreadsheetStorages implements PublicStaticHelper {

    /**
     * {@see SpreadsheetStorageSpreadsheetCell}
     */
    public static Storage<SpreadsheetStorageContext> cell() {
        return SpreadsheetStorageSpreadsheetCell.INSTANCE;
    }

    /**
     * {@see StorageSharedExpandedCurrentWorkingDirectory}
     */
    public static Storage<SpreadsheetStorageContext> currentWorkingDirectory() {
        return Storages.currentWorkingDirectory();
    }

    /**
     * {@link Storages#empty()}
     */
    public static Storage<SpreadsheetStorageContext> empty() {
        return Storages.empty();
    }

    /**
     * {@link Storages#environment()}
     */
    public static Storage<SpreadsheetStorageContext> env() {
        return Storages.environment();
    }

    /**
     * {@see SpreadsheetStorageForm}
     */
    public static Storage<SpreadsheetStorageContext> form() {
        return SpreadsheetStorageForm.INSTANCE;
    }

    /**
     * {@see StorageSharedExpandedHomeDirectory}
     */
    public static Storage<SpreadsheetStorageContext> homeDirectory() {
        return Storages.homeDirectory();
    }

    /**
     * {@see SpreadsheetStorageSpreadsheetLabel}
     */
    public static Storage<SpreadsheetStorageContext> label() {
        return SpreadsheetStorageSpreadsheetLabel.INSTANCE;
    }

    /**
     * {@see SpreadsheetStorageSpreadsheetMetadata}
     */
    public static Storage<SpreadsheetStorageContext> metadata() {
        return SpreadsheetStorageSpreadsheetMetadata.INSTANCE;
    }

    /**
     * {@see StorageSharedMount}
     */
    public static Storage<SpreadsheetStorageContext> mount(final Storage<SpreadsheetStorageContext> storage) {
        return Storages.mount(storage);
    }

    /**
     * {@see StorageShared2NativeFile}
     */
    @GwtIncompatible
    public static Storage<SpreadsheetStorageContext> nativeFile(final Path root,
                                                                final SpreadsheetStorageContext context) {
        return Storages.nativeStorage(
            root,
            context
        );
    }

    /**
     * {@link Storages#prefixed(StoragePath, Storage)}
     */
    public static Storage<SpreadsheetStorageContext> prefixed(final StoragePath path,
                                                              final Storage<SpreadsheetStorageContext> storage) {
        return Storages.prefixed(
            path,
            storage
        );
    }

    /**
     * {@see SpreadsheetStorageRouter}
     */
    public static Storage<SpreadsheetStorageContext> router(final Storage<SpreadsheetStorageContext> cells,
                                                            final Storage<SpreadsheetStorageContext> forms,
                                                            final Storage<SpreadsheetStorageContext> labels,
                                                            final Storage<SpreadsheetStorageContext> metadatas,
                                                            final Storage<SpreadsheetStorageContext> root) {
        return SpreadsheetStorageRouter.with(
            cells,
            forms,
            labels,
            metadatas,
            root
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetStorages() {
        throw new UnsupportedOperationException();
    }
}
