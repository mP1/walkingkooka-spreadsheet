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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.plugin.store.PluginStores;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
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
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContextTesting2;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContextTest implements SpreadsheetStorageContextTesting2<SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext>,
    SpreadsheetMetadataTesting {

    private final static SpreadsheetEngine SPREADSHEET_ENGINE = SpreadsheetEngines.fake();

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

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
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        context.deleteCells(SpreadsheetSelection.A1);

        this.loadCellsAndCheck(
            context,
            cell.reference()
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
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext();
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        this.loadLabelAndCheck(
            context,
            LABEL_NAME,
            mapping
        );
    }

    // saveLabel........................................................................................................

    @Test
    public void testSaveLabelAndEnvironmentMissingSpreadsheetIdFails() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext();

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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        this.saveLabelAndCheck(
            context,
            mapping,
            mapping
        );
    }

    // deleteLabel......................................................................................................

    @Test
    public void testDeleteLabelAndEnvironmentMissingSpreadsheetIdFails() {
        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext();

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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

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

        final SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext context = this.createContext(repo);
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        this.findLabelsByNameAndCheck(
            context,
            "",
            0,
            3,
            LABEL_NAME,
            labelName2
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext createContext() {
        return this.createContext(
            SpreadsheetStoreRepositories.treeMap(
                SpreadsheetMetadataStores.treeMap()
            )
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext createContext(final SpreadsheetStoreRepository repo) {
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
                INDENTATION,
                LineEnding.NL,
                locale,
                () -> LocalDateTime.MIN,
                Optional.of(
                    EmailAddress.parse("user@example.com")
                )
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );

        final Storage<SpreadsheetStorageContext> storage = Storages.tree();
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            storage,
            environmentContext
        );

        final LocaleContext localeContext = LocaleContexts.jre(locale);

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

        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext.with(
            SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext.with(
                new SpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.of(
                            SpreadsheetContexts.fixedSpreadsheetId(
                                SpreadsheetEngines.basic(),
                                repo,
                                (c) -> {
                                    throw new UnsupportedOperationException();
                                }, // Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory
                                spreadsheetEnvironmentContext,
                                localeContext,
                                spreadsheetProvider,
                                providerContext
                            )
                        );
                    }
                },
                localeContext,
                spreadsheetEnvironmentContext,
                SpreadsheetMetadataContexts.basic(
                    (e, l) -> {
                        throw new UnsupportedOperationException();
                    },
                    metadataStore
                ),
                TERMINAL_CONTEXT,
                spreadsheetProvider,
                providerContext
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext> type() {
        return SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext.class;
    }
}
