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

package walkingkooka.spreadsheet.convert.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MissingConverterVerifierTest implements TreePrintableTesting,
    ClassTesting<MissingConverterVerifier>,
    SpreadsheetMetadataTesting {

    @Test
    public void testVerifyAndCheckWithNullConverterFails() {
        assertThrows(
            NullPointerException.class,
            () -> MissingConverterVerifier.verify(
                null,
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                SpreadsheetConverterContexts.fake()
            )
        );
    }

    @Test
    public void testVerifyAndCheckWithNullSpreadsheetMetadataPropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> MissingConverterVerifier.verify(
                Converters.fake(),
                null,
                SpreadsheetConverterContexts.fake()
            )
        );
    }

    @Test
    public void testVerifyAndCheckWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> MissingConverterVerifier.verify(
                Converters.fake(),
                SpreadsheetMetadataPropertyName.FIND_CONVERTER,
                null
            )
        );
    }

    @Test
    public void testVerifyAndCheckWithWithFindConverterAndSpreadsheetMetadataTestingMetadataEnAu() {
        this.verifyAndCheck(
            SpreadsheetMetadataPropertyName.FIND_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithFormulaConverterAndSpreadsheetMetadataTestingMetadataEnAu() {
        this.verifyAndCheck(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithFormattingConverterAndSpreadsheetMetadataTestingMetadataEnAu() {
        this.verifyAndCheck(
            SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithScriptingConverterAndSpreadsheetMetadataTestingMetadataEnAu() {
        this.verifyAndCheck(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithSortConverterAndSpreadsheetMetadataTestingMetadataEnAu() {
        this.verifyAndCheck(
            SpreadsheetMetadataPropertyName.SORT_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithValidationConverter() {
        this.verifyAndCheck(
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER
        );
    }

    @Test
    public void testVerifyAndCheckWithWithFormulaConverterAndSpreadsheetMetadataTestingSpreadsheetProvider() {
        this.verifyAndCheck(
            SpreadsheetMetadataPropertyName.FORMULA_CONVERTER,
            SpreadsheetConverters.SYSTEM_CONVERTER_SELECTOR,
            SpreadsheetMetadataTesting.CONVERTER_PROVIDER
        );
    }

    private void verifyAndCheck(final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                final MissingConverter... expected) {
        this.verifyAndCheck(
            propertyName,
            SpreadsheetMetadataTesting.METADATA_EN_AU.getOrFail(propertyName),
            SpreadsheetMetadataTesting.CONVERTER_PROVIDER,
            expected
        );
    }

    private void verifyAndCheck(final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                final ConverterSelector converterSelector,
                                final ConverterProvider converterProvider,
                                final MissingConverter... expected) {
        // KEEP useful helps to update SpreadsheetMetadataDefaultTextResource.json
        System.out.println(propertyName + "=" + converterSelector);

        this.verifyAndCheck(
            converterProvider.converter(
                converterSelector,
                PROVIDER_CONTEXT
            ),
            propertyName,
            expected
        );
    }

    private void verifyAndCheck(final Converter<SpreadsheetConverterContext> converter,
                                final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                final MissingConverter... expected) {
        this.verifyAndCheck(
            converter,
            propertyName,
            new TestSpreadsheetConverterContext(converter),
            expected
        );
    }

    static class TestSpreadsheetConverterContext implements SpreadsheetConverterContext,
        DateTimeContextDelegator,
        DecimalNumberContextDelegator,
        JsonNodeMarshallUnmarshallContextDelegator,
        LocaleContextDelegator {

        TestSpreadsheetConverterContext(final Converter<SpreadsheetConverterContext> converter) {
            this.converter = converter;
        }

        @Override
        public StoragePath parseStoragePath(final String value) {
            return StoragePath.parseSpecial(
                value,
                this  // HasUserDirectories
            );
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> type) {
            return converter.canConvert(
                value,
                type,
                this
            );
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> target) {
            return converter.convert(
                value,
                target,
                this
            );
        }

        private final Converter<SpreadsheetConverterContext> converter;

        @Override
        public ExpressionNumberKind expressionNumberKind() {
            return ExpressionNumberKind.BIG_DECIMAL;
        }

        @Override
        public JsonNode marshall(final Object object) {
            return JsonNodeMarshallContexts.basic()
                .marshall(object);
        }

        @Override
        public DateTimeContext dateTimeContext() {
            return this.dateTimeContext;
        }

        private final DateTimeContext dateTimeContext = METADATA_EN_AU.dateTimeContext(
            SpreadsheetMetadata.NO_CELL,
            HAS_NOW,
            LOCALE_CONTEXT
        );

        @Override
        public DecimalNumberContext decimalNumberContext() {
            return this.decimalNumberContext;
        }

        @Override
        public MathContext mathContext() {
            return this.decimalNumberContext.mathContext();
        }

        private final DecimalNumberContext decimalNumberContext = METADATA_EN_AU.decimalNumberContext(
            SpreadsheetMetadata.NO_CELL,
            LOCALE_CONTEXT
        );

        @Override
        public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
            return this.jsonNodeMarshallUnmarshallContext;
        }

        private final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext = JsonNodeMarshallUnmarshallContexts.basic(
            JSON_NODE_MARSHALL_CONTEXT,
            JSON_NODE_UNMARSHALL_CONTEXT
        );

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            return CURRENCY_CONTEXT.currencyForLocale(locale);
        }

        @Override
        public boolean canNumbersHaveGroupSeparator() {
            return false;
        }

        @Override
        public long dateOffset() {
            return 0;
        }

        @Override
        public Indentation indentation() {
            return Indentation.SPACES2;
        }

        @Override
        public LineEnding lineEnding() {
            return LINE_ENDING;
        }

        @Override
        public char valueSeparator() {
            return ',';
        }

        @Override
        public Converter<SpreadsheetConverterContext> converter() {
            return this.converter;
        }

        @Override
        public LocaleContext localeContext() {
            return LOCALE_CONTEXT;
        }

        @Override
        public Locale locale() {
            return LOCALE_CONTEXT.locale();
        }

        @Override
        public void setLocale(final Locale locale) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<StoragePath> currentWorkingDirectory() {
            return NO_CURRENT_WORKING_DIRECTORY;
        }

        @Override
        public Optional<StoragePath> homeDirectory() {
            return HOME_DIRECTORY;
        }

        @Override
        public SpreadsheetExpressionReference validationReference() {
            return SpreadsheetSelection.A1;
        }

        @Override
        public SpreadsheetMetadata spreadsheetMetadata() {
            return METADATA_EN_AU;
        }

        @Override
        public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
            return Optional.empty();
        }

        @Override
        public SpreadsheetConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Locale> localeForLanguageTag(final String languageTag) {
            return LOCALE_CONTEXT.localeForLanguageTag(languageTag);
        }
    }

    private void verifyAndCheck(final Converter<SpreadsheetConverterContext> converter,
                                final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                final SpreadsheetConverterContext context,
                                final MissingConverter... expected) {
        this.verifyAndCheck(
            converter,
            propertyName,
            context,
            Sets.of(expected)
        );
    }

    private void verifyAndCheck(final Converter<SpreadsheetConverterContext> converter,
                                final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                final SpreadsheetConverterContext context,
                                final Set<MissingConverter> expected) {
        this.checkEquals(
            expected,
            MissingConverterVerifier.verify(
                converter,
                propertyName,
                context
            ),
            () -> propertyName + "=" + converter
        );
    }

    @Test
    public void testVerifyAndMarshall() {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            (ProviderContext p) -> SpreadsheetMetadata.EMPTY.dateTimeConverter(
                SPREADSHEET_FORMATTER_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                p
            )
        ).converter(
            ConverterSelector.parse("basic"),
            PROVIDER_CONTEXT
        );

        final Set<MissingConverter> missing = MissingConverterVerifier.verify(
            converter,
            SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER,
            new TestSpreadsheetConverterContext(converter)
        );

        JSON_NODE_MARSHALL_CONTEXT.marshall(missing);
    }

    // class............................................................................................................

    @Override
    public Class<MissingConverterVerifier> type() {
        return MissingConverterVerifier.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
