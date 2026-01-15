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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converters;
import walkingkooka.environment.AuditInfo;
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
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.engine.collection.SpreadsheetCellSet;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTerminalStorageSpreadsheetCellTest implements StorageTesting<SpreadsheetTerminalStorageSpreadsheetCell, SpreadsheetStorageContext>,
    SpreadsheetMetadataTesting {

    @Test
    public void testLoadMissingCellReference() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.ROOT;

        this.loadAndCheck(
            this.createStorage(),
            path,
            context
        );
    }

    @Test
    public void testLoadInvalidCellReferenceFails() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/999");

        final InvalidCharacterException thrown = assertThrows(
            InvalidCharacterException.class,
            () -> this.createStorage()
                .load(
                    path,
                    context
                )
        );
        this.checkEquals(
            "Invalid character '9' at 0",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testLoadMissingCell() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/A1");

        this.loadAndCheck(
            this.createStorage(),
            path,
            context
        );
    }

    @Test
    public void testLoadCell() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        SpreadsheetEngines.basic()
            .saveCell(
                cell,
                spreadsheetContext.spreadsheetEngineContext()
            );

        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final StoragePath path = StoragePath.parse("/A1");

        this.loadAndCheck(
            this.createStorage(),
            path,
            storageContext,
            StorageValue.with(
                path,
                Optional.of(
                    SpreadsheetCellSet.EMPTY.concat(
                        spreadsheetContext.storeRepository()
                            .cells()
                            .loadOrFail(cell.reference())
                    )
                )
            ).setContentType(SpreadsheetMediaTypes.MEMORY_CELL)
        );
    }

    @Test
    public void testLoadCellRange() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("=2")
            );

        SpreadsheetEngines.basic()
            .saveCells(
                Sets.of(
                    a1,
                    a2
                ),
                spreadsheetContext.spreadsheetEngineContext()
            );

        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final StoragePath path = StoragePath.parse("/A1:A2");
        final SpreadsheetCellStore cellStore = spreadsheetContext.storeRepository()
            .cells();

        this.loadAndCheck(
            this.createStorage(),
            path,
            storageContext,
            StorageValue.with(
                path,
                Optional.of(
                    SpreadsheetCellSet.EMPTY.concat(
                        cellStore.loadOrFail(a1.reference())
                    ).concat(
                        cellStore.loadOrFail(a2.reference())
                    )
                )
            ).setContentType(SpreadsheetMediaTypes.MEMORY_CELL)
        );
    }

    @Test
    public void testSaveWithInvalidCellReferenceFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/999"),
                        Optional.of(
                            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        )
                    ),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path, must not contain selection",
            thrown.getMessage()
        );
    }

    @Test
    public void testSaveWithStorageValueMissingCell() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.ROOT;

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.empty()
            ),
            context,
            StorageValue.with(
                path,
                Optional.empty()
            ).setContentType(SpreadsheetMediaTypes.MEMORY_CELL)
        );
    }

    @Test
    public void testSave() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();
        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final SpreadsheetCell cell = SpreadsheetEngines.basic()
            .saveCell(
                SpreadsheetSelection.A1.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=1")
                ),
                spreadsheetContext.spreadsheetEngineContext()
            ).cells()
            .iterator()
            .next();

        final StoragePath path = StoragePath.ROOT;

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(cell)
            ),
            storageContext,
            StorageValue.with(
                path,
                Optional.of(
                    SpreadsheetCellSet.EMPTY.concat(
                        spreadsheetContext.storeRepository()
                            .cells()
                            .loadOrFail(cell.reference())
                    )
                )
            ).setContentType(SpreadsheetMediaTypes.MEMORY_CELL)
        );
    }

    @Test
    public void testDeleteMissingReferenceFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.ROOT,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Missing selection",
            thrown.getMessage()
        );
    }

    @Test
    public void testDelete() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();

        SpreadsheetEngines.basic()
            .saveCell(
                cell,
                spreadsheetContext.spreadsheetEngineContext()
            );

        final StoragePath path = StoragePath.parse("/A1");
        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final SpreadsheetTerminalStorageSpreadsheetCell storage = this.createStorage();
        storage.delete(
            path,
            storageContext
        );

        this.loadAndCheck(
            storage,
            path,
            storageContext
        );
    }

    @Test
    public void testListWithExtraPathFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .list(
                    StoragePath.parse("/A1/extra"),
                    0,
                    1,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after selection",
            thrown.getMessage()
        );
    }

    @Test
    public void testListWithoutSelection() {

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("=2")
            );

        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();

        SpreadsheetEngines.basic()
            .saveCells(
                Sets.of(
                    a1,
                    a2
                ),
                spreadsheetContext.spreadsheetEngineContext()
            );

        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        this.listAndCheck(
            this.createStorage(),
            StoragePath.ROOT,
            0,
            3,
            storageContext,
            StorageValueInfo.with(
                StoragePath.parse("/A1"),
                storageContext.createdAuditInfo()
            ),
            StorageValueInfo.with(
                StoragePath.parse("/A2"),
                storageContext.createdAuditInfo()
            )
        );
    }

    @Test
    public void testList() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("=2")
            );

        final SpreadsheetCell a3 = SpreadsheetSelection.parseCell("A3")
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("=3")
            );

        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();

        SpreadsheetEngines.basic()
            .saveCells(
                Sets.of(
                    a1,
                    a2,
                    a3
                ),
                spreadsheetContext.spreadsheetEngineContext()
            );

        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final StoragePath path = StoragePath.parse("/A1:C2");

        this.listAndCheck(
            this.createStorage(),
            path,
            0,
            2,
            storageContext,
            StorageValueInfo.with(
                StoragePath.parse("/A1"),
                storageContext.createdAuditInfo()
            ),
            StorageValueInfo.with(
                StoragePath.parse("/A2"),
                storageContext.createdAuditInfo()
            )
        );
    }

    @Override
    public SpreadsheetTerminalStorageSpreadsheetCell createStorage() {
        return SpreadsheetTerminalStorageSpreadsheetCell.INSTANCE;
    }

    @Override
    public SpreadsheetStorageContext createContext() {
        return this.createContext(
            this.createSpreadsheetContext()
        );
    }

    private SpreadsheetStorageContext createContext(final SpreadsheetContext spreadsheetContext) {
        return SpreadsheetStorageContexts.basic(
            SpreadsheetEngines.basic(),
            spreadsheetContext
        );
    }

    private SpreadsheetContext createSpreadsheetContext() {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

        final LocaleContext localeContext = LocaleContexts.jre(LOCALE);

        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT;

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
                ).set(
                    SpreadsheetMetadataPropertyName.STYLE,
                    TextStyle.parse("width: 100px; height: 30px")
                ).set(
                    SpreadsheetMetadataPropertyName.DATE_FORMATTER,
                    formatter
                ).set(
                    SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER,
                    formatter
                ).set(
                    SpreadsheetMetadataPropertyName.ERROR_FORMATTER,
                    formatter
                ).set(
                    SpreadsheetMetadataPropertyName.NUMBER_FORMATTER,
                    formatter
                ).set(
                    SpreadsheetMetadataPropertyName.TEXT_FORMATTER,
                    formatter
                ).set(
                    SpreadsheetMetadataPropertyName.TIME_FORMATTER,
                    formatter
                ).set(
                    SpreadsheetMetadataPropertyName.COMPARATORS,
                    SpreadsheetComparatorAliasSet.EMPTY
                ).set(
                    SpreadsheetMetadataPropertyName.CONVERTERS,
                    SpreadsheetConvertersConverterProviders.ALL.aliasSet()
                ).set(
                    SpreadsheetMetadataPropertyName.EXPORTERS,
                    SpreadsheetExporterAliasSet.EMPTY
                ).set(
                    SpreadsheetMetadataPropertyName.FORM_HANDLERS,
                    FormHandlerAliasSet.EMPTY
                ).set(
                    SpreadsheetMetadataPropertyName.FORMATTERS,
                    SpreadsheetFormatterAliasSet.parse(formatter.name().text())
                ).set(
                    SpreadsheetMetadataPropertyName.FUNCTIONS,
                    SpreadsheetExpressionFunctions.EMPTY_ALIAS_SET
                ).set(
                    SpreadsheetMetadataPropertyName.IMPORTERS,
                    SpreadsheetImporterAliasSet.EMPTY
                ).set(
                    SpreadsheetMetadataPropertyName.PARSERS,
                    SpreadsheetParserAliasSet.EMPTY
                ).set(
                    SpreadsheetMetadataPropertyName.VALIDATORS,
                    ValidatorAliasSet.EMPTY
                ).setDefaults(
                    SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                )
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
            SpreadsheetEnvironmentContexts.basic(
                EnvironmentContexts.map(
                    EnvironmentContexts.empty(
                        LineEnding.NL,
                        LOCALE,
                        () -> LocalDateTime.MIN,
                        Optional.of(
                            EmailAddress.parse("user@example.com")
                        )
                    )
                ).setEnvironmentValue(
                    SpreadsheetEnvironmentContext.SPREADSHEET_ID,
                    spreadsheetId
                ).setEnvironmentValue(
                    SpreadsheetEnvironmentContext.SERVER_URL,
                    Url.parseAbsolute("https://example.com")
                )
            ),
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
    public Class<SpreadsheetTerminalStorageSpreadsheetCell> type() {
        return SpreadsheetTerminalStorageSpreadsheetCell.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
