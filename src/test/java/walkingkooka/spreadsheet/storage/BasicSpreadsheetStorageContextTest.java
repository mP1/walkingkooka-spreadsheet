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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.header.MediaTypeDetector;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.spreadsheet.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.FakeSpreadsheetMetadataCreator;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContexts;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.Storages;
import walkingkooka.store.StoreWatcher;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetStorageContextTest implements SpreadsheetStorageContextTesting2<BasicSpreadsheetStorageContext>,
    SpreadsheetMetadataTesting {

    private final static FormName FORM_NAME = FormName.with("Form111");

    private final static SpreadsheetLabelName LABEL_NAME = SpreadsheetSelection.labelName("Label123");

    // loadCells........................................................................................................

    @Test
    public void testLoadCellsAndEnvironmentMissingSpreadsheetId() {
        this.loadCellsAndCheck(
            this.createContext(),
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testLoadCellsWithUnknownCell() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.loadCellsAndCheck(
            context,
            SpreadsheetSelection.A1
        );
    }

    // Cell A1
    //  Formula
    //    value:
    //      "Hello World"
    //    error:
    //      #FORMATTING
    //        "Unknown formatter date"
    //  formattedValue:
    //    Text "#ERROR"
    @Test
    public void testLoadCells() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello World")
            )
        );

        repo.cells()
            .save(cell);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.loadCellsAndCheck(
            context,
            cell.reference(),
            cell.setFormula(
                cell.formula()
                    .setError(
                        Optional.of(
                            SpreadsheetErrorKind.FORMATTING.setMessage("Unknown formatter date")
                        )
                    )
            ).setFormattedValue(
                Optional.of(
                    TextNode.text("#ERROR")
                )
            )
        );
    }

    // saveCells........................................................................................................

    @Test
    public void testSaveCellsAndEnvironmentMissingSpreadsheetIdFails() {
        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createContext()
                .saveCells(
                    Sets.of(
                        SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    )
                )
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName(),
            "MissingEnvironmentValueException.environmentValueName"
        );
    }

    @Test
    public void testSaveCells() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello World")
            )
        );

        repo.cells()
            .save(cell);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.saveCellsAndCheck(
            context,
            Sets.of(cell),
            Sets.of(
                cell.setFormula(
                    cell.formula()
                        .setError(
                            Optional.of(
                                SpreadsheetErrorKind.FORMATTING.setMessage("Unknown formatter date")
                            )
                        )
                ).setFormattedValue(
                    Optional.of(
                        TextNode.text("#ERROR")
                    )
                )
            )
        );
    }

    // deleteCells......................................................................................................

    @Test
    public void testDeleteCellsAndEnvironmentMissingSpreadsheetId() {
        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createContext()
                .deleteCells(SpreadsheetSelection.A1)
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName(),
            "MissingEnvironmentValueException.environmentValueName"
        );
    }

    @Test
    public void testDeleteCellsWithUnknownCell() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        context.deleteCells(SpreadsheetSelection.A1);
    }

    @Test
    public void testDeleteCells() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello World")
            )
        );

        repo.cells()
            .save(cell);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        context.deleteCells(SpreadsheetSelection.A1);

        this.loadCellsAndCheck(
            context,
            cell.reference()
        );
    }

    // addCellWatcher...................................................................................................

    @Test
    @Override
    public void testAddCellWatcherWithNullWatcherFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        assertThrows(
            NullPointerException.class,
            () ->
                context.addCellWatcher(null)
        );
    }

    @Test
    public void testAddCellWatcherAndSaveCells() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello World")
            )
        );

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        final SpreadsheetCell savedCell = cell.setFormula(
            cell.formula()
                .setError(
                    Optional.of(
                        SpreadsheetErrorKind.FORMATTING.setMessage("Unknown formatter date")
                    )
                )
        ).setFormattedValue(
            Optional.of(
                TextNode.text("#ERROR")
            )
        );

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
                        Optional.of(savedCell),
                        newValue,
                        "newValue"
                    );

                    BasicSpreadsheetStorageContextTest.this.fired = true;
                }
            }
        );

        this.fired = false;

        this.saveCellsAndCheck(
            context,
            Sets.of(cell),
            Sets.of(savedCell)
        );

        this.checkEquals(
            true,
            this.fired
        );
    }

    // addCellWatcherOnce...............................................................................................

    @Test
    @Override
    public void testAddCellWatcherOnceWithNullWatcherFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        assertThrows(
            NullPointerException.class,
            () ->
                context.addCellWatcherOnce(null)
        );
    }

    @Test
    public void testAddCellWatcherOnceAndSaveCells() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello World")
            )
        );

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        final SpreadsheetCell savedCell = cell.setFormula(
            cell.formula()
                .setError(
                    Optional.of(
                        SpreadsheetErrorKind.FORMATTING.setMessage("Unknown formatter date")
                    )
                )
        ).setFormattedValue(
            Optional.of(
                TextNode.text("#ERROR")
            )
        );

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
                        Optional.of(savedCell),
                        newValue,
                        "newValue"
                    );

                    BasicSpreadsheetStorageContextTest.this.fired = true;
                }
            }
        );

        this.fired = false;

        this.saveCellsAndCheck(
            context,
            Sets.of(cell),
            Sets.of(savedCell)
        );

        this.checkEquals(
            true,
            this.fired
        );

        context.saveCells(
            Sets.of(
                SpreadsheetSelection.A1.setFormula(
                    SpreadsheetFormula.EMPTY.setValue(
                        Optional.of("Different value not seen by cell StoreWatcher")
                    )
                )
            )
        );
    }

    // loadForm........................................................................................................

    @Test
    public void testLoadFormAndEnvironmentMissingSpreadsheetId() {
        this.loadFormAndCheck(
            this.createContext(),
            FORM_NAME
        );
    }

    @Test
    public void testLoadFormMissingForm() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.loadFormAndCheck(
            context,
            FORM_NAME
        );
    }

    @Test
    public void testLoadForm() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(FORM_NAME);

        repo.forms()
            .save(form);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.loadFormAndCheck(
            context,
            FORM_NAME,
            form
        );
    }

    // saveForm.........................................................................................................

    @Test
    public void testSaveFormAndEnvironmentMissingSpreadsheetIdFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> context.saveForm(
                SpreadsheetForms.form(FORM_NAME)
            )
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName()
        );
    }

    @Test
    public void testSaveForm() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(FORM_NAME);

        this.saveFormAndCheck(
            context,
            form,
            form
        );
    }

    // deleteForm.......................................................................................................

    @Test
    public void testDeleteFormAndEnvironmentMissingSpreadsheetIdFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> context.deleteForm(FORM_NAME)
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName()
        );
    }

    @Test
    public void testDeleteForm() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        repo.forms()
            .save(
                SpreadsheetForms.form(FORM_NAME)
            );

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        context.deleteForm(FORM_NAME);

        this.loadFormAndCheck(
            context,
            FORM_NAME
        );
    }

    // findFormsByNameForm..............................................................................................

    @Test
    public void testFindFormsByNameFormAndEnvironmentMissingSpreadsheetId() {
        this.findFormsByNameAndCheck(
            this.createContext(),
            "",
            0,
            3
        );
    }

    @Test
    public void testFindFormsByName() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final Form<SpreadsheetValidationReference> form1 = SpreadsheetForms.form(
            FormName.with("Form111")
        );

        repo.forms()
            .save(form1);

        final Form<SpreadsheetValidationReference> form2 = SpreadsheetForms.form(
            FormName.with("Form222")
        );

        repo.forms()
            .save(form2);

        final Form<SpreadsheetValidationReference> form3 = SpreadsheetForms.form(
            FormName.with("Form333")
        );

        repo.forms()
            .save(form3);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.findFormsByNameAndCheck(
            context,
            "",
            0,
            3,
            form1,
            form2,
            form3
        );
    }

    @Test
    public void testFindFormsByNameWithOffsetAndCount() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final Form<SpreadsheetValidationReference> form1 = SpreadsheetForms.form(
            FormName.with("Form111")
        );

        repo.forms()
            .save(form1);

        final Form<SpreadsheetValidationReference> form2 = SpreadsheetForms.form(
            FormName.with("Form222")
        );

        repo.forms()
            .save(form2);

        final Form<SpreadsheetValidationReference> form3 = SpreadsheetForms.form(
            FormName.with("Form333")
        );

        repo.forms()
            .save(form3);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.findFormsByNameAndCheck(
            context,
            "",
            1,
            1,
            form2
        );
    }

    // addFormWatcher...................................................................................................

    @Test
    @Override
    public void testAddFormWatcherWithNullWatcherFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        assertThrows(
            NullPointerException.class,
            () -> context.addFormWatcher(null)
        );
    }

    @Test
    public void testAddFormWatcherAndSaveForm() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(FORM_NAME);

        this.fired = true;
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

                    BasicSpreadsheetStorageContextTest.this.fired = true;
                }
            }
        );

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
    }

    // addFormWatcherOnce...............................................................................................

    @Test
    @Override
    public void testAddFormWatcherOnceWithNullWatcherFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        assertThrows(
            NullPointerException.class,
            () -> context.addFormWatcherOnce(null)
        );
    }

    @Test
    public void testAddFormWatcherOnceAndSaveForm() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(FORM_NAME);

        this.fired = true;
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

                    BasicSpreadsheetStorageContextTest.this.fired = true;
                }
            }
        );

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

        context.saveForm(
            form.setName(
                FormName.with("DifferentForm222")
            )
        );
    }

    // loadLabel........................................................................................................

    @Test
    public void testLoadLabelAndEnvironmentMissingSpreadsheetId() {
        this.loadLabelAndCheck(
            this.createContext(),
            LABEL_NAME
        );
    }

    @Test
    public void testLoadLabelMissingLabel() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.loadLabelAndCheck(
            context,
            LABEL_NAME
        );
    }

    @Test
    public void testLoadLabel() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        final SpreadsheetLabelMapping mapping = LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.A1);

        repo.labels()
            .save(mapping);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.loadLabelAndCheck(
            context,
            LABEL_NAME,
            mapping
        );
    }

    // saveLabel........................................................................................................

    @Test
    public void testSaveLabelAndEnvironmentMissingSpreadsheetIdFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> context.saveLabel(
                LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.A1)
            )
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName()
        );
    }

    @Test
    public void testSaveLabel() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        repo.labels()
            .save(
                LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.labelName("ReplacedBySave"))
            );

        final SpreadsheetLabelMapping mapping = LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.A1);

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.saveLabelAndCheck(
            context,
            mapping,
            mapping
        );
    }

    // deleteLabel......................................................................................................

    @Test
    public void testDeleteLabelAndEnvironmentMissingSpreadsheetIdFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();

        final MissingEnvironmentValueException thrown = assertThrows(
            MissingEnvironmentValueException.class,
            () -> context.deleteLabel(LABEL_NAME)
        );

        this.checkEquals(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            thrown.environmentValueName()
        );
    }

    @Test
    public void testDeleteLabel() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        repo.labels()
            .save(LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.A1));

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        context.deleteLabel(LABEL_NAME);

        this.loadLabelAndCheck(
            context,
            LABEL_NAME
        );
    }

    // findLabelsByNameLabel............................................................................................

    @Test
    public void testFindLabelsByNameLabelAndEnvironmentMissingSpreadsheetId() {
        this.findLabelsByNameAndCheck(
            this.createContext(),
            "",
            0,
            3
        );
    }

    @Test
    public void testFindLabelsByNameLabel() {
        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(
            SpreadsheetMetadataStores.treeMap()
        );

        repo.labels()
            .save(LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.A1));

        final SpreadsheetLabelName labelName2 = SpreadsheetSelection.labelName("Label222");

        repo.labels()
            .save(
                labelName2.setLabelMappingReference(SpreadsheetSelection.A1));

        final BasicSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        this.findLabelsByNameAndCheck(
            context,
            "",
            0,
            3,
            LABEL_NAME,
            labelName2
        );
    }

    // addLabelWatcher..................................................................................................

    @Test
    @Override
    public void testAddLabelWatcherWithNullWatcherFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        assertThrows(
            NullPointerException.class,
            () -> context.addLabelWatcher(null)
        );
    }

    @Test
    public void testAddLabelWatcherAndSaveLabel() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        final SpreadsheetLabelMapping mapping = LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.A1);

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

                    BasicSpreadsheetStorageContextTest.this.fired = true;
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
    }

    // addLabelWatcherOnce..............................................................................................

    @Test
    @Override
    public void testAddLabelWatcherOnceWithNullWatcherFails() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        assertThrows(
            NullPointerException.class,
            () -> context.addLabelWatcherOnce(null)
        );
    }

    @Test
    public void testAddLabelWatcherOnceAndSaveLabel() {
        final BasicSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        final SpreadsheetLabelMapping mapping = LABEL_NAME.setLabelMappingReference(SpreadsheetSelection.A1);

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

                    BasicSpreadsheetStorageContextTest.this.fired = true;
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

        context.saveLabel(
            mapping.setLabel(
                SpreadsheetSelection.labelName("DifferentLabel")
            )
        );
    }

    private boolean fired;

    // Storage..........................................................................................................

    @Test
    public void testSaveStorageAndLoadStorage() {
        final BasicSpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/path1/to2/value3");
        final String value = "Hello456";

        final StorageValue storageValue = StorageValue.with(path)
            .setValue(
                Optional.of(value)
            );

        context.saveStorage(storageValue);

        this.loadStorageAndCheck(
            context,
            path,
            storageValue
        );
    }

    @Override
    public BasicSpreadsheetStorageContext createContext() {
        return this.createContext(
            SpreadsheetStoreRepositories.treeMap(
                SpreadsheetMetadataStores.treeMap()
            )
        );
    }

    private BasicSpreadsheetStorageContext createContext(final SpreadsheetStoreRepository repo) {
        final SpreadsheetMetadataStore metadataStore = repo.metadatas();

        metadataStore.save(
            METADATA_EN_AU.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SPREADSHEET_ID
            )
        );

        final Locale locale = Locale.forLanguageTag("en-AU");

        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                CHARSET,
                CURRENCY,
                INDENTATION,
                LINE_ENDING,
                locale,
                HAS_NOW,
                OPTIONAL_USER
            )
        );

        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            BasicSpreadsheetStorageContextTest.CURRENT_WORKING_DIRECTORY
        );

        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            BasicSpreadsheetStorageContextTest.SERVER_URL
        );

        final Storage<SpreadsheetStorageContext> storage = Storages.treeMapStore();
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            storage,
            environmentContext
        );

        final CurrencyLocaleContext currencyLocaleContext = CURRENCY_CONTEXT.setLocaleContext(
            LocaleContexts.jre(locale)
        );

        final ProviderContext providerContext = ProviderContexts.basic(
            ConverterContexts.fake(),
            spreadsheetEnvironmentContext.cloneEnvironment(),
            PluginStores.fake()
        );

        final SpreadsheetProvider spreadsheetProvider = SpreadsheetProviders.basic(
            CONVERTER_PROVIDER,
            ExpressionFunctionProviders.empty(
                SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY
            ),
            SpreadsheetComparatorProviders.empty(),
            SpreadsheetExporterProviders.empty(),
            SpreadsheetFormatterProviders.empty(),
            FormHandlerProviders.empty(),
            SpreadsheetImporterProviders.empty(),
            SpreadsheetParserProviders.empty(),
            ValidatorProviders.empty()
        );

        final MediaTypeDetector mediaTypeDetector = MediaTypeDetectors.binary();

        final SpreadsheetEngineContext spreadsheetEngineContext = SpreadsheetEngineContexts.spreadsheetEnvironmentContext(
            mediaTypeDetector,
            BinaryNumberConverterFunctions.fake(),
            SPREADSHEET_ENGINE,
            new SpreadsheetContextSupplier() {
                @Override
                public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                    return Optional.of(
                        new FakeSpreadsheetContext() {
                            @Override
                            public SpreadsheetStoreRepository storeRepository() {
                                return repo;
                            }

                            @Override
                            public SpreadsheetMetadata spreadsheetMetadata() {
                                return metadataStore.loadOrFail(id);
                            }

                            @Override
                            public void setLocale(final Locale locale) {
                                environmentContext.setLocale(locale);
                            }

                            @Override
                            public ProviderContext providerContext() {
                                return providerContext;
                            }

                            @Override
                            public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                                             final ProviderContext context) {
                                return spreadsheetProvider.spreadsheetFormatter(
                                    selector,
                                    context
                                );
                            }
                        }
                    );
                }
            },
            currencyLocaleContext,
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataContexts.basic(
                new FakeSpreadsheetMetadataCreator(),
                metadataStore
            ),
            TERMINAL_CONTEXT,
            spreadsheetProvider,
            providerContext
        );

        return BasicSpreadsheetStorageContext.with(
            SpreadsheetEngines.basic(),
            spreadsheetEnvironmentContext,
            (SpreadsheetId spreadsheetId) -> Optional.of(
                spreadsheetEngineContext
            ),
            SpreadsheetMetadataContexts.basic(
                (e, l) -> {
                    throw new UnsupportedOperationException();
                },
                metadataStore
            ),
            StorageContexts.basic(
                ConverterContexts.fake(), // ConverterLike
                mediaTypeDetector,
                spreadsheetEnvironmentContext // EnvironmentContext
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetStorageContext> type() {
        return BasicSpreadsheetStorageContext.class;
    }
}
