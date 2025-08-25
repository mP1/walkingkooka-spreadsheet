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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public final class SpreadsheetFormatterConverterSpreadsheetFormatterContextTest implements SpreadsheetFormatterContextTesting2<SpreadsheetFormatterConverterSpreadsheetFormatterContext>,
    DecimalNumberContextDelegator {

    private final static Locale LOCALE = Locale.FRANCE;

    @Test
    public void testConvertSameType() {
        this.convertAndCheck(this.createContext(), "value123", String.class, "value123");
    }

    @Test
    public void testConvertSubClass() {
        final BigDecimal value = BigDecimal.ONE;
        this.convertAndCheck(value, Number.class, value);
    }

    @Test
    public void testConvertByteToBigDecimal() {
        this.convertAndCheck(Byte.MAX_VALUE, BigDecimal.class, BigDecimal.valueOf(Byte.MAX_VALUE));
    }

    @Test
    public void testConvertBigDecimalToByte() {
        this.convertAndCheck(BigDecimal.valueOf(Byte.MAX_VALUE), Byte.class, Byte.MAX_VALUE);
    }

    @Test
    public void testConvertDate() {
        final LocalDate date = LocalDate.of(2000, 1, 31);
        this.convertAndCheck(date,
            LocalDateTime.class,
            Converters.localDateToLocalDateTime()
                .convertOrFail(
                    date,
                    LocalDateTime.class,
                    this.converterContext()
                )
        );
    }

    @Test
    public void testConvertTime() {
        final LocalTime time = LocalTime.of(12, 58, 59);
        this.convertAndCheck(
            time,
            LocalDateTime.class,
            Converters.localTimeToLocalDateTime()
                .convertOrFail(
                    time,
                    LocalDateTime.class,
                    this.converterContext()
                )
        );
    }

    @Test
    public void testToString() {
        final SpreadsheetConverterContext converterContext = this.converterContext();
        this.toStringAndCheck(
            SpreadsheetFormatterConverterSpreadsheetFormatterContext.with(converterContext),
            converterContext.toString()
        );
    }

    @Override
    public void testSpreadsheetExpressionEvaluationContextWithNullValueFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetFormatterConverterSpreadsheetFormatterContext createContext() {
        return SpreadsheetFormatterConverterSpreadsheetFormatterContext.with(this.converterContext());
    }

    @Override
    public String currencySymbol() {
        return this.decimalNumberContext().currencySymbol();
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

    private SpreadsheetConverterContext converterContext() {
        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.system(),
            (s) -> {
                throw new UnsupportedOperationException();
            },
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.fake(),
                    ConverterContexts.basic(
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        Converters.fake(),
                        dateTimeContext(),
                        decimalNumberContext()
                    ),
                    ExpressionNumberKind.DEFAULT
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            ),
            LocaleContexts.fake()
        );
    }

    private DateTimeContext dateTimeContext() {
        return DateTimeContexts.basic(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(LOCALE)
            ),
            LOCALE,
            1900,
            19,
            LocalDateTime::now
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
            DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                DecimalFormatSymbols.getInstance(LOCALE)
            ),
            LOCALE,
            MathContext.UNLIMITED
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterConverterSpreadsheetFormatterContext> type() {
        return SpreadsheetFormatterConverterSpreadsheetFormatterContext.class;
    }
}
