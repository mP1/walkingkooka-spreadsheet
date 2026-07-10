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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.FakeSpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContextSuppliers;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataCreator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.Storages;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineContextSharedSpreadsheetEnvironmentContextTest extends SpreadsheetEngineContextSharedTestCase<SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext> {

    private final static CurrencyLocaleContext CURRENCY_LOCALE_CONTEXT = CURRENCY_CONTEXT.setLocaleContext(LOCALE_CONTEXT);

    private final static SpreadsheetContextSupplier SPREADSHEET_CONTEXT_SUPPLIER = SpreadsheetContextSuppliers.fake();
    
    private final static int DECIMAL_NUMBER_DIGIT_COUNT = 6;

    static {
        SpreadsheetEnvironmentContext context = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        for (final EnvironmentValueName<?> name : SpreadsheetEnvironmentContextFactory.ENVIRONMENT_VALUE_NAMES) {
            if (name.equals(SpreadsheetEnvironmentContextFactory.CONVERTER)) {
                continue;
            }

            context.setEnvironmentValue(
                name,
                Cast.to(
                    METADATA_EN_AU.getOrFail(
                        SpreadsheetMetadataPropertyName.fromEnvironmentValueName(name)
                    )
                )
            );
        }

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            CURRENT_WORKING_DIRECTORY
        );

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.CONVERTER,
            METADATA_EN_AU.getOrFail(
                SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER
            )
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.DECIMAL_NUMBER_DIGIT_COUNT,
            DECIMAL_NUMBER_DIGIT_COUNT
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("test-context-loadCell, test-context-serverUrl, test-context-spreadsheet-metadata, xyz")
        );

        SPREADSHEET_ENVIRONMENT_CONTEXT = SpreadsheetEnvironmentContexts.readOnly(context);
    }

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT;

    private final static SpreadsheetMetadataContext SPREADSHEET_METADATA_CONTEXT = SpreadsheetMetadataContexts.fake();

    private final static SpreadsheetMetadata SAVED_METADATA = METADATA.set(
        SpreadsheetMetadataPropertyName.AUDIT_INFO,
        AuditInfo.create(
            USER,
            HAS_NOW.now()
        )
    ).set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    // with.............................................................................................................

    @Test
    public void testWithNullMediaTypeDetectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                null,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMultiplierFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                null,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEngineFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                null,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetContextSupplierFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                null,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullCurrencyLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                null,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                null,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetMetadataContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                null,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                null
            )
        );
    }

    @Override
    SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            SPREADSHEET_CONTEXT_SUPPLIER,
            spreadsheetEnvironmentContext
        );
    }

    private SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext createContext(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                                      final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
            MEDIA_TYPE_DETECTOR,
            MULTIPLIER,
            SPREADSHEET_ENGINE,
            spreadsheetContextSupplier,
            CURRENCY_LOCALE_CONTEXT,
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataContexts.basic(
                CREATE_METADATA,
                SpreadsheetMetadataStores.treeMap()
            ),
            TERMINAL_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private final static SpreadsheetMetadataCreator CREATE_METADATA = (e, l) -> {
        SpreadsheetMetadata created = METADATA_EN_AU;

        created = created.set(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            created.getOrFail(SpreadsheetMetadataPropertyName.AUDIT_INFO)
                .setCreatedBy(e)
        );

        if (l.isPresent()) {
            created = created.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                l.get()
            );
        }


        return created;
    };

    // httpRouter.......................................................................................................

    @Test
    public void testHttpRouter() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .httpRouter()
        );
    }

    @Test
    public void testSaveMetadata() {
        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final SpreadsheetMetadata metadata = METADATA.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with(this.getClass().getName())
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SPREADSHEET_ID
        ).set(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.create(
                USER,
                HAS_NOW.now()
            )
        );

        final SpreadsheetMetadata saved = context.saveMetadata(metadata);

        this.loadMetadataAndCheck(
            context,
            SPREADSHEET_ID,
            saved
        );
    }

    @Test
    public void testCreateSpreadsheet() {
        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final EmailAddress user = EmailAddress.parse("different@example.com");
        this.checkNotEquals(
            USER,
            user,
            "user"
        );

        final Locale locale = Locale.forLanguageTag("FR");
        this.checkNotEquals(
            LOCALE,
            locale,
            "locale"
        );

        final SpreadsheetMetadata metadata = context.createMetadata(
            user,
            Optional.of(locale)
        );

        this.checkNotEquals(
            null,
            metadata
        );

        final SpreadsheetId spreadsheetId = metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID);

        this.checkNotEquals(
            null,
            spreadsheetId,
            "spreadsheetId"
        );

        this.localeAndCheck(
            metadata,
            locale
        );

        this.checkEquals(
            user,
            metadata.getOrFail(SpreadsheetMetadataPropertyName.AUDIT_INFO)
                .createdBy(),
            "createdBy"
        );

        this.loadMetadataAndCheck(
            context,
            spreadsheetId,
            metadata
        );
    }

    // spreadsheetMetadata..............................................................................................

    @Test
    public void testSpreadsheetMetadataMissingSpreadsheetIdFails() {
        final EnvironmentContext environmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        this.environmentValueAndCheck(
            environmentContext,
            SpreadsheetEngineContext.SPREADSHEET_ID
        );

        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext(environmentContext);

        assertThrows(
            MissingEnvironmentValueException.class,
            context::spreadsheetMetadata
        );
    }

    @Test
    public void testSpreadsheetMetadata() {
        this.spreadsheetMetadataAndCheck(
            this.createContextWithSpreadsheetId(),
            SAVED_METADATA
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSpreadsheetIdMissing() {
        this.spreadsheetIdAndCheck(
            this.createContext()
        );
    }

    // setSpreadsheetId.................................................................................................

    @Test
    public void testSetSpreadsheetId() {
        this.setSpreadsheetIdAndCheck(
            this.createContext(),
            SpreadsheetId.with(222)
        );
    }

    // storeRepository..................................................................................................

    @Test
    public void testStoreRepositoryMissingSpreadsheetIdFails() {
        final SpreadsheetEnvironmentContext environmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        this.environmentValueAndCheck(
            environmentContext,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID
        );

        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext(environmentContext);

        assertThrows(
            MissingEnvironmentValueException.class,
            context::storeRepository
        );
    }

    @Test
    public void testStoreRepository() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.fake();

        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext(
            new FakeSpreadsheetContextSupplier() {
                @Override
                public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                    return Optional.ofNullable(
                        SPREADSHEET_ID.equals(id) ?
                            new FakeSpreadsheetContext() {

                                @Override
                                public SpreadsheetStoreRepository storeRepository() {
                                    return repo;
                                }
                            } :
                            null
                    );
                }
            },
            spreadsheetEnvironmentContext
        );

        this.checkEquals(
            repo,
            context.storeRepository()
        );
    }

    // evaluate.........................................................................................................

    @Test
    public void testEvaluateMissingSpreadsheetId() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.removeEnvironmentValue(SpreadsheetEnvironmentContext.SPREADSHEET_ID);

        this.evaluateAndCheck(
            this.createContext(
                SpreadsheetContextSuppliers.fake(), // SpreadsheetContext should never be fetched
                spreadsheetEnvironmentContext
            ),
            Expression.add(
                this.expression(1),
                this.expression(2)
            ),
            SpreadsheetExpressionEvaluationContext.NO_CELL,
            SpreadsheetExpressionReferenceLoaders.fake(),
            this.number(1 + 2)
        );
    }

    @Test
    public void testEvaluateWhenSpreadsheetId() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        final SpreadsheetMetadata metadata = metadataStore.save(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SPREADSHEET_ID
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.create(
                    USER,
                    HAS_NOW.now()
                )
            )
        );

        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(metadataStore);

        this.evaluateAndCheck(
            this.createContext(
                new FakeSpreadsheetContextSupplier() {
                    @Override
                    public Optional<SpreadsheetContext> spreadsheetContext(final SpreadsheetId id) {
                        return Optional.ofNullable(
                            SPREADSHEET_ID.equals(id) ?
                                new FakeSpreadsheetContext() {

                                    @Override
                                    public Charset charset() {
                                        return SpreadsheetEngineContextSharedSpreadsheetEnvironmentContextTest.CHARSET;
                                    }

                                    @Override
                                    public SpreadsheetStoreRepository storeRepository() {
                                        return repo;
                                    }

                                    @Override
                                    public SpreadsheetMetadata spreadsheetMetadata() {
                                        return metadata;
                                    }

                                    @Override
                                    public ProviderContext providerContext() {
                                        return PROVIDER_CONTEXT;
                                    }

                                    @Override
                                    public <C extends ConverterContext> Converter<C> converter(final ConverterName name,
                                                                                               final List<?> value,
                                                                                               final ProviderContext context) {
                                        return CONVERTER_PROVIDER.converter(
                                            name,
                                            value,
                                            context
                                        );
                                    }

                                    @Override
                                    public Optional<StoragePath> currentWorkingDirectory() {
                                        return Optional.of(
                                            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContextTest.CURRENT_WORKING_DIRECTORY
                                        );
                                    }

                                    @Override
                                    public Indentation indentation() {
                                        return SpreadsheetEngineContextSharedSpreadsheetEnvironmentContextTest.INDENTATION;
                                    }

                                    @Override
                                    public LineEnding lineEnding() {
                                        return LineEnding.NL;
                                    }

                                    @Override
                                    public BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier() {
                                        return MULTIPLIER;
                                    }
                                } :
                                null
                        );
                    }
                },
                spreadsheetEnvironmentContext
            ),
            Expression.add(
                this.expression(1),
                this.expression(2)
            ),
            SpreadsheetExpressionEvaluationContext.NO_CELL,
            SpreadsheetExpressionReferenceLoaders.fake(),
            this.number(1 + 2)
        );
    }

    // SpreadsheetStorageContext........................................................................................

    @Override
    public void testAddCellWatcherWithNullWatcherFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddCellWatcherOnceWithNullWatcherFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddFormWatcherWithNullWatcherFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddFormWatcherOnceWithNullWatcherFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddLabelWatcherWithNullWatcherFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testAddLabelWatcherOnceWithNullWatcherFails() {
        throw new UnsupportedOperationException();
    }

    // storage..........................................................................................................

    @Test
    public void testStorage() {
        this.storageAndCheck(
            this.createContext(),
            STORAGE
        );
    }

    @Test
    public void testSaveStorageAndLoadStorage() {
        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContextWithSpreadsheetId();

        final StoragePath path = StoragePath.parse("/path1/file2");

        final StorageValue value = StorageValue.with(path)
            .setValue(
                Optional.of(111)
            );

        this.saveStorageAndCheck(
            context,
            value,
            value
        );

        this.loadStorageAndCheck(
            context,
            path,
            value
        );
    }

    // createContext....................................................................................................

    @Override
    public SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext createContext() {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
    }

    private SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext createContextWithSpreadsheetId() {
        this.context = null;

        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext(
            (SpreadsheetId spreadsheetId) -> Optional.ofNullable(
                SPREADSHEET_ID.equals(spreadsheetId) ?
                    this.context :
                    null
            ),
            SpreadsheetEnvironmentContexts.basic(
                Storages.treeMapStore(),
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
            )
        );
        this.context = context;
        context.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );
        context.saveMetadata(SAVED_METADATA);
        return context;
    }

    private SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context;

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentMediaTypeDetector() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MediaTypeDetectors.fake(),
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentMultiplier() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                BinaryNumberConverterFunctions.fake(),
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetEngine() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetContextSupplier() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SpreadsheetContextSuppliers.fake(),
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentCurrencyLocaleContext() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CurrencyLocaleContexts.fake(),
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetEnvironmentContext() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
            EnvironmentValueName.with(
                "different",
                Integer.class
            ),
            1
        );

        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                spreadsheetEnvironmentContext,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetMetadataContext() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SpreadsheetMetadataContexts.fake(),
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentTerminalContext() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TerminalContexts.fake(),
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetProvider() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SpreadsheetProviders.fake(),
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentProviderContext() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                SPREADSHEET_ENGINE,
                SPREADSHEET_CONTEXT_SUPPLIER,
                CURRENCY_LOCALE_CONTEXT,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                ProviderContexts.fake()
            )
        );
    }

    @Override
    public SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext createObject() {
        return this.createContext();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{charset=\"UTF-8\", converter=collection(text, boolean, number, date-time, basic, spreadsheet-value, environment, error-throwing, expression, form-and-validation, locale, plugins, properties, template, json), currency=\"AUD\", currentWorkingDirectory=/current1/working2/directory3, dateParser=date yyyy/mm/dd, dateTimeOffset=-25569, dateTimeParser=date-time yyyy/mm/dd hh:mm, dateTimeSymbols=ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\", decimalNumberDigitCount=6, decimalNumberSymbols=negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰', defaultYear=2000, expressionNumberKind=BIG_DECIMAL, functions=[test-context-loadCell, test-context-serverUrl, test-context-spreadsheet-metadata, xyz], indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, numberParser=number 0.#;0.#;0, precision=7, roundingMode=HALF_UP, serverUrl=https://example.com, timeOffset=Z, timeParser=time hh:mm:ss, twoDigitYear=50, user=user@example.com, valueSeparator=,}"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext> type() {
        return SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.class;
    }


    @Override
    public String typeNameSuffix() {
        return SpreadsheetEnvironmentContext.class.getSimpleName();
    }
}
