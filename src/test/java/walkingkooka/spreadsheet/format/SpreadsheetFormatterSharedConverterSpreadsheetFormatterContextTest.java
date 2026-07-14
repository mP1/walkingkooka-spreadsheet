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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyExchange;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.currency.FakeCurrencyContext;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.header.MediaTypeDetectors;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetFormatterSharedConverterSpreadsheetFormatterContextTest implements SpreadsheetFormatterContextTesting2<SpreadsheetFormatterSharedConverterSpreadsheetFormatterContext>,
    DecimalNumberContextDelegator {

    private final static Locale LOCALE = Locale.FRANCE;

    private final static DateTimeContext DATE_TIME_CONTEXT = DateTimeContexts.basic(
        DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(LOCALE)
        ),
        LOCALE,
        1900,
        19,
        LocalDateTime::now
    );

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.basic(
        DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
        DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            DecimalFormatSymbols.getInstance(LOCALE)
        ),
        LOCALE,
        MathContext.UNLIMITED
    );

    private final static LocaleContext LOCALE_CONTEXT =  LocaleContexts.jre(LOCALE);

    private final static CurrencyLocaleContext CURRENCY_LOCALE_CONTEXT = new FakeCurrencyContext() {

        @Override
        public Optional<Number> currencyExchangeRate(final CurrencyExchange currencyExchange,
                                                     final Optional<LocalDateTime> dateTime) {
            Objects.requireNonNull(currencyExchange, "currencyExchange");
            Objects.requireNonNull(dateTime, "dateTime");

            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
            Objects.requireNonNull(currencyCode, "currencyCode");
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Currency> currencyForLocale(final Locale locale) {
            return Optional.of(
                Currency.getInstance(locale)
            );
        }
    }.setLocaleContext(LOCALE_CONTEXT);

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata SPREADSHEET_METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    private final static SpreadsheetConverterContext CONVERTER_CONTEXT = SpreadsheetConverterContexts.basic(
        HasUserDirectorieses.fake(),
        SpreadsheetConverterContexts.NO_METADATA,
        SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
        SpreadsheetConverters.system(),
        MediaTypeDetectors.binary(),
        BinaryNumberConverterFunctions.multiply(), // multiplier
        SpreadsheetLabelNameResolvers.empty(),
        new SpreadsheetMetadataLoader() {
            @Override
            public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
                Objects.requireNonNull(id, "id");

                return Optional.ofNullable(
                    SPREADSHEET_ID.equals(id) ?
                        SPREADSHEET_METADATA :
                        null
                );
            }
        },
        JsonNodeConverterContexts.basic(
            ExpressionNumberConverterContexts.basic(
                Converters.fake(),
                BinaryNumberConverterFunctions.multiply(), // multiplier
                ConverterContexts.basic(
                    false, // canNumbersHaveGroupSeparator
                    Converters.JAVA_EPOCH_OFFSET, // dateOffset
                    ',', // valueSeparator
                    Converters.fake(),
                    BinaryNumberConverterFunctions.fake(), // multiplier
                    BINARY_TEXT_CONTEXT,
                    CURRENCY_LOCALE_CONTEXT,
                    DATE_TIME_CONTEXT,
                    DECIMAL_NUMBER_CONTEXT
                ),
                EXPRESSION_NUMBER_KIND
            ),
            JsonNodeMarshallUnmarshallContexts.basic(
                JsonNodeMarshallContexts.basic(),
                JsonNodeUnmarshallContexts.basic(
                    EXPRESSION_NUMBER_KIND,
                    CURRENCY_LOCALE_CONTEXT,
                    DECIMAL_NUMBER_CONTEXT.mathContext()
                )
            )
        ),
        LOCALE_CONTEXT
    );

    @Test
    public void testConvertSameType() {
        this.convertAndCheck(
            this.createContext(),
            "value123",
            String.class,
            "value123"
        );
    }

    @Test
    public void testConvertSubClass() {
        final BigDecimal value = BigDecimal.ONE;
        this.convertAndCheck(
            value,
            Number.class,
            value
        );
    }

    @Test
    public void testConvertByteToBigDecimal() {
        this.convertAndCheck(
            Byte.MAX_VALUE,
            BigDecimal.class,
            BigDecimal.valueOf(
                Byte.MAX_VALUE
            )
        );
    }

    @Test
    public void testConvertBigDecimalToByte() {
        this.convertAndCheck(
            BigDecimal.valueOf(
                Byte.MAX_VALUE
            ),
            Byte.class,
            Byte.MAX_VALUE
        );
    }

    @Test
    public void testConvertDate() {
        final LocalDate date = LocalDate.of(
            2000,
            1,
            31
        );

        this.convertAndCheck(
            date,
            LocalDateTime.class,
            Converters.localDateToLocalDateTime()
                .convertOrFail(
                    date,
                    LocalDateTime.class,
                    CONVERTER_CONTEXT
                )
        );
    }

    @Test
    public void testConvertTime() {
        final LocalTime time = LocalTime.of(
            12,
            58,
            59
        );
        this.convertAndCheck(
            time,
            LocalDateTime.class,
            Converters.localTimeToLocalDateTime()
                .convertOrFail(
                    time,
                    LocalDateTime.class,
                    CONVERTER_CONTEXT
                )
        );
    }

    @Test
    public void testLoadMetadata() {
        this.loadMetadataAndCheck(
            this.createContext(),
            SPREADSHEET_ID,
            SPREADSHEET_METADATA
        );
    }

    @Test
    public void testToString() {
        final SpreadsheetConverterContext converterContext = CONVERTER_CONTEXT;
        this.toStringAndCheck(
            SpreadsheetFormatterSharedConverterSpreadsheetFormatterContext.with(converterContext),
            converterContext.toString()
        );
    }

    @Override
    public void testSpreadsheetExpressionEvaluationContextWithNullValueFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormatterSharedConverterSpreadsheetFormatterContext createContext() {
        return SpreadsheetFormatterSharedConverterSpreadsheetFormatterContext.with(CONVERTER_CONTEXT);
    }

    @Override
    public String currencySymbol() {
        return this.decimalNumberContext().currencySymbol();
    }

    @Override
    public int decimalNumberDigitCount() {
        return this.decimalNumberContext()
            .decimalNumberDigitCount();
    }

    @Override
    public char decimalSeparator() {
        return this.decimalNumberContext().decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.decimalNumberContext().exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.decimalNumberContext().groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return this.decimalNumberContext().mathContext();
    }

    @Override
    public char negativeSign() {
        return this.decimalNumberContext().negativeSign();
    }

    @Override
    public char percentSymbol() {
        return this.decimalNumberContext().percentSymbol();
    }

    @Override
    public char positiveSign() {
        return this.decimalNumberContext().positiveSign();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSharedConverterSpreadsheetFormatterContext> type() {
        return SpreadsheetFormatterSharedConverterSpreadsheetFormatterContext.class;
    }
}
