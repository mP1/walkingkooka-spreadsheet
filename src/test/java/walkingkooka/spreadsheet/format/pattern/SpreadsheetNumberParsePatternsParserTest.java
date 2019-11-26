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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.text.cursor.parser.ParserTokens;

import java.math.BigDecimal;

public final class SpreadsheetNumberParsePatternsParserTest extends SpreadsheetNumberParsePatternsTestCase2<SpreadsheetNumberParsePatternsParser>
        implements ParserTesting2<SpreadsheetNumberParsePatternsParser, ParserContext> {

    @Test
    public void testHashInvalidFails() {
        this.parseAndFail2("#", "A");
    }

    // integer values single digit pattern..............................................................................

    @Test
    public void testHashBigDecimalZero() {
        this.parseAndCheck2("#",
                "0",
                BigDecimal.valueOf(0));
    }

    @Test
    public void testHashBigDecimalPlusInteger() {
        this.parseAndCheck2("#",
                "Q1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashBigDecimalPlusInteger2() {
        this.parseAndCheck2("#",
                "Q23",
                BigDecimal.valueOf(23));
    }

    @Test
    public void testHashBigDecimalPlusInteger3() {
        this.parseAndCheck2("#",
                "Q456",
                BigDecimal.valueOf(456));
    }

    @Test
    public void testHashBigDecimalMinusInteger() {
        this.parseAndCheck2("#",
                "N1",
                BigDecimal.valueOf(-1));
    }

    @Test
    public void testHashBigDecimalMinusInteger2() {
        this.parseAndCheck2("#",
                "N23",
                BigDecimal.valueOf(-23));
    }

    @Test
    public void testHashBigDecimalMinusInteger3() {
        this.parseAndCheck2("#",
                "N456",
                BigDecimal.valueOf(-456));
    }

    @Test
    public void testHashBigDecimalLeadingZeroInteger() {
        this.parseAndCheck2("#",
                "N0789",
                BigDecimal.valueOf(-789));
    }

    @Test
    public void testHashBigDecimalLeadingZeroInteger2() {
        this.parseAndCheck2("#",
                "N00789",
                BigDecimal.valueOf(-789));
    }

    @Test
    public void testQuestionMarkBigDecimalLeadingZeroInteger2() {
        this.parseAndCheck2("?",
                "N00789",
                BigDecimal.valueOf(-789));
    }

    @Test
    public void testZeroBigDecimalLeadingZeroInteger2() {
        this.parseAndCheck2("0",
                "N00789",
                BigDecimal.valueOf(-789));
    }

    @Test
    public void testHashHashBigDecimal() {
        this.parseAndCheck2("##",
                "12",
                BigDecimal.valueOf(12));
    }

    @Test
    public void testQuestionQuestionQuestionBigDecimal() {
        this.parseAndCheck2("???",
                "123",
                BigDecimal.valueOf(123));
    }

    @Test
    public void testZeroZeroZeroZeroBigDecimal() {
        this.parseAndCheck2("0000",
                "1234",
                BigDecimal.valueOf(1234));
    }

    @Test
    public void testHashHashBigDecimalExtraPattern() {
        this.parseAndCheck2("##",
                "9",
                BigDecimal.valueOf(9));
    }

    @Test
    public void testQuestionQuestionQuestionBigDecimalExtraPattern() {
        this.parseAndCheck2("???",
                "78",
                BigDecimal.valueOf(78));
    }

    @Test
    public void testZeroZeroZeroZeroBigDecimalExtraPattern() {
        this.parseAndCheck2("0000",
                "6",
                BigDecimal.valueOf(6));
    }

    // fraction values .................................................................................................

    @Test
    public void testHashBigDecimalFraction() {
        final String text = "1";
        final String after = "D5";

        this.parseAndCheck(this.createParser("#"),
                text + after,
                ParserTokens.bigDecimal(BigDecimal.ONE, text),
                text,
                after);
    }

    @Test
    public void testHashDecimalHashBigDecimalFraction() {
        this.parseAndCheck2("#.#",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testQuestionDecimalQuestionBigDecimalFraction() {
        this.parseAndCheck2("?.?",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFraction() {
        this.parseAndCheck2("0.0",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFractionExtraPattern() {
        this.parseAndCheck2("0.00",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroBigDecimalFractionExtraPattern2() {
        this.parseAndCheck2("0.000",
                "0D5",
                BigDecimal.valueOf(0.5));
    }

    @Test
    public void testZeroDecimalZeroZeroBigDecimalFraction() {
        this.parseAndCheck2("0.00",
                "0D56",
                BigDecimal.valueOf(0.56));
    }

    @Test
    public void testZeroDecimalZeroZeroZeroBigDecimalFraction() {
        this.parseAndCheck2("0.000",
                "0D56",
                BigDecimal.valueOf(0.56));
    }

    // mixed patterns...................................................................................................

    @Test
    public void testHashQuestionZeroBigDecimalDigit() {
        this.parseAndCheck2("#?0",
                "1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashQuestionZeroBigDecimalSpaceDigit() {
        this.parseAndCheck2("#?0",
                " 1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashQuestionZeroBigDecimalDigitSpaceDigit() {
        this.parseAndCheck2("#?0",
                "0 1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashQuestionZeroBigDecimalDigitSpaceDigit2() {
        this.parseAndCheck2("#?0",
                "3 4",
                BigDecimal.valueOf(34));
    }

    // exponent.........................................................................................................

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentDigit() {
        this.parseAndCheck2("#E+#",
                "2XQ3",
                BigDecimal.valueOf(2000));
    }

    @Test
    public void testHashExponentMinusHashBigDecimalDigitExponentDigit() {
        this.parseAndCheck2("#E+#",
                "2XQ3",
                BigDecimal.valueOf(2000));
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentDigit2() {
        this.parseAndCheck2("#E+#",
                "2xQ3",
                BigDecimal.valueOf(2000));
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentPlusDigit() {
        this.parseAndCheck2("#E+#",
                "4XQ5",
                new BigDecimal("4E+5"));
    }

    @Test
    public void testHashExponentPlusHashBigDecimalDigitExponentMinusDigit() {
        this.parseAndCheck2("#E+#",
                "6XN7",
                new BigDecimal("6E-7"));
    }

    @Test
    public void testHashExponentPlusHashHashBigDecimalDigitExponentDigit() {
        this.parseAndCheck2("#E+##",
                "8X90",
                new BigDecimal("8E+90"));
    }

    @Test
    public void testHashExponentPlusQuestionBigDecimalDigitExponentSpaceDigit() {
        this.parseAndCheck2("#E+?",
                "1X 2",
                new BigDecimal("1E+2"));
    }

    // currency.........................................................................................................

    @Test
    public void testCurrencyHashBigDecimal() {
        this.parseAndCheck2("$#",
                "aud1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testHashCurrencyBigDecimal() {
        this.parseAndCheck2("#$",
                "1aud",
                BigDecimal.valueOf(1));
    }

    // percent..........................................................................................................

    @Test
    public void testPercentHashBigDecimalPercentDigit() {
        this.parseAndCheck2("%#",
                "P1",
                BigDecimal.valueOf(0.01));
    }

    @Test
    public void testHashPercentBigDecimalDigitPercent() {
        this.parseAndCheck2("#%",
                "1P",
                BigDecimal.valueOf(0.01));
    }

    @Test
    public void testHashPercentBigDecimalDigitDigitDigitPercent() {
        this.parseAndCheck2("#%",
                "123P",
                BigDecimal.valueOf(1.23));
    }

    @Test
    public void testHashDecimalPercentBigDecimalDigitDigitDigitPercent() {
        this.parseAndCheck2("#.#%",
                "45D6P",
                BigDecimal.valueOf(0.456));
    }

    // escape.............................................................................................................

    @Test
    public void testEscapeHashBigDecimalTextDigit() {
        this.parseAndCheck2("\\a#",
                "a1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testEscapeHashEscapeHashBigDecimalTextDigitTextDigit() {
        this.parseAndCheck2("\\a#\\b#",
                "a2b3",
                BigDecimal.valueOf(23));
    }

    // text.............................................................................................................

    @Test
    public void testTextHashBigDecimalTextDigit() {
        this.parseAndCheck2("\"abc\"#",
                "abc1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testTextHashBigDecimalTextDigitDigit() {
        this.parseAndCheck2("\"abc\"#",
                "abc23",
                BigDecimal.valueOf(23));
    }

    @Test
    public void testHashTextBigDecimalTextDigit() {
        this.parseAndCheck2("#\"abc\"",
                "4abc",
                BigDecimal.valueOf(4));
    }

    @Test
    public void testTextDigitTextBigDecimalTextDigitTextDigitTextDigit() {
        this.parseAndCheck2("\"a\"#\"b\"#\"c\"#",
                "a5b6c7",
                BigDecimal.valueOf(567));
    }

    @Test
    public void testTextDigitTextBigDecimalTextMinusDigitTextDigitTextDigit() {
        this.parseAndCheck2("\"a\"#\"b\"#\"c\"#",
                "aN8b9c0",
                BigDecimal.valueOf(-890));
    }

    // whitespace.......................................................................................................

    @Test
    public void testWhitespaceHashBigDecimalSpaceDigit() {
        this.parseAndCheck2(" #",
                " 1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testWhitespaceHashBigDecimalSpaceDigitDigitDigit() {
        this.parseAndCheck2(" #",
                " 234",
                BigDecimal.valueOf(234));
    }

    @Test
    public void testWhitespaceHashBigDecimalSpaceMinusDigit() {
        this.parseAndCheck2(" #",
                " N5",
                BigDecimal.valueOf(-5));
    }

    @Test
    public void testWhitespaceHashWhitespaceHashBigDecimal() {
        this.parseAndCheck2(" # #",
                " 6 7",
                BigDecimal.valueOf(67));
    }

    @Test
    public void testWhitespaceHashWhitespaceDecimalHashBigDecimal() {
        this.parseAndCheck2(" # .#",
                " 8 D9",
                BigDecimal.valueOf(8.9));
    }

    // several patterns.................................................................................................

    @Test
    public void testFirstPatternMatches() {
        this.parseAndCheck2("0;\"text-literal\"",
                "1",
                BigDecimal.valueOf(1));
    }

    @Test
    public void testLastPatternMatches() {
        this.parseAndCheck2("\"text-literal\";0",
                "2",
                BigDecimal.valueOf(2));
    }

    // partial..........................................................................................................

    @Test
    public void testPartial() {
        final String text = "123D45";
        final String after = "abc";

        this.parseAndCheck(this.createParser("#.#"),
                text + after,
                ParserTokens.bigDecimal(new BigDecimal("123.45"), text),
                text,
                after);
    }

    @Test
    public void testTelephonePartial() {
        final String text = "02-123-4567";
        final String after = "abc";

        this.parseAndCheck(this.createParser("00\\-000\\-0000"),
                text + after,
                ParserTokens.bigDecimal(new BigDecimal("21234567"), text),
                text,
                after);
    }

    // helpers..........................................................................................................

    private void parseAndFail2(final String pattern,
                               final String text) {
        this.parseFailAndCheck(this.createParser(pattern),
                text);
    }

    private void parseAndCheck2(final String pattern,
                                final String text,
                                final BigDecimal expected) {
        this.parseAndCheck(this.createParser(pattern),
                text,
                ParserTokens.bigDecimal(expected, text),
                text,
                "");
    }

    // ParserTesting....................................................................................................

    @Override
    public SpreadsheetNumberParsePatternsParser createParser() {
        return this.createParser("#");
    }

    private SpreadsheetNumberParsePatternsParser createParser(final String pattern) {
        return SpreadsheetNumberParsePatterns.parseNumberParsePatterns(pattern).createParser();
    }

    @Override
    public ParserContext createContext() {
        return ParserContexts.basic(DateTimeContexts.fake(), // DateTimeContext unused
                this.decimalNumberContext());
    }

    @Override
    public Class<SpreadsheetNumberParsePatternsParser> type() {
        return SpreadsheetNumberParsePatternsParser.class;
    }

    @Override
    public String typeNameSuffix() {
        return Parser.class.getSimpleName();
    }
}
