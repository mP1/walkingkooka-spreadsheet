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

import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
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
import walkingkooka.spreadsheet.meta.FakeSpreadsheetMetadataCreator;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContextDelegatorTest.TestSpreadsheetStorageDelegatorContext;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.storage.Storages;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetStorageContextDelegatorTest implements SpreadsheetStorageContextTesting2<TestSpreadsheetStorageDelegatorContext> {

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetSpreadsheetIdWithSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetStorageDelegatorContext createContext() {
        return new TestSpreadsheetStorageDelegatorContext();
    }

    @Override
    public Class<TestSpreadsheetStorageDelegatorContext> type() {
        return TestSpreadsheetStorageDelegatorContext.class;
    }

    final static class TestSpreadsheetStorageDelegatorContext implements SpreadsheetStorageContextDelegator {

        @Override
        public SpreadsheetStorageContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetStorageContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetStorageContext spreadsheetStorageContext() {
            return this.context;
        }

        {
            final SpreadsheetId spreadsheetId = SpreadsheetId.with(1);

            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
            spreadsheetEnvironmentContext.setSpreadsheetId(
                Optional.of(spreadsheetId)
            );

            final SpreadsheetMetadataStore spreadsheetMetadataStore = SpreadsheetMetadataStores.treeMap();
            spreadsheetMetadataStore.save(
                SpreadsheetMetadataTesting.METADATA_EN_AU.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    spreadsheetId
                )
            );

            this.context = SpreadsheetStorageContexts.spreadsheetContext(
                SpreadsheetContexts.fixedSpreadsheetId(
                    MediaTypeDetectors.binary(),
                    new FakeSpreadsheetMetadataCreator(),
                    BinaryNumberConverterFunctions.fake(), // multiplier
                    SpreadsheetEngines.basic(),
                    //SpreadsheetStoreRepositories.treeMap(metadataStore),
                    SpreadsheetStoreRepositories.treeMap(spreadsheetMetadataStore),
                    (c) -> {
                        throw new UnsupportedOperationException();
                    }, // HttpRouter
                    CurrencyContexts.fake()
                        .setLocaleContext(
                            LocaleContexts.jre(
                                Locale.forLanguageTag("en-AU")
                            )
                        ),
                    SpreadsheetEnvironmentContexts.basic(
                        Storages.treeMapStore(),
                        spreadsheetEnvironmentContext
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


        private final SpreadsheetStorageContext context;

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
