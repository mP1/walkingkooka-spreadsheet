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

import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for all {@link Storage} implementations, using an abstract template class that handles null parameter checking.
 */
abstract class SpreadsheetStorage implements Storage<SpreadsheetStorageContext> {

    SpreadsheetStorage() {
        super();
    }

    @Override
    public final Optional<StorageValue> load(final StoragePath path,
                                             final SpreadsheetStorageContext context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        return this.loadNonNull(
            path,
            context
        );
    }

    abstract Optional<StorageValue> loadNonNull(final StoragePath path,
                                                final SpreadsheetStorageContext context);

    @Override
    public final StorageValue save(final StorageValue value,
                                   final SpreadsheetStorageContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return this.saveNonNull(
            value,
            context
        );
    }

    abstract StorageValue saveNonNull(final StorageValue value,
                                      final SpreadsheetStorageContext context);

    @Override
    public final void delete(final StoragePath path,
                             final SpreadsheetStorageContext context) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(context, "context");

        this.deleteNonNull(
            path,
            context
        );
    }

    abstract void deleteNonNull(final StoragePath path,
                                final SpreadsheetStorageContext context);

    @Override
    public final List<StorageValueInfo> list(final StoragePath path,
                                             final int offset,
                                             final int count,
                                             final SpreadsheetStorageContext context) {
        Objects.requireNonNull(path, "path");
        Store.checkOffsetAndCount(offset, count);
        Objects.requireNonNull(context, "context");

        return this.listNonNull(
            path,
            offset,
            count,
            context
        );
    }

    abstract List<StorageValueInfo> listNonNull(final StoragePath path,
                                                final int offset,
                                                final int count,
                                                final SpreadsheetStorageContext context);
}
