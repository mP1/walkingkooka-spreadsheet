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

import walkingkooka.convert.ConverterContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
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
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.FakeSpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContextTesting2;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContextTest implements SpreadsheetStorageContextTesting2<SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext>,
    SpreadsheetMetadataTesting {

    private final static SpreadsheetEngine SPREADSHEET_ENGINE = SpreadsheetEngines.fake();

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    @Override
    public SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContextSpreadsheetStorageContext createContext() {
        final Locale locale = Locale.forLanguageTag("en-AU");

        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                LineEnding.NL,
                locale,
                () -> LocalDateTime.MIN,
                Optional.of(
                    EmailAddress.parse("user@example.com")
                )
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
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

        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(metadataStore);

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
                                SPREADSHEET_ENGINE,
                                repo,
                                (c) -> {
                                    throw new UnsupportedOperationException();
                                }, // Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory
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
                new FakeSpreadsheetMetadataContext() {
                    @Override
                    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
                        return metadataStore.load(id);
                    }

                    @Override
                    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
                        return metadataStore.save(metadata);
                    }

                    @Override
                    public void deleteMetadata(final SpreadsheetId id) {
                        metadataStore.delete(id);
                    }

                    @Override
                    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                                   final int offset,
                                                                                   final int count) {
                        return metadataStore.findByName(
                            name,
                            offset,
                            count
                        );
                    }
                },
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
