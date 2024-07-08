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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetCurrencySymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDecimalSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDigitsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetExponentSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMinusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTesting2;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPercentSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPlusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetWhitespaceParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetNumberParsePatternParserTest extends SpreadsheetNumberParsePatternTestCase2<SpreadsheetNumberParsePatternParser>
        implements SpreadsheetParserTesting2<SpreadsheetNumberParsePatternParser>,
        HashCodeEqualsDefinedTesting2<SpreadsheetNumberParsePatternParser> {

    private final static char VALUE_SEPARATOR = ';';

    @Test
    public void testHashInvalidFails() {
        this.parseAndFail2("#", "A");
    }

    // expression parser fail...........................................................................................

    @Test
    public void testExpressionGroupSeparatorFails() {
        this.parseExpressionFails("#,###.##");
    }

    @Test
    public void testExpressionTextLiteralFails() {
        this.parseExpressionFails("#\"text\"#");
    }

    private void parseExpressionFails(final String pattern) {
        assertThrows(
                IllegalStateException.class,
                () -> SpreadsheetNumberParsePattern.parseNumberParsePattern(pattern).expressionParser()
        );
    }

    // integer values single digit pattern..............................................................................

    @Test
    public void testHashZero() {
        this.parseAndCheck2(
                "#",
                "0",
                digits(0)
        );
    }

    @Test
    public void testHashPlusInteger() {
        this.parseAndCheck2(
                "#",
                PLUS + "1",
                plus(),
                digits(1)
        );
    }

    @Test
    public void testHashPlusInteger2() {
        this.parseAndCheck2(
                "#",
                PLUS + "23",
                plus(),
                digits(23)
        );
    }

    @Test
    public void testHashPlusInteger3() {
        this.parseAndCheck2(
                "#",
                PLUS + "456",
                plus(),
                digits(456)
        );
    }

    @Test
    public void testHashMinusInteger() {
        this.parseAndCheck2(
                "#",
                MINUS + "1",
                minus(),
                digits(1)
        );
    }

    @Test
    public void testHashMinusInteger2() {
        this.parseAndCheck2(
                "#",
                MINUS + "23",
                minus(),
                digits(23)
        );
    }

    @Test
    public void testHashMinusInteger3() {
        this.parseAndCheck2(
                "#",
                MINUS + "456",
                minus(),
                digits(456)
        );
    }

    @Test
    public void testHashDecimalLeadingZeroInteger() {
        this.parseAndCheck2(
                "#",
                "0789",
                digits("0789")
        );
    }

    @Test
    public void testHashDecimalLeadingZeroInteger2() {
        this.parseAndCheck2(
                "#",
                "00789",
                digits("00789")
        );
    }

    @Test
    public void testQuestionMarkDecimalLeadingZeroInteger2() {
        this.parseAndCheck2(
                "?",
                "00789",
                digits("00789")
        );
    }

    @Test
    public void testZeroDecimalLeadingZeroInteger2() {
        this.parseAndCheck2(
                "0",
                "00789",
                digits("00789")
        );
    }

    @Test
    public void testHashHashDecimal() {
        this.parseAndCheck2(
                "##",
                "12",
                digits(12)
        );
    }

    @Test
    public void testQuestionQuestionQuestionDecimal() {
        this.parseAndCheck2(
                "???",
                "123",
                digits(123)
        );
    }

    @Test
    public void testZeroZeroZeroZeroDecimal() {
        this.parseAndCheck2(
                "0000",
                "1234",
                digits(1234)
        );
    }

    @Test
    public void testHashHashDecimalExtraPattern() {
        this.parseAndCheck2(
                "##",
                "9",
                digits(9)
        );
    }

    @Test
    public void testQuestionQuestionQuestionDecimalExtraPattern() {
        this.parseAndCheck2(
                "???",
                "78",
                digits(78)
        );
    }

    @Test
    public void testZeroZeroZeroZeroDecimalExtraPattern() {
        this.parseAndCheck2(
                "0000",
                "6",
                digits(6)
        );
    }

    // fraction values .................................................................................................

    @Test
    public void testHashDecimalFraction() {
        final String text = "1";
        final String after = DECIMAL + "5";

        this.parseAndCheck(
                this.createParser("#"),
                text + after,
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits("1", "1")
                        ),
                        text
                ),
                text,
                after
        );
    }

    @Test
    public void testHashDecimalHashDecimalFraction() {
        this.parseAndCheck2(
                "#.#",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testQuestionDecimalQuestionDecimalFraction() {
        this.parseAndCheck2(
                "?.?",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testZeroDecimalZeroDecimalFraction() {
        this.parseAndCheck2(
                "0.0",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testZeroDecimalZeroDecimalFractionExtraPattern() {
        this.parseAndCheck2(
                "0.00",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testZeroDecimalZeroDecimalFractionExtraPattern2() {
        this.parseAndCheck2(
                "0.000",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testZeroDecimalZeroZeroDecimalFraction() {
        this.parseAndCheck2(
                "0.00",
                "0" + DECIMAL + "56",
                digits(0),
                decimal(),
                digits(56)
        );
    }

    @Test
    public void testZeroDecimalZeroZeroZeroDecimalFraction() {
        this.parseAndCheck2(
                "0.000",
                "0" + DECIMAL + "56",
                digits(0),
                decimal(),
                digits(56)
        );
    }

    // mixed patterns...................................................................................................

    @Test
    public void testHashQuestionZeroDecimalDigit() {
        this.parseAndCheck2(
                "#?0",
                "1",
                digits(1)
        );
    }

    @Test
    public void testHashQuestionZeroDecimalSpaceDigit() {
        this.parseAndCheck2(
                "#?0",
                "1 ",
                digits(1),
                whitespace()
        );
    }

    @Test
    public void testHashQuestionZeroDecimalDigitSpaceDigit() {
        this.parseAndCheck2(
                "#?0",
                "0 1",
                digits(0),
                whitespace(),
                digits(1)
        );
    }

    @Test
    public void testHashQuestionZeroDecimalDigitSpaceDigit2() {
        this.parseAndCheck2(
                "#?0",
                "3 4",
                digits(3),
                whitespace(),
                digits(4)
        );
    }

    // exponent.........................................................................................................

    @Test
    public void testHashExponentPlusHashDecimalDigitExponentDigit() {
        this.parseAndCheck2(
                "#E+#",
                "2" + EXPONENT + PLUS + "3",
                digits(2),
                exponent(),
                plus(),
                digits(3)
        );
    }

    @Test
    public void testHashExponentMinusHashDecimalDigitExponentDigit() {
        this.parseAndCheck2(
                "#E+#",
                "2" + EXPONENT + MINUS + "3",
                digits(2),
                exponent(),
                minus(),
                digits(3)
        );
    }

    @Test
    public void testHashExponentPlusHashDecimalDigitExponentDigit2() {
        this.parseAndCheck2(
                "#E+#",
                "2" + EXPONENT + PLUS + "3",
                digits(2),
                exponent(),
                plus(),
                digits(3)
        );
    }

    @Test
    public void testHashExponentPlusHashDecimalDigitExponentPlusDigit() {
        this.parseAndCheck2(
                "#E+#",
                4 + EXPONENT + PLUS + 5,
                digits(4),
                exponent(),
                plus(),
                digits(5)
        );
    }

    @Test
    public void testHashExponentPlusHashDecimalDigitExponentMinusDigit() {
        this.parseAndCheck2(
                "#E+#",
                6 + EXPONENT + MINUS + 7,
                digits(6),
                exponent(),
                minus(),
                digits(7)
        );
    }

    @Test
    public void testHashExponentPlusHashHashDecimalDigitExponentDigit() {
        this.parseAndCheck2(
                "#E+##",
                7 + EXPONENT + 890,
                digits(7),
                exponent(),
                digits("890")
        );
    }

    @Test
    public void testHashExponentPlusQuestionDecimalDigitExponentSpaceDigit() {
        this.parseAndCheck2(
                "#E+?",
                1 + EXPONENT + PLUS + " 2",
                digits(1),
                exponent(),
                plus(),
                whitespace(),
                digits(2)
        );
    }

    // currency.........................................................................................................

    @Test
    public void testCurrencyHashDecimal() {
        this.parseAndCheck2(
                "$#",
                CURRENCY + "1",
                currency(),
                digits(1)
        );
    }

    @Test
    public void testHashCurrencyDecimal() {
        this.parseAndCheck2(
                "#$",
                "1" + CURRENCY,
                digits(1),
                currency()
        );
    }

    // percent..........................................................................................................

    @Test
    public void testPercentHashDecimalPercentDigit() {
        this.parseAndCheck2(
                "%#",
                PERCENT + "1",
                percent(),
                digits(1)
        );
    }

    @Test
    public void testHashPercentDecimalDigitPercent() {
        this.parseAndCheck2(
                "#%",
                "1" + PERCENT,
                digits(1),
                percent()
        );
    }

    @Test
    public void testHashPercentDecimalDigitDigitDigitPercent() {
        this.parseAndCheck2(
                "#%",
                "123" + PERCENT,
                digits(123),
                percent()
        );
    }

    @Test
    public void testHashDecimalPercentDecimalDigitDigitDigitPercent() {
        this.parseAndCheck2(
                "#.#%",
                "45" + DECIMAL + "6" + PERCENT,
                digits(45),
                decimal(),
                digits(6),
                percent()
        );
    }

    // several patterns.................................................................................................

    @Test
    public void testFirstPatternMatches() {
        this.parseAndCheck2(
                "0;$0",
                "1",
                digits(1)
        );
    }

    @Test
    public void testLastPatternMatches() {
        this.parseAndCheck2(
                "$0;0",
                "2",
                digits(2));
    }

    // helpers..........................................................................................................

    private void parseAndFail2(final String pattern,
                               final String text) {
        this.parseFailAndCheck(
                this.createParser(pattern),
                text
        );
    }

    private void parseAndCheck2(final String pattern,
                                final String text,
                                final SpreadsheetParserToken... tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);
        this.parseAndCheck(
                this.createParser(pattern),
                text,
                SpreadsheetParserToken.number(tokensList, ParserToken.text(tokensList)),
                text,
                ""
        );
    }

    // ParserTesting....................................................................................................

    @Override
    public SpreadsheetNumberParsePatternParser createParser() {
        return this.createParser("#");
    }

    private SpreadsheetNumberParsePatternParser createParser(final String pattern) {
        return SpreadsheetNumberParsePattern.parseNumberParsePattern(pattern).createParser();
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.basic(
                DateTimeContexts.fake(),
                ExpressionNumberContexts.basic(
                        ExpressionNumberKind.BIG_DECIMAL,
                        this.decimalNumberContext()
                ),
                VALUE_SEPARATOR
        );
    }

    private SpreadsheetCurrencySymbolParserToken currency() {
        return SpreadsheetParserToken.currencySymbol(CURRENCY, CURRENCY);
    }

    private SpreadsheetDecimalSeparatorSymbolParserToken decimal() {
        return SpreadsheetParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL);
    }

    private SpreadsheetDigitsParserToken digits(final int value) {
        return digits("" + value);
    }

    private SpreadsheetDigitsParserToken digits(final String text) {
        return SpreadsheetParserToken.digits(text, text);
    }

    private SpreadsheetExponentSymbolParserToken exponent() {
        return SpreadsheetParserToken.exponentSymbol(EXPONENT, EXPONENT);
    }

    private SpreadsheetMinusSymbolParserToken minus() {
        return SpreadsheetParserToken.minusSymbol("" + MINUS, "" + MINUS);
    }

    private SpreadsheetPercentSymbolParserToken percent() {
        return SpreadsheetParserToken.percentSymbol("" + PERCENT, "" + PERCENT);
    }

    private SpreadsheetPlusSymbolParserToken plus() {
        return SpreadsheetParserToken.plusSymbol("" + PLUS, "" + PLUS);
    }

    private SpreadsheetWhitespaceParserToken whitespace() {
        return SpreadsheetParserToken.whitespace(" ", " ");
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testDifferentPattern() {
        this.checkNotEquals(
                this.createParser("0.00"),
                this.createParser("$0.00")
        );
    }

    @Override
    public SpreadsheetNumberParsePatternParser createObject() {
        return this.createParser();
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternParser> type() {
        return SpreadsheetNumberParsePatternParser.class;
    }

    @Override
    public String typeNameSuffix() {
        return Parser.class.getSimpleName();
    }
}
