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
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.math.DecimalNumberContexts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetConverterTest extends SpreadsheetConverterTestCase<SpreadsheetConverter>
        implements ConverterTesting2<SpreadsheetConverter> {

    private final static long DATE_OFFSET = Converters.JAVA_EPOCH_OFFSET;

    private final static Double DECIMAL_DOUBLE = 12.5;
    private final static BigDecimal DECIMAL_BIG_DECIMAL = BigDecimal.valueOf(DECIMAL_DOUBLE);
    private final static String DECIMAL_STRING = "12D50!!!";
    private final static String DECIMAL_STRING_LOCAL_DATE_TIME = "13 01 1970 12 00 00!!!";

    private final static Long WHOLE_LONG = 12L;
    private final static BigDecimal WHOLE_BIG_DECIMAL = BigDecimal.valueOf(WHOLE_LONG);
    private final static BigInteger WHOLE_BIG_INTEGER = BigInteger.valueOf(WHOLE_LONG);
    private final static Double WHOLE_DOUBLE = WHOLE_LONG.doubleValue();
    private final static LocalDate WHOLE_LOCAL_DATE = LocalDate.ofEpochDay(DATE_OFFSET + WHOLE_LONG);
    private final static LocalDateTime WHOLE_LOCAL_DATE_TIME = LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.MIDNIGHT);
    private final static LocalTime WHOLE_LOCAL_TIME = LocalTime.ofSecondOfDay(WHOLE_LONG);
    private final static String WHOLE_STRING = "12!!!";
    private final static String WHOLE_STRING_LOCAL_DATE = "13 01 1970!!!";
    private final static String WHOLE_STRING_LOCAL_TIME = "12 00 00!!!";

    private final static LocalDate LOCAL_DATE_FALSE = LocalDate.ofEpochDay(DATE_OFFSET);
    private final static LocalDate LOCAL_DATE_TRUE = LOCAL_DATE_FALSE.plusDays(1);

    private final static LocalDateTime LOCAL_DATE_TIME_FALSE = LocalDateTime.ofEpochSecond(DATE_OFFSET, 0, ZoneOffset.UTC);

    private final static LocalTime LOCAL_TIME_FALSE = LocalTime.ofSecondOfDay(0);
    private final static LocalTime LOCAL_TIME_TRUE = LocalTime.ofSecondOfDay(1);

    private final static Long LONG_FALSE = 0L;
    private final static Long LONG_TRUE = 1L;

    private final static String STRING_FALSE = "false";
    private final static String STRING_TRUE = "true";

    private final static LocalDateTime DECIMAL_LOCAL_DATE_TIME = LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.NOON);

    // BigDecimal.......................................................................................................

    @Test
    public void testBigDecimalBigDecimal() {
        this.convertAndCheck(DECIMAL_BIG_DECIMAL);
    }

    @Test
    public void testBigDecimalBigInteger() {
        this.convertAndCheck(WHOLE_BIG_DECIMAL,
                BigInteger.class,
                WHOLE_BIG_INTEGER);
    }

    @Test
    public void testBigDecimalBooleanTrue() {
        this.convertAndCheck(WHOLE_BIG_DECIMAL,
                Boolean.class,
                true);
    }

    @Test
    public void testBigDecimalBooleanFalse() {
        this.convertAndCheck(BigDecimal.ZERO,
                Boolean.class,
                false);
    }

    @Test
    public void testBigDecimalDouble() {
        this.convertAndCheck(DECIMAL_BIG_DECIMAL,
                Double.class,
                DECIMAL_DOUBLE);
    }

    @Test
    public void testBigDecimalLocalDate() {
        this.convertAndCheck(WHOLE_BIG_DECIMAL,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testBigDecimalLocalDateTime() {
        this.convertAndCheck(DECIMAL_BIG_DECIMAL,
                LocalDateTime.class,
                LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.NOON));
    }

    @Test
    public void testBigDecimalLocalTime() {
        this.convertAndCheck(WHOLE_BIG_DECIMAL,
                LocalTime.class,
                WHOLE_LOCAL_TIME);
    }

    @Test
    public void testBigDecimalLong() {
        this.convertAndCheck(WHOLE_BIG_DECIMAL,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testBigDecimalString() {
        this.convertAndCheck(DECIMAL_BIG_DECIMAL,
                String.class,
                DECIMAL_STRING);
    }

    // BigInteger.......................................................................................................

    @Test
    public void testBigIntegerBigDecimal() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                BigDecimal.class,
                WHOLE_BIG_DECIMAL);
    }

    @Test
    public void testBigIntegerBigInteger() {
        this.convertAndCheck(WHOLE_BIG_DECIMAL);
    }

    @Test
    public void testBigIntegerBooleanTrue() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                Boolean.class,
                true);
    }

    @Test
    public void testBigIntegerBooleanFalse() {
        this.convertAndCheck(BigInteger.ZERO,
                Boolean.class,
                false);
    }

    @Test
    public void testBigIntegerDouble() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                Double.class,
                WHOLE_DOUBLE);
    }

    @Test
    public void testBigIntegerLocalDate() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testBigIntegerLocalDateTime() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                LocalDateTime.class,
                LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testBigIntegerLocalTime() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                LocalTime.class,
                WHOLE_LOCAL_TIME);
    }

    @Test
    public void testBigIntegerLong() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testBigIntegerString() {
        this.convertAndCheck(WHOLE_BIG_INTEGER,
                String.class,
                WHOLE_STRING);
    }

    // BigInteger.......................................................................................................

    @Test
    public void testBooleanTrueBigDecimal() {
        this.convertAndCheck(Boolean.TRUE,
                BigDecimal.class,
                BigDecimal.ONE);
    }

    @Test
    public void testBooleanFalseBigDecimal() {
        this.convertAndCheck(Boolean.FALSE,
                BigDecimal.class,
                BigDecimal.ZERO);
    }

    @Test
    public void testBooleanTrueBigInteger() {
        this.convertAndCheck(Boolean.TRUE,
                BigInteger.class,
                BigInteger.ONE);
    }

    @Test
    public void testBooleanFalseBigInteger() {
        this.convertAndCheck(Boolean.FALSE,
                BigInteger.class,
                BigInteger.ZERO);
    }

    @Test
    public void testBooleanTrueBoolean() {
        this.convertAndCheck(true);
    }

    @Test
    public void testBooleanFalseBooleanFalse() {
        this.convertAndCheck(false);
    }

    @Test
    public void testBooleanTrueDouble() {
        this.convertAndCheck(true,
                Double.class,
                1.0);
    }

    @Test
    public void testBooleanFalseDouble() {
        this.convertAndCheck(false,
                Double.class,
                0.0);
    }

    @Test
    public void testBooleanTrueLocalDate() {
        this.convertAndCheck(true,
                LocalDate.class,
                LOCAL_DATE_TRUE);
    }

    @Test
    public void testBooleanFalseLocalDate() {
        this.convertAndCheck(false,
                LocalDate.class,
                LocalDate.ofEpochDay(DATE_OFFSET + 0));
    }

    @Test
    public void testBooleanTrueLocalDateTime() {
        this.convertAndCheck(true,
                LocalDateTime.class,
                LocalDateTime.of(LOCAL_DATE_TRUE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testBooleanFalseLocalDateTime() {
        this.convertAndCheck(false,
                LocalDateTime.class,
                LocalDateTime.of(LOCAL_DATE_FALSE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testBooleanTrueLocalTime() {
        this.convertAndCheck(true,
                LocalTime.class,
                LOCAL_TIME_TRUE);
    }

    @Test
    public void testBooleanFalseLocalTime() {
        this.convertAndCheck(false,
                LocalTime.class,
                LocalTime.ofSecondOfDay(0));
    }

    @Test
    public void testBooleanTrueLong() {
        this.convertAndCheck(true,
                Long.class,
                LONG_TRUE);
    }

    @Test
    public void testBooleanFalseLong() {
        this.convertAndCheck(false,
                Long.class,
                LONG_FALSE);
    }

    @Test
    public void testBooleanTrueString() {
        this.convertAndCheck(true,
                String.class,
                "true");
    }

    @Test
    public void testBooleanFalseString() {
        this.convertAndCheck(false,
                String.class,
                "false");
    }

    // Double...........................................................................................................

    @Test
    public void testDoubleBigDecimal() {
        this.convertAndCheck(DECIMAL_DOUBLE,
                BigDecimal.class,
                DECIMAL_BIG_DECIMAL);
    }

    @Test
    public void testDoubleBigInteger() {
        this.convertAndCheck(WHOLE_DOUBLE,
                BigInteger.class,
                WHOLE_BIG_INTEGER);
    }

    @Test
    public void testDoubleBooleanTrue() {
        this.convertAndCheck(1.0,
                Boolean.class,
                true);
    }

    @Test
    public void testDoubleBooleanFalse() {
        this.convertAndCheck(0.0,
                Boolean.class,
                false);
    }

    @Test
    public void testDoubleDouble() {
        this.convertAndCheck(DECIMAL_DOUBLE);
    }

    @Test
    public void testDoubleLocalDate() {
        this.convertAndCheck(WHOLE_DOUBLE,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testDoubleLocalDateTime() {
        this.convertAndCheck(WHOLE_DOUBLE,
                LocalDateTime.class,
                LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testDoubleLocalTime() {
        this.convertAndCheck(WHOLE_DOUBLE,
                LocalTime.class,
                WHOLE_LOCAL_TIME);
    }

    @Test
    public void testDoubleLong() {
        this.convertAndCheck(WHOLE_DOUBLE,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testDoubleString() {
        this.convertAndCheck(DECIMAL_DOUBLE,
                String.class,
                DECIMAL_STRING);
    }

    // LocalDate........................................................................................................

    @Test
    public void testLocalDateBigDecimal() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                BigDecimal.class,
                WHOLE_BIG_DECIMAL);
    }

    @Test
    public void testLocalDateBigInteger() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                BigInteger.class,
                WHOLE_BIG_INTEGER);
    }

    @Test
    public void testLocalDateBooleanTrue() {
        this.convertAndCheck(LOCAL_DATE_TRUE,
                Boolean.class,
                true);
    }

    @Test
    public void testLocalDateBooleanFalse() {
        this.convertAndCheck(LOCAL_DATE_FALSE,
                Boolean.class,
                false);
    }

    @Test
    public void testLocalDateDouble() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                Double.class,
                WHOLE_DOUBLE);
    }

    @Test
    public void testLocalDateLocalDate() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testLocalDateLocalDateTime() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                LocalDateTime.class,
                LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testLocalDateLocalTime() {
        this.convertFails(WHOLE_LOCAL_DATE,
                LocalTime.class);
    }

    @Test
    public void testLocalDateLong() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testLocalDateString() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                String.class,
                WHOLE_STRING_LOCAL_DATE);
    }

    // LocalDateTime....................................................................................................

    @Test
    public void testLocalDateTimeBigDecimal() {
        this.convertAndCheck(DECIMAL_LOCAL_DATE_TIME,
                BigDecimal.class,
                DECIMAL_BIG_DECIMAL);
    }

    @Test
    public void testLocalDateTimeBigInteger() {
        this.convertAndCheck(WHOLE_LOCAL_DATE_TIME,
                BigInteger.class,
                WHOLE_BIG_INTEGER);
    }

    @Test
    public void testLocalDateTimeBooleanTrue() {
        this.convertAndCheck(DECIMAL_LOCAL_DATE_TIME,
                Boolean.class,
                true);
    }

    @Test
    public void testLocalDateTimeBooleanFalse() {
        this.convertAndCheck(LOCAL_DATE_TIME_FALSE,
                Boolean.class,
                false);
    }

    @Test
    public void testLocalDateTimeDouble() {
        this.convertAndCheck(DECIMAL_LOCAL_DATE_TIME,
                Double.class,
                DECIMAL_DOUBLE);
    }

    @Test
    public void testLocalDateTimeLocalDate() {
        this.convertAndCheck(WHOLE_LOCAL_DATE_TIME,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testLocalDateTimeLocalDateTime() {
        this.convertAndCheck(DECIMAL_LOCAL_DATE_TIME);
    }

    @Test
    public void testLocalDateTimeLocalTime() {
        this.convertAndCheck(LocalDateTime.of(LOCAL_DATE_FALSE, LocalTime.NOON),
                LocalTime.class,
                LocalTime.NOON);
    }

    @Test
    public void testLocalDateTimeLong() {
        this.convertAndCheck(WHOLE_LOCAL_DATE_TIME,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testLocalDateTimeString() {
        this.convertAndCheck(DECIMAL_LOCAL_DATE_TIME,
                String.class,
                DECIMAL_STRING_LOCAL_DATE_TIME);
    }

    // LocalTime........................................................................................................

    @Test
    public void testLocalTimeBigDecimal() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                BigDecimal.class,
                WHOLE_BIG_DECIMAL);
    }

    @Test
    public void testLocalTimeBigInteger() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                BigInteger.class,
                WHOLE_BIG_INTEGER);
    }

    @Test
    public void testLocalTimeBooleanTrue() {
        this.convertAndCheck(LOCAL_TIME_TRUE,
                Boolean.class,
                true);
    }

    @Test
    public void testLocalTimeBooleanFalse() {
        this.convertAndCheck(LOCAL_TIME_FALSE,
                Boolean.class,
                false);
    }

    @Test
    public void testLocalTimeDouble() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                Double.class,
                WHOLE_DOUBLE);
    }

    @Test
    public void testLocalTimeLocalDate() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testLocalTimeLocalDateTime() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                LocalDateTime.class,
                LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testLocalTimeLocalTime() {
        this.convertFails(WHOLE_LOCAL_DATE,
                LocalTime.class);
    }

    @Test
    public void testLocalTimeLong() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testLocalTimeString() {
        this.convertAndCheck(WHOLE_LOCAL_DATE,
                String.class,
                WHOLE_STRING_LOCAL_DATE);
    }

    // LocalTime........................................................................................................

    @Test
    public void testLongBigDecimal() {
        this.convertAndCheck(WHOLE_LONG,
                BigDecimal.class,
                WHOLE_BIG_DECIMAL);
    }

    @Test
    public void testLongBigInteger() {
        this.convertAndCheck(WHOLE_LONG,
                BigInteger.class,
                WHOLE_BIG_INTEGER);
    }

    @Test
    public void testLongBooleanTrue() {
        this.convertAndCheck(LONG_TRUE,
                Boolean.class,
                true);
    }

    @Test
    public void testLongBooleanFalse() {
        this.convertAndCheck(LONG_FALSE,
                Boolean.class,
                false);
    }

    @Test
    public void testLongDouble() {
        this.convertAndCheck(WHOLE_LONG,
                Double.class,
                WHOLE_DOUBLE);
    }

    @Test
    public void testLongLocalDate() {
        this.convertAndCheck(WHOLE_LONG,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testLongLocalDateTime() {
        this.convertAndCheck(WHOLE_LONG,
                LocalDateTime.class,
                LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testLongLocalTime() {
        this.convertAndCheck(WHOLE_LONG,
                LocalTime.class,
                WHOLE_LOCAL_TIME);
    }

    @Test
    public void testLongLong() {
        this.convertAndCheck(WHOLE_LONG,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testLongString() {
        this.convertAndCheck(WHOLE_LONG,
                String.class,
                WHOLE_STRING);
    }

    // String........................................................................................................

    @Test
    public void testStringBigDecimal() {
        this.convertAndCheck(DECIMAL_STRING,
                BigDecimal.class,
                DECIMAL_BIG_DECIMAL);
    }

    @Test
    public void testStringBigInteger() {
        this.convertAndCheck(WHOLE_STRING,
                BigInteger.class,
                WHOLE_BIG_INTEGER);
    }

    @Test
    public void testStringBooleanTrue() {
        this.convertAndCheck(STRING_TRUE,
                Boolean.class,
                true);
    }

    @Test
    public void testStringBooleanFalse() {
        this.convertAndCheck(STRING_FALSE,
                Boolean.class,
                false);
    }

    @Test
    public void testStringDouble() {
        this.convertAndCheck(WHOLE_STRING,
                Double.class,
                WHOLE_DOUBLE);
    }

    @Test
    public void testStringLocalDate() {
        this.convertAndCheck(WHOLE_LONG,
                LocalDate.class,
                WHOLE_LOCAL_DATE);
    }

    @Test
    public void testStringLocalDateTime() {
        this.convertAndCheck(DECIMAL_STRING_LOCAL_DATE_TIME,
                LocalDateTime.class,
                LocalDateTime.of(WHOLE_LOCAL_DATE, LocalTime.NOON));
    }

    @Test
    public void testStringLocalTime() {
        this.convertAndCheck(WHOLE_STRING_LOCAL_TIME,
                LocalTime.class,
                LocalTime.NOON);
    }

    @Test
    public void testStringLong() {
        this.convertAndCheck(WHOLE_STRING,
                Long.class,
                WHOLE_LONG);
    }

    @Test
    public void testStringString() {
        this.convertAndCheck("abc123");
    }

    @Test
    public void testBigDecimalBigIntegerDoubleLocalDateLocalDateTimeLongStringBigDecimal() {
        final SpreadsheetConverter converter = this.createConverter();

        final List<Class<?>> allTargets = Lists.of(BigInteger.class,
                Double.class,
                LocalDate.class,
                LocalDateTime.class,
                Long.class,
                String.class,
                BigDecimal.class);

        final ConverterContext context = this.createContext();

        for (int i = 0; i < 10; i++) {
            final StringBuilder b = new StringBuilder();

            final BigDecimal bigDecimal = BigDecimal.valueOf(i);

            Object value = bigDecimal;
            for (Class<?> target : allTargets) {
                b.append(value)
                        .append(' ')
                        .append(target.getName())
                        .append(' ');

                value = converter.convert(value, target, context);

                b.append(value).append('\n');
            }

            assertEquals(bigDecimal, value, () -> b.toString());
        }
    }

    // ConverterTesting.................................................................................................

    @Override
    public SpreadsheetConverter createConverter() {
        return SpreadsheetConverter.with(Converters.JAVA_EPOCH_OFFSET,
                "##00.00'!!!'",
                "##00'!!!'",
                "##00.00'!!!'",
                DateTimeFormatter.ofPattern("dd MM yyyy'!!!'"),
                DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss'!!!'"),
                DateTimeFormatter.ofPattern("HH mm ss'!!!'"),
                "##00'!!!'");
    }

    @Override
    public ConverterContext createContext() {
        return ConverterContexts.basic(DecimalNumberContexts.basic("C",
                'D',
                'E',
                'G',
                'M',
                'P',
                'L',
                MathContext.DECIMAL32));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetConverter> type() {
        return SpreadsheetConverter.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
