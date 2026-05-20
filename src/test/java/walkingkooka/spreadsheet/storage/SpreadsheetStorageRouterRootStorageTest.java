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
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;

import java.time.LocalDateTime;
import java.util.Optional;

public final class SpreadsheetStorageRouterRootStorageTest implements StorageTesting<SpreadsheetStorageRouterRootStorage, SpreadsheetStorageContext>,
    SpreadsheetMetadataTesting {

    private final static AuditInfo AUDIT_INFO = SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo();

    private final static StoragePath ITEM1 = StoragePath.parse("/item111");

    private final static StoragePath ITEM2 = StoragePath.parse("/item222");

    private final static StoragePath ITEM3 = StoragePath.parse("/other/item333");

    @Test
    public void testListOffsetZeroCountZero() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0,
            0,
            this.createContext()
        );
    }

    @Test
    public void testListOffsetZeroCountFive() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0,
            5,
            this.createContext(),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.CELL,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.ENVIRONMENT,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.FORM,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.LABEL,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.SPREADSHEET,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListOffsetSpreadsheetCountOne() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            4,
            1,
            this.createContext(),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.SPREADSHEET,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListOffsetSpreadsheetCountTwo() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            4,
            2,
            this.createContext(),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.SPREADSHEET,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                ITEM1,
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListOffsetSpreadsheetCountFour() {
        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            4,
            3,
            this.createContext(),
            StorageValueInfo.with(
                SpreadsheetStorageRouter.SPREADSHEET,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                ITEM1,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                ITEM2,
                AUDIT_INFO
            )
        );
    }

    @Override
    public SpreadsheetStorageRouterRootStorage createStorage() {
        final Storage<SpreadsheetStorageContext> root = Storages.treeMapStore();

        final SpreadsheetStorageContext context = this.createContext();

        root.save(
            StorageValue.with(ITEM1)
                .setValue(
                    Optional.of("111")
                ),
            context
        );

        root.save(
            StorageValue.with(ITEM2)
                .setValue(
                    Optional.of("222")
                ),
            context
        );

        root.save(
            StorageValue.with(ITEM3)
                .setValue(
                    Optional.of("333")
                ),
            context
        );

        return SpreadsheetStorageRouterRootStorage.with(
            root,
            SpreadsheetStorageRouter.with(
                SpreadsheetStorages.cell(),
                SpreadsheetStorages.env(),
                SpreadsheetStorages.form(),
                SpreadsheetStorages.label(),
                SpreadsheetStorages.metadata(),
                root
            )
        );
    }

    @Override
    public void testListWithNegativeCountFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testListWithNegativeOffsetFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetStorageContext createContext() {
        return new FakeSpreadsheetStorageContext() {

            @Override
            public LocalDateTime now() {
                return AUDIT_INFO.createdTimestamp();
            }

            @Override
            public Optional<EmailAddress> user() {
                return Optional.of(
                    AUDIT_INFO.createdBy()
                );
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetStorageRouterRootStorage> type() {
        return SpreadsheetStorageRouterRootStorage.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
