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
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.LineEnding;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.provider.ValidatorAliasSet;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetContextSharedFixedSpreadsheetIdTest extends SpreadsheetContextSharedTestCase<SpreadsheetContextSharedFixedSpreadsheetId> {

    private final static SpreadsheetStoreRepository REPO = SpreadsheetStoreRepositories.fake();

    final Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> HTTP_ROUTER_FACTORY = (SpreadsheetEngineContext c) -> {
        throw new UnsupportedOperationException();
    };

    private final static LineEnding LINE_ENDING = LineEnding.NL;

    @Test
    public void testWithNullSpreadsheetEngineFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSharedFixedSpreadsheetId.with(
                null,
                REPO,
                HTTP_ROUTER_FACTORY,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSharedFixedSpreadsheetId.with(
                SPREADSHEET_ENGINE,
                null,
                HTTP_ROUTER_FACTORY,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullHttpRouterFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSharedFixedSpreadsheetId.with(
                SPREADSHEET_ENGINE,
                REPO,
                null,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    // saveMetadata.....................................................................................................

    @Test
    public void testSaveMetadataWithSameId() {
        final SpreadsheetContextSharedFixedSpreadsheetId context = this.createContext();

        final Locale locale = Locale.forLanguageTag("FR");

        final SpreadsheetMetadata saved = context.saveMetadata(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                locale
            ).loadFromLocale(
                LocaleContexts.jre(locale)
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SPREADSHEET_ID
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
        final SpreadsheetContextSharedFixedSpreadsheetId context = this.createContext();

        final SpreadsheetId id = SpreadsheetId.with(999);
        this.checkNotEquals(
            SPREADSHEET_ID,
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

    // removeEnvironment................................................................................................

    @Test
    public void testRemoveEnvironmentValueWithSpreadsheetIdFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .removeEnvironmentValue(
                    SpreadsheetEnvironmentContext.SPREADSHEET_ID
                )
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSetEnvironmentValueWithSpreadsheetIdFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .setEnvironmentValue(
                    SpreadsheetEnvironmentContext.SPREADSHEET_ID,
                    SPREADSHEET_ID
                )
        );
    }

    @Test
    public void testSetSpreadsheetIdFails() {
        final SpreadsheetContextSharedFixedSpreadsheetId context = this.createContext();

        assertThrows(
            IllegalArgumentException.class,
            () -> context.setSpreadsheetId(
                Optional.of(
                    SpreadsheetId.with(999)
                )
            )
        );
    }

    // spreadsheetProvider..............................................................................................

    @Test
    public void testSpreadsheetProviderAfterSpreadsheetMetadataDelete() {
        final SpreadsheetContextSharedFixedSpreadsheetId context = this.createContext();

        final SpreadsheetMetadata saved = context.saveMetadata(
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                LOCALE
            ).loadFromLocale(
                LocaleContexts.jre(LOCALE)
            ).set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SPREADSHEET_ID
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
    public SpreadsheetContextSharedFixedSpreadsheetId createContext() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                CURRENCY,
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                Optional.empty() // no user
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            CURRENT_WORKING_DIRECTORY
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

        return this.createContext(
            SpreadsheetEnvironmentContexts.basic(
                STORAGE,
                environmentContext
            )
        );
    }

    @Override
    SpreadsheetContextSharedFixedSpreadsheetId createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            spreadsheetEnvironmentContext,
            LOCALE_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Override
    SpreadsheetContextSharedFixedSpreadsheetId createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                             final LocaleContext localeContext,
                                                             final SpreadsheetProvider spreadsheetProvider,
                                                             final ProviderContext providerContext) {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            LOCALE
        ).loadFromLocale(
            LocaleContexts.jre(LOCALE)
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SPREADSHEET_ID
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

        return SpreadsheetContextSharedFixedSpreadsheetId.with(
            SPREADSHEET_ENGINE,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return store;
                }
            },
            (c) -> new Router<>() {
                @Override
                public Optional<HttpHandler> route(final Map<HttpRequestAttribute<?>, Object> attributes) {
                    throw new UnsupportedOperationException();
                }
            },
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }


    // class............................................................................................................

    @Override
    public Class<SpreadsheetContextSharedFixedSpreadsheetId> type() {
        return SpreadsheetContextSharedFixedSpreadsheetId.class;
    }
}
