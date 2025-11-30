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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
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
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalId;
import walkingkooka.terminal.server.FakeTerminalServerContext;
import walkingkooka.terminal.server.TerminalServerContext;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorAliasSet;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetContextTest implements SpreadsheetContextTesting<BasicSpreadsheetContext>,
    ToStringTesting<BasicSpreadsheetContext> {

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static SpreadsheetId ID = SpreadsheetId.with(1);

    private final static Function<SpreadsheetId, SpreadsheetStoreRepository> REPO = (i) -> SpreadsheetStoreRepositories.fake();

    private final static SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.fake();

    private final Function<SpreadsheetContext, SpreadsheetEngineContext> SPREADSHEET_ENGINE_CONTEXT_FACTORY = (SpreadsheetContext c) -> {
        throw new UnsupportedOperationException();
    };

    final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> HTTP_ROUTER_FACTORY = (SpreadsheetEngineContext c) -> {
        throw new UnsupportedOperationException();
    };

    private final static LineEnding LINE_ENDING = LineEnding.NL;

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static AuditInfo AUDIT_INFO = AuditInfo.with(
        EmailAddress.parse("creator@example.com"),
        LocalDateTime.MIN,
        EmailAddress.parse("modified@example.com"),
        LocalDateTime.MIN
    );

    private final static EnvironmentContext ENVIRONMENT_CONTEXT = EnvironmentContexts.map(
        EnvironmentContexts.empty(
            LINE_ENDING,
            LOCALE,
            LocalDateTime::now,
            Optional.empty() // no user
        )
    );
    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.readOnly(
        LocaleContexts.jre(Locale.ENGLISH)
    );

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    private final static TerminalServerContext TERMINAL_SERVER_CONTEXT = new FakeTerminalServerContext() {

        @Override
        public TerminalContext addTerminalContext(final Function<TerminalId, TerminalContext> terminalContextFactory) {
            Objects.requireNonNull(terminalContextFactory, "terminalContextFactory");
            throw new UnsupportedOperationException();
        }

        @Override
        public TerminalContext createTerminalContext(final EnvironmentContext context) {
            Objects.requireNonNull(context, "context");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TerminalContext> terminalContext(final TerminalId id) {
            Objects.requireNonNull(id, "id");
            throw new UnsupportedOperationException();
        }

        @Override
        public TerminalServerContext removeTerminalContext(final TerminalId id) {
            Objects.requireNonNull(id, "id");
            throw new UnsupportedOperationException();
        }
    };

    @Test
    public void testWithNullServerUrlFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                null,
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetIdFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                null,
                REPO,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                null,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                REPO,
                null,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEngineContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                null,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullHttpRouterFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                null,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                null,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                null,
                PROVIDER_CONTEXT,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                null,
                TERMINAL_SERVER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullTerminalServerContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                SERVER_URL,
                ID,
                REPO,
                SPREADSHEET_PROVIDER,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                HTTP_ROUTER_FACTORY,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT,
                null
            )
        );
    }

    // serverUrl........................................................................................................

    @Test
    public void testServerUrl() {
        this.serverUrlAndCheck(
            this.createContext(),
            SERVER_URL
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
        final BasicSpreadsheetContext context = this.createContext();

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
        final BasicSpreadsheetContext context = this.createContext();

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
        final BasicSpreadsheetContext context = this.createContext();

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
        final BasicSpreadsheetContext context = this.createContext();

        final Locale locale = Locale.forLanguageTag("FR");
        this.localeAndCheck(
            context.setLocale(locale),
            locale
        );
    }

    // spreadsheetProvider..............................................................................................

    @Test
    public void testSpreadsheetProvider() {
        final BasicSpreadsheetContext context = this.createContext();

        final SpreadsheetProvider spreadsheetProvider = context.spreadsheetProvider();
        assertSame(
            spreadsheetProvider,
            context.spreadsheetProvider()
        );
    }

    @Test
    public void testSpreadsheetProviderAfterSpreadsheetMetadataDelete() {
        final BasicSpreadsheetContext context = this.createContext();

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

        context.deleteMetadata(
            saved.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
        );

        final IllegalStateException expected = assertThrows(
            IllegalStateException.class,
            () -> context.spreadsheetProvider()
        );

        this.checkEquals(
            "SpreadsheetMetadata 1 deleted",
            expected.getMessage()
        );
    }

    @Override
    public BasicSpreadsheetContext createContext() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
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
        );

        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        store.save(metadata);

        return BasicSpreadsheetContext.with(
            SERVER_URL,
            ID,
            (i) -> {
                checkEquals(ID, i, "id -> SpreadsheetStoreRepository");
                return new FakeSpreadsheetStoreRepository() {

                    @Override
                    public SpreadsheetMetadataStore metadatas() {
                        return store;
                    }
                };
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
            (c) -> SpreadsheetEngineContexts.fake(),
            (c) -> new Router<>() {
                @Override
                public Optional<HttpHandler> route(final Map<HttpRequestAttribute<?>, Object> attributes) {
                    throw new UnsupportedOperationException();
                }
            },
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LINE_ENDING,
                    LOCALE,
                    LocalDateTime::now,
                    Optional.empty() // no user
                )
            ),
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT,
            TERMINAL_SERVER_CONTEXT
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
