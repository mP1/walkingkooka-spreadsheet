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
import walkingkooka.spreadsheet.formula.CurrencySymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.DecimalSeparatorSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.DigitsSpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.ExponentSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.MinusSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.PercentSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.PlusSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.SpreadsheetParserToken;
import walkingkooka.spreadsheet.formula.WhitespaceSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTesting2;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetNumberParsePatternSpreadsheetParserTest extends SpreadsheetNumberParsePatternTestCase2<SpreadsheetNumberParsePatternSpreadsheetParser>
        implements SpreadsheetParserTesting2<SpreadsheetNumberParsePatternSpreadsheetParser>,
        HashCodeEqualsDefinedTesting2<SpreadsheetNumberParsePatternSpreadsheetParser> {

    private final static char VALUE_SEPARATOR = ';';

    @Test
    public void testParseHashInvalidFails() {
        this.parseAndFail2("#", "A");
    }

    // expression parser fail...........................................................................................

    @Test
    public void testParseExpressionGroupSeparatorFails() {
        this.parseExpressionFails("#,###.##");
    }

    @Test
    public void testParseExpressionTextLiteralFails() {
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
    public void testParseHashZero() {
        this.parseAndCheck2(
                "#",
                "0",
                digits(0)
        );
    }

    @Test
    public void testParseHashPlusInteger() {
        this.parseAndCheck2(
                "#",
                PLUS + "1",
                plus(),
                digits(1)
        );
    }

    @Test
    public void testParseHashPlusInteger2() {
        this.parseAndCheck2(
                "#",
                PLUS + "23",
                plus(),
                digits(23)
        );
    }

    @Test
    public void testParseHashPlusInteger3() {
        this.parseAndCheck2(
                "#",
                PLUS + "456",
                plus(),
                digits(456)
        );
    }

    @Test
    public void testParseHashMinusInteger() {
        this.parseAndCheck2(
                "#",
                MINUS + "1",
                minus(),
                digits(1)
        );
    }

    @Test
    public void testParseHashMinusInteger2() {
        this.parseAndCheck2(
                "#",
                MINUS + "23",
                minus(),
                digits(23)
        );
    }

    @Test
    public void testParseHashMinusInteger3() {
        this.parseAndCheck2(
                "#",
                MINUS + "456",
                minus(),
                digits(456)
        );
    }

    @Test
    public void testParseHashDecimalLeadingZeroInteger() {
        this.parseAndCheck2(
                "#",
                "0789",
                digits("0789")
        );
    }

    @Test
    public void testParseHashDecimalLeadingZeroInteger2() {
        this.parseAndCheck2(
                "#",
                "00789",
                digits("00789")
        );
    }

    @Test
    public void testParseQuestionMarkDecimalLeadingZeroInteger2() {
        this.parseAndCheck2(
                "?",
                "00789",
                digits("00789")
        );
    }

    @Test
    public void testParseZeroDecimalLeadingZeroInteger2() {
        this.parseAndCheck2(
                "0",
                "00789",
                digits("00789")
        );
    }

    @Test
    public void testParseHashHashDecimal() {
        this.parseAndCheck2(
                "##",
                "12",
                digits(12)
        );
    }

    @Test
    public void testParseQuestionQuestionQuestionDecimal() {
        this.parseAndCheck2(
                "???",
                "123",
                digits(123)
        );
    }

    @Test
    public void testParseZeroZeroZeroZeroDecimal() {
        this.parseAndCheck2(
                "0000",
                "1234",
                digits(1234)
        );
    }

    @Test
    public void testParseHashHashDecimalExtraPattern() {
        this.parseAndCheck2(
                "##",
                "9",
                digits(9)
        );
    }

    @Test
    public void testParseQuestionQuestionQuestionDecimalExtraPattern() {
        this.parseAndCheck2(
                "???",
                "78",
                digits(78)
        );
    }

    @Test
    public void testParseZeroZeroZeroZeroDecimalExtraPattern() {
        this.parseAndCheck2(
                "0000",
                "6",
                digits(6)
        );
    }

    // fraction values .................................................................................................

    @Test
    public void testParseHashDecimalFraction() {
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
    public void testParseHashDecimalHashDecimalFraction() {
        this.parseAndCheck2(
                "#.#",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testParseQuestionDecimalQuestionDecimalFraction() {
        this.parseAndCheck2(
                "?.?",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testParseZeroDecimalZeroDecimalFraction() {
        this.parseAndCheck2(
                "0.0",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testParseZeroDecimalZeroDecimalFractionExtraPattern() {
        this.parseAndCheck2(
                "0.00",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testParseZeroDecimalZeroDecimalFractionExtraPattern2() {
        this.parseAndCheck2(
                "0.000",
                "0" + DECIMAL + "5",
                digits(0),
                decimal(),
                digits(5)
        );
    }

    @Test
    public void testParseZeroDecimalZeroZeroDecimalFraction() {
        this.parseAndCheck2(
                "0.00",
                "0" + DECIMAL + "56",
                digits(0),
                decimal(),
                digits(56)
        );
    }

    @Test
    public void testParseZeroDecimalZeroZeroZeroDecimalFraction() {
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
    public void testParseHashQuestionZeroDecimalDigit() {
        this.parseAndCheck2(
                "#?0",
                "1",
                digits(1)
        );
    }

    @Test
    public void testParseHashQuestionZeroDecimalSpaceDigit() {
        this.parseAndCheck2(
                "#?0",
                "1 ",
                digits(1),
                whitespace()
        );
    }

    @Test
    public void testParseHashQuestionZeroDecimalDigitSpaceDigit() {
        this.parseAndCheck2(
                "#?0",
                "0 1",
                digits(0),
                whitespace(),
                digits(1)
        );
    }

    @Test
    public void testParseHashQuestionZeroDecimalDigitSpaceDigit2() {
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
    public void testParseHashExponentPlusHashDecimalDigitExponentDigit() {
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
    public void testParseHashExponentMinusHashDecimalDigitExponentDigit() {
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
    public void testParseHashExponentPlusHashDecimalDigitExponentDigit2() {
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
    public void testParseHashExponentPlusHashDecimalDigitExponentPlusDigit() {
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
    public void testParseHashExponentPlusHashDecimalDigitExponentMinusDigit() {
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
    public void testParseHashExponentPlusHashHashDecimalDigitExponentDigit() {
        this.parseAndCheck2(
                "#E+##",
                7 + EXPONENT + 890,
                digits(7),
                exponent(),
                digits("890")
        );
    }

    @Test
    public void testParseHashExponentPlusQuestionDecimalDigitExponentSpaceDigit() {
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
    public void testParseCurrencyHashDecimal() {
        this.parseAndCheck2(
                "$#",
                CURRENCY + "1",
                currency(),
                digits(1)
        );
    }

    @Test
    public void testParseHashCurrencyDecimal() {
        this.parseAndCheck2(
                "#$",
                "1" + CURRENCY,
                digits(1),
                currency()
        );
    }

    // percent..........................................................................................................

    @Test
    public void testParsePercentHashDecimalPercentDigit() {
        this.parseAndCheck2(
                "%#",
                PERCENT + "1",
                percent(),
                digits(1)
        );
    }

    @Test
    public void testParseHashPercentDecimalDigitPercent() {
        this.parseAndCheck2(
                "#%",
                "1" + PERCENT,
                digits(1),
                percent()
        );
    }

    @Test
    public void testParseHashPercentDecimalDigitDigitDigitPercent() {
        this.parseAndCheck2(
                "#%",
                "123" + PERCENT,
                digits(123),
                percent()
        );
    }

    @Test
    public void testParseHashDecimalPercentDecimalDigitDigitDigitPercent() {
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
    public void testParseFirstPatternMatches() {
        this.parseAndCheck2(
                "0;$0",
                "1",
                digits(1)
        );
    }

    @Test
    public void testParseLastPatternMatches() {
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

    @Test
    public void testMinCount() {
        this.minCountAndCheck(
                1
        );
    }

    @Test
    public void testMaxCount() {
        this.maxCountAndCheck(
                1
        );
    }

    // ParserTesting....................................................................................................

    @Override
    public SpreadsheetNumberParsePatternSpreadsheetParser createParser() {
        return this.createParser("#");
    }

    private SpreadsheetNumberParsePatternSpreadsheetParser createParser(final String pattern) {
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

    private CurrencySymbolSpreadsheetParserToken currency() {
        return SpreadsheetParserToken.currencySymbol(CURRENCY, CURRENCY);
    }

    private DecimalSeparatorSymbolSpreadsheetParserToken decimal() {
        return SpreadsheetParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL);
    }

    private DigitsSpreadsheetParserToken digits(final int value) {
        return digits("" + value);
    }

    private DigitsSpreadsheetParserToken digits(final String text) {
        return SpreadsheetParserToken.digits(text, text);
    }

    private ExponentSymbolSpreadsheetParserToken exponent() {
        return SpreadsheetParserToken.exponentSymbol(EXPONENT, EXPONENT);
    }

    private MinusSymbolSpreadsheetParserToken minus() {
        return SpreadsheetParserToken.minusSymbol("" + MINUS, "" + MINUS);
    }

    private PercentSymbolSpreadsheetParserToken percent() {
        return SpreadsheetParserToken.percentSymbol("" + PERCENT, "" + PERCENT);
    }

    private PlusSymbolSpreadsheetParserToken plus() {
        return SpreadsheetParserToken.plusSymbol("" + PLUS, "" + PLUS);
    }

    private WhitespaceSpreadsheetParserToken whitespace() {
        return SpreadsheetParserToken.whitespace(" ", " ");
    }

    // tokens...........................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck(
                SpreadsheetParsePattern.parseNumberParsePattern("$0.00").parser(),
                this.createContext(),
                SpreadsheetParserSelectorToken.with(
                        "$",
                        "$",
                        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
                ),
                SpreadsheetParserSelectorToken.with(
                        "0",
                        "0",
                        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
                ),
                SpreadsheetParserSelectorToken.with(
                        ".",
                        ".",
                        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
                ),
                SpreadsheetParserSelectorToken.with(
                        "0",
                        "0",
                        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
                ),
                SpreadsheetParserSelectorToken.with(
                        "0",
                        "0",
                        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
                )
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentPattern() {
        this.checkNotEquals(
                this.createParser("0.00"),
                this.createParser("$0.00")
        );
    }

    @Override
    public SpreadsheetNumberParsePatternSpreadsheetParser createObject() {
        return this.createParser();
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternSpreadsheetParser> type() {
        return SpreadsheetNumberParsePatternSpreadsheetParser.class;
    }

    @Override
    public String typeNameSuffix() {
        return Parser.class.getSimpleName();
    }
}
