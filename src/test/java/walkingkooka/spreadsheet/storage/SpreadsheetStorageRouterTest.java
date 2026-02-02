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
import walkingkooka.collect.map.Maps;
import walkingkooka.convert.Converters;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.provider.ValidatorAliasSet;

import java.math.MathContext;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetStorageRouterTest extends SpreadsheetStorageTestCase<SpreadsheetStorageRouter>
    implements SpreadsheetStorageContextTesting {

    private final static SpreadsheetId SPREADSHEET_ID1 = SpreadsheetId.with(0x111);

    private final static SpreadsheetId SPREADSHEET_ID2 = SpreadsheetId.with(0x222);

    private final static SpreadsheetMetadata METADATA1 = METADATA_EN_AU.set(
        SpreadsheetMetadataPropertyName.LOCALE,
        LOCALE
    ).set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID1
    ).set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
        SpreadsheetName.with("Spreadsheet111")
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
        SpreadsheetFormatterAliasSet.EMPTY
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
    );

    private final static SpreadsheetMetadata METADATA2 = METADATA1.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID2
    ).set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
        SpreadsheetName.with("Spreadsheet222")
    );

    private final Storage<SpreadsheetStorageContext> CELLS = Storages.fake();
    private final Storage<SpreadsheetStorageContext> LABELS = Storages.fake();
    private final Storage<SpreadsheetStorageContext> METADATAS = Storages.fake();
    private final Storage<SpreadsheetStorageContext> OTHER = Storages.fake();

    private final static SpreadsheetCell CELL1 = SpreadsheetSelection.A1.setFormula(
        SpreadsheetFormula.EMPTY.setValue(
            Optional.of(111)
        )
    );

    private final static SpreadsheetCell CELL2 = SpreadsheetSelection.parseCell("b2")
        .setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(222)
            )
        );

    private final static SpreadsheetLabelName LABEL1 = SpreadsheetSelection.labelName("Label111");

    private final static SpreadsheetLabelName LABEL2 = SpreadsheetSelection.labelName("Label222");

    private final static SpreadsheetLabelMapping MAPPING1 = LABEL1.setLabelMappingReference(
        SpreadsheetSelection.labelName("Target111")
    );

    private final static SpreadsheetLabelMapping MAPPING2 = LABEL2.setLabelMappingReference(
        SpreadsheetSelection.labelName("Target222")
    );

    private final static AuditInfo AUDIT_INFO = SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo();

    private static final StorageValueInfo METADATA_INFO1 = StorageValueInfo.with(
        StoragePath.parse("/spreadsheet/111"),
        AUDIT_INFO
    );

    private static final StorageValueInfo METADATA_INFO2 = StorageValueInfo.with(
        StoragePath.parse("/spreadsheet/222"),
        AUDIT_INFO
    );

    private static final SpreadsheetCellReference DIFFERENT_CELL_REFERENCE = SpreadsheetSelection.parseCell("C3");

    private static final SpreadsheetCell DIFFERENT_UNFORMATTED_CELL = DIFFERENT_CELL_REFERENCE.setFormula(
        SpreadsheetFormula.EMPTY.setValue(
            Optional.of(999)
        )
    );

    private static final SpreadsheetCell DIFFERENT_FORMATTED_CELL = DIFFERENT_UNFORMATTED_CELL.setFormattedValue(
        Optional.of(
            TextNode.text("999.")
        )
    );

    private static final SpreadsheetLabelMapping DIFFERENT_MAPPING = SpreadsheetSelection.labelName("DifferentLabel")
        .setLabelMappingReference(DIFFERENT_CELL_REFERENCE);

    // with.............................................................................................................

    @Test
    public void testWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetStorageRouter.with(
                null,
                LABELS,
                METADATAS,
                OTHER
            )
        );
    }

    @Test
    public void testWithNullLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetStorageRouter.with(
                CELLS,
                null,
                METADATAS,
                OTHER
            )
        );
    }

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetStorageRouter.with(
                CELLS,
                LABELS,
                null,
                OTHER
            )
        );
    }

    @Test
    public void testWithNullOtherFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetStorageRouter.with(
                CELLS,
                LABELS,
                METADATAS,
                null
            )
        );
    }

    // Storage.load.....................................................................................................

    @Test
    public void testLoadWithoutSpreadsheet() {
        final StoragePath path = StoragePath.parse("/spreadsheet");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext()
        );
    }

    @Test
    public void testLoadWithUnknownSpreadsheetId() {
        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/spreadsheet/404"),
            this.createContext()
        );
    }

    @Test
    public void testLoadWithSpreadsheetId1() {
        final StoragePath path = StoragePath.parse("/spreadsheet/111");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(METADATA1)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_SPREADSHEET_METADATA)
        );
    }

    @Test
    public void testLoadWithSpreadsheetId2() {
        final StoragePath path = StoragePath.parse("/spreadsheet/222.JSON");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(METADATA2)
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_SPREADSHEET_METADATA
            )
        );
    }

    @Test
    public void testLoadWithCellAndMissingEnvironmentValueSpreadsheetIdFails() {
        final StoragePath path = StoragePath.parse("/cell/A1");

        final SpreadsheetStorageContext context = this.createContext();

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createStorage()
                .load(
                    path,
                    context
                )
        );

        thrown.printStackTrace();

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName(),
            "MissingEnvironmentValueException.environmentValueName"
        );
    }

    @Test
    public void testLoadWithCell() {
        final StoragePath path = StoragePath.parse("/cell/A1");

        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(
                    CELL1.setFormattedValue(
                        Optional.of(
                            TextNode.text("111.")
                        )
                    )
                )
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_CELL
            )
        );
    }

    @Test
    public void testLoadWithCell2() {
        final StoragePath path = StoragePath.parse("/cell/a1");

        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(
                    CELL1.setFormattedValue(
                        Optional.of(
                            TextNode.text("111.")
                        )
                    )
                )
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_CELL
            )
        );
    }

    @Test
    public void testLoadWithCellAndFileExtension() {
        final StoragePath path = StoragePath.parse("/cell/a1.json");

        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(
                    CELL1.setFormattedValue(
                        Optional.of(
                            TextNode.text("111.")
                        )
                    )
                )
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_CELL
            )
        );
    }

    @Test
    public void testLoadWithUnknownCell() {
        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/cell/Z999"),
            context
        );
    }

    @Test
    public void testLoadWithLabel() {
        final StoragePath path = StoragePath.parse("/label/Label111");

        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(MAPPING1)
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_LABEL
            )
        );
    }

    @Test
    public void testLoadWithLabelAndFileExtension() {
        final StoragePath path = StoragePath.parse("/label/Label111.json");

        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(MAPPING1)
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_LABEL
            )
        );
    }

    @Test
    public void testLoadWithLabel2WrongSpreadsheet() {
        final StoragePath path = StoragePath.parse("/label/Label222");

        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            path,
            context
        );
    }

    @Test
    public void testLoadWithUnknownLabelAndMissingSpreadsheetIdFails() {
        final SpreadsheetStorageContext context = this.createContext();

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createStorage()
                .load(
                    StoragePath.parse("/label/UnknownLabel404"),
                    context
                )
        );

        this.checkEquals(
            SpreadsheetEngineContext.SPREADSHEET_ID,
            thrown.environmentValueName(),
            "MissingEnvironmentValueException.environmentValueName"
        );
    }

    @Test
    public void testLoadWithUnknownLabel() {
        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.loadAndCheck(
            this.createStorage(),
            StoragePath.parse("/label/UnknownLabel404"),
            context
        );
    }

    @Test
    public void testLoadWithSpreadsheetAndCell() {
        final StoragePath path = StoragePath.parse("/spreadsheet/111/cell/A1");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(
                    CELL1.setFormattedValue(
                        Optional.of(
                            TextNode.text("111.")
                        )
                    )
                )
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_CELL
            )
        );
    }

    @Test
    public void testLoadWithSpreadsheetAndCell2() {
        final StoragePath path = StoragePath.parse("/spreadsheet/222/cell/B2");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(
                    CELL2.setFormattedValue(
                        Optional.of(
                            TextNode.text("222.")
                        )
                    )
                )
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_CELL
            )
        );
    }

    @Test
    public void testLoadWithSpreadsheetAndCellAndFileExtension() {
        final StoragePath path = StoragePath.parse("/spreadsheet/222/cell/B2.json");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(
                    CELL2.setFormattedValue(
                        Optional.of(
                            TextNode.text("222.")
                        )
                    )
                )
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_CELL
            )
        );
    }

    @Test
    public void testLoadWithSpreadsheetAndLabel() {
        final StoragePath path = StoragePath.parse("/spreadsheet/111/label/Label111");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(MAPPING1)
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_LABEL
            )
        );
    }

    @Test
    public void testLoadWithSpreadsheetAndLabel2() {
        final StoragePath path = StoragePath.parse("/spreadsheet/222/label/Label222");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(MAPPING2)
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_LABEL
            )
        );
    }

    @Test
    public void testLoadWithSpreadsheetAndLabelAndFileExtension() {
        final StoragePath path = StoragePath.parse("/spreadsheet/222/label/Label222.json");

        this.loadAndCheck(
            this.createStorage(),
            path,
            this.createContext(),
            StorageValue.with(
                path,
                Optional.of(MAPPING2)
            ).setContentType(
                SpreadsheetMediaTypes.MEMORY_LABEL
            )
        );
    }

    // Storage.save.....................................................................................................

    @Test
    public void testSaveWithSpreadsheetIdAndMissingSpreadsheetEnvironmentContextSpreadsheetIdFails() {
        final SpreadsheetStorageContext context = this.createContext();

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(0x333);

        final SpreadsheetMetadata metadata = METADATA1.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            spreadsheetId
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with("Spreadsheet333")
        );

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/spreadsheet"),
                        Optional.of(metadata)
                    ),
                    context
                )
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName(),
            "MissingEnvironmentValueException.environmentValueName"
        );
    }

    @Test
    public void testSaveWithSpreadsheetId() {
        final SpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID1)
        );

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(0x333);

        final SpreadsheetMetadata metadata = METADATA1.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            spreadsheetId
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with("Spreadsheet333")
        );

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                StoragePath.parse("/spreadsheet"),
                Optional.of(metadata)
            ),
            context,
            StorageValue.with(
                StoragePath.parse("/spreadsheet/333"),
                Optional.of(
                    metadata
                )
            ).setContentType(SpreadsheetMediaTypes.MEMORY_SPREADSHEET_METADATA)
        );

        this.checkEquals(
            Optional.of(metadata),
            context.loadMetadata(spreadsheetId)
        );
    }

    @Test
    public void testSaveWithCellAndMissingEnvironmentValueSpreadsheetId() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/cell");

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        path,
                        Optional.of(DIFFERENT_UNFORMATTED_CELL)
                    ),
                    context
                )
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName(),
            "MissingEnvironmentValueException.environmentValueName"
        );
    }

    @Test
    public void testSaveWithCell() {
        final SpreadsheetStorageContext context = this.createContext();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        final StoragePath path = StoragePath.parse("/cell");

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_UNFORMATTED_CELL)
            ),
            context,
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_FORMATTED_CELL)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_CELL)
        );

        this.loadCellsAndCheck(
            context,
            DIFFERENT_CELL_REFERENCE,
            DIFFERENT_FORMATTED_CELL
        );
    }

    @Test
    public void testSaveWithLabel() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();
        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        storageContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        final StoragePath path = StoragePath.parse("/label/DifferentLabel");

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_MAPPING)
            ),
            storageContext,
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_MAPPING)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_LABEL)
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID1)
        );
        this.checkEquals(
            Optional.of(DIFFERENT_MAPPING),
            spreadsheetContext.storeRepository()
                .labels()
                .load(DIFFERENT_MAPPING.label())
        );
    }

    @Test
    public void testSaveWithSpreadsheetIdAndCell() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();
        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final StoragePath path = StoragePath.parse("/spreadsheet/111/cell");

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_UNFORMATTED_CELL)
            ),
            storageContext,
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_FORMATTED_CELL)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_CELL)
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID1)
        );

        this.checkEquals(
            Optional.of(DIFFERENT_FORMATTED_CELL),
            spreadsheetContext.storeRepository()
                .cells()
                .load(DIFFERENT_CELL_REFERENCE)
        );
    }

    @Test
    public void testSaveWithSpreadsheetIdAndCell2() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();
        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final StoragePath path = StoragePath.parse("/spreadsheet/222/cell");

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_UNFORMATTED_CELL)
            ),
            storageContext,
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_FORMATTED_CELL)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_CELL)
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID2)
        );

        this.checkEquals(
            Optional.of(DIFFERENT_FORMATTED_CELL),
            spreadsheetContext.storeRepository()
                .cells()
                .load(DIFFERENT_CELL_REFERENCE)
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID1)
        );

        this.checkEquals(
            Optional.empty(),
            spreadsheetContext.storeRepository()
                .cells()
                .load(DIFFERENT_CELL_REFERENCE)
        );
    }

    @Test
    public void testSaveWithSpreadsheetIdLabel1() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();
        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final StoragePath path = StoragePath.parse("/spreadsheet/111/label/DifferentLabel");

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_MAPPING)
            ),
            storageContext,
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_MAPPING)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_LABEL)
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID1)
        );
        this.checkEquals(
            Optional.of(DIFFERENT_MAPPING),
            spreadsheetContext.spreadsheetEngineContext()
                .storeRepository()
                .labels()
                .load(DIFFERENT_MAPPING.label())
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID2)
        );
        this.checkEquals(
            Optional.empty(),
            spreadsheetContext.spreadsheetEngineContext()
                .storeRepository()
                .labels()
                .load(DIFFERENT_MAPPING.label())
        );
    }

    @Test
    public void testSaveWithSpreadsheetIdLabel2() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();
        final SpreadsheetStorageContext storageContext = this.createContext(spreadsheetContext);

        final StoragePath path = StoragePath.parse("/spreadsheet/222/label/DifferentLabel");

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_MAPPING)
            ),
            storageContext,
            StorageValue.with(
                path,
                Optional.of(DIFFERENT_MAPPING)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_LABEL)
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID2)
        );

        this.checkEquals(
            Optional.of(DIFFERENT_MAPPING),
            spreadsheetContext.spreadsheetEngineContext()
                .storeRepository()
                .labels()
                .load(DIFFERENT_MAPPING.label())
        );

        spreadsheetContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID1)
        );

        this.checkEquals(
            Optional.empty(),
            spreadsheetContext.spreadsheetEngineContext()
                .storeRepository()
                .labels()
                .load(DIFFERENT_MAPPING.label())
        );
    }

    @Test
    public void testSaveOther() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/other");
        final String value = "value123";

        this.saveAndCheck(
            storage,
            StorageValue.with(
                path,
                Optional.of(value)
            ),
            context,
            StorageValue.with(
                path,
                Optional.of(value)
            )
        );
    }

    @Test
    public void testSaveOtherAndLoad() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/other");
        final String value = "value123";

        this.saveAndCheck(
            storage,
            StorageValue.with(
                path,
                Optional.of(value)
            ),
            context,
            StorageValue.with(
                path,
                Optional.of(value)
            )
        );

        this.loadAndCheck(
            storage,
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(value)
            )
        );
    }

    @Test
    public void testSaveOtherDeleteAndLoad() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/other");
        final String value = "value123";

        this.saveAndCheck(
            storage,
            StorageValue.with(
                path,
                Optional.of(value)
            ),
            context,
            StorageValue.with(
                path,
                Optional.of(value)
            )
        );

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

    // Storage.delete...................................................................................................

    @Test
    public void testDeleteWithUnknownSpreadsheetId() {
        this.createStorage()
            .delete(
                StoragePath.parse("/spreadsheet/404"),
                this.createContext()
            );
    }

    @Test
    public void testDeleteWithSpreadsheetId() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        // must delete spreadsheet/222 because context is for spreadsheet/111
        final StoragePath path = StoragePath.parse("/spreadsheet/222");

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
    public void testDeleteWithUnknownCell() {
        this.createStorage()
            .delete(
                StoragePath.parse("/spreadsheet/111/cell/Z9"),
                this.createContext()
            );
    }

    @Test
    public void testDeleteWithCell() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        final StoragePath path = StoragePath.parse("/cell/A1");

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
    public void testDeleteWithLabel() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        final StoragePath path = StoragePath.parse("/label/Label111");

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
    public void testDeleteWithSpreadsheetIdAndCellAndMissingSpreadsheetEnvironmentValueSpreadsheetId() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        storage.delete(
            StoragePath.parse("/spreadsheet/111/cell/A1"),
            context
        );

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> storage.load(
                StoragePath.parse("/cell/A1"),
                context
            )
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName(),
            "MissingEnvironmentValueException.environmentValueName"
        );
    }

    @Test
    public void testDeleteWithSpreadsheetIdAndCellAndPresentSpreadsheetEnvironmentValueSpreadsheetId() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID1)
        );

        storage.delete(
            StoragePath.parse("/spreadsheet/111/cell/A1"),
            context
        );

        this.loadAndCheck(
            storage,
            StoragePath.parse("/cell/A1"),
            context
        );
    }

    @Test
    public void testDeleteWithSpreadsheetIdAndCell2() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/spreadsheet/222/cell/B2");

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
    public void testDeleteWithSpreadsheetIdAndLabel() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/spreadsheet/111/label/Label111");

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
    public void testDeleteWithSpreadsheetIdAndLabel2() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/spreadsheet/222/label/Label222");

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

    // Storage.list.....................................................................................................

    @Test
    public void testListWithSpreadsheet() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet"),
            0, // offset
            3, // count
            context,
            METADATA_INFO1,
            METADATA_INFO2
        );
    }

    @Test
    public void testListWithSpreadsheetAndOffset() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet"),
            1, // offset
            3, // count
            context,
            METADATA_INFO2
        );
    }

    @Test
    public void testListWithSpreadsheetAndOffset2() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet"),
            2, // offset
            3, // count
            context
        );
    }

    @Test
    public void testListWithSpreadsheetAndSize() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet"),
            0, // offset
            1, // count
            context,
            METADATA_INFO1
        );
    }

    @Test
    public void testListWithCell() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.listAndCheck(
            storage,
            StoragePath.parse("/cell"),
            0, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse("/cell/A1"),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListWithLabel() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID1
        );

        this.listAndCheck(
            storage,
            StoragePath.parse("/label"),
            0, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse("/label/Label111"),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListWithSpreadsheetIdCell1() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet/111/cell"),
            0, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse("/spreadsheet/111/cell/A1"),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListWithSpreadsheetIdCell2() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet/222/cell"),
            0, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse("/spreadsheet/222/cell/B2"),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListWithSpreadsheetIdLabel1() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet/111/label/"),
            0, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse("/spreadsheet/111/label/Label111"),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListWithSpreadsheetIdLabel2() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        this.listAndCheck(
            storage,
            StoragePath.parse("/spreadsheet/222/label/"),
            0, // offset
            2, // count
            context,
            StorageValueInfo.with(
                StoragePath.parse("/spreadsheet/222/label/Label222"),
                AUDIT_INFO
            )
        );
    }

    @Test
    public void testListWithOther() {
        final SpreadsheetStorageRouter storage = this.createStorage();
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path1 = StoragePath.parse("/other/1.txt");
        final String value1 = "value123";

        this.saveAndCheck(
            storage,
            StorageValue.with(
                path1,
                Optional.of(value1)
            ),
            context,
            StorageValue.with(
                path1,
                Optional.of(value1)
            )
        );

        final StoragePath path2 = StoragePath.parse("/other/2.txt");
        final String value2 = "value223";

        this.saveAndCheck(
            storage,
            StorageValue.with(
                path2,
                Optional.of(value2)
            ),
            context,
            StorageValue.with(
                path2,
                Optional.of(value2)
            )
        );

        this.listAndCheck(
            storage,
            StoragePath.ROOT,
            0,
            2,
            context,
            StorageValueInfo.with(
                StoragePath.parse("/other"),
                AUDIT_INFO
            )
        );

        this.listAndCheck(
            storage,
            StoragePath.parse("/other/"),
            0,
            3,
            context,
            StorageValueInfo.with(
                path1,
                AUDIT_INFO
            ),
            StorageValueInfo.with(
                path2,
                AUDIT_INFO
            )
        );
    }

    @Override
    public SpreadsheetStorageRouter createStorage() {
        return SpreadsheetStorageRouter.with(
            SpreadsheetStorages.cell(),
            SpreadsheetStorages.label(),
            SpreadsheetStorages.metadata(),
            Storages.tree()
        );
    }

    @Override
    public SpreadsheetStorageContext createContext() {
        return this.createContext(
            this.createSpreadsheetContext()
        );
    }

    private SpreadsheetStorageContext createContext(final SpreadsheetContext context) {
        return SpreadsheetStorageContexts.spreadsheetContext(context);
    }

    private SpreadsheetContext createSpreadsheetContext() {
        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();
        metadataStore.save(METADATA1);
        metadataStore.save(METADATA2);

        final Map<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdSpreadsheetStoreRepository = Maps.sorted();

        {
            final SpreadsheetStoreRepository repo1 = SpreadsheetStoreRepositories.treeMap(metadataStore);

            repo1.cells()
                .save(CELL1);
            repo1.labels()
                .save(MAPPING1);

            spreadsheetIdSpreadsheetStoreRepository.put(
                SPREADSHEET_ID1,
                repo1
            );
        }

        {
            final SpreadsheetStoreRepository repo2 = SpreadsheetStoreRepositories.treeMap(metadataStore);

            repo2.cells()
                .save(CELL2);
            repo2.labels()
                .save(MAPPING2);

            spreadsheetIdSpreadsheetStoreRepository.put(
                SPREADSHEET_ID2,
                repo2
            );
        }

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.removeEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID
        );

        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.CONVERTER,
            SpreadsheetMetadataTesting.METADATA_EN_AU.getOrFail(SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER)
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET.toEnvironmentValueName(),
            Converters.EXCEL_1900_DATE_SYSTEM_OFFSET
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT.toEnvironmentValueName(),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.DEFAULT_YEAR.toEnvironmentValueName(),
            1900
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND.toEnvironmentValueName(),
            ExpressionNumberKind.DEFAULT
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.PRECISION.toEnvironmentValueName(),
            MathContext.DECIMAL32.getPrecision()
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName(),
            MathContext.DECIMAL32.getRoundingMode()
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR.toEnvironmentValueName(),
            20
        );
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.VALUE_SEPARATOR.toEnvironmentValueName(),
            ','
        );


        spreadsheetEnvironmentContext.setUser(
            Optional.of(
                EmailAddress.parse("user@example.com")
            )
        );

        final SpreadsheetMetadataContext spreadsheetMetadataContext = SpreadsheetMetadataContexts.basic(
            (u, dl) -> {
                throw new UnsupportedOperationException();
            },
            metadataStore
        );

        final SpreadsheetContextSupplier spreadsheetContextSupplier = (final SpreadsheetId id) -> {
            final SpreadsheetStoreRepository repo = spreadsheetIdSpreadsheetStoreRepository.get(id);
            if (null == repo) {
                throw new IllegalArgumentException("SpreadsheetStoreRepository: Missing for SpreadsheetId " + id);
            }

            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext2 = spreadsheetEnvironmentContext.cloneEnvironment();
            spreadsheetEnvironmentContext2.setSpreadsheetId(
                Optional.of(id)
            );

            return Optional.of(
                SpreadsheetContexts.fixedSpreadsheetId(
                    SpreadsheetEngines.basic(),
                    repo,
                    (c) -> {
                        throw new UnsupportedOperationException();
                    },// httpRouterFactory
                    spreadsheetEnvironmentContext2,
                    LOCALE_CONTEXT,
                    SPREADSHEET_PROVIDER,
                    PROVIDER_CONTEXT
                )
            );
        };


//        return SpreadsheetContexts.mutableSpreadsheetId(
//            SpreadsheetEngines.basic(),
//            (final SpreadsheetId id) -> {
//                final SpreadsheetStoreRepository repo = spreadsheetIdSpreadsheetStoreRepository.get(id);
//                if (null == repo) {
//                    throw new IllegalArgumentException("SpreadsheetStoreRepository: Missing for SpreadsheetId " + id);
//                }
//                return Optional.of(
//                    new FakeSpreadsheetContext() {
//
//                        @Override
//                        public SpreadsheetStoreRepository storeRepository() {
//                            return repo;
//                        }
//                    }
//                );
//            }, // spreadsheetIdToStoreRepository
//            spreadsheetMetadataContext,
//            spreadsheetEnvironmentContext,
//            LOCALE_CONTEXT,
//            SPREADSHEET_PROVIDER,
//            PROVIDER_CONTEXT
//        );

        return this.createSpreadsheetContext(
            spreadsheetContextSupplier,
            spreadsheetMetadataContext,
            spreadsheetEnvironmentContext
        );
    }

    private SpreadsheetContext createSpreadsheetContext(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                        final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return SpreadsheetContexts.mutableSpreadsheetId(
            SpreadsheetEngines.basic(),
//            (final SpreadsheetId id) -> {
//                final SpreadsheetStoreRepository repo = spreadsheetIdSpreadsheetStoreRepository.get(id);
//                if (null == repo) {
//                    throw new IllegalArgumentException("SpreadsheetStoreRepository: Missing for SpreadsheetId " + id);
//                }
//                return Optional.of(
//                    new FakeSpreadsheetContext() {
//
//                        @Override
//                        public SpreadsheetStoreRepository storeRepository() {
//                            return repo;
//                        }
//                    }
//                );
//            }, // spreadsheetIdToStoreRepository
            spreadsheetContextSupplier,
            spreadsheetMetadataContext,
            spreadsheetEnvironmentContext,
            LOCALE_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetStorageRouter.with(
                CELLS,
                LABELS,
                METADATAS,
                OTHER
            ),
            "/cell " + CELLS + ", /label " + LABELS + ", /spreadsheet " + METADATAS + ", /* " + OTHER
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetStorageRouter> type() {
        return SpreadsheetStorageRouter.class;
    }
}
