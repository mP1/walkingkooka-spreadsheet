
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
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.provider.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.storage.InvalidStoragePathException;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetStorageFormTest extends SpreadsheetStorageTestCase<SpreadsheetStorageForm> {

    private final static FormName FORM_NAME1 = FormName.with("Form111");

    private final static Form<SpreadsheetValidationReference> FORM1 = SpreadsheetForms.form(FORM_NAME1);

    private final static FormName FORM_NAME2 = FormName.with("Form222");

    private final static Form<SpreadsheetValidationReference> FORM2 = SpreadsheetForms.form(FORM_NAME2);

    private final static FormName FORM_NAME3 = FormName.with("Form333");

    private final static Form<SpreadsheetValidationReference> FORM3 = SpreadsheetForms.form(FORM_NAME3);

    private final static StorageValueInfo INFO1 = StorageValueInfo.with(
        StoragePath.parse("/" + FORM_NAME1),
        SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo()
    );

    private final static StorageValueInfo INFO2 = StorageValueInfo.with(
        StoragePath.parse("/" + FORM_NAME2),
        SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo()
    );

    private final static StorageValueInfo INFO3 = StorageValueInfo.with(
        StoragePath.parse("/" + FORM_NAME3),
        SPREADSHEET_ENVIRONMENT_CONTEXT.createdAuditInfo()
    );

    @Test
    public void testLoadWithExtraPathFails() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .load(
                    StoragePath.parse("/" + FORM_NAME1 + "/extra"),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid extra path after FormName \"/Form111/extra\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testLoadMissingForm() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/" + FORM_NAME1);

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
            .saveForm(
                FORM1,
                spreadsheetContext.spreadsheetEngineContext()
            );
        final SpreadsheetStorageContext context = this.createContext(spreadsheetContext);


        final StoragePath path = StoragePath.parse("/" + FORM_NAME1);

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(FORM1)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_FORM)
        );
    }

    @Test
    public void testLoadWIthFileExtension() {
        final SpreadsheetContext spreadsheetContext = this.createSpreadsheetContext();

        SpreadsheetEngines.basic()
            .saveForm(
                FORM1,
                spreadsheetContext.spreadsheetEngineContext()
            );
        final SpreadsheetStorageContext context = this.createContext(spreadsheetContext);


        final StoragePath path = StoragePath.parse("/" + FORM_NAME1 + ".json");

        this.loadAndCheck(
            this.createStorage(),
            path,
            context,
            StorageValue.with(
                path,
                Optional.of(FORM1)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_FORM)
        );
    }

    @Test
    public void testSaveWithExtraPathFails() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/" + FORM_NAME1 + "/extra"),
                        Optional.of(FORM1)
                    ),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after FormName \"/Form111/extra\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testSaveWithStorageValueMissingForm() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStorage()
                .save(
                    StorageValue.with(
                        StoragePath.parse("/" + FORM_NAME1),
                        Optional.empty()
                    ),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Missing Form",
            thrown.getMessage()
        );
    }

    @Test
    public void testSave() {
        final SpreadsheetStorageContext context = this.createContext();

        final StoragePath path = StoragePath.parse("/" + FORM_NAME1);

        this.saveAndCheck(
            this.createStorage(),
            StorageValue.with(
                path,
                Optional.of(FORM1)
            ),
            context,
            StorageValue.with(
                path,
                Optional.of(FORM1)
            ).setContentType(SpreadsheetMediaTypes.MEMORY_FORM)
        );
    }

    @Test
    public void testDeleteMissingFormFails() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.ROOT,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Missing FormName \"/\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testDeleteWithExtraPathFails() {
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .delete(
                    StoragePath.parse("/" + FORM_NAME1 + "/extra"),
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after FormName \"/Form111/extra\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testDelete() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveForm(FORM1);

        final StoragePath path = StoragePath.parse("/" + FORM_NAME1);

        final SpreadsheetStorageForm storage = this.createStorage();
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
        final InvalidStoragePathException thrown = assertThrows(
            InvalidStoragePathException.class,
            () -> this.createStorage()
                .list(
                    StoragePath.parse("/" + FORM_NAME1 + "/extra"),
                    0,
                    1,
                    this.createContext()
                )
        );

        this.checkEquals(
            "Invalid path after FormName \"/Form111/extra\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testListMissingForm() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveForm(FORM1);
        context.saveForm(FORM2);
        context.saveForm(FORM3);

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
    public void testListMissingFormWithOffset() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveForm(FORM1);
        context.saveForm(FORM2);
        context.saveForm(FORM3);

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
    public void testListMissingFormWithCount() {
        final SpreadsheetStorageContext context = this.createContext();

        context.saveForm(FORM1);
        context.saveForm(FORM2);
        context.saveForm(FORM3);

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

        context.saveForm(FORM1);
        context.saveForm(FORM2);
        context.saveForm(FORM3);

        this.listAndCheck(
            this.createStorage(),
            StoragePath.parse("/Form"),
            0,
            2,
            context,
            INFO1,
            INFO2
        );
    }

    @Override
    public SpreadsheetStorageForm createStorage() {
        return SpreadsheetStorageForm.INSTANCE;
    }

    @Override
    public SpreadsheetStorageContext createContext() {
        return this.createContext(
            this.createSpreadsheetContext()
        );
    }

    private SpreadsheetStorageContext createContext(final SpreadsheetContext spreadsheetContext) {
        return SpreadsheetStorageContexts.spreadsheetContext(spreadsheetContext);
    }

    private SpreadsheetContext createSpreadsheetContext() {
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

        final LocaleContext localeContext = LocaleContexts.jre(LOCALE);

        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        metadataStore.save(
            SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.LOCALE,
                    LOCALE
                ).loadFromLocale(
                    CURRENCY_CONTEXT.setLocaleContext(localeContext)
                ).set(
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
                Currency.getInstance("AUD"),
                Indentation.SPACES4,
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

        final Storage<SpreadsheetStorageContext> storage = Storages.treeMapStore();

        return SpreadsheetContexts.fixedSpreadsheetId(
            SpreadsheetEngines.basic(),
            SpreadsheetStoreRepositories.treeMap(metadataStore),
            (c) -> {
                throw new UnsupportedOperationException();
            }, // HttpRouter
            CurrencyContexts.fake()
                .setLocaleContext(localeContext),
            SpreadsheetEnvironmentContexts.basic(
                storage,
                environmentContext
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
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetStorageForm> type() {
        return SpreadsheetStorageForm.class;
    }
}
