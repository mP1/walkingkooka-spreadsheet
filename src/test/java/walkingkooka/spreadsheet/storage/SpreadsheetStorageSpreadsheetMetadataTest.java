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

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetStorageSpreadsheetMetadataTest implements StorageTesting<SpreadsheetStorageSpreadsheetMetadata, SpreadsheetStorageContext>,
    SpreadsheetMetadataTesting {

    @Test
    public void testLoadMissingSpreadsheetMetadata() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final StoragePath path = StoragePath.parse("/404");

        this.loadAndCheck(
            this.createStorage(),
            path,
            context
        );
    }

    @Test
    public void testLoadMissingSpreadsheetId() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final SpreadsheetMetadata metadata = context.saveMetadata(METADATA_EN_AU);

        final StoragePath path = StoragePath.ROOT;

        this.loadAndCheck(
            this.createStorage(),
            path,
            context
        );
    }

    @Test
    public void testLoad() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final SpreadsheetMetadata metadata = context.saveMetadata(METADATA_EN_AU);

        final StoragePath path = StoragePath.parse("/" + metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID));

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(metadata)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_SPREADSHEET_METADATA)
        );
    }

    @Test
    public void testSaveWithStoragePathIncludingSpreadsheetIdFails() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/1"),
                        Optional.of(METADATA_EN_AU)
                    ),
                    context
                )
        );

        this.checkEquals(
            "Invalid path, SpreadsheetId should not be present",
            thrown.getMessage()
        );
    }

    @Test
    public void testSavePathIncludesSpreadsheetId() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/1"),
                        Optional.of(
                            SpreadsheetMetadata.EMPTY
                        )
                    ),
                    new TestSpreadsheetStorageContext()
                )
        );

        this.checkEquals(
            "Invalid path, SpreadsheetId should not be present",
            thrown.getMessage()
        );
    }

    @Test
    public void testSaveWithStorageValueMissingSpreadsheetMetadataFails() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.ROOT,
                        Optional.empty()
                    ),
                    context
                )
        );

        this.checkEquals(
            "Missing SpreadsheetMetadata",
            thrown.getMessage()
        );
    }

    @Test
    public void testSave() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final SpreadsheetMetadata metadata = context.saveMetadata(METADATA_EN_AU);

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                StoragePath.ROOT,
                Optional.of(metadata)
            ),
            context,
            StorageValue.with(
                StoragePath.parse("/" + metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)),
                Optional.of(metadata)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_SPREADSHEET_METADATA)
        );
    }

    @Test
    public void testDelete() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final SpreadsheetMetadata metadata = context.saveMetadata(METADATA_EN_AU);

        final SpreadsheetStorageSpreadsheetMetadata storage = this.createStorage();
        final StoragePath path = StoragePath.parse("/" + metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID));

        storage.delete(
            path,
            context
        );

        this.loadAndCheck(
            storage,
            path,
            context
        );
    }

    @Test
    public void testListMissingFilter() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final SpreadsheetStorageSpreadsheetMetadata storage = this.createStorage();

        final StorageValue value1 = storage.save(
            StorageValue.with(
                StoragePath.ROOT,
                Optional.of(
                    METADATA_EN_AU.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                        SpreadsheetName.with("Hello1")
                    )
                )
            ),
            context
        );

        final StorageValue value2 = storage.save(
            StorageValue.with(
                StoragePath.ROOT,
                Optional.of(
                    METADATA_EN_AU.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                        SpreadsheetName.with("Hello2")
                    )
                )
            ),
            context
        );

        final StorageValue value3 = storage.save(
            StorageValue.with(
                StoragePath.ROOT,
                Optional.of(
                    METADATA_EN_AU.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                        SpreadsheetName.with("Different3")
                    )
                )
            ),
            context
        );

        final StoragePath path = StoragePath.ROOT;

        this.listAndCheck(
            this.createStorage(),
            path,
            0,
            2,
            context,
            StorageValueInfo.with(
                StoragePath.parse(
                    "/" + (
                        (SpreadsheetMetadata) value1.value()
                            .get()
                    ).getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                ),
                context.createdAuditInfo()
            ),
            StorageValueInfo.with(
                StoragePath.parse(
                    "/" + (
                        (SpreadsheetMetadata) value2.value()
                            .get()
                    ).getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                ),
                context.createdAuditInfo()
            )
        );
    }

    @Test
    public void testList() {
        final TestSpreadsheetStorageContext context = new TestSpreadsheetStorageContext();

        final SpreadsheetStorageSpreadsheetMetadata storage = this.createStorage();

        final StorageValue value1 = storage.save(
            StorageValue.with(
                StoragePath.ROOT,
                Optional.of(
                    METADATA_EN_AU.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                        SpreadsheetName.with("Hello1")
                    )
                )
            ),
            context
        );

        final StorageValue value2 = storage.save(
            StorageValue.with(
                StoragePath.ROOT,
                Optional.of(
                    METADATA_EN_AU.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                        SpreadsheetName.with("Hello2")
                    )
                )
            ),
            context
        );

        final StorageValue value3 = storage.save(
            StorageValue.with(
                StoragePath.ROOT,
                Optional.of(
                    METADATA_EN_AU.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                        SpreadsheetName.with("Different3")
                    )
                )
            ),
            context
        );

        final StoragePath path = StoragePath.parse("/Hello");

        this.listAndCheck(
            this.createStorage(),
            path,
            0,
            2,
            context,
            StorageValueInfo.with(
                StoragePath.parse(
                    "/" + (
                        (SpreadsheetMetadata) value1.value()
                            .get()
                    ).getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                ),
                context.createdAuditInfo()
            ),
            StorageValueInfo.with(
                StoragePath.parse(
                    "/" + (
                        (SpreadsheetMetadata) value2.value()
                            .get()
                    ).getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
                ),
                context.createdAuditInfo()
            )
        );
    }

    @Override
    public SpreadsheetStorageSpreadsheetMetadata createStorage() {
        return SpreadsheetStorageSpreadsheetMetadata.INSTANCE;
    }

    @Override
    public SpreadsheetStorageContext createContext() {
        return SpreadsheetStorageContexts.fake();
    }

    final static class TestSpreadsheetStorageContext extends FakeSpreadsheetStorageContext {

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> target) {
            return this.successfulConversion(
                target.cast(value),
                target
            );
        }

        @Override
        public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
            return this.spreadsheetContext.loadMetadata(id);
        }

        @Override
        public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
            return this.spreadsheetContext.saveMetadata(metadata);
        }

        @Override
        public void deleteMetadata(final SpreadsheetId id) {
            this.spreadsheetContext.deleteMetadata(id);
        }

        @Override
        public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                       final int offset,
                                                                       final int count) {
            return this.spreadsheetContext.findMetadataBySpreadsheetName(
                name,
                offset,
                count
            );
        }

        {
            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
            spreadsheetEnvironmentContext.setSpreadsheetId(
                SpreadsheetId.with(1)
            );

            spreadsheetContext = SpreadsheetContexts.fixedSpreadsheetId(
                SpreadsheetEngines.basic(),
                SpreadsheetStoreRepositories.treeMap(
                    SpreadsheetMetadataStores.treeMap()
                ),
                (c) -> {
                    throw new UnsupportedOperationException();
                }, // Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory
                (c) -> {
                    throw new UnsupportedOperationException();
                }, // Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory
                spreadsheetEnvironmentContext,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            );
        }

        private final SpreadsheetContext spreadsheetContext;

        @Override
        public LocalDateTime now() {
            return HAS_NOW.now();
        }

        @Override
        public Optional<EmailAddress> user() {
            return Optional.of(SpreadsheetMetadataTesting.USER);
        }
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetStorageSpreadsheetMetadata> type() {
        return SpreadsheetStorageSpreadsheetMetadata.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
