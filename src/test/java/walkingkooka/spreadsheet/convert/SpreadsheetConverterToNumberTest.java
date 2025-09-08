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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class SpreadsheetConverterToNumberTest extends SpreadsheetConverterTestCase<SpreadsheetConverterToNumber> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testConvertNullToNumberFails() {
        this.convertFails(
            null,
            Number.class
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testConvertBooleanTrueToNumberFails() {
        this.convertFails(
            true,
            Number.class
        );
    }

    @Test
    public void testConvertBooleanFalseToNumberFails() {
        this.convertFails(
            false,
            Number.class
        );
    }

    @Test
    public void testConvertBooleanTrueToByte() {
        this.convertAndCheck(
            true,
            (byte) 1
        );
    }

    @Test
    public void testConvertBooleanFalseToByte() {
        this.convertAndCheck(
            false,
            (byte) 0
        );
    }

    @Test
    public void testConvertBooleanTrueToShort() {
        this.convertAndCheck(
            true,
            (short) 1
        );
    }

    @Test
    public void testConvertBooleanFalseToShort() {
        this.convertAndCheck(
            false,
            (short) 0
        );
    }

    @Test
    public void testConvertBooleanTrueToInteger() {
        this.convertAndCheck(
            true,
            1
        );
    }

    @Test
    public void testConvertBooleanFalseToInteger() {
        this.convertAndCheck(
            false,
            0
        );
    }

    @Test
    public void testConvertBooleanTrueToLong() {
        this.convertAndCheck(
            true,
            1L
        );
    }

    @Test
    public void testConvertBooleanFalseToLong() {
        this.convertAndCheck(
            false,
            0L
        );
    }

    @Test
    public void testConvertBooleanTrueToFloat() {
        this.convertAndCheck(
            true,
            1f
        );
    }

    @Test
    public void testConvertBooleanFalseToFloat() {
        this.convertAndCheck(
            false,
            0f
        );
    }

    @Test
    public void testConvertBooleanTrueToDouble() {
        this.convertAndCheck(
            true,
            1.0
        );
    }

    @Test
    public void testConvertBooleanFalseToDouble() {
        this.convertAndCheck(
            false,
            0.0
        );
    }

    @Test
    public void testConvertBooleanTrueToBigInteger() {
        this.convertAndCheck(
            true,
            BigInteger.ONE
        );
    }

    @Test
    public void testConvertBooleanFalseToBigInteger() {
        this.convertAndCheck(
            false,
            BigInteger.ZERO
        );
    }

    @Test
    public void testConvertBooleanTrueToBigDecimal() {
        this.convertAndCheck(
            true,
            BigDecimal.ONE
        );
    }

    @Test
    public void testConvertBooleanFalseToBigDecimal() {
        this.convertAndCheck(
            false,
            BigDecimal.ZERO
        );
    }

    @Test
    public void testConvertBooleanTrueToExpressionNumber() {
        this.convertAndCheck(
            true,
            EXPRESSION_NUMBER_KIND.one()
        );
    }

    @Test
    public void testConvertBooleanFalseToExpressionNumber() {
        this.convertAndCheck(
            false,
            EXPRESSION_NUMBER_KIND.zero()
        );
    }

    // date.............................................................................................................

    private final static LocalDate DATE = LocalDate.of(1900, 1, 31);

    private final static byte BYTE_DATE = 32;
    
    @Test
    @Disabled
    public void testConvertDateToNumberFails() {
        this.convertFails(
            DATE,
            Number.class
        );
    }

    @Test
    public void testConvertDateToByte() {
        this.convertAndCheck(
            DATE,
            BYTE_DATE
        );
    }

    @Test
    public void testConvertDateToShort() {
        this.convertAndCheck(
            DATE,
            (short) BYTE_DATE
        );
    }

    @Test
    public void testConvertDateToInteger() {
        this.convertAndCheck(
            DATE,
            (int)BYTE_DATE
        );
    }

    @Test
    public void testConvertDateToLong() {
        this.convertAndCheck(
            DATE,
            (long)BYTE_DATE
        );
    }

    @Test
    public void testConvertDateToFloat() {
        this.convertAndCheck(
            DATE,
            1f * BYTE_DATE
        );
    }

    @Test
    public void testConvertDateToDouble() {
        this.convertAndCheck(
            DATE,
            1.0 * BYTE_DATE
        );
    }

    @Test
    public void testConvertDateToBigInteger() {
        this.convertAndCheck(
            DATE,
            BigInteger.valueOf(BYTE_DATE)
        );
    }

    @Test
    public void testConvertDateToBigDecimal() {
        this.convertAndCheck(
            DATE,
            BigDecimal.valueOf(BYTE_DATE)
        );
    }

    @Test
    public void testConvertDateToExpressionNumber() {
        this.convertAndCheck(
            DATE,
            EXPRESSION_NUMBER_KIND.create(BYTE_DATE)
        );
    }

    // dateTime.........................................................................................................

    private final static LocalDateTime DATETIME = LocalDateTime.of(1900, 1, 31, 0, 0, 0);

    private final static LocalDateTime DATETIME_HALF = LocalDateTime.of(1900, 1, 31, 12, 0, 0);

    private final static byte BYTE_DATETIME = 32;

    private final static float BYTE_DATETIME_HALF = 32.5f;

    @Test
    @Disabled
    public void testConvertDateTimeToNumberFails() {
        this.convertFails(
            DATETIME,
            Number.class
        );
    }

    @Test
    public void testConvertDateTimeToByte() {
        this.convertAndCheck(
            DATETIME,
            BYTE_DATETIME
        );
    }

    @Test
    public void testConvertDateTimeToShort() {
        this.convertAndCheck(
            DATETIME,
            (short) BYTE_DATETIME
        );
    }

    @Test
    public void testConvertDateTimeToInteger() {
        this.convertAndCheck(
            DATETIME,
            (int)BYTE_DATETIME
        );
    }

    @Test
    public void testConvertDateTimeToLong() {
        this.convertAndCheck(
            DATETIME,
            (long)BYTE_DATETIME
        );
    }

    @Test
    public void testConvertDateTimeToFloat() {
        this.convertAndCheck(
            DATETIME_HALF,
            1f * BYTE_DATETIME_HALF
        );
    }

    @Test
    public void testConvertDateTimeToDouble() {
        this.convertAndCheck(
            DATETIME_HALF,
            1.0 * BYTE_DATETIME_HALF
        );
    }

    @Test
    public void testConvertDateTimeToBigInteger() {
        this.convertAndCheck(
            DATETIME,
            BigInteger.valueOf(BYTE_DATETIME)
        );
    }

    @Test
    public void testConvertDateTimeToBigDecimal() {
        this.convertAndCheck(
            DATETIME_HALF,
            BigDecimal.valueOf(BYTE_DATETIME_HALF)
        );
    }

    @Test
    public void testConvertDateTimeToExpressionNumber() {
        this.convertAndCheck(
            DATETIME_HALF,
            EXPRESSION_NUMBER_KIND.create(BYTE_DATETIME_HALF)
        );
    }

    // Time.............................................................................................................

    private final static LocalTime TIME = LocalTime.of(12, 58, 59);

    @Test
    @Disabled
    public void testConvertTimeToNumberFails() {
        this.convertFails(
            TIME,
            Number.class
        );
    }

    @Test
    public void testConvertTimeToByteFails() {
        this.convertFails(
            TIME,
            Byte.class
        );
    }

    @Test
    public void testConvertTimeToShortFails() {
        this.convertFails(
            TIME,
            Short.class
        );
    }

    @Test
    public void testConvertTimeToIntegerFails() {
        this.convertFails(
            TIME,
            Integer.class
        );
    }

    @Test
    public void testConvertTimeToLongFails() {
        this.convertFails(
            TIME,
            Long.class
        );
    }

    @Test
    public void testConvertTimeToFloatFails() {
        this.convertFails(
            TIME,
            Float.class
        );
    }

    @Test
    public void testConvertTimeToDoubleFails() {
        this.convertFails(
            TIME,
            Double.class
        );
    }

    @Test
    public void testConvertTimeToBigIntegerFails() {
        this.convertFails(
            TIME,
            BigInteger.class
        );
    }

    @Test
    public void testConvertTimeToBigDecimalFails() {
        this.convertFails(
            TIME,
            BigDecimal.class
        );
    }

    @Test
    public void testConvertTimeToExpressionNumber() {
        this.convertFails(
            TIME,
            ExpressionNumber.class
        );
    }

    // string...........................................................................................................

    private final static String STRING = "123";

    private final static String STRING_HALF = "123.5";

    private final static byte BYTE_STRING = 123;

    private final static float BYTE_STRING_HALF = 123.5f;

    @Test
    @Disabled
    public void testConvertStringToNumberFails() {
        this.convertFails(
            STRING,
            Number.class
        );
    }

    @Test
    public void testConvertStringToByte() {
        this.convertAndCheck(
            STRING,
            BYTE_STRING
        );
    }

    @Test
    public void testConvertStringToShort() {
        this.convertAndCheck(
            STRING,
            (short) BYTE_STRING
        );
    }

    @Test
    public void testConvertStringToInteger() {
        this.convertAndCheck(
            STRING,
            (int)BYTE_STRING
        );
    }

    @Test
    public void testConvertStringToLong() {
        this.convertAndCheck(
            STRING,
            (long)BYTE_STRING
        );
    }

    @Test
    public void testConvertStringToFloat() {
        this.convertAndCheck(
            STRING_HALF,
            1f * BYTE_STRING_HALF
        );
    }

    @Test
    public void testConvertStringToDouble() {
        this.convertAndCheck(
            STRING_HALF,
            1.0 * BYTE_STRING_HALF
        );
    }

    @Test
    public void testConvertStringToBigInteger() {
        this.convertAndCheck(
            STRING,
            BigInteger.valueOf(BYTE_STRING)
        );
    }

    @Test
    public void testConvertStringToBigDecimal() {
        this.convertAndCheck(
            STRING_HALF,
            BigDecimal.valueOf(BYTE_STRING_HALF)
        );
    }

    @Test
    public void testConvertStringToExpressionNumber() {
        this.convertAndCheck(
            STRING_HALF,
            EXPRESSION_NUMBER_KIND.create(BYTE_STRING_HALF)
        );
    }

    @Override
    public SpreadsheetConverterToNumber createConverter() {
        return SpreadsheetConverterToNumber.INSTANCE;
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
                SpreadsheetConverters.text(),
                SpreadsheetConverters.numberToNumber()
                )
            );

            @Override
            public char decimalSeparator() {
                return '.';
            }

            @Override
            public String exponentSymbol() {
                return "E";
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public char negativeSign() {
                return '-';
            }

            @Override
            public char positiveSign() {
                return '+';
            }

            @Override
            public char zeroDigit() {
                return '0';
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterToNumber> type() {
        return SpreadsheetConverterToNumber.class;
    }
}
