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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetContextTest implements SpreadsheetContextTesting<BasicSpreadsheetContext>,
    ToStringTesting<BasicSpreadsheetContext> {

    private final static SpreadsheetId ID = SpreadsheetId.with(1);

    private final static SpreadsheetStoreRepository REPO = SpreadsheetStoreRepositories.fake();

    private final static SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.fake();

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static AuditInfo AUDIT_INFO = AuditInfo.with(
        EmailAddress.parse("creator@example.com"),
        LocalDateTime.MIN,
        EmailAddress.parse("modified@example.com"),
        LocalDateTime.MIN
    );

    private final static EnvironmentContext ENVIRONMENT_CONTEXT = EnvironmentContexts.map(
        EnvironmentContexts.empty(
            LOCALE,
            LocalDateTime::now,
            Optional.empty() // no user
        )
    );
    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(Locale.ENGLISH);

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullSpreadsheetIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                null,
                REPO,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                ID,
                null,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                ID,
                REPO,
                null,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                null,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderontextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                null
            )
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSpreadsheetId() {
        this.spreadsheetIdAndCheck(
            this.createContext(),
            ID
        );
    }

    // saveMetadata.....................................................................................................

    @Test
    public void testSaveMetadataWithSameId() {
        final BasicSpreadsheetContext context = BasicSpreadsheetContext.with(
            ID,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            SPREADSHEET_PROVIDER,
            EnvironmentContexts.map(ENVIRONMENT_CONTEXT),
            LocaleContexts.fake(),
            PROVIDER_CONTEXT
        );

        final Locale locale = Locale.forLanguageTag("FR");

        final SpreadsheetMetadata saved = context.saveMetadata(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                ID
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AUDIT_INFO
            )
        );

        this.checkEquals(
            locale,
            saved.locale()
        );

        this.localeAndCheck(
            context,
            locale
        );

        this.environmentValueAndCheck(
            context,
            EnvironmentValueName.LOCALE,
            locale
        );
    }

    @Test
    public void testSaveMetadataWithDifferentId() {
        final BasicSpreadsheetContext context = BasicSpreadsheetContext.with(
            ID,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            SPREADSHEET_PROVIDER,
            EnvironmentContexts.map(ENVIRONMENT_CONTEXT),
            LocaleContexts.fake(),
            PROVIDER_CONTEXT
        );

        final SpreadsheetId id = SpreadsheetId.with(999);
        this.checkNotEquals(
            ID,
            id
        );

        final Locale locale = Locale.forLanguageTag("FR");

        final SpreadsheetMetadata saved = context.saveMetadata(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                id
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AUDIT_INFO
            )
        );

        this.checkEquals(
            locale,
            saved.locale()
        );

        this.localeAndCheck(
            context,
            LOCALE
        );

        this.environmentValueAndCheck(
            context,
            EnvironmentValueName.LOCALE,
            LOCALE
        );
    }

    // setEnvironment...................................................................................................

    @Test
    public void testSetEnvironmentLocaleDifferent() {
        final BasicSpreadsheetContext context = BasicSpreadsheetContext.with(
            ID,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            SPREADSHEET_PROVIDER,
            EnvironmentContexts.map(ENVIRONMENT_CONTEXT),
            LocaleContexts.fake(),
            PROVIDER_CONTEXT
        );

        final Locale locale = Locale.forLanguageTag("FR");
        this.localeAndCheck(
            context.setEnvironmentValue(
                EnvironmentValueName.LOCALE,
                locale
            ),
            locale
        );

        this.environmentValueAndCheck(
            context,
            EnvironmentValueName.LOCALE,
            locale
        );
    }

    // setLocale........................................................................................................

    @Test
    public void testSetLocaleDifferent() {
        final BasicSpreadsheetContext context = BasicSpreadsheetContext.with(
            ID,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            SPREADSHEET_PROVIDER,
            EnvironmentContexts.map(ENVIRONMENT_CONTEXT),
            LocaleContexts.fake(),
            PROVIDER_CONTEXT
        );

        final Locale locale = Locale.forLanguageTag("FR");
        this.localeAndCheck(
            context.setLocale(locale),
            locale
        );
    }

    // spreadsheetProvider..............................................................................................

    @Test
    public void testSpreadsheetProvider() {
        final BasicSpreadsheetContext context = BasicSpreadsheetContext.with(
            ID,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            SpreadsheetProviders.basic(
                ConverterProviders.empty(),
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
            ),
            EnvironmentContexts.map(ENVIRONMENT_CONTEXT),
            LocaleContexts.fake(),
            PROVIDER_CONTEXT
        );

        final SpreadsheetMetadata saved = context.saveMetadata(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                LOCALE
            ).loadFromLocale(
                LocaleContexts.jre(LOCALE)
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                ID
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AUDIT_INFO
            ).set(
                SpreadsheetMetadataPropertyName.COMPARATORS,
                SpreadsheetComparatorAliasSet.EMPTY
            ).set(
                SpreadsheetMetadataPropertyName.CONVERTERS,
                ConverterAliasSet.EMPTY
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
            )
        );

        final SpreadsheetProvider spreadsheetProvider = context.spreadsheetProvider();
        assertSame(
            spreadsheetProvider,
            context.spreadsheetProvider()
        );
    }

    @Override
    public BasicSpreadsheetContext createContext() {
        return BasicSpreadsheetContext.with(
            ID,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            SPREADSHEET_PROVIDER,
            ENVIRONMENT_CONTEXT,
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "spreadsheetId=1"
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetContext> type() {
        return BasicSpreadsheetContext.class;
    }
}
