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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.convert.ShortCircuitingConverter;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.function.Function;

public final class SpreadsheetConverterDateTimeTest extends SpreadsheetConverterTestCase<SpreadsheetConverterDateTime> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    private final static LocalDate FALSE_DATE = LocalDate.of(1899, 12, 30);
    private final static LocalDate TRUE_DATE = FALSE_DATE.plusDays(1);

    private final static LocalTime FALSE_TIME = LocalTime.of(0, 0, 0);

    private final static byte BYTE = 123;

    private final static LocalDate BYTE_DATE = LocalDate.of(1899, 12, 30)
        .plusDays(BYTE);

    private final static LocalDateTime BYTE_DATE_TIME = LocalDateTime.of(
        FALSE_DATE.plusDays(BYTE),
        LocalTime.MIDNIGHT
    );

    private final static float FLOAT = BYTE + 0.5f;

    private final static LocalTime FLOAT_TIME = LocalTime.NOON;

    private final static LocalDateTime FLOAT_DATE_TIME = LocalDateTime.of(
        FALSE_DATE.plusDays(BYTE),
        LocalTime.NOON // 0.5f
    );

    private final static LocalDateTime TRUE_DATE_TIME = LocalDateTime.of(
        TRUE_DATE,
        LocalTime.MIDNIGHT
    );

    private final static LocalDateTime FALSE_DATE_TIME = LocalDateTime.of(
        FALSE_DATE,
        LocalTime.MIDNIGHT
    );

    // boolean..........................................................................................................

    @Test
    public void testConvertBooleanTrueToDate() {
        this.convertAndCheck(
            Boolean.TRUE,
            TRUE_DATE
        );
    }

    @Test
    public void testConvertBooleanFalseToDate() {
        this.convertAndCheck(
            Boolean.FALSE,
            FALSE_DATE
        );
    }

    @Test
    public void testConvertBooleanTrueToDateTime() {
        this.convertAndCheck(
            Boolean.TRUE,
            LocalDateTime.of(
                TRUE_DATE,
                LocalTime.MIDNIGHT
            )
        );
    }

    @Test
    public void testConvertBooleanFalseToDateTime() {
        this.convertAndCheck(
            Boolean.FALSE,
            LocalDateTime.of(
                FALSE_DATE,
                FALSE_TIME
            )
        );
    }

    @Test
    public void testConvertBooleanTrueToTimeFails() {
        this.convertFails(
            Boolean.TRUE,
            LocalTime.class
        );
    }

    @Test
    public void testConvertBooleanFalseToTime() {
        this.convertAndCheck(
            Boolean.FALSE,
            FALSE_TIME
        );
    }

    // Date.............................................................................................................

    @Test
    public void testConvertDateToBooleanTrueFails() {
        this.convertFails(
            TRUE_DATE,
            Boolean.class
        );
    }

    @Test
    public void testConvertDateToBooleanFails() {
        this.convertFails(
            FALSE_DATE,
            Boolean.class
        );
    }

    @Test
    public void testConvertDateToDate() {
        this.convertAndCheck(
            BYTE_DATE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertDateToDateTime() {
        this.convertAndCheck(
            BYTE_DATE,
            LocalDateTime.class,
            LocalDateTime.of(
                BYTE_DATE,
                LocalTime.MIDNIGHT
            )
        );
    }

    @Test
    public void testConvertDateToTimeFails() {
        this.convertFails(
            BYTE_DATE,
            LocalTime.class
        );
    }

    @Test
    public void testConvertDateToNumberByte() {
        this.convertAndCheck(
            BYTE_DATE,
            Byte.class,
            BYTE
        );
    }

    @Test
    public void testConvertDateToNumberShort() {
        this.convertAndCheck(
            BYTE_DATE,
            Short.class,
            (short) BYTE
        );
    }

    @Test
    public void testConvertDateToNumberInteger() {
        this.convertAndCheck(
            BYTE_DATE,
            Integer.class,
            (int) BYTE
        );
    }

    @Test
    public void testConvertDateToNumberLong() {
        this.convertAndCheck(
            BYTE_DATE,
            Long.class,
            (long) BYTE
        );
    }

    @Test
    public void testConvertDateToNumberFloat() {
        this.convertAndCheck(
            BYTE_DATE,
            Float.class,
            (float) BYTE
        );
    }

    @Test
    public void testConvertDateToNumberDouble() {
        this.convertAndCheck(
            BYTE_DATE,
            Double.class,
            (double) BYTE
        );
    }

    @Test
    public void testConvertDateToNumberBigInteger() {
        this.convertAndCheck(
            BYTE_DATE,
            BigInteger.class,
            BigInteger.valueOf(BYTE)
        );
    }

    @Test
    public void testConvertDateToNumberBigDecimal() {
        this.convertAndCheck(
            BYTE_DATE,
            BigDecimal.class,
            BigDecimal.valueOf(BYTE)
        );
    }

    @Test
    public void testConvertDateToNumberExpressionNumber() {
        this.convertAndCheck(
            BYTE_DATE,
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(BYTE)
        );
    }

    @Test
    public void testConvertDateToString() {
        final LocalDate date = LocalDate.of(
            1999,
            12,
            31
        );

        this.convertAndCheck(
            BYTE_DATE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    // DateTime.........................................................................................................

    @Test
    public void testConvertDateTimeToBooleanTrueFails() {
        this.convertFails(
            TRUE_DATE_TIME,
            Boolean.class
        );
    }

    @Test
    public void testConvertDateTimeToBooleanFails() {
        this.convertFails(
            TRUE_DATE_TIME,
            Boolean.class
        );
    }

    @Test
    public void testConvertDateTimeToDate() {
        this.convertAndCheck(
            LocalDateTime.of(
                BYTE_DATE,
                LocalTime.MIDNIGHT
            ),
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertDateTimeToDateTime() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            LocalDateTime.class,
            FLOAT_DATE_TIME
        );
    }

    @Test
    public void testConvertDateTimeToTime() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            FLOAT_TIME
        );
    }

    @Test
    public void testConvertDateTimeToNumberByte() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            Byte.class,
            BYTE
        );
    }

    @Test
    public void testConvertDateTimeToNumberShort() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            Short.class,
            (short) BYTE
        );
    }

    @Test
    public void testConvertDateTimeToNumberInteger() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            Integer.class,
            (int) BYTE
        );
    }

    @Test
    public void testConvertDateTimeToNumberLong() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            Long.class,
            (long) BYTE
        );
    }

    @Test
    public void testConvertDateTimeToNumberFloat() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            Float.class,
            FLOAT
        );
    }

    @Test
    public void testConvertDateTimeToNumberDouble() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            Double.class,
            (double) FLOAT
        );
    }

    @Test
    public void testConvertDateTimeToNumberBigIntegerFails() {
        this.convertFails(
            FLOAT_DATE_TIME,
            BigInteger.class
        );
    }

    @Test
    public void testConvertDateTimeToNumberBigInteger() {
        this.convertAndCheck(
            BYTE_DATE_TIME,
            BigInteger.class,
            BigInteger.valueOf(BYTE)
        );
    }

    @Test
    public void testConvertDateTimeToNumberBigDecimal() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            BigDecimal.class,
            BigDecimal.valueOf(FLOAT)
        );
    }

    @Test
    public void testConvertDateTimeToNumberExpressionNumber() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(FLOAT)
        );
    }

    @Test
    public void testConvertDateTimeToString() {
        final LocalDateTime dateTime = LocalDateTime.now();

        this.convertAndCheck(
            dateTime,
            "DateTime: " + dateTime
        );
    }

    // Time.............................................................................................................

    @Test
    public void testConvertTimeToBooleanTrue() {
        this.convertAndCheck(
            LocalTime.NOON,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertTimeToBooleanFalse() {
        this.convertAndCheck(
            FALSE_TIME,
            Boolean.FALSE
        );
    }

    @Test
    public void testConvertTimeToDate() {
        this.convertFails(
            FLOAT_TIME,
            LocalDate.class
        );
    }

    @Test
    public void testConvertTimeToDateTime() {
        this.convertAndCheck(
            FLOAT_TIME,
            LocalDateTime.class,
            LocalDateTime.of(
                FALSE_DATE,
                FLOAT_TIME
            )
        );
    }

    @Test
    public void testConvertTimeToTime() {
        this.convertAndCheck(
            FLOAT_TIME,
            LocalTime.class,
            FLOAT_TIME
        );
    }

    @Test
    public void testConvertTimeToNumberByte() {
        this.convertAndCheck(
            FLOAT_TIME,
            Byte.class,
            (byte)0
        );
    }

    @Test
    public void testConvertTimeToNumberShort() {
        this.convertAndCheck(
            FLOAT_TIME,
            Short.class,
            (short)0
        );
    }

    @Test
    public void testConvertTimeToNumberInteger() {
        this.convertAndCheck(
            FLOAT_TIME,
            Integer.class,
            0
        );
    }

    @Test
    public void testConvertTimeToNumberLong() {
        this.convertAndCheck(
            FLOAT_TIME,
            Long.class,
            0L
        );
    }

    @Test
    public void testConvertTimeToNumberFloat() {
        this.convertAndCheck(
            FLOAT_TIME,
            Float.class,
            0.5f
        );
    }

    @Test
    public void testConvertTimeToNumberDouble() {
        this.convertAndCheck(
            FLOAT_TIME,
            Double.class,
            0.5
        );
    }

    @Test
    public void testConvertTimeToNumberBigIntegerFails() {
        this.convertFails(
            FLOAT_TIME,
            BigInteger.class
        );
    }

    @Test
    public void testConvertTimeToNumberBigDecimal() {
        this.convertAndCheck(
            FLOAT_TIME,
            BigDecimal.class,
            BigDecimal.valueOf(0.5f)
        );
    }

    @Test
    public void testConvertTimeToNumberExpressionNumber() {
        this.convertAndCheck(
            FLOAT_TIME,
            ExpressionNumber.class,
            EXPRESSION_NUMBER_KIND.create(0.5)
        );
    }

    @Test
    public void testConvertTimeToString() {
        final LocalTime time = LocalTime.NOON;

        this.convertAndCheck(
            time,
            "Time: " + time
        );
    }

    // Number Byte......................................................................................................

    @Test
    public void testConvertNumberByteToDate() {
        this.convertAndCheck(
            BYTE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberByteToDateTime() {
        this.convertAndCheck(
            BYTE,
            LocalDateTime.class,
            LocalDateTime.of(
                BYTE_DATE,
                LocalTime.MIDNIGHT
            )
        );
    }

    @Test
    public void testConvertNumberByteToTime() {
        this.convertFails(
            BYTE,
            LocalTime.class
        );
    }

    // Number Short......................................................................................................

    @Test
    public void testConvertNumberShortToDate() {
        this.convertAndCheck(
            (short) BYTE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberShortToDateTime() {
        this.convertAndCheck(
            (short) BYTE,
            LocalDateTime.class,
            LocalDateTime.of(
                BYTE_DATE,
                LocalTime.MIDNIGHT
            )
        );
    }

    @Test
    public void testConvertNumberShortToTime() {
        this.convertFails(
            (short) BYTE,
            LocalTime.class
        );
    }

    // Number Integer...................................................................................................

    @Test
    public void testConvertNumberIntegerToDate() {
        this.convertAndCheck(
            (int) BYTE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberIntegerToDateTime() {
        this.convertAndCheck(
            (int) BYTE,
            LocalDateTime.class,
            LocalDateTime.of(
                BYTE_DATE,
                LocalTime.MIDNIGHT
            )
        );
    }

    @Test
    public void testConvertNumberIntegerToTime() {
        this.convertFails(
            (int) BYTE,
            LocalTime.class
        );
    }

    // Number Long......................................................................................................

    @Test
    public void testConvertNumberLongToDate() {
        this.convertAndCheck(
            (long) BYTE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberLongToDateTime() {
        this.convertAndCheck(
            (long) BYTE,
            LocalDateTime.class,
            LocalDateTime.of(
                BYTE_DATE,
                LocalTime.MIDNIGHT
            )
        );
    }

    @Test
    public void testConvertNumberLongToTime() {
        this.convertFails(
            (long) BYTE,
            LocalTime.class
        );
    }

    // Number Float......................................................................................................

    @Test
    public void testConvertNumberFloatToDate() {
        this.convertAndCheck(
            (float) BYTE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberFloatToDateTime() {
        this.convertAndCheck(
            FLOAT,
            LocalDateTime.class,
            FLOAT_DATE_TIME
        );
    }

    @Test
    public void testConvertNumberFloatToTime() {
        this.convertAndCheck(
            0.5f,
            LocalTime.class,
            FLOAT_TIME
        );
    }

    // Number Double....................................................................................................

    @Test
    public void testConvertNumberDoubleToDate() {
        this.convertAndCheck(
            (double) BYTE,
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberDoubleToDateTime() {
        this.convertAndCheck(
            (double) BYTE,
            LocalDateTime.class,
            BYTE_DATE_TIME
        );
    }

    @Test
    public void testConvertNumberDoubleToTime() {
        this.convertAndCheck(
            (double) 0.5f,
            LocalTime.class,
            FLOAT_TIME
        );
    }

    // Number BigInteger................................................................................................

    @Test
    public void testConvertNumberBigIntegerToDate() {
        this.convertAndCheck(
            BigInteger.valueOf(BYTE),
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberBigIntegerToDateTime() {
        this.convertAndCheck(
            BigInteger.valueOf(BYTE),
            LocalDateTime.class,
            LocalDateTime.of(
                BYTE_DATE,
                LocalTime.MIDNIGHT
            )
        );
    }

    @Test
    public void testConvertNumberBigIntegerToTime() {
        this.convertFails(
            BigInteger.valueOf(BYTE),
            LocalTime.class
        );
    }

    // Number BigDecimal ...............................................................................................

    @Test
    public void testConvertNumberBigDecimalToDate() {
        this.convertAndCheck(
            BigDecimal.valueOf(BYTE),
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberBigDecimalToDateTime() {
        this.convertAndCheck(
            BigDecimal.valueOf(FLOAT),
            LocalDateTime.class,
            FLOAT_DATE_TIME
        );
    }

    @Test
    public void testConvertNumberBigDecimalToTime() {
        this.convertAndCheck(
            FLOAT_DATE_TIME,
            FLOAT_TIME
        );
    }

    @Test
    public void testConvertNumberBigDecimalToTime2() {
        this.convertAndCheck(
            BigDecimal.valueOf(FLOAT - BYTE),
            LocalTime.class,
            FLOAT_TIME
        );
    }

    // Number ExpressionNumber .........................................................................................

    @Test
    public void testConvertNumberExpressionNumberToDate() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.create(BYTE),
            LocalDate.class,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertNumberExpressionNumberToDateTime() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.create(FLOAT),
            LocalDateTime.class,
            FLOAT_DATE_TIME
        );
    }

    @Test
    public void testConvertNumberExpressionNumberToTime() {
        this.convertAndCheck(
            EXPRESSION_NUMBER_KIND.create(0.5),
            LocalTime.class,
            LocalTime.NOON
        );
    }

    // String Date/DateTime/Time........................................................................................

    @Test
    public void testConvertStringToDate() {
        this.convertAndCheck(
            "2000-12-31",
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConvertStringToDateTime() {
        this.convertAndCheck(
            "2000-12-31T12:00:00",
            LocalDateTime.of(2000, 12, 31, 12, 0, 0)
        );
    }

    @Test
    public void testConvertStringToTime() {
        this.convertAndCheck(
            "12:00:00",
            LocalTime.NOON
        );
    }

    // Converter........................................................................................................

    @Override
    public SpreadsheetConverterDateTime createConverter() {
        return SpreadsheetConverterDateTime.with(
            temporalToStringConverter(LocalDate.class), // dateToString
            temporalToStringConverter(LocalDateTime.class), // dateTimeToString
            temporalToStringConverter(LocalTime.class), // timeToString
            stringToTemporalConverter(LocalDate.class, LocalDate::parse), // stringToDate
            stringToTemporalConverter(LocalDateTime.class, LocalDateTime::parse), // stringToDateTime
            stringToTemporalConverter(LocalTime.class, LocalTime::parse) // stringToTime
        );
    }

    private static Converter<SpreadsheetConverterContext> temporalToStringConverter(final Class<? extends Temporal> temporal) {
        return new ShortCircuitingConverter<>() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type,
                                      final SpreadsheetConverterContext context) {
                return temporal.isInstance(value) && String.class == type;
            }

            @Override
            public <T> Either<T, String> doConvert(final Object value,
                                                   final Class<T> type,
                                                   final SpreadsheetConverterContext context) {
                return this.successfulConversion(
                    value.getClass()
                        .getSimpleName()
                        .replace("Local", "") +
                        ": " +
                        value,
                    type
                );
            }

            @Override
            public String toString() {
                return temporal.getSimpleName() + " to String";
            }
        };
    }

    private static <T extends Temporal> Converter<SpreadsheetConverterContext> stringToTemporalConverter(final Class<T> temporal,
                                                                                                         final Function<String, T> stringToTemporal) {
        return new ShortCircuitingConverter<>() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type,
                                      final SpreadsheetConverterContext context) {
                return value instanceof CharSequence && temporal == type;
            }

            @Override
            public <TT> Either<TT, String> doConvert(final Object value,
                                                     final Class<TT> type,
                                                     final SpreadsheetConverterContext context) {
                return this.successfulConversion(
                    stringToTemporal.apply(value.toString()),
                    type
                );
            }

            @Override
            public String toString() {
                return "String to " + temporal.getSimpleName();
            }
        };
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public long dateOffset() {
                return Converters.EXCEL_1900_DATE_SYSTEM_OFFSET;
            }

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return EXPRESSION_NUMBER_KIND;
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
                Lists.of(
                    SpreadsheetConverters.number(),
                    SpreadsheetConverters.booleans(),
                    SpreadsheetConverterDateTimeTest.this.createConverter()
                )
            );

            @Override
            public SpreadsheetMetadata spreadsheetMetadata() {
                return SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                    DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT
                );
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterDateTime> type() {
        return SpreadsheetConverterDateTime.class;
    }
}
