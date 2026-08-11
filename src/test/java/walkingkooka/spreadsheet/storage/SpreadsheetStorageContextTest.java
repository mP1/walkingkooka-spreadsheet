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

import org.junit.jupiter.api.Test;
import walkingkooka.environment.HasAuditInfoTesting;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;

import java.time.LocalDateTime;
import java.util.Optional;

public final class SpreadsheetStorageContextTest implements SpreadsheetStorageContextTesting, ClassTesting<SpreadsheetStorageContext>,
    StorageTesting,
    HasAuditInfoTesting {

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/path1");

    private final static StorageValue STORAGE_VALUE = StorageValue.with(STORAGE_PATH)
        .setValue(
            Optional.of("111")
        );

    @Test
    public void testLoadStorage() {
        final Storage<SpreadsheetStorageContext> storage = Storages.treeMapStore();

        final SpreadsheetStorageContext context = new TestSpreadsheetStorageContext(storage);

        storage.save(
            STORAGE_VALUE,
            context
        );

        this.loadStorageAndCheck(
            context,
            STORAGE_PATH,
            STORAGE_VALUE
        );
    }

    @Test
    public void testSaveStorage() {
        final Storage<SpreadsheetStorageContext> storage = Storages.treeMapStore();

        final SpreadsheetStorageContext context = new TestSpreadsheetStorageContext(storage);

        this.saveStorageAndCheck(
            context,
            STORAGE_VALUE,
            STORAGE_VALUE
        );

        this.loadAndCheck(
            storage,
            STORAGE_PATH,
            context,
            STORAGE_VALUE
        );
    }

    @Test
    public void testDeleteStorage() {
        final Storage<SpreadsheetStorageContext> storage = Storages.treeMapStore();

        final SpreadsheetStorageContext context = new TestSpreadsheetStorageContext(storage);

        storage.save(
            STORAGE_VALUE,
            context
        );

        context.deleteStorage(STORAGE_PATH);

        this.loadAndCheck(
            storage,
            STORAGE_PATH,
            context
        );
    }

    @Test
    public void testListStorage() {
        final Storage<SpreadsheetStorageContext> storage = Storages.treeMapStore();

        final SpreadsheetStorageContext context = new TestSpreadsheetStorageContext(storage);

        storage.save(
            STORAGE_VALUE,
            context
        );

        this.listStorageAndCheck(
            context,
            StoragePath.ROOT,
            0,
            2,
            StorageValueInfo.with(
                STORAGE_PATH,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testMountStorage() {
        final Storage<SpreadsheetStorageContext> storage = Storages.mount(
            Storages.treeMapStore()
        );

        final SpreadsheetStorageContext context = new TestSpreadsheetStorageContext(storage);

        final StorageMountPoint<SpreadsheetStorageContext> mountPoint = StorageMountPoint.with(
            STORAGE_PATH,
            Storages.fake()
        );

        context.mountStorage(mountPoint);

        this.mountPointsAndCheck(
            storage,
            mountPoint
        );
    }

    @Test
    public void testUnmountStorage() {
        final Storage<SpreadsheetStorageContext> storage = Storages.mount(
            Storages.treeMapStore()
        );

        final SpreadsheetStorageContext context = new TestSpreadsheetStorageContext(storage);

        final StorageMountPoint<SpreadsheetStorageContext> mountPoint = StorageMountPoint.with(
            STORAGE_PATH,
            Storages.fake()
        );

        storage.mount(
            mountPoint,
            context
        );

        context.unmountStorage(STORAGE_PATH);

        this.mountPointsAndCheck(
            storage
        );
    }

    @Test
    public void testStorageMountPoints() {
        final Storage<SpreadsheetStorageContext> storage = Storages.mount(
            Storages.treeMapStore()
        );

        final SpreadsheetStorageContext context = new TestSpreadsheetStorageContext(storage);

        final StorageMountPoint<SpreadsheetStorageContext> mountPoint = StorageMountPoint.with(
            STORAGE_PATH,
            Storages.fake()
        );

        context.mountStorage(mountPoint);

        this.storageMountPointsAndCheck(
            context,
            mountPoint
        );
    }
    
    static final class TestSpreadsheetStorageContext extends FakeSpreadsheetStorageContext {

        TestSpreadsheetStorageContext(final Storage<SpreadsheetStorageContext> storage) {
            super();
            this.storage = storage;
        }

        @Override
        public Storage<SpreadsheetStorageContext> storage() {
            return storage;
        }
        
        private final Storage<SpreadsheetStorageContext> storage;

        @Override
        public LocalDateTime now() {
            return SpreadsheetStorageContextTest.NOW;
        }

        @Override
        public Optional<EmailAddress> user() {
            return OPTIONAL_USER;
        }
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetStorageContext> type() {
        return SpreadsheetStorageContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
