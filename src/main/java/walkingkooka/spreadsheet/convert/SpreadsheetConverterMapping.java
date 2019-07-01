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

import walkingkooka.ToStringBuilder;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Holds individual values for each spreadsheet value type.
 */
final class SpreadsheetConverterMapping<T> {

    private final static BigDecimal BIG_DECIMAL_TRUE = BigDecimal.ONE;
    private final static BigDecimal BIG_DECIMAL_FALSE = BigDecimal.ZERO;

    private final static BigInteger BIG_INTEGER_TRUE = BigInteger.ONE;
    private final static BigInteger BIG_INTEGER_FALSE = BigInteger.ZERO;

    private final static Double DOUBLE_TRUE = 1.0;
    private final static Double DOUBLE_FALSE = 0.0;

    private final static LocalTime LOCAL_TIME_TRUE = LocalTime.ofSecondOfDay(1);
    private final static LocalTime LOCAL_TIME_FALSE = LocalTime.ofSecondOfDay(0);

    private final static Long LONG_TRUE = 1L;
    private final static Long LONG_FALSE = 0L;

    private final static String STRING_TRUE = Boolean.TRUE.toString();
    private final static String STRING_FALSE = Boolean.FALSE.toString();

    /**
     * Creates a new {@link SpreadsheetConverterMapping}
     */
    static SpreadsheetConverterMapping<SpreadsheetConverterMapping<Converter>> with(final long dateOffset,
                                                                                    final String bigDecimalFormat,
                                                                                    final String bigIntegerFormat,
                                                                                    final String doubleFormat,
                                                                                    final DateTimeFormatter date,
                                                                                    final DateTimeFormatter dateTime,
                                                                                    final DateTimeFormatter time,
                                                                                    final String longFormat) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(BigDecimal.class.getSimpleName())
                .label("dateOffset").value(dateOffset)
                .label("bigDecimalFormat").value(bigDecimalFormat)
                .label("bigIntegerFormat").value(bigIntegerFormat)
                .label("doubleFormat").value(doubleFormat)
                .label("date").value(date)
                .label("dateTime").value(dateTime)
                .label("time").value(time)
                .label("longFormat").value(longFormat);

        return new SpreadsheetConverterMapping<>(bigDecimal(dateOffset, bigDecimalFormat),
                bigInteger(dateOffset, bigIntegerFormat),
                booleanMapping(dateOffset),
                doubleMapping(dateOffset, doubleFormat),
                localDate(dateOffset, date),
                localDateTime(dateOffset, dateTime),
                localTime(time),
                longMapping(dateOffset, longFormat),
                string(date, dateTime, time),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link BigDecimal}.
     */
    private static SpreadsheetConverterMapping<Converter> bigDecimal(final long dateOffset,
                                                                     final String format) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(BigDecimal.class.getSimpleName())
                .label("dateOffset").value(dateOffset)
                .label("format").value(format);

        return new SpreadsheetConverterMapping<>(Converters.simple(), // bigDecimal -> bigDecimal
                Converters.numberBigInteger(),
                Converters.bigDecimalBoolean(),
                Converters.numberDouble(),
                Converters.numberLocalDate(dateOffset),
                Converters.numberLocalDateTime(dateOffset),
                Converters.numberLocalTime(),
                Converters.numberLong(),
                Converters.decimalFormatString(format),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link BigInteger}.
     */
    private static SpreadsheetConverterMapping<Converter> bigInteger(final long dateOffset,
                                                                     final String format) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(BigInteger.class.getSimpleName())
                .label("dateOffset").value(dateOffset)
                .label("format").value(format);

        return new SpreadsheetConverterMapping<>(Converters.numberBigDecimal(),
                Converters.simple(), // bigDecimal -> bigInteger
                toBoolean(BigInteger.class, BIG_INTEGER_FALSE),
                Converters.numberDouble(),
                Converters.numberLocalDate(dateOffset),
                Converters.numberLocalDateTime(dateOffset),
                Converters.numberLocalTime(),
                Converters.numberLong(),
                Converters.decimalFormatString(format),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link Boolean}.
     */
    private static SpreadsheetConverterMapping<Converter> booleanMapping(final long dateOffset) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(Boolean.class.getSimpleName())
                .label("dateOffset").value(dateOffset);

        return new SpreadsheetConverterMapping<>(fromBoolean(BigDecimal.class, BIG_DECIMAL_TRUE, BIG_DECIMAL_FALSE),
                fromBoolean(BigInteger.class, BIG_INTEGER_TRUE, BIG_INTEGER_FALSE),
                Converters.simple(), // boolean -> boolean
                fromBoolean(Double.class, DOUBLE_TRUE, DOUBLE_FALSE),
                fromBoolean(LocalDate.class, localDateTrue(dateOffset), localDateFalse(dateOffset)),
                fromBoolean(LocalDateTime.class, localDateTimeTrue(dateOffset), localDateTimeFalse(dateOffset)),
                fromBoolean(LocalTime.class, LOCAL_TIME_TRUE, LOCAL_TIME_FALSE),
                fromBoolean(Long.class, LONG_TRUE, LONG_FALSE),
                fromBoolean(String.class, STRING_TRUE, STRING_FALSE),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link Double}.
     */
    private static SpreadsheetConverterMapping<Converter> doubleMapping(final long dateOffset,
                                                                        final String format) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(Double.class.getSimpleName())
                .label("dateOffset").value(dateOffset)
                .label("format").value(format);

        return new SpreadsheetConverterMapping<>(Converters.numberBigDecimal(),
                Converters.numberBigInteger(),
                toBoolean(Double.class, DOUBLE_FALSE),
                Converters.simple(), // double -> double
                Converters.numberLocalDate(dateOffset),
                Converters.numberLocalDateTime(dateOffset),
                Converters.numberLocalTime(),
                Converters.numberLong(),
                Converters.decimalFormatString(format),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link LocalDate}.
     */
    private static SpreadsheetConverterMapping<Converter> localDate(final long dateOffset,
                                                                    final DateTimeFormatter formatter) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(LocalDate.class.getSimpleName())
                .label("dateOffset").value(dateOffset)
                .value("formatter").value(formatter);

        return new SpreadsheetConverterMapping<>(Converters.localDateBigDecimal(dateOffset),
                Converters.localDateBigInteger(dateOffset),
                toBoolean(LocalDate.class, localDateFalse(dateOffset)),
                Converters.localDateDouble(dateOffset),
                Converters.simple(), // date -> date
                Converters.localDateLocalDateTime(),
                Converters.fail(LocalDate.class, LocalTime.class),
                Converters.localDateLong(dateOffset),
                Converters.localDateString(formatter),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link LocalDateTime}.
     */
    private static SpreadsheetConverterMapping<Converter> localDateTime(final long dateOffset,
                                                                        final DateTimeFormatter formatter) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(LocalDateTime.class.getSimpleName())
                .label("dateOffset").value(dateOffset)
                .value("formatter").value(formatter);

        return new SpreadsheetConverterMapping<>(Converters.localDateTimeBigDecimal(dateOffset),
                Converters.localDateTimeBigInteger(dateOffset),
                toBoolean(LocalDateTime.class, LocalDateTime.of(localDateFalse(dateOffset), LOCAL_TIME_FALSE)),
                Converters.localDateTimeDouble(dateOffset),
                Converters.localDateTimeLocalDate(),
                Converters.simple(), // datetime -> datetime
                Converters.localDateTimeLocalTime(),
                Converters.localDateTimeLong(dateOffset),
                Converters.localDateTimeString(formatter),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link LocalTime}.
     */
    private static SpreadsheetConverterMapping<Converter> localTime(final DateTimeFormatter formatter) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(LocalTime.class.getSimpleName())
                .value("formatter").value(formatter);

        return new SpreadsheetConverterMapping<>(Converters.localTimeBigDecimal(),
                Converters.localTimeBigInteger(),
                toBoolean(LocalTime.class, LOCAL_TIME_FALSE),
                Converters.localTimeDouble(),
                Converters.fail(LocalTime.class, LocalDate.class), // time -> date fails
                Converters.localTimeLocalDateTime(),
                Converters.simple(), // time -> time
                Converters.localTimeLong(),
                Converters.localTimeString(formatter),
                toString.build());
    }

    /**
     * Holds all the {@link Converter converters} from a {@link Long}.
     */
    private static SpreadsheetConverterMapping<Converter> longMapping(final long dateOffset,
                                                                      final String format) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(Long.class.getSimpleName())
                .label("dateOffset").value(dateOffset)
                .value("format").value(format);

        return new SpreadsheetConverterMapping<>(Converters.numberBigDecimal(),
                Converters.numberBigInteger(),
                toBoolean(Long.class, LONG_FALSE),
                Converters.numberDouble(),
                Converters.numberLocalDate(dateOffset),
                Converters.numberLocalDateTime(dateOffset),
                Converters.numberLocalTime(),
                Converters.simple(), // long -> long
                Converters.decimalFormatString(format),
                toString.build());
    }

    private final static int RADIX = 10;

    /**
     * Holds all the {@link Converter converters} from a {@link Long}.
     */
    private static SpreadsheetConverterMapping<Converter> string(final DateTimeFormatter date,
                                                                 final DateTimeFormatter dateTime,
                                                                 final DateTimeFormatter time) {
        final ToStringBuilder toString = ToStringBuilder.empty()
                .value(Boolean.class.getSimpleName())
                .value("date").value(date)
                .value("dateTime").value(dateTime)
                .value("time").value(time);

        return new SpreadsheetConverterMapping<>(parser(BigDecimal.class, Parsers.bigDecimal()),
                parser(BigInteger.class, Parsers.bigInteger(RADIX)),
                toBoolean(String.class, STRING_FALSE),
                parser(Double.class, Parsers.doubleParser()),
                Converters.stringLocalDate(date),
                Converters.stringLocalDateTime(dateTime),
                Converters.stringLocalTime(time),
                parser(Long.class, Parsers.longParser(RADIX)),
                Converters.simple(),
                toString.build()); // string -> string
    }

    private static <T> Converter parser(final Class<T> type,
                                        final Parser parser) {
        return Converters.parser(type, parser, SpreadsheetConverterMapping::parserContext);
    }

    private static ParserContext parserContext(final ConverterContext context) {
        return ParserContexts.basic(context);
    }

    private static <T> Converter fromBoolean(final Class<T> type,
                                             final T trueValue,
                                             final T falseValue) {
        return Converters.booleanConverter(Boolean.class,
                Boolean.FALSE,
                type,
                trueValue,
                falseValue);
    }

    private static <T> Converter toBoolean(final Class<T> from,
                                           final T falseValue) {
        return Converters.booleanConverter(from,
                falseValue,
                Boolean.class,
                Boolean.TRUE,
                Boolean.FALSE);
    }

    private static LocalDate localDateTrue(final long dateOffset) {
        return localDateFalse(dateOffset + 1);
    }

    private static LocalDate localDateFalse(final long dateOffset) {
        return LocalDate.ofEpochDay(dateOffset);
    }

    private static LocalDateTime localDateTimeTrue(final long dateOffset) {
        return localDateTimeFalse(1 + dateOffset);
    }

    private static LocalDateTime localDateTimeFalse(final long dateOffset) {
        return LocalDateTime.of(localDateFalse(dateOffset), LOCAL_TIME_FALSE);
    }

    private SpreadsheetConverterMapping(final T bigDecimal,
                                        final T bigInteger,
                                        final T booleanValue,
                                        final T doubleValue,
                                        final T localDate,
                                        final T localDateTime,
                                        final T localTime,
                                        final T longValue,
                                        final T string,
                                        final String toString) {
        super();

        this.bigDecimal = bigDecimal;
        this.bigInteger = bigInteger;
        this.booleanValue = booleanValue;
        this.doubleValue = doubleValue;
        this.localDate = localDate;
        this.localDateTime = localDateTime;
        this.localTime = localTime;
        this.longValue = longValue;
        this.string = string;

        this.toString = toString;
    }

    final T bigDecimal;
    final T bigInteger;
    final T booleanValue;
    final T doubleValue;
    final T localDate;
    final T localDateTime;
    final T localTime;
    final T longValue;
    final T string;

    @Override
    public String toString() {
        return this.toString;
    }

    private final String toString;
}
