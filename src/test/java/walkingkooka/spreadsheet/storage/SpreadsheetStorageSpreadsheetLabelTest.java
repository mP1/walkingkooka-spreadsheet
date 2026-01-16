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
import walkingkooka.convert.Converters;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetStorageSpreadsheetLabelTest implements StorageTesting<SpreadsheetStorageSpreadsheetLabel, SpreadsheetStorageContext>,
    SpreadsheetMetadataTesting {

    private final static SpreadsheetLabelName LABEL1 = SpreadsheetSelection.labelName("Label111");

    private final static SpreadsheetLabelMapping MAPPING1 = LABEL1.setLabelMappingReference(SpreadsheetSelection.A1);

    private final static SpreadsheetLabelName LABEL2 = SpreadsheetSelection.labelName("Label222");

    private final static SpreadsheetLabelMapping MAPPING2 = LABEL2.setLabelMappingReference(
        SpreadsheetSelection.parseCell("B2")
    );

    private final static SpreadsheetLabelName LABEL3 = SpreadsheetSelection.labelName("Label333");

    private final static SpreadsheetLabelMapping MAPPING3 = LABEL3.setLabelMappingReference(
        SpreadsheetSelection.parseCell("C3")
    );

    private final static StorageValueInfo INFO1 = StorageValueInfo.with(
        StoragePath.parse("/" + LABEL1),
        SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo()
    );

    private final static StorageValueInfo INFO2 = StorageValueInfo.with(
        StoragePath.parse("/" + LABEL2),
        SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo()
    );

    private final static StorageValueInfo INFO3 = StorageValueInfo.with(
        StoragePath.parse("/" + LABEL3),
        SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo()
    );

    @Test
    public void testLoadWithExtraPathFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .load(
                    StoragePath.parse("/" + LABEL1 + "/extra"),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after label name",
            thrown.getMessage()
        );
    }

    @Test
    public void testLoadMissingLabel() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/" + LABEL1);

        this.loadAndCheck(
            this.createStorage(),
            path,
            context
        );
    }

    @Test
    public void testLoad() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();

        SpreadsheetEngines.basic()
            .saveLabel(
                MAPPING1,
                spreadsheetContext.spreadsheetEngineContext()
            );
        final SpreadsheetStorageContext context = this.createContext(spreadsheetContext);


        final StoragePath path = StoragePath.parse("/" + LABEL1);

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(MAPPING1)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_LABEL)
        );
    }

    @Test
    public void testSaveWithExtraPathFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/" + LABEL1 + "/extra"),
                        Optional.of(MAPPING1)
                    ),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after label",
            thrown.getMessage()
        );
    }

    @Test
    public void testSaveWithStorageValueMissingSpreadsheetLabelMapping() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/" + LABEL1),
                        Optional.empty()
                    ),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Missing SpreadsheetLabelMapping",
            thrown.getMessage()
        );
    }

    @Test
    public void testSave() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/" + LABEL1);

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(MAPPING1)
            ),
            context,
            StorageValue.with(
                path,
                Optional.of(MAPPING1)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_LABEL)
        );
    }

    @Test
    public void testDeleteMissingLabelFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.ROOT,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Missing label",
            thrown.getMessage()
        );
    }

    @Test
    public void testDeleteWithExtraPathFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.parse("/" + LABEL1 + "/extra"),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after label",
            thrown.getMessage()
        );
    }

    @Test
    public void testDelete() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveLabel(MAPPING1);

        final StoragePath path = StoragePath.parse("/" + LABEL1);

        final SpreadsheetStorageSpreadsheetLabel storage = this.createStorage();
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
    public void testListWithExtraPathFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .list(
                    StoragePath.parse("/" + LABEL1 + "/extra"),
                    0,
                    1,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after label",
            thrown.getMessage()
        );
    }

    @Test
    public void testListMissingLabel() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveLabel(MAPPING1);
        context.saveLabel(MAPPING2);
        context.saveLabel(MAPPING3);

        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0,
            4,
            context,
            INFO1,
            INFO2,
            INFO3
        );
    }

    @Test
    public void testListMissingLabelWithOffset() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveLabel(MAPPING1);
        context.saveLabel(MAPPING2);
        context.saveLabel(MAPPING3);

        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            1,
            4,
            context,
            INFO2,
            INFO3
        );
    }

    @Test
    public void testListMissingLabelWithCount() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveLabel(MAPPING1);
        context.saveLabel(MAPPING2);
        context.saveLabel(MAPPING3);

        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0,
            2,
            context,
            INFO1,
            INFO2
        );
    }

    @Test
    public void testListWithPrefix() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveLabel(MAPPING1);
        context.saveLabel(MAPPING2);
        context.saveLabel(MAPPING3);

        this.listAndCheck(
            this.createStorage(),
            StoragePath.parse("/Label"),
            0,
            2,
            context,
            INFO1,
            INFO2
        );
    }

    @Override
    public SpreadsheetStorageSpreadsheetLabel createStorage() {
        return SpreadsheetStorageSpreadsheetLabel.INSTANCE;
    }

    @Override
    public SpreadsheetStorageContext createContext() {
        return this.createContext(
            this.createSpreadsheetContext()
        );
    }

    private SpreadsheetStorageContext createContext(final SpreadsheetContext spreadsheetContext) {
        return SpreadsheetStorageContexts.spreadsheetContext(
            SpreadsheetEngines.basic(),
            spreadsheetContext
        );
    }

    private SpreadsheetContext createSpreadsheetContext() {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

        final LocaleContext localeContext = LocaleContexts.jre(LOCALE);

        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        metadataStore.save(
            SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.LOCALE,
                    LOCALE
                ).loadFromLocale(localeContext)
                .set(
                    SpreadsheetMetadataPropertyName.AUDIT_INFO,
                    AuditInfo.create(
                        EmailAddress.parse("user@example.com"),
                        LocalDateTime.MIN
                    )
                ).setDefaults(
                    SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                )
        );

        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                LineEnding.NL,
                LOCALE,
                HAS_NOW,
                Optional.of(
                    EmailAddress.parse("user@example.com")
                )
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            spreadsheetId
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            Url.parseAbsolute("https://example.com")
        );

        return SpreadsheetContexts.fixedSpreadsheetId(
            SpreadsheetStoreRepositories.treeMap(
                metadataStore,
                Storages.tree()
            ), // SpreadsheetStoreRepository
            (c) -> SpreadsheetEngineContexts.spreadsheetContext(
                SpreadsheetMetadataMode.FORMULA,
                c,
                TerminalContexts.fake()
            ), // SpreadsheetEngineContext factory
            (c) -> {
                throw new UnsupportedOperationException();
            }, // HttpRouter
            SpreadsheetEnvironmentContexts.basic(environmentContext),
            localeContext,
            SpreadsheetProviders.basic(
                SpreadsheetConvertersConverterProviders.spreadsheetConverters(
                    (ProviderContext p) -> Converters.never()
                ),
                ExpressionFunctionProviders.empty(
                    SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY
                ),
                SpreadsheetComparatorProviders.empty(),
                SpreadsheetExporterProviders.empty(),
                SpreadsheetFormatterProviders.spreadsheetFormatters(),
                FormHandlerProviders.empty(),
                SpreadsheetImporterProviders.empty(),
                SpreadsheetParserProviders.empty(),
                ValidatorProviders.empty()
            ),
            ProviderContexts.fake()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetStorageSpreadsheetLabel> type() {
        return SpreadsheetStorageSpreadsheetLabel.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
