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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;

public final class SpreadsheetNumberParsePatternsConverterTest extends SpreadsheetNumberParsePatternsTestCase2<SpreadsheetNumberParsePatternsConverter>
        implements ConverterTesting2<SpreadsheetNumberParsePatternsConverter, ExpressionNumberConverterContext> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    @Test
    public void testHashInvalidFails() {
        this.convertAndFail2("#", "A");
    }

    // integer values single digit pattern..............................................................................

    @Test
    public void testHashBigDecimalZero() {
        this.convertAndCheck2(
                "#",
                "0",
                BigDecimal.valueOf(0)
        );
    }

    @Test
    public void testHashBigDecimalPlusInteger() {
        this.convertAndCheck2(
                "#",
                PLUS + "1",
                BigDecimal.ONE
        );
    }

    @Test
    public void testHashBigDecimalPlusInteger2() {
        this.convertAndCheck2(
                "#",
                PLUS + "23",
                BigDecimal.valueOf(23)
        );
    }

    @Test
    public void testHashBigDecimalPlusInteger3() {
        this.convertAndCheck2(
                "#",
                PLUS + "456",
                BigDecimal.valueOf(456)
        );
    }

    @Test
    public void testHashBigDecimalMinusInteger() {
        this.convertAndCheck2(
                "#",
                MINUS + "1",
                BigDecimal.valueOf(-1)
        );
    }

    @Test
    public void testHashBigDecimalMinusInteger2() {
        this.convertAndCheck2(
                "#",
                MINUS + "23",
                BigDecimal.valueOf(-23)
        );
    }

    @Test
    public void testHashBigDecimalMinusInteger3() {
        this.convertAndCheck2(
                "#",
                MINUS + "456",
                BigDecimal.valueOf(-456)
        );
    }

    @Test
    public void testHashBigDecimalLeadingZeroInteger() {
        this.convertAndCheck2(
                "#",
                MINUS + "0789",
                BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testHashBigDecimalLeadingZeroInteger2() {
        this.convertAndCheck2(
                "#",
                MINUS + "00789",
                BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testQuestionMarkBigDecimalLeadingZeroInteger2() {
        this.convertAndCheck2(
                "?",
                MINUS + "00789",
                BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testZeroBigDecimalLeadingZeroInteger2() {
        this.convertAndCheck2(
                "0",
                MINUS + "00789",
                BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testHashHashBigDecimal() {
        this.convertAndCheck2("##",
                "12",
                BigDecimal.valueOf(12));
    }

    @Test
    public void testQuestionQuestionQuestionBigDecimal() {
        this.convertAndCheck2("???",
                "123",
                BigDecimal.valueOf(123));
    }

    @Test
    public void testZeroZeroZeroZeroBigDecimal() {
        this.convertAndCheck2("0000",
                "1234",
                BigDecimal.valueOf(1234));
    }

    @Test
    public void testHashHashBigDecimalExtraPattern() {
        this.convertAndCheck2(
                "##",
                "9",
                BigDecimal.valueOf(9)
        );
    }

    @Test
    public void testQuestionQuestionQuestionBigDecimalExtraPattern() {
        this.convertAndCheck2(
                "???",
                "78",
                BigDecimal.valueOf(78)
        );
    }

    @Test
    public void testZeroZeroZeroZeroBigDecimalExtraPattern() {
        this.convertAndCheck2(
                "0000",
                "6",
                BigDecimal.valueOf(6)
        );
    }

    // fraction values .................................................................................................

    @Test
    public void testHashBigDecimalFractionFails() {
        this.convertAndFail2(
                "#",
                "0.5"
        );
    }

    @Test
    public void testHashDecimalHashBigDecimalFraction() {
        this.convertAndCheck2(
                "#.#",
                "0" + DECIMAL + "5",
                BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testQuestionDecimalQuestionBigDecimalFraction() {
        this.convertAndCheck2(
                "?.?",
                "0" + DECIMAL + "5",
                BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFraction() {
        this.convertAndCheck2(
                "0.0",
                "0" + DECIMAL + "5",
                BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFractionExtraPattern() {
        this.convertAndCheck2(
                "0.00",
                "0" + DECIMAL + "5",
                BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFractionExtraPattern2() {
        this.convertAndCheck2(
                "0.000",
                "0" + DECIMAL + "5",
                BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testZeroDecimalZeroZeroBigDecimalFraction() {
        this.convertAndCheck2(
                "0.00",
                "0" + DECIMAL + "56",
                BigDecimal.valueOf(0.56)
        );
    }

    @Test
    public void testZeroDecimalZeroZeroZeroBigDecimalFraction() {
        this.convertAndCheck2(
                "0.000",
                "0" + DECIMAL + "56",
                BigDecimal.valueOf(0.56)
        );
    }

    // mixed patterns...................................................................................................

    @Test
    public void testHashQuestionZeroBigDecimalDigit() {
        this.convertAndCheck2(
                "#?0",
                "1",
                BigDecimal.ONE
        );
    }

    @Test
    public void testHashQuestionZeroBigDecimalSpaceDigit() {
        this.convertAndCheck2(
                "#?0",
                " 1",
                BigDecimal.ONE
        );
    }

    @Test
    public void testHashQuestionZeroBigDecimalDigitSpaceDigit() {
        this.convertAndCheck2(
                "#?0",
                "0 1",
                BigDecimal.ONE
        );
    }

    @Test
    public void testHashQuestionZeroBigDecimalDigitSpaceDigit2() {
        this.convertAndCheck2(
                "#?0",
                "3 4",
                BigDecimal.valueOf(34)
        );
    }

    // exponent.........................................................................................................

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentDigit() {
        this.convertAndCheck2(
                "#E+#",
                "2" + EXPONENT + PLUS + "3",
                BigDecimal.valueOf(2000)
        );
    }

    @Test
    public void testHashExponentMinusHashBigDecimalDigitExponentDigit() {
        this.convertAndCheck2(
                "#E+#",
                "2" + EXPONENT + PLUS + "3",
                BigDecimal.valueOf(2000)
        );
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentDigit2() {
        this.convertAndCheck2(
                "#E+#",
                "2" + EXPONENT + PLUS + "3",
                BigDecimal.valueOf(2000)
        );
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentPlusDigit() {
        this.convertAndCheck2(
                "#E+#",
                "4" + EXPONENT + PLUS + "5",
                new BigDecimal("4E+5")
        );
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentMinusDigit() {
        this.convertAndCheck2(
                "#E+#",
                "6" + EXPONENT + MINUS + "7",
                new BigDecimal("6E-7")
        );
    }

    @Test
    public void testHashExponentPlusHashHashBigDecimalDigitExponentDigit() {
        this.convertAndCheck2(
                "#E+##",
                "8" + EXPONENT + PLUS + "90",
                new BigDecimal("8E+90")
        );
    }

    @Test
    public void testHashExponentPlusQuestionBigDecimalDigitExponentSpaceDigit() {
        this.convertAndCheck2(
                "#E+?",
                "1" + EXPONENT + " 2",
                new BigDecimal("1E+2")
        );
    }

    // currency.........................................................................................................

    @Test
    public void testCurrencyHashBigDecimal() {
        this.convertAndCheck2("$#",
                CURRENCY + "1",
                BigDecimal.ONE);
    }

    @Test
    public void testHashCurrencyBigDecimal() {
        this.convertAndCheck2(
                "#$",
                "1" + CURRENCY,
                BigDecimal.ONE
        );
    }

    // percent..........................................................................................................

    @Test
    public void testPercentHashBigDecimalPercentDigit() {
        this.convertAndCheck2(
                "%#",
                PERCENT + "1",
                BigDecimal.valueOf(0.01)
        );
    }

    @Test
    public void testHashPercentBigDecimalDigitPercent() {
        this.convertAndCheck2(
                "#%",
                "12" + PERCENT,
                BigDecimal.valueOf(0.12)
        );
    }

    @Test
    public void testHashPercentBigDecimalDigitDigitDigitPercent() {
        this.convertAndCheck2("#%",
                "123" + PERCENT,
                BigDecimal.valueOf(1.23));
    }

    @Test
    public void testHashDecimalPercentBigDecimalDigitDigitDigitPercent() {
        this.convertAndCheck2("#.#%",
                "45" + DECIMAL + "6" + PERCENT,
                BigDecimal.valueOf(0.456));
    }

    // several patterns.................................................................................................

    @Test
    public void testFirstPatternMatches() {
        this.convertAndCheck2("0;\"text-literal\"",
                "1",
                BigDecimal.ONE);
    }

    @Test
    public void testLastPatternMatches() {
        this.convertAndCheck2("\"text-literal\";0",
                "2",
                BigDecimal.valueOf(2));
    }

    // other Number types...............................................................................................

    @Test
    public void testByte() {
        this.convertAndCheck2(Byte.MAX_VALUE);
    }

    @Test
    public void testByteRangeFail() {
        this.convertAndFail2("#",
                String.valueOf(Short.MAX_VALUE),
                Byte.class);
    }

    @Test
    public void testShort() {
        this.convertAndCheck2(Short.MAX_VALUE);
    }

    @Test
    public void testShortRangeFail() {
        this.convertAndFail2("#",
                String.valueOf(Integer.MAX_VALUE),
                Short.class);
    }

    @Test
    public void testInteger() {
        this.convertAndCheck2(Integer.MAX_VALUE);
    }

    @Test
    public void testIntegerRangeFail() {
        this.convertAndFail2("#",
                String.valueOf(Long.MAX_VALUE),
                Integer.class);
    }

    @Test
    public void testLong() {
        this.convertAndCheck2(999L);
    }

    @Test
    public void testFloat() {
        this.convertAndCheck2(123.5f);
    }

    @Test
    public void testDouble() {
        this.convertAndCheck2(67.5);
    }

    @Test
    public void testBigDecimal() {
        this.convertAndCheck2(1234567.5);
    }

    @Test
    public void testBigInteger() {
        this.convertAndCheck2(1234567890);
    }

    private void convertAndCheck2(final Number number) {
        this.convertAndCheck2(
                "#.#;#",
                number.toString().replace('.', DECIMAL).replace('+', PLUS).replace('-', MINUS).replace("E", EXPONENT),
                number
        );
    }

    @Test
    public void testNumber() {
        this.convertAndCheck3(
                "##.##",
                "1" + DECIMAL + "25",
                1.25
        );
    }

    private void convertAndCheck3(final String pattern,
                                  final String text,
                                  final Number number) {
        this.convertAndCheck4(
                pattern,
                text,
                ExpressionNumberKind.BIG_DECIMAL,
                number
        );
        this.convertAndCheck4(
                pattern,
                text,
                ExpressionNumberKind.DOUBLE,
                number
        );
    }

    private void convertAndCheck4(final String pattern,
                                  final String text,
                                  final ExpressionNumberKind kind,
                                  final Number number) {
        this.convertAndCheck(
                SpreadsheetNumberParsePatterns.parseNumberParsePatterns(pattern).createConverter(),
                text,
                ExpressionNumber.class,
                this.createContext0(kind),
                kind.create(number)
        );
    }

    // helpers..........................................................................................................

    private void convertAndFail2(final String pattern,
                                 final String text) {
        this.convertAndFail2(pattern, text, BigDecimal.class);
    }

    private void convertAndFail2(final String pattern,
                                 final String text,
                                 final Class<? extends Number> type) {
        this.convertFails(this.createConverter(pattern),
                text,
                type);
    }

    private void convertAndCheck2(final String pattern,
                                  final String text,
                                  final Number expected) {
        this.convertAndCheck2(pattern,
                text,
                Cast.to(expected.getClass()),
                expected);
    }

    private <N extends Number> void convertAndCheck2(final String pattern,
                                                     final String text,
                                                     final Class<N> number,
                                                     final N expected) {
        this.convertAndCheck(this.createConverter(pattern),
                text,
                number,
                expected);
    }

    // ConverterTesting.................................................................................................

    @Override
    public SpreadsheetNumberParsePatternsConverter createConverter() {
        return this.createConverter("#");
    }

    private SpreadsheetNumberParsePatternsConverter createConverter(final String pattern) {
        return SpreadsheetNumberParsePatterns.parseNumberParsePatterns(pattern).createConverter();
    }

    @Override
    public ExpressionNumberConverterContext createContext() {
        return this.createContext0(EXPRESSION_NUMBER_KIND);
    }

    private ExpressionNumberConverterContext createContext0(final ExpressionNumberKind kind) {
        return ExpressionNumberConverterContexts.basic(
                Converters.fake(),
                ConverterContexts.basic(Converters.fake(),
                        DateTimeContexts.fake(), // DateTimeContext unused
                        this.decimalNumberContext()),
                kind);
    }

    @Override
    public Class<SpreadsheetNumberParsePatternsConverter> type() {
        return SpreadsheetNumberParsePatternsConverter.class;
    }
}
