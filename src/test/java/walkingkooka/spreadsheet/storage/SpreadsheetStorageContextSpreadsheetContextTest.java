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
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.HasAuditInfoTesting;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
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
import walkingkooka.spreadsheet.meta.FakeSpreadsheetMetadataCreator;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageMountPoint;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageTesting;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.store.StoreWatcher;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetStorageContextSpreadsheetContextTest implements SpreadsheetStorageContextTesting2<SpreadsheetStorageContextSpreadsheetContext>,
    CurrencyLocaleContextTesting,
    HasAuditInfoTesting,
    StorageTesting {

    // with.............................................................................................................

    @Test
    public void testWithNullEngineContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetStorageContextSpreadsheetContext.with(
                null
            )
        );
    }

    // indentation......................................................................................................

    @Test
    public void testIndentation() {
        this.indentationAndCheck(
            this.createContext(),
            INDENTATION
        );
    }

    // cells............................................................................................................

    @Test
    public void testLoadCells() {
        this.loadCellsAndCheck(
            this.createContext(),
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testSaveCells() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello")
            )
        );

        this.saveCellsAndCheck(
            this.createContext(),
            Sets.of(cell),
            cell.setFormattedValue(
                Optional.of(
                    TextNode.text("Hello")
                )
            )
        );
    }

    @Test
    public void testSaveCellsAndLoad() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello")
            )
        );

        context.saveCells(
            Sets.of(cell)
        );

        this.loadCellsAndCheck(
            context,
            cell.reference(),
            cell.setFormattedValue(
                Optional.of(
                    TextNode.text("Hello")
                )
            )
        );
    }

    @Test
    public void testDeleteCell() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello")
            )
        );

        context.saveCells(
            Sets.of(cell)
        );

        context.deleteCells(
            cell.reference()
        );

        this.loadCellsAndCheck(
            context,
            cell.reference()
        );
    }

    @Test
    public void testAddCellWatcher() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello")
            )
        );

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        context.addCellWatcher(
            new StoreWatcher<>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetCell> oldValue,
                                          final Optional<SpreadsheetCell> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(
                            cell.setFormattedValue(
                                Optional.of(
                                    TextNode.text("Hello")
                                )
                            )
                        ),
                        newValue,
                        "newValue"
                    );

                    SpreadsheetStorageContextSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        this.fired = false;

        this.saveCellsAndCheck(
            context,
            Sets.of(cell),
            cell.setFormattedValue(
                Optional.of(
                    TextNode.text("Hello")
                )
            )
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    @Test
    public void testAddCellWatcherOnce() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello111")
            )
        );

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        context.addCellWatcherOnce(
            new StoreWatcher<>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetCell> oldValue,
                                          final Optional<SpreadsheetCell> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(
                            cell.setFormattedValue(
                                Optional.of(
                                    TextNode.text("Hello111")
                                )
                            )
                        ),
                        newValue,
                        "newValue"
                    );

                    SpreadsheetStorageContextSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        this.fired = false;

        this.saveCellsAndCheck(
            context,
            Sets.of(cell),
            cell.setFormattedValue(
                Optional.of(
                    TextNode.text("Hello111")
                )
            )
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );

        context.saveCells(
            Sets.of(
                cell.setFormula(
                    SpreadsheetFormula.EMPTY.setValue(
                        Optional.of("Hello222") // should not be seen by StorageWatcher#onValueChange
                    )
                )
            )
        );
    }

    // forms............................................................................................................

    @Test
    public void testLoadForm() {
        this.loadFormAndCheck(
            this.createContext(),
            FormName.with("Form123")
        );
    }

    @Test
    public void testSaveForm() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final FormName formName = FormName.with("Form123");
        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(formName);

        this.saveFormAndCheck(
            context,
            form,
            form
        );

        this.loadFormAndCheck(
            context,
            formName,
            form
        );
    }

    @Test
    public void testDeleteForm() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final FormName formName = FormName.with("Form123");
        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(formName);

        context.saveForm(form);
        context.deleteForm(formName);

        this.loadFormAndCheck(
            context,
            formName
        );
    }

    @Test
    public void testFindFormByName() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final Form<SpreadsheetValidationReference> form1 = SpreadsheetForms.form(
            FormName.with("Form111")
        );
        context.saveForm(form1);

        final Form<SpreadsheetValidationReference> form2 = SpreadsheetForms.form(
            FormName.with("Form222")
        );
        context.saveForm(form2);

        final Form<SpreadsheetValidationReference> form3 = SpreadsheetForms.form(
            FormName.with("DifferentForm")
        );

        context.saveForm(form3);

        this.findFormsByNameAndCheck(
            context,
            "Form",
            0,
            3,
            form1,
            form2,
            form3
        );
    }

    @Test
    public void testFindFormByNameWithOffsetAndCount() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final Form<SpreadsheetValidationReference> form1 = SpreadsheetForms.form(
            FormName.with("Form222")
        );
        context.saveForm(form1);

        final Form<SpreadsheetValidationReference> form2 = SpreadsheetForms.form(
            FormName.with("Form333")
        );
        context.saveForm(form2);

        final Form<SpreadsheetValidationReference> form3 = SpreadsheetForms.form(
            FormName.with("Different111")
        );
        context.saveForm(form3);

        this.findFormsByNameAndCheck(
            context,
            "Form",
            1,
            1,
            form2
        );
    }

    @Test
    public void testAddFormWatcherAndSaveForm() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final FormName formName = FormName.with("Form123");
        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(formName);

        context.addFormWatcher(
            new StoreWatcher<>() {
                @Override
                public void onValueChange(final Optional<Form<SpreadsheetValidationReference>> oldValue,
                                          final Optional<Form<SpreadsheetValidationReference>> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(form),
                        newValue,
                        "newValue"
                    );

                    SpreadsheetStorageContextSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        this.fired = false;

        this.saveFormAndCheck(
            context,
            form,
            form
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );

        this.loadFormAndCheck(
            context,
            formName,
            form
        );
    }

    @Test
    public void testAddFormWatcherOnceAndSaveForm() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final FormName formName = FormName.with("Form123");
        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(formName);

        context.addFormWatcherOnce(
            new StoreWatcher<>() {
                @Override
                public void onValueChange(final Optional<Form<SpreadsheetValidationReference>> oldValue,
                                          final Optional<Form<SpreadsheetValidationReference>> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(form),
                        newValue,
                        "newValue"
                    );

                    SpreadsheetStorageContextSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        this.fired = false;

        this.saveFormAndCheck(
            context,
            form,
            form
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );

        this.loadFormAndCheck(
            context,
            formName,
            form
        );

        context.saveForm(
            form.setName(
                FormName.with("DifferentForm222")
            )
        );
    }

    // labels...........................................................................................................

    @Test
    public void testLoadLabel() {
        this.loadLabelAndCheck(
            this.createContext(),
            SpreadsheetSelection.labelName("Label123")
        );
    }

    @Test
    public void testSaveLabel() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(
            SpreadsheetSelection.A1
        );

        this.saveLabelAndCheck(
            context,
            mapping,
            mapping
        );

        this.loadLabelAndCheck(
            context,
            label,
            mapping
        );
    }

    @Test
    public void testDeleteLabel() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(
            SpreadsheetSelection.A1
        );

        context.saveLabel(mapping);
        context.deleteLabel(label);

        this.loadLabelAndCheck(
            context,
            label
        );
    }

    @Test
    public void testFindLabelByName() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(
            SpreadsheetSelection.A1
        );

        context.saveLabel(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label223");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(
            SpreadsheetSelection.A1
        );

        context.saveLabel(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("DifferentLabel");
        final SpreadsheetLabelMapping mapping3 = label2.setLabelMappingReference(
            SpreadsheetSelection.A1
        );

        context.saveLabel(mapping3);

        
        this.findLabelsByNameAndCheck(
            context,
            "Label",
            0,
            2,
            label1,
            label2
        );
    }

    @Test
    public void testAddLabelWatcherSaveLabel() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(
            SpreadsheetSelection.A1
        );

        this.fired = false;

        context.addLabelWatcher(
            new StoreWatcher<>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetLabelMapping> oldValue,
                                          final Optional<SpreadsheetLabelMapping> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(mapping),
                        newValue,
                        "newValue"
                    );

                    SpreadsheetStorageContextSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        this.saveLabelAndCheck(
            context,
            mapping,
            mapping
        );

        this.checkEquals(
            true,
            fired,
            "fired"
        );

        this.loadLabelAndCheck(
            context,
            label,
            mapping
        );
    }

    @Test
    public void testAddLabelWatcherOnceSaveLabel() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(
            SpreadsheetSelection.A1
        );

        this.fired = false;

        context.addLabelWatcherOnce(
            new StoreWatcher<>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetLabelMapping> oldValue,
                                          final Optional<SpreadsheetLabelMapping> newValue) {
                    checkEquals(
                        Optional.empty(),
                        oldValue,
                        "oldValue"
                    );
                    checkEquals(
                        Optional.of(mapping),
                        newValue,
                        "newValue"
                    );

                    SpreadsheetStorageContextSpreadsheetContextTest.this.fired = true;
                }
            }
        );

        this.saveLabelAndCheck(
            context,
            mapping,
            mapping
        );

        this.checkEquals(
            true,
            fired,
            "fired"
        );

        this.loadLabelAndCheck(
            context,
            label,
            mapping
        );

        context.saveLabel(
            mapping.setLabel(
                SpreadsheetSelection.labelName("DifferentLabel")
            )
        );
    }

    private boolean fired;

    // converter........................................................................................................

    @Test
    public void testConvert() {
        this.convertAndCheck(
            "true",
            Boolean.class,
            Boolean.TRUE
        );
    }

    // SpreadsheetContext...............................................................................................

    @Test
    public void testLoadSpreadsheetMetadata() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        this.checkNotEquals(
            null,
            context.loadMetadata(SpreadsheetId.with(1))
        );
    }

    @Test
    public void testSaveSpreadsheetMetadata() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetMetadata metadata = context.loadMetadataOrFail(
            SpreadsheetId.with(1)
        );

        final SpreadsheetMetadata different = metadata.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.with(2)
        ).set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.FRANCE
        );

        this.saveMetadataAndCheck(
            context,
            different,
            different
        );
    }

    @Test
    public void testDeleteSpreadsheetMetadata() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

        final SpreadsheetMetadata metadata = context.loadMetadataOrFail(spreadsheetId);

        this.saveMetadataAndCheck(
            context,
            metadata,
            metadata
        );

        context.deleteMetadata(spreadsheetId);

        this.loadMetadataAndCheck(
            context,
            spreadsheetId
        );
    }

    @Test
    public void testFindMetadataBySpreadsheetName() {
        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext();

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

        final SpreadsheetMetadata metadata1 = context.loadMetadataOrFail(spreadsheetId)
            .set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                SpreadsheetName.with("Hello111")
            );

        this.saveMetadataAndCheck(
            context,
            metadata1,
            metadata1
        );

        final SpreadsheetMetadata metadata2 = metadata1.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.with(2)
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with("Hello222")
        );

        this.saveMetadataAndCheck(
            context,
            metadata2,
            metadata2
        );

        final SpreadsheetMetadata metadata3 = metadata1.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.with(3)
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with("Different")
        );

        this.saveMetadataAndCheck(
            context,
            metadata3,
            metadata3
        );

        this.findMetadataBySpreadsheetNameAndCheck(
            context,
            "Hello",
            0,
            3,
            metadata1,
            metadata2
        );
    }

    // StorageContext...................................................................................................

    private final static StoragePath STORAGE_PATH = StoragePath.parse("/value111");

    private final static StorageValue STORAGE_VALUE = StorageValue.with(STORAGE_PATH)
        .setValue(
            Optional.of(111)
        );

    @Test
    public void testLoadStorage() {
        final Storage<SpreadsheetStorageContext> storage = SpreadsheetStorages.treeMapStore();

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext(storage);

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
        final Storage<SpreadsheetStorageContext> storage = SpreadsheetStorages.treeMapStore();

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext(storage);

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
        final Storage<SpreadsheetStorageContext> storage = SpreadsheetStorages.treeMapStore();

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext(storage);

        this.saveAndCheck(
            storage,
            STORAGE_VALUE,
            context,
            STORAGE_VALUE
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
        final Storage<SpreadsheetStorageContext> storage = SpreadsheetStorages.treeMapStore();

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext(storage);

        this.saveStorageAndCheck(
            context,
            STORAGE_VALUE,
            STORAGE_VALUE
        );

        this.listStorageAndCheck(
            context,
            StoragePath.ROOT,
            0,
            1000,
            StorageValueInfo.with(
                STORAGE_PATH,
                AUDIT_INFO
            )
        );
    }

    private final static StoragePath MOUNT_PATH = StoragePath.parse("/mount1");

    @Test
    public void testMountStorage() {
        final Storage<SpreadsheetStorageContext> root = SpreadsheetStorages.treeMapStore();
        final Storage<SpreadsheetStorageContext> storage = Storages.mount(
            root
        );

        final Storage<SpreadsheetStorageContext> mount = SpreadsheetStorages.treeMapStore();

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext(storage);;

        context.mountStorage(
            StorageMountPoint.with(
                MOUNT_PATH,
                mount
            )
        );

        final StorageValue storageValue = STORAGE_VALUE.setPath(
            MOUNT_PATH.append(STORAGE_PATH)
        );

        this.saveStorageAndCheck(
            context,
            storageValue,
            storageValue
        );

        this.loadAndCheck(
            mount,
            STORAGE_PATH,
            context,
            STORAGE_VALUE
        );
    }

    @Test
    public void testUnmountStorage() {
        final Storage<SpreadsheetStorageContext> root = SpreadsheetStorages.treeMapStore();
        final Storage<SpreadsheetStorageContext> storage = Storages.mount(
            root
        );

        final Storage<SpreadsheetStorageContext> mount = SpreadsheetStorages.treeMapStore();

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext(storage);

        final StorageMountPoint<?> storageMountPoint = StorageMountPoint.with(
            MOUNT_PATH,
            mount
        );

        context.mountStorage(storageMountPoint);

        final StoragePath storagePath = MOUNT_PATH.append(STORAGE_PATH);
        final StorageValue storageValue = STORAGE_VALUE.setPath(storagePath);

        this.saveStorageAndCheck(
            context,
            storageValue,
            storageValue
        );

        context.unmountStorage(MOUNT_PATH);

        this.loadAndCheck(
            root,
            storagePath,
            context
        );
    }

    @Test
    public void testStorageMountPoints() {
        final Storage<SpreadsheetStorageContext> root = SpreadsheetStorages.treeMapStore();
        final Storage<SpreadsheetStorageContext> storage = Storages.mount(
            root
        );

        final Storage<SpreadsheetStorageContext> mount = SpreadsheetStorages.treeMapStore();

        final SpreadsheetStorageContextSpreadsheetContext context = this.createContext(storage);

        final StorageMountPoint<?> storageMountPoint = StorageMountPoint.with(
            MOUNT_PATH,
            mount
        );

        context.mountStorage(storageMountPoint);

        this.storageMountPointsAndCheck(
            context,
            StorageMountPoint.with(
                StoragePath.ROOT,
                root
            ),
            StorageMountPoint.with(
                MOUNT_PATH,
                mount
            )
        );
    }

    @Override
    public SpreadsheetStorageContextSpreadsheetContext createContext() {
        return this.createContext(
            Storages.mount(
                SpreadsheetStorages.treeMapStore()
            )
        );
    }

    private SpreadsheetStorageContextSpreadsheetContext createContext(final Storage<SpreadsheetStorageContext> storage) {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT;

        metadataStore.save(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                LOCALE
            ).loadFromLocale(
                CURRENCY_LOCALE_CONTEXT
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.create(
                    USER,
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

        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        storageEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            spreadsheetId
        );
        storageEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );

        return SpreadsheetStorageContextSpreadsheetContext.with(
            SpreadsheetContexts.fixedSpreadsheetId(
                MediaTypeDetectors.binary(),
                new FakeSpreadsheetMetadataCreator(),
                BinaryNumberConverterFunctions.fake(), // multiplier
                SpreadsheetEngines.basic(),
                SpreadsheetStoreRepositories.treeMap(metadataStore),
                (c) -> {
                    throw new UnsupportedOperationException();
                }, // HttpRouter
                CURRENCY_LOCALE_CONTEXT,
                SpreadsheetEnvironmentContexts.basic(
                    storage,
                    storageEnvironmentContext
                ),
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
            )
        );
    }

    // HasEnvironmentContext............................................................................................

    @Test
    @Override
    public void testEnvironmentContext() {
        final SpreadsheetContext spreadsheetContext = SpreadsheetContexts.fake();

        this.environmentContextAndCheck(
            SpreadsheetStorageContextSpreadsheetContext.with(spreadsheetContext),
            spreadsheetContext
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetStorageContextSpreadsheetContext> type() {
        return SpreadsheetStorageContextSpreadsheetContext.class;
    }
}
