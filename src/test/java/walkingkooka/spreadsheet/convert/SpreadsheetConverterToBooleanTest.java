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
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class SpreadsheetConverterToBooleanTest extends SpreadsheetConverterTestCase<SpreadsheetConverterToBoolean> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testConvertNullToBooleanFails() {
        this.convertFails(
            null,
            Number.class
        );
    }

    // boolean..........................................................................................................

    @Test
    public void testConvertBooleanTrueToVoidFails() {
        this.convertFails(
            Boolean.TRUE,
            Void.class
        );
    }

    @Test
    public void testConvertBooleanTrueToBoolean() {
        this.convertAndCheck(
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertBooleanFalseToBoolean() {
        this.convertAndCheck(
            Boolean.FALSE
        );
    }

    // date.............................................................................................................

    private final static LocalDate DATE_FALSE = LocalDate.of(1899, 12, 30);

    private final static LocalDate DATE_TRUE = DATE_FALSE.plusDays(1);

    @Test
    public void testConvertDateToVoidFails() {
        this.convertFails(
            DATE_FALSE,
            Void.class
        );
    }

    @Test
    public void testConvertDateToBooleanFalse() {
        this.convertAndCheck(
            DATE_FALSE,
            Boolean.FALSE
        );
    }

    @Test
    public void testConvertDateToBooleanTrue() {
        this.convertAndCheck(
            DATE_FALSE,
            Boolean.FALSE
        );
    }

    // dateTime.........................................................................................................

    private final static LocalDateTime DATE_TIME_FALSE = LocalDateTime.of(
        DATE_FALSE,
        LocalTime.MIDNIGHT
    );

    private final static LocalDateTime DATE_TIME_TRUE = LocalDateTime.of(
        DATE_TRUE,
        LocalTime.MIDNIGHT
    );

    @Test
    public void testConvertDateTimeToVoidFails() {
        this.convertFails(
            DATE_TIME_TRUE,
            Void.class
        );
    }

    @Test
    public void testConvertDateTimeToBooleanTrue() {
        this.convertAndCheck(
            DATE_TIME_TRUE,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertDateTimeToBooleanFalse() {
        this.convertAndCheck(
            DATE_TIME_FALSE,
            Boolean.FALSE
        );
    }

    // Time.............................................................................................................

    private final static LocalTime TIME = LocalTime.of(12, 58, 59);

    @Test
    @Disabled
    public void testConvertTimeToBooleanFails() {
        this.convertFails(
            TIME,
            Number.class
        );
    }

    // byte.............................................................................................................

    @Test
    public void testConvertByteToVoidFails() {
        this.convertFails(
            (byte) 1,
            Void.class
        );
    }

    @Test
    public void testConvertByteToBooleanTrue() {
        this.convertAndCheck(
            (byte) 1,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertByteToBooleanFalse() {
        this.convertAndCheck(
            (byte) 0,
            Boolean.FALSE
        );
    }

    // short............................................................................................................

    @Test
    public void testConvertShortToVoidFails() {
        this.convertFails(
            (short) 1,
            Void.class
        );
    }

    @Test
    public void testConvertShortToBooleanTrue() {
        this.convertAndCheck(
            (short) 1,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertShortToBooleanFalse() {
        this.convertAndCheck(
            (short) 0,
            Boolean.FALSE
        );
    }

    // integer..........................................................................................................

    @Test
    public void testConvertIntegerToVoidFails() {
        this.convertFails(
            1,
            Void.class
        );
    }

    @Test
    public void testConvertIntegerToBooleanTrue() {
        this.convertAndCheck(
            1,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertIntegerToBooleanFalse() {
        this.convertAndCheck(
            0,
            Boolean.FALSE
        );
    }

    // long.............................................................................................................

    @Test
    public void testConvertLongToVoidFails() {
        this.convertFails(
            1L,
            Void.class
        );
    }

    @Test
    public void testConvertLongToBooleanTrue() {
        this.convertAndCheck(
            1L,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertLongToBooleanFalse() {
        this.convertAndCheck(
            0L,
            Boolean.FALSE
        );
    }

    // float............................................................................................................

    @Test
    public void testConvertFloatToVoidFails() {
        this.convertFails(
            1f,
            Void.class
        );
    }

    @Test
    public void testConvertFloatToBooleanTrue() {
        this.convertAndCheck(
            1f,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertFloatToBooleanTrueHalf() {
        this.convertAndCheck(
            0.5f,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertFloatToBooleanFalse() {
        this.convertAndCheck(
            0f,
            Boolean.FALSE
        );
    }

    // double............................................................................................................

    @Test
    public void testConvertDoubleToVoidFails() {
        this.convertFails(
            1.0,
            Void.class
        );
    }

    @Test
    public void testConvertDoubleToBooleanTrue() {
        this.convertAndCheck(
            1.0,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertDoubleToBooleanTrueHalf() {
        this.convertAndCheck(
            0.5,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertDoubleToBooleanFalse() {
        this.convertAndCheck(
            0.0,
            Boolean.FALSE
        );
    }

    // BigInteger.......................................................................................................

    @Test
    public void testConvertBigIntegerToVoidFails() {
        this.convertFails(
            BigInteger.ONE,
            Void.class
        );
    }

    @Test
    public void testConvertBigIntegerToBooleanTrue() {
        this.convertAndCheck(
            BigInteger.ONE,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertBigIntegerToBooleanFalse() {
        this.convertAndCheck(
            BigInteger.ZERO,
            Boolean.FALSE
        );
    }

    // BigDecimal.......................................................................................................

    @Test
    public void testConvertBigDecimalToVoidFails() {
        this.convertFails(
            BigDecimal.ONE,
            Void.class
        );
    }

    @Test
    public void testConvertBigDecimalToBooleanTrue() {
        this.convertAndCheck(
            BigDecimal.ONE,
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertBigDecimalToBooleanTrueHalf() {
        this.convertAndCheck(
            BigDecimal.valueOf(0.5),
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertBigDecimalToBooleanFalse() {
        this.convertAndCheck(
            BigDecimal.ZERO,
            Boolean.FALSE
        );
    }

    // ExpressionNumberBigDecimal.......................................................................................

    @Test
    public void testConvertExpressionNumberBigDecimalVoidFails() {
        this.convertFails(
            ExpressionNumberKind.BIG_DECIMAL.one(),
            Void.class
        );
    }

    @Test
    public void testConvertExpressionNumberBigDecimalToBooleanTrue() {
        this.convertAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.one(),
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertExpressionNumberBigDecimalToBooleanTrueHalf() {
        this.convertAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.create(0.5),
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertExpressionNumberBigDecimalToBooleanFalse() {
        this.convertAndCheck(
            ExpressionNumberKind.BIG_DECIMAL.zero(),
            Boolean.FALSE
        );
    }

    // ExpressionNumberDouble...........................................................................................

    @Test
    public void testConvertExpressionNumberDoubleToVoidFails() {
        this.convertFails(
            ExpressionNumberKind.DOUBLE.one(),
            Void.class
        );
    }

    @Test
    public void testConvertExpressionNumberDoubleToBooleanTrue() {
        this.convertAndCheck(
            ExpressionNumberKind.DOUBLE.one(),
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertExpressionNumberDoubleToBooleanTrueHalf() {
        this.convertAndCheck(
            ExpressionNumberKind.DOUBLE.create(0.5),
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertExpressionNumberDoubleToBooleanFalse() {
        this.convertAndCheck(
            ExpressionNumberKind.DOUBLE.zero(),
            Boolean.FALSE
        );
    }
    // string...........................................................................................................

    @Test
    public void testConvertStringInvalidToBooleanFails() {
        this.convertFails(
            "ABC",
            Boolean.class
        );
    }

    @Test
    public void testConvertStringTrueToVoidFails() {
        this.convertFails(
            "TRUE",
            Void.class
        );
    }

    @Test
    public void testConvertStringTrueUpperCasedToBooleanTrue() {
        this.convertAndCheck(
            "TRUE",
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertStringFalseUpperCasedToBooleanFalse() {
        this.convertAndCheck(
            "FALSE",
            Boolean.FALSE
        );
    }

    @Test
    public void testConvertStringTrueLowerCasedToBooleanTrue() {
        this.convertAndCheck(
            "true",
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertStringFalseLowerCasedToBooleanFalse() {
        this.convertAndCheck(
            "false",
            Boolean.FALSE
        );
    }

    @Test
    public void testConvertStringTrueCapitalCaseToBooleanTrue() {
        this.convertAndCheck(
            "True",
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertStringFalseCapitalCaseToBooleanFalse() {
        this.convertAndCheck(
            "False",
            Boolean.FALSE
        );
    }

    @Test
    public void testConvertStringNumberTrueToBooleanTrue() {
        this.convertAndCheck(
            "1",
            Boolean.TRUE
        );
    }

    @Test
    public void testConvertStringNumberFalseToBooleanFalse() {
        this.convertAndCheck(
            "0",
            Boolean.FALSE
        );
    }

    @Override
    public SpreadsheetConverterToBoolean createConverter() {
        return SpreadsheetConverterToBoolean.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public boolean canNumbersHaveGroupSeparator() {
                return false;
            }

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
                    SpreadsheetConverters.number()
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
    public Class<SpreadsheetConverterToBoolean> type() {
        return SpreadsheetConverterToBoolean.class;
    }
}
