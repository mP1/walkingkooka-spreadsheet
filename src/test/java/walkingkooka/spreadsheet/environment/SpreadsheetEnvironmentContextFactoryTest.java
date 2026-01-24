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

package walkingkooka.spreadsheet.environment;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.lang.reflect.Field;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEnvironmentContextFactoryTest implements SpreadsheetEnvironmentContextTesting2<SpreadsheetEnvironmentContextFactory>,
    ConverterTesting,
    SpreadsheetMetadataTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetEnvironmentContextFactory>,
    ClassTesting2<SpreadsheetEnvironmentContextFactory> {

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

        SPREADSHEET_ENVIRONMENT_CONTEXT = context;
    }

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT;

    private final static SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.basic(
        CONVERTER_PROVIDER,
        ExpressionFunctionProviders.fake(),
        SpreadsheetComparatorProviders.fake(),
        SpreadsheetExporterProviders.fake(),
        SpreadsheetFormatterProviders.fake(),
        FormHandlerProviders.fake(),
        SpreadsheetImporterProviders.fake(),
        SPREADSHEET_PARSER_PROVIDER,
        ValidatorProviders.fake()
    );

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    // istEnvironmentValueName..........................................................................................

    @Test
    public void testIsEnvironmentValueNameWithNull() {
        this.isEnvironmentValueNameAndCheck(
            null,
            false
        );
    }

    @Test
    public void testIsEnvironmentValueNameWithLocale() {
        this.isEnvironmentValueNameAndCheck(
            EnvironmentValueName.LOCALE,
            true
        );
    }

    @Test
    public void testIsEnvironmentValueNameWithUser() {
        this.isEnvironmentValueNameAndCheck(
            EnvironmentValueName.USER,
            false
        );
    }

    @Test
    public void testIsEnvironmentValueNameWithSpreadsheetId() {
        this.isEnvironmentValueNameAndCheck(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            false
        );
    }

    @Test
    public void testIsEnvironmentValueNameWithConverter() {
        this.isEnvironmentValueNameAndCheck(
            SpreadsheetEnvironmentContextFactory.CONVERTER,
            true
        );
    }

    @Test
    public void testIsEnvironmentValueNameWithRoundingMode() {
        this.isEnvironmentValueNameAndCheck(
            SpreadsheetEnvironmentContextFactory.ROUNDING_MODE,
            true
        );
    }

    @Test
    public void testIsEnvironmentValueNameWithAllConstants() throws Exception {
        int i = 0;

        for(final Field field : SpreadsheetEnvironmentContextFactory.class.getDeclaredFields()) {
            if(FieldAttributes.STATIC.is(field) && field.getType() == EnvironmentValueName.class) {
                this.isEnvironmentValueNameAndCheck(
                    (EnvironmentValueName<?>) field.get(null),
                    true
                );
                i++;
            }
        }

        this.checkNotEquals(
            0,
            i,
            "no constants of type " + EnvironmentValueName.class.getName() + " found"
        );
    }

    private void isEnvironmentValueNameAndCheck(final EnvironmentValueName<?> name,
                                                final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetEnvironmentContextFactory.isEnvironmentValueName(name),
            () -> String.valueOf(name)
        );
    }

    // with.............................................................................................................

    @Test
    public void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextFactory.with(
                null,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextFactory.with(
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                null,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextFactory.with(
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextFactory.with(
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                null
            )
        );
    }

    // ENVIRONMENT_VALUE_NAMES..........................................................................................

    @Test
    public void testEnvironmentValueNamesAreConstants() {
        this.checkEquals(
            Sets.empty(),
            SpreadsheetEnvironmentContextFactory.ENVIRONMENT_VALUE_NAMES.stream()
                .filter(n -> n != EnvironmentValueName.with(n.value(), n.type()))
                .collect(Collectors.toSet())
        );
    }

    // environmentContext...............................................................................................

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetEnvironmentContextFactory context = this.createContext();

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        final EnvironmentContext differentEnvironmentContext = EnvironmentContexts.empty(
            INDENTATION,
            lineEnding,
            LOCALE,
            HAS_NOW,
            EnvironmentContext.ANONYMOUS
        );

        final SpreadsheetEnvironmentContextFactory afterSet = context.setEnvironmentContext(differentEnvironmentContext);
        this.checkNotEquals(
            context,
            afterSet
        );
    }

    // HasIndentation..................................................................................................

    @Test
    public void testIndentation() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.indentationAndCheck(
            this.createContext(context),
            context.indentation()
        );
    }

    @Test
    public void testSetIndentation() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    INDENTATION,
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        final SpreadsheetEnvironmentContextFactory context = this.createContext(spreadsheetEnvironmentContext);

        final Indentation indentation = Indentation.EMPTY;

        this.checkNotEquals(
            INDENTATION,
            indentation
        );

        context.setIndentation(indentation);

        this.indentationAndCheck(
            context,
            indentation
        );
    }
    
    // HasLineEndings...................................................................................................

    @Test
    public void testLineEnding() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.lineEndingAndCheck(
            this.createContext(context),
            context.lineEnding()
        );
    }

    @Test
    public void testSetLineEnding() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    INDENTATION,
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        final SpreadsheetEnvironmentContextFactory context = this.createContext(spreadsheetEnvironmentContext);

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        context.setLineEnding(lineEnding);

        this.setLineEndingAndCheck(
            context,
            lineEnding
        );
    }

    @Test
    public void testLocale() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                Locale.FRANCE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );

        this.localeAndCheck(
            this.createContext(context),
            context.locale()
        );
    }

    @Test
    public void testSetLocale() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    INDENTATION,
                    LINE_ENDING,
                    Locale.FRANCE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            )
        );

        final SpreadsheetEnvironmentContextFactory context = this.createContext(spreadsheetEnvironmentContext);

        final Locale locale = Locale.GERMAN;
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );
    }

    @Test
    public void testEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            name,
            value
        );
    }

    @Test
    public void testSetEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        final SpreadsheetEnvironmentContextFactory context = this.createContext(spreadsheetEnvironmentContext);
        context.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            context,
            name,
            value
        );
    }

    @Test
    public void testRemoveEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        final SpreadsheetEnvironmentContextFactory context = this.createContext(spreadsheetEnvironmentContext);
        context.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            context,
            name
        );
    }

    @Test
    public void testUser() {
        final EmailAddress user = EmailAddress.parse("user123@example.com");

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                Optional.of(user)
            )
        );

        this.userAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            user
        );
    }

    // EnvironmentValueName.............................................................................................

    @Test
    public void testFireEnvironmentValueNameChangeWithRoundingMode() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContextFactory.FUNCTIONS,
            SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
        );

        final SpreadsheetEnvironmentContextFactory context = this.createContext(spreadsheetEnvironmentContext);

        final DecimalNumberContext decimalNumberContext = context.decimalNumberContext();

        RoundingMode roundingMode = RoundingMode.UP;
        if (roundingMode == decimalNumberContext.mathContext()
            .getRoundingMode()) {
            roundingMode = RoundingMode.CEILING;
        }

        this.setEnvironmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContextFactory.ROUNDING_MODE,
            roundingMode
        );

        final DecimalNumberContext decimalNumberContext2 = context.decimalNumberContext();

        assertNotSame(
            decimalNumberContext,
            decimalNumberContext2,
            "DecimalNumberCoontext should have been recreated with new RoundingMode"
        );

        this.checkEquals(
            roundingMode,
            decimalNumberContext2.mathContext()
                .getRoundingMode(),
            "DecimalNumberContext.roundingMode"
        );
    }

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public SpreadsheetEnvironmentContextFactory createContext() {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
    }

    private SpreadsheetEnvironmentContextFactory createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return createContext(
            spreadsheetEnvironmentContext,
            PROVIDER_CONTEXT
        );
    }

    private SpreadsheetEnvironmentContextFactory createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                               final ProviderContext providerContext) {
        return SpreadsheetEnvironmentContextFactory.with(
            spreadsheetEnvironmentContext,
            LOCALE_CONTEXT,
            SPREADSHEET_PROVIDER,
            providerContext
        );
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverter() {
        this.createContext()
            .converter();
    }

    @Test
    public void testConverterConvertExpressionNumberToString() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.create(123),
            String.class,
            "123"
        );
    }


    @Test
    public void testConverterConvertStringToExpressionNumber() {
        this.convertAndCheck(
            "123",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(123)
        );
    }

    @Test
    public void testConverterConvertStringToExpressionNumber2() {
        this.convertAndCheck(
            "123.5",
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(123.5)
        );
    }

    private <T> T convertAndCheck(final Object value,
                                  final Class<T> target,
                                  final T expected) {
        final SpreadsheetEnvironmentContextFactory factory = this.createContext();

        return this.convertAndCheck(
            factory.converter(),
            value,
            target,
            factory.spreadsheetConverterContext(),
            expected
        );
    }

    // DecimalNumberContext.............................................................................................

    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_DIGIT_COUNT;
    }

    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = METADATA_EN_AU.decimalNumberContext(
        SpreadsheetExpressionEvaluationContext.NO_CELL,
        LOCALE_CONTEXT
    );

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentSpreadsheetEnvironmentContext() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
            EnvironmentValueName.with(
                "Different",
                Integer.class
            ),
            1
        );

        this.checkNotEquals(
            SpreadsheetEnvironmentContextFactory.with(
                spreadsheetEnvironmentContext,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentLocaleContext() {
        this.checkNotEquals(
            SpreadsheetEnvironmentContextFactory.with(
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LocaleContexts.fake(),
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetProvider() {
        this.checkNotEquals(
            SpreadsheetEnvironmentContextFactory.with(
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
                SpreadsheetProviders.fake(),
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testEqualsDifferentProviderContext() {
        this.checkNotEquals(
            SpreadsheetEnvironmentContextFactory.with(
                SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                ProviderContexts.fake()
            )
        );
    }

    @Override
    public SpreadsheetEnvironmentContextFactory createObject() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<SpreadsheetEnvironmentContextFactory> type() {
        return SpreadsheetEnvironmentContextFactory.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
