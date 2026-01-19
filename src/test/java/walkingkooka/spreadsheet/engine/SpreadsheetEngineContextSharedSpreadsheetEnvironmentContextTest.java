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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.FakeSpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetContextSuppliers;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineContextSharedSpreadsheetEnvironmentContextTest extends SpreadsheetEngineContextSharedTestCase<SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext> {

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

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetContextSupplierFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                null,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                null,
                LOCALE_CONTEXT,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
            spreadsheetContextSupplier,
            spreadsheetEnvironmentContext,
            LOCALE_CONTEXT,
            SpreadsheetMetadataContexts.basic(
                CREATE_METADATA,
                SpreadsheetMetadataStores.treeMap()
            ),
            TERMINAL_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private final static BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> CREATE_METADATA = (e, l) -> METADATA_EN_AU;

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
            () -> context.spreadsheetMetadata()
        );
    }

    @Test
    public void testSpreadsheetMetadata() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(SPREADSHEET_ID);

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetEngineContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext(spreadsheetEnvironmentContext);

        final SpreadsheetMetadata metadata = METADATA.set(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.create(
                USER,
                HAS_NOW.now()
            )
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

        context.saveMetadata(metadata);

        this.spreadsheetMetadataAndCheck(
            context,
            metadata
        );
    }

    // spreadsheetId....................................................................................................

    @Override
    public void testEnvironmentValueNameWithSpreadsheetId() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testEnvironmentValueNameWithSpreadsheetIdMissing() {
        this.environmentValueAndCheck(SpreadsheetEngineContext.SPREADSHEET_ID);
    }

    @Test
    public void testSpreadsheetIdFails() {
        assertThrows(
            MissingEnvironmentValueException.class,
            () -> this.createContext()
                .spreadsheetId()
        );
    }

    // setSpreadsheetId.................................................................................................

    @Test
    public void testSetSpreadsheetId() {
        final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext context = this.createContext();

        final SpreadsheetId spreadsheetId = SpreadsheetId.with(222);
        context.setSpreadsheetId(spreadsheetId);

        this.spreadsheetIdAndCheck(
            context,
            spreadsheetId
        );
    }

    @Override
    public void testSetSpreadsheetIdWithSame() {
        throw new UnsupportedOperationException();
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
            () -> context.storeRepository()
        );
    }

    @Test
    public void testStoreRepository() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setSpreadsheetId(SPREADSHEET_ID);

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
        spreadsheetEnvironmentContext.setSpreadsheetId(SPREADSHEET_ID);

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
                                    public LineEnding lineEnding() {
                                        return LineEnding.NL;
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

    // createContext....................................................................................................

    @Override
    public SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext createContext() {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentSpreadsheetContextSupplier() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                SpreadsheetContextSuppliers.fake(),
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                spreadsheetEnvironmentContext,
                LOCALE_CONTEXT,
                SPREADSHEET_METADATA_CONTEXT,
                TERMINAL_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentLocaleContext() {
        this.checkNotEquals(
            SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext.with(
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LocaleContexts.jre(Locale.FRANCE),
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
                SPREADSHEET_CONTEXT_SUPPLIER,
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
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
            "{converter=collection(text, number, date-time, basic, spreadsheet-value, boolean, environment, error-throwing, expression, form-and-validation, locale, plugins, template), dateParser=date yyyy/mm/dd, dateTimeOffset=-25569, dateTimeParser=date-time yyyy/mm/dd hh:mm, dateTimeSymbols=ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\", decimalNumberDigitCount=6, decimalNumberSymbols=negativeSign='-' positiveSign='+' zeroDigit='0' currencySymbol=\"$\" decimalSeparator='.' exponentSymbol=\"e\" groupSeparator=',' infinitySymbol=\"∞\" monetaryDecimalSeparator='.' nanSymbol=\"NaN\" percentSymbol='%' permillSymbol='‰', defaultYear=2000, expressionNumberKind=BIG_DECIMAL, functions=[test-context-loadCell, test-context-serverUrl, test-context-spreadsheet-metadata, xyz], lineEnding=\"\\n\", locale=en_AU, numberParser=number 0.#;0.#;0, precision=7, roundingMode=HALF_UP, serverUrl=https://example.com, timeParser=time hh:mm:ss, twoDigitYear=50, user=user@example.com, valueSeparator=,}"
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
