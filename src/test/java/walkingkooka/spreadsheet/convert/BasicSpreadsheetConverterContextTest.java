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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyCodeLanguageTagContext;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.currency.CurrencyValue;
import walkingkooka.currency.FakeCurrencyContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.header.MediaTypeDetector;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.storage.HasUserDirectories;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetConverterContextTest implements SpreadsheetConverterContextTesting<BasicSpreadsheetConverterContext>,
    DecimalNumberContextDelegator {

    private final static HasUserDirectories HAS_USER_DIRECTORIES = HasUserDirectorieses.fake();

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private final static Optional<SpreadsheetValidationReference> VALIDATION_REFERENCE = Optional.empty();

    private final static Converter<SpreadsheetConverterContext> CONVERTER = SpreadsheetConverters.collection(
        Lists.of(
            SpreadsheetConverters.currencyValueToNumber(),
            SpreadsheetConverters.numberToNumber()
        )
    );

    private final DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    private final static SpreadsheetLabelNameResolver LABEL_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    private final static MediaTypeDetector MEDIA_TYPE_DETECTOR = MediaTypeDetectors.binary();

    private final static BinaryNumberConverterFunction<SpreadsheetConverterContext> MULTIPLIER = ExpressionNumberBinaryNumberConverterFunctions.multiply();

    private final static JsonNodeConverterContext JSON_NODE_CONVERTER_CONTEXT = JsonNodeConverterContexts.fake();

    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(LOCALE);

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata SPREADSHEET_METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    private final static SpreadsheetMetadataLoader SPREADSHEET_METADATA_LOADER = new SpreadsheetMetadataLoader() {
        @Override
        public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId spreadsheetId) {
            Objects.requireNonNull(spreadsheetId, "spreadsheetId");

            return Optional.ofNullable(
                SPREADSHEET_ID.equals(spreadsheetId) ?
                    SPREADSHEET_METADATA :
                    null
            );
        }
    };

    // with.............................................................................................................

    @Test
    public void testWithNullHasUserDirectoriesFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                null,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                LABEL_RESOLVER,
                SPREADSHEET_METADATA_LOADER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                null,
                VALIDATION_REFERENCE,
                CONVERTER,
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                LABEL_RESOLVER,
                SPREADSHEET_METADATA_LOADER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullConverterFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                null,
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                LABEL_RESOLVER,
                SPREADSHEET_METADATA_LOADER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMediaTypeDetectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                null,
                MULTIPLIER,
                LABEL_RESOLVER,
                SPREADSHEET_METADATA_LOADER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullMultiplierFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                MEDIA_TYPE_DETECTOR,
                null,
                LABEL_RESOLVER,
                SPREADSHEET_METADATA_LOADER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetLabelNameResolverFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                null,
                SPREADSHEET_METADATA_LOADER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetMetadataLoaderFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                LABEL_RESOLVER,
                null,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullJsonNodeConverterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                LABEL_RESOLVER,
                SPREADSHEET_METADATA_LOADER,
                null,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                HAS_USER_DIRECTORIES,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                MEDIA_TYPE_DETECTOR,
                MULTIPLIER,
                LABEL_RESOLVER,
                SPREADSHEET_METADATA_LOADER,
                JSON_NODE_CONVERTER_CONTEXT,
                null
            )
        );
    }

    // convert..........................................................................................................

    private final static CurrencyCode FROM_CURRENCY_CODE = CurrencyCode.parse("AUD");
    private final static CurrencyCode TO_CURRENCY_CODE = CurrencyCode.parse("NZD");

    private final static int CURRENCY_MULTIPLIER = 2;

    @Test
    public void testConvertCurrencyValueIntegerToExpressionNumber() {
        final int number = 123;

        this.convertAndCheck(
            CurrencyValue.with(
                number,
                FROM_CURRENCY_CODE
            ),
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(number * CURRENCY_MULTIPLIER)
        );
    }

    @Test
    public void testConvertCurrencyValueIntegerToInteger() {
        final int number = 123;

        this.convertAndCheck(
            CurrencyValue.with(
                number,
                FROM_CURRENCY_CODE
            ),
            Integer.class,
            number * CURRENCY_MULTIPLIER
        );
    }

    @Test
    public void testConvertIntegerToExpressionNumber() {
        final int number = 123;

        this.convertAndCheck(
            number,
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(number)
        );
    }

    @Test
    public void testConvertIntegerToFloat() {
        this.convertAndCheck(
            123,
            Float.class,
            123f
        );
    }

    @Test
    public void testConvertIntegerToNumber() {
        this.convertAndCheck(
            123,
            Float.class,
            123f
        );
    }

    // locale...........................................................................................................

    @Test
    public void testLocale() {
        this.localeAndCheck(
            this.createContext(),
            LOCALE_CONTEXT.locale()
        );
    }

    // SpreadsheetMetadataLoader........................................................................................

    @Test
    public void testLoadMetadata() {
        this.loadMetadataAndCheck(
            this.createContext(),
            SPREADSHEET_ID,
            SPREADSHEET_METADATA
        );
    }

    @Override
    public BasicSpreadsheetConverterContext createContext() {
        return BasicSpreadsheetConverterContext.with(
            HAS_USER_DIRECTORIES,
            SpreadsheetConverterContexts.NO_METADATA,
            VALIDATION_REFERENCE,
            CONVERTER,
            MEDIA_TYPE_DETECTOR,
            MULTIPLIER,
            LABEL_RESOLVER,
            SPREADSHEET_METADATA_LOADER,
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ExpressionNumberBinaryNumberConverterFunctions.multiply(), // multiplier
                    ConverterContexts.basic(
                        false, // canNumbersHaveGroupSeparator
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        ',', // valueSeparator
                        Converters.fake(),
                        BinaryNumberConverterFunctions.fake(), // multiplier
                        BINARY_TEXT_CONTEXT,
                        new FakeCurrencyContext() {

                            @Override
                            public Currency currency() {
                                return Currency.getInstance(
                                    TO_CURRENCY_CODE.value()
                                );
                            }

                            @Override
                            public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                                         final Optional<LocalDateTime> dateTime) {
                                Objects.requireNonNull(currencyExchange, "currencyExchange");
                                Objects.requireNonNull(dateTime, "dateTime");

                                return Optional.ofNullable(
                                    CurrencyExchange.with(
                                        FROM_CURRENCY_CODE,
                                        TO_CURRENCY_CODE
                                    ).equals(currencyExchange) ?
                                        CURRENCY_MULTIPLIER :
                                        null
                                );
                            }

                            @Override
                            public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
                                return Optional.of(
                                    Currency.getInstance(
                                        currencyCode.value()
                                    )
                                );
                            }

                            @Override
                            public Optional<Currency> currencyForLocale(final Locale locale) {
                                return Optional.of(
                                    Currency.getInstance(locale)
                                );
                            }
                        }.setLocaleContext(
                            LocaleContexts.jre(
                                this.locale()
                            )
                        ),
                        DateTimeContexts.basic(
                            LOCALE_CONTEXT.dateTimeSymbolsForLocale(LOCALE)
                                .get(),
                            LOCALE_CONTEXT.locale(),
                            1900,
                            20,
                            LocalDateTime::now
                        ),
                        this.decimalNumberContext()
                    ),
                    EXPRESSION_NUMBER_KIND
                ),
                JsonNodeMarshallUnmarshallContexts.basic(
                    JsonNodeMarshallContexts.basic(),
                    JsonNodeUnmarshallContexts.basic(
                        EXPRESSION_NUMBER_KIND,
                        new CurrencyCodeLanguageTagContext() {

                            @Override
                            public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
                                return Optional.ofNullable(
                                    Currency.getInstance(
                                        currencyCode.value()
                                    )
                                );
                            }

                            @Override
                            public Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
                                return Optional.of(
                                    Locale.forLanguageTag(
                                        languageTag.value()
                                    )
                                );
                            }
                        },
                        this.decimalNumberContext()
                            .mathContext()
                    )
                )
            ),
            LOCALE_CONTEXT
        );
    }

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final ExpressionNumberConverterContext converterContext = ExpressionNumberConverterContexts.fake();
        final JsonNodeMarshallUnmarshallContext marshallUnmarshallContext = JsonNodeMarshallUnmarshallContexts.fake();

        this.toStringAndCheck(
            JsonNodeConverterContexts.basic(
                converterContext,
                marshallUnmarshallContext
            ),
            converterContext +
                " " +
                marshallUnmarshallContext
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetConverterContext> type() {
        return BasicSpreadsheetConverterContext.class;
    }
}
