
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
import walkingkooka.collect.map.Maps;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorAliasSet;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterAliasSet;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterAliasSet;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterAliasSet;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.LineEnding;
import walkingkooka.validation.form.provider.FormHandlerAliasSet;
import walkingkooka.validation.provider.ValidatorAliasSet;

import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetContextSharedMutableSpreadsheetIdTest extends SpreadsheetContextSharedTestCase<SpreadsheetContextSharedMutableSpreadsheetId> {

    private final static Function<SpreadsheetId, SpreadsheetStoreRepository> SPREADSHEET_ID_TO_STORE_REPOSITORY = (id) -> {
        throw new UnsupportedOperationException();
    };

    private final static SpreadsheetMetadataContext SPREADSHEET_METADATA_CONTEXT = SpreadsheetMetadataContexts.fake();

    private final static SpreadsheetId OTHER_SPREADSHEET_ID = SpreadsheetId.with(999);

    private final static LineEnding LINE_ENDING = LineEnding.NL;

    private final static Locale OTHER_SPREADSHEET_LOCALE = Locale.FRANCE;

    @Test
    public void testWithNullSpreadsheetEngineFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSharedMutableSpreadsheetId.with(
                null,
                SPREADSHEET_ID_TO_STORE_REPOSITORY,
                SPREADSHEET_METADATA_CONTEXT,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetIdToStoreRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSharedMutableSpreadsheetId.with(
                SPREADSHEET_ENGINE,
                null,
                SPREADSHEET_METADATA_CONTEXT,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetMetadataContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetContextSharedMutableSpreadsheetId.with(
                SPREADSHEET_ENGINE,
                SPREADSHEET_ID_TO_STORE_REPOSITORY,
                null,
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
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
        final SpreadsheetContextSharedMutableSpreadsheetId context = this.createContext();

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
        final SpreadsheetContextSharedMutableSpreadsheetId context = this.createContext();

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

    // loadSpreadsheet...................................................................................................

    @Test
    public void testLoadSpreadsheet() {
        final SpreadsheetContextSharedMutableSpreadsheetId context = this.createContext();

        final SpreadsheetMetadata metadata = context.loadMetadataOrFail(SPREADSHEET_ID);

        this.checkNotEquals(
            null,
            metadata
        );

        this.checkEquals(
            SPREADSHEET_ID,
            metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID)
        );
    }

    // setSpreadsheetId.................................................................................................

    @Test
    public void testSpreadsheetIdWithDifferent() {
        final SpreadsheetContextSharedMutableSpreadsheetId context = this.createContext();

        this.localeAndCheck(
            context,
            LOCALE
        );

        this.setEnvironmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

        this.localeAndCheck(
            context,
            LOCALE
        );

        final SpreadsheetMetadata metadata = context.spreadsheetMetadata();
        final SpreadsheetMetadata otherMetadata = context.loadMetadataOrFail(OTHER_SPREADSHEET_ID);

        this.loadMetadataAndCheck(
            context,
            SPREADSHEET_ID,
            metadata
        );

        this.setEnvironmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            OTHER_SPREADSHEET_ID
        );

        this.loadMetadataAndCheck(
            context,
            OTHER_SPREADSHEET_ID,
            otherMetadata
        );

        this.localeAndCheck(
            context,
            LOCALE
        );
    }

    // removeEnvironment................................................................................................

    @Test
    public void testRemoveEnvironmentValueWithSpreadsheetId() {
        final SpreadsheetContextSharedMutableSpreadsheetId context = this.createContext();

        this.removeEnvironmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID
        );

        assertThrows(
            MissingEnvironmentValueException.class,
            () -> context.spreadsheetId()
        );
    }

    // spreadsheetProvider..............................................................................................

    @Test
    public void testSpreadsheetProviderAfterSpreadsheetMetadataDelete() {
        final SpreadsheetContextSharedMutableSpreadsheetId context = this.createContext();

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
    public SpreadsheetContextSharedMutableSpreadsheetId createContext() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                Optional.empty() // no user
            )
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
    SpreadsheetContextSharedMutableSpreadsheetId createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            (c) -> SpreadsheetEngineContexts.fake(),
            spreadsheetEnvironmentContext,
            LOCALE_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Override
    SpreadsheetContextSharedMutableSpreadsheetId createContext(final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                                                               final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
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
        
        final Map<SpreadsheetId, SpreadsheetStoreRepository> spreadsheetIdToSpreadsheetStoreRepository = Maps.sorted();
        spreadsheetIdToSpreadsheetStoreRepository.put(
            SPREADSHEET_ID,
            SpreadsheetStoreRepositories.treeMap(store)
        );

        final SpreadsheetMetadata otherMetadata = metadata.set(
            SpreadsheetMetadataPropertyName.ROUNDING_MODE,
            RoundingMode.HALF_DOWN
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            OTHER_SPREADSHEET_ID
        ).set(
            SpreadsheetMetadataPropertyName.LOCALE,
            OTHER_SPREADSHEET_LOCALE
        ).loadFromLocale(
            LocaleContexts.jre(OTHER_SPREADSHEET_LOCALE)
        );

        store.save(otherMetadata);

        spreadsheetIdToSpreadsheetStoreRepository.put(
            OTHER_SPREADSHEET_ID,
            SpreadsheetStoreRepositories.treeMap(store)
        );

        this.checkNotEquals(
            metadata,
            otherMetadata
        );

        return SpreadsheetContextSharedMutableSpreadsheetId.with(
            SPREADSHEET_ENGINE,
            (SpreadsheetId id) -> {
                final SpreadsheetStoreRepository repo = spreadsheetIdToSpreadsheetStoreRepository.get(id);
                if(null == repo) {
                    throw new IllegalArgumentException("SpreadsheetId " + id + " not found");
                }
                return repo;
            },
            SpreadsheetMetadataContexts.basic(
                (u, dl) -> {
                    throw new UnsupportedOperationException();
                },
                store
            ),
            spreadsheetEngineContextFactory,
            spreadsheetEnvironmentContext,
            localeContext,
            spreadsheetProvider,
            providerContext
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetContextSharedMutableSpreadsheetId> type() {
        return SpreadsheetContextSharedMutableSpreadsheetId.class;
    }
}
