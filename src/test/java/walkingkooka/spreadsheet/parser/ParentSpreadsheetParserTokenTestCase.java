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
package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.ValueExpression;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ParentSpreadsheetParserTokenTestCase<T extends ParentSpreadsheetParserToken> extends SpreadsheetParserTokenTestCase<T> {

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    final static String APOSTROPHE = "'";
    final static String DOUBLE_QUOTE = "\"";

    final static BigInteger NUMBER1 = BigInteger.valueOf(1);
    final static BigInteger NUMBER2 = BigInteger.valueOf(22);

    final static String TEXT = "abc123";

    final static String WHITESPACE = "   ";

    ParentSpreadsheetParserTokenTestCase() {
        super();
    }

    @Test
    public final void testWithNullTokensFails() {
        assertThrows(NullPointerException.class, () -> this.createToken(this.text(), Cast.<List<ParserToken>>to(null)));
    }

    @Test
    public final void testWithEmptyTokensFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(this.text(), Lists.empty()));
    }

    @Test
    public final void testWithCopiesTokens() {
        final List<ParserToken> tokens = this.tokens();
        final String text = this.text();
        final T token = this.createToken(text, tokens);
        this.textAndCheck(token, text);
        this.checkEquals(tokens, token.value(), "tokens");
        this.checkEquals(tokens, token.value(), "tokens not copied");
    }

    abstract T createToken(final String text, final List<ParserToken> tokens);

    @Override
    final public T createToken(final String text) {
        return this.createToken(text, this.tokens());
    }

    final T createToken(final String text, final ParserToken... tokens) {
        return this.createToken(text, Lists.of(tokens));
    }

    abstract List<ParserToken> tokens();

    final void checkValue(final ParentSpreadsheetParserToken token, final ParserToken... value) {
        this.checkValue(token, Lists.of(value));
    }

    final void checkValue(final ParentSpreadsheetParserToken token, final List<ParserToken> value) {
        this.checkEquals(value, token.value(), "value");
    }

    final ApostropheSymbolSpreadsheetParserToken apostropheSymbol() {
        return SpreadsheetParserToken.apostropheSymbol(APOSTROPHE, APOSTROPHE);
    }

    final TextLiteralSpreadsheetParserToken colonTextLiteral() {
        return SpreadsheetParserToken.textLiteral(":", ":");
    }

    final static int DAY = 31;

    final DayNumberSpreadsheetParserToken dayNumber() {
        return SpreadsheetParserToken.dayNumber(DAY, "" + DAY);
    }

    final DecimalSeparatorSymbolSpreadsheetParserToken decimalSeparator() {
        return SpreadsheetParserToken.decimalSeparatorSymbol(".", ".");
    }

    final DoubleQuoteSymbolSpreadsheetParserToken doubleQuoteSymbol() {
        return SpreadsheetParserToken.doubleQuoteSymbol(DOUBLE_QUOTE, DOUBLE_QUOTE);
    }

    final EqualsSymbolSpreadsheetParserToken equalsSymbol() {
        return SpreadsheetParserToken.equalsSymbol("=", "=");
    }

    final static int HOUR = 12;

    final HourSpreadsheetParserToken hour() {
        return SpreadsheetParserToken.hour(HOUR, "" + HOUR);
    }

    final static int MILLISECOND = 123456789;

    final MillisecondSpreadsheetParserToken millisecond() {
        return SpreadsheetParserToken.millisecond(MILLISECOND, "" + MILLISECOND);
    }

    final static int MINUTE = 58;

    final MinuteSpreadsheetParserToken minute() {
        return SpreadsheetParserToken.minute(MINUTE, "" + MINUTE);
    }

    final static int MONTH = 12;

    final MonthNameSpreadsheetParserToken monthName() {
        return SpreadsheetParserToken.monthName(MONTH, "December");
    }

    final MonthNameAbbreviationSpreadsheetParserToken monthNameAbbreviation() {
        return SpreadsheetParserToken.monthNameAbbreviation(MONTH, "Dec.");
    }

    final MonthNameInitialSpreadsheetParserToken monthNameInitial() {
        return SpreadsheetParserToken.monthNameInitial(MONTH, "F");
    }

    final MonthNumberSpreadsheetParserToken monthNumber() {
        return SpreadsheetParserToken.monthNumber(MONTH, "" + MONTH);
    }

    final MinusSymbolSpreadsheetParserToken minusSymbol() {
        return SpreadsheetParserToken.minusSymbol("-", "-");
    }

    final NumberSpreadsheetParserToken number1() {
        return this.number("" + NUMBER1);
    }

    final NumberSpreadsheetParserToken number2() {
        return this.number("" + NUMBER2);
    }

    private NumberSpreadsheetParserToken number(final String value) {
        return this.number(value, value);
    }

    final NumberSpreadsheetParserToken number(final String value, final String text) {
        return SpreadsheetParserToken.number(
                Lists.of(
                        SpreadsheetParserToken.digits(value, text)
                ),
                "" + value
        );
    }

    final static int SECONDS = 59;

    final SecondsSpreadsheetParserToken seconds() {
        return SpreadsheetParserToken.seconds(SECONDS, "" + SECONDS);
    }

    final TextLiteralSpreadsheetParserToken slashTextLiteral() {
        return SpreadsheetParserToken.textLiteral("/", "/");
    }

    final TextLiteralSpreadsheetParserToken textLiteral() {
        return SpreadsheetParserToken.textLiteral(TEXT, TEXT);
    }

    final WhitespaceSpreadsheetParserToken whitespace() {
        return SpreadsheetParserToken.whitespace(WHITESPACE, WHITESPACE);
    }

    final static int YEAR = 1999;

    final YearSpreadsheetParserToken year() {
        return SpreadsheetParserToken.year(YEAR, "" + YEAR);
    }

    final ParenthesisOpenSymbolSpreadsheetParserToken openParenthesisSymbol() {
        return SpreadsheetParserToken.parenthesisOpenSymbol("(", "(");
    }

    final ParenthesisCloseSymbolSpreadsheetParserToken closeParenthesisSymbol() {
        return SpreadsheetParserToken.parenthesisCloseSymbol(")", ")");
    }

    final ValueExpression<ExpressionNumber> expression1() {
        return this.expression(NUMBER1);
    }

    final ValueExpression<ExpressionNumber> expression2() {
        return this.expression(NUMBER2);
    }

    final ValueExpression<ExpressionNumber> expression(final Number number) {
        return Expression.value(
                EXPRESSION_NUMBER_KIND.create(number)
        );
    }

    final ExpressionNumber expressionNumber(final Number value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    final ExpressionEvaluationContext expressionEvaluationContext(final int defaultYear,
                                                                  final int twoDigitYear) {
        return new FakeExpressionEvaluationContext() {

            @Override
            public int defaultYear() {
                return defaultYear;
            }

            @Override
            public int twoDigitYear() {
                return twoDigitYear;
            }
        };
    }
}
