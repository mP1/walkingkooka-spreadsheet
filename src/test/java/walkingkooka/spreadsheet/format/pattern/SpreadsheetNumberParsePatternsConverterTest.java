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
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.datetime.DateTimeContexts;

import java.math.BigDecimal;

public final class SpreadsheetNumberParsePatternsConverterTest extends SpreadsheetNumberParsePatternsTestCase2<SpreadsheetNumberParsePatternsConverter>
        implements ConverterTesting2<SpreadsheetNumberParsePatternsConverter> {

    @Test
    public void testHashInvalidFails() {
        this.convertAndFail2("#", "A");
    }

    // integer values single digit pattern..............................................................................

    @Test
    public void testHashBigDecimalZero() {
        this.convertAndCheck2("#",
                "0",
                BigDecimal.valueOf(0));
    }

    @Test
    public void testHashBigDecimalPlusInteger() {
        this.convertAndCheck2("#",
                "Q1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashBigDecimalPlusInteger2() {
        this.convertAndCheck2("#",
                "Q23",
                BigDecimal.valueOf(23));
    }

    @Test
    public void testHashBigDecimalPlusInteger3() {
        this.convertAndCheck2("#",
                "Q456",
                BigDecimal.valueOf(456));
    }

    @Test
    public void testHashBigDecimalMinusInteger() {
        this.convertAndCheck2("#",
                "N1",
                BigDecimal.valueOf(-1));
    }

    @Test
    public void testHashBigDecimalMinusInteger2() {
        this.convertAndCheck2("#",
                "N23",
                BigDecimal.valueOf(-23));
    }

    @Test
    public void testHashBigDecimalMinusInteger3() {
        this.convertAndCheck2("#",
                "N456",
                BigDecimal.valueOf(-456));
    }

    @Test
    public void testHashBigDecimalLeadingZeroInteger() {
        this.convertAndCheck2("#",
                "N0789",
                BigDecimal.valueOf(-789));
    }

    @Test
    public void testHashBigDecimalLeadingZeroInteger2() {
        this.convertAndCheck2("#",
                "N00789",
                BigDecimal.valueOf(-789));
    }

    @Test
    public void testQuestionMarkBigDecimalLeadingZeroInteger2() {
        this.convertAndCheck2("?",
                "N00789",
                BigDecimal.valueOf(-789));
    }

    @Test
    public void testZeroBigDecimalLeadingZeroInteger2() {
        this.convertAndCheck2("0",
                "N00789",
                BigDecimal.valueOf(-789));
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
        this.convertAndCheck2("##",
                "9",
                BigDecimal.valueOf(9));
    }

    @Test
    public void testQuestionQuestionQuestionBigDecimalExtraPattern() {
        this.convertAndCheck2("???",
                "78",
                BigDecimal.valueOf(78));
    }

    @Test
    public void testZeroZeroZeroZeroBigDecimalExtraPattern() {
        this.convertAndCheck2("0000",
                "6",
                BigDecimal.valueOf(6));
    }

    // fraction values .................................................................................................

    @Test
    public void testHashBigDecimalFractionFails() {
        this.convertAndFail2("#",
                "0.5");
    }

    @Test
    public void testHashDecimalHashBigDecimalFraction() {
        this.convertAndCheck2("#.#",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testQuestionDecimalQuestionBigDecimalFraction() {
        this.convertAndCheck2("?.?",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFraction() {
        this.convertAndCheck2("0.0",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFractionExtraPattern() {
        this.convertAndCheck2("0.00",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFractionExtraPattern2() {
        this.convertAndCheck2("0.000",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroZeroBigDecimalFraction() {
        this.convertAndCheck2("0.00",
                "0D56",
                BigDecimal.valueOf(0.56));
    }

    @Test
    public void testZeroDecimalZeroZeroZeroBigDecimalFraction() {
        this.convertAndCheck2("0.000",
                "0D56",
                BigDecimal.valueOf(0.56));
    }

    // mixed patterns...................................................................................................

    @Test
    public void testHashQuestionZeroBigDecimalDigit() {
        this.convertAndCheck2("#?0",
                "1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashQuestionZeroBigDecimalSpaceDigit() {
        this.convertAndCheck2("#?0",
                " 1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashQuestionZeroBigDecimalDigitSpaceDigit() {
        this.convertAndCheck2("#?0",
                "0 1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashQuestionZeroBigDecimalDigitSpaceDigit2() {
        this.convertAndCheck2("#?0",
                "3 4",
                BigDecimal.valueOf(34));
    }

    // exponent.........................................................................................................

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentDigit() {
        this.convertAndCheck2("#E+#",
                "2XQ3",
                BigDecimal.valueOf(2000));
    }

    @Test
    public void testHashExponentMinusHashBigDecimalDigitExponentDigit() {
        this.convertAndCheck2("#E+#",
                "2XQ3",
                BigDecimal.valueOf(2000));
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentDigit2() {
        this.convertAndCheck2("#E+#",
                "2xQ3",
                BigDecimal.valueOf(2000));
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentPlusDigit() {
        this.convertAndCheck2("#E+#",
                "4XQ5",
                new BigDecimal("4E+5"));
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentMinusDigit() {
        this.convertAndCheck2("#E+#",
                "6XN7",
                new BigDecimal("6E-7"));
    }

    @Test
    public void testHashExponentPlusHashHashBigDecimalDigitExponentDigit() {
        this.convertAndCheck2("#E+##",
                "8X90",
                new BigDecimal("8E+90"));
    }

    @Test
    public void testHashExponentPlusQuestionBigDecimalDigitExponentSpaceDigit() {
        this.convertAndCheck2("#E+?",
                "1X 2",
                new BigDecimal("1E+2"));
    }

    // currency.........................................................................................................

    @Test
    public void testCurrencyHashBigDecimal() {
        this.convertAndCheck2("$#",
                "aud1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashCurrencyBigDecimal() {
        this.convertAndCheck2("#$",
                "1aud",
                BigDecimal.valueOf(1));
    }

    // percent..........................................................................................................

    @Test
    public void testPercentHashBigDecimalPercentDigit() {
        this.convertAndCheck2("%#",
                "P1",
                BigDecimal.valueOf(0.01));
    }

    @Test
    public void testHashPercentBigDecimalDigitPercent() {
        this.convertAndCheck2("#%",
                "1P",
                BigDecimal.valueOf(0.01));
    }

    @Test
    public void testHashPercentBigDecimalDigitDigitDigitPercent() {
        this.convertAndCheck2("#%",
                "123P",
                BigDecimal.valueOf(1.23));
    }

    @Test
    public void testHashDecimalPercentBigDecimalDigitDigitDigitPercent() {
        this.convertAndCheck2("#.#%",
                "45D6P",
                BigDecimal.valueOf(0.456));
    }

    // escape.............................................................................................................

    @Test
    public void testEscapeHashBigDecimalTextDigit() {
        this.convertAndCheck2("\\a#",
                "a1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testEscapeHashEscapeHashBigDecimalTextDigitTextDigit() {
        this.convertAndCheck2("\\a#\\b#",
                "a2b3",
                BigDecimal.valueOf(23));
    }

    // text.............................................................................................................

    @Test
    public void testTextHashBigDecimalTextDigit() {
        this.convertAndCheck2("\"abc\"#",
                "abc1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testTextHashBigDecimalTextDigitDigit() {
        this.convertAndCheck2("\"abc\"#",
                "abc23",
                BigDecimal.valueOf(23));
    }
    
    @Test
    public void testHashTextBigDecimalTextDigit() {
        this.convertAndCheck2("#\"abc\"",
                "4abc",
                BigDecimal.valueOf(4));
    }

    @Test
    public void testTextDigitTextBigDecimalTextDigitTextDigitTextDigit() {
        this.convertAndCheck2("\"a\"#\"b\"#\"c\"#",
                "a5b6c7",
                BigDecimal.valueOf(567));
    }

    @Test
    public void testTextDigitTextBigDecimalTextMinusDigitTextDigitTextDigit() {
        this.convertAndCheck2("\"a\"#\"b\"#\"c\"#",
                "aN8b9c0",
                BigDecimal.valueOf(-890));
    }

    // whitespace.......................................................................................................

    @Test
    public void testWhitespaceHashBigDecimalSpaceDigit() {
        this.convertAndCheck2(" #",
                " 1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testWhitespaceHashBigDecimalSpaceDigitDigitDigit() {
        this.convertAndCheck2(" #",
                " 234",
                BigDecimal.valueOf(234));
    }

    @Test
    public void testWhitespaceHashBigDecimalSpaceMinusDigit() {
        this.convertAndCheck2(" #",
                " N5",
                BigDecimal.valueOf(-5));
    }

    @Test
    public void testWhitespaceHashWhitespaceHashBigDecimal() {
        this.convertAndCheck2(" # #",
                " 6 7",
                BigDecimal.valueOf(67));
    }

    @Test
    public void testWhitespaceHashWhitespaceDecimalHashBigDecimal() {
        this.convertAndCheck2(" # .#",
                " 8 D9",
                BigDecimal.valueOf(8.9));
    }

    // several patterns.................................................................................................

    @Test
    public void testFirstPatternMatches() {
        this.convertAndCheck2("0;\"text-literal\"",
                "1",
                BigDecimal.valueOf(1));
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
        this.convertAndCheck2(Long.MAX_VALUE);
    }

    @Test
    public void testFloat() {
        this.convertAndCheck2(123.45f);
    }

    @Test
    public void testDouble() {
        this.convertAndCheck2(67.89);
    }

    @Test
    public void testBigDecimal() {
        this.convertAndCheck2(1234567.890);
    }

    @Test
    public void testBigInteger() {
        this.convertAndCheck2(1234567890);
    }

    @Test
    public void testNumber() {
        this.convertAndCheck2("#",
                "1234567890",
                Number.class,
                BigDecimal.valueOf(1234567890));
    }

    private void convertAndCheck2(final Number number) {
        this.convertAndCheck2("#;#.#",
                number.toString().replace('.', 'D'),
                number);
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
                expected.getClass(),
                expected);
    }

    private void convertAndCheck2(final String pattern,
                                  final String text,
                                  final Class<?> number,
                                  final Number expected) {
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
    public ConverterContext createContext() {
        return ConverterContexts.basic(DateTimeContexts.fake(), // DateTimeContext unused
                this.decimalNumberContext());
    }

    @Override
    public Class<SpreadsheetNumberParsePatternsConverter> type() {
        return SpreadsheetNumberParsePatternsConverter.class;
    }
}
