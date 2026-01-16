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
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
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
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storages;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetContextSpreadsheetStorageContextTest implements SpreadsheetStorageContextTesting2<SpreadsheetContextSpreadsheetStorageContext> {

    private final static Locale LOCALE = Locale.ENGLISH;

    // with.............................................................................................................

    @Test
    public void testWithNullEngineContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSpreadsheetStorageContext.with(
                null,
                SpreadsheetContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSpreadsheetStorageContext.with(
                SpreadsheetEngines.fake(),
                null
            )
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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

        this.checkNotEquals(
            null,
            context.loadMetadata(SpreadsheetId.with(1))
        );
    }

    @Test
    public void testSaveSpreadsheetMetadata() {
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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
        final SpreadsheetContextSpreadsheetStorageContext context = this.createContext();

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

    @Override
    public void testSetSpreadsheetIdWithSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetContextSpreadsheetStorageContext createContext() {
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

        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                LineEnding.NL,
                LOCALE,
                () -> LocalDateTime.MIN,
                EnvironmentContext.ANONYMOUS
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

        return SpreadsheetContextSpreadsheetStorageContext.with(
            SpreadsheetEngines.basic(),
            SpreadsheetContexts.fixedSpreadsheetId(
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
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetContextSpreadsheetStorageContext> type() {
        return SpreadsheetContextSpreadsheetStorageContext.class;
    }
}
