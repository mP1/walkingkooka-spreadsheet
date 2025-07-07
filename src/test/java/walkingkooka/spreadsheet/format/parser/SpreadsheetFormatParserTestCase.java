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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public abstract class SpreadsheetFormatParserTestCase {

    SpreadsheetFormatParserTestCase() {
        super();
    }

    static SpreadsheetFormatParserToken aSlashP() {
        return SpreadsheetFormatParserToken.amPm("A/P", "A/P");
    }

    static SpreadsheetFormatParserToken amSlashPm() {
        return SpreadsheetFormatParserToken.amPm("AM/PM", "AM/PM");
    }

    static SpreadsheetFormatParserToken bracketCloseSymbol() {
        return SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]");
    }

    static SpreadsheetFormatParserToken color() {
        final List<ParserToken> tokens = Lists.of(
            bracketOpenSymbol(),
            colorLiteral(),
            whitespace3(),
            colorNumberFive(),
            bracketCloseSymbol()
        );
        return SpreadsheetFormatParserToken.color(tokens, ParserToken.text(tokens));
    }

    static SpreadsheetFormatParserToken colorName(final String name) {
        return SpreadsheetFormatParserToken.colorName(name, name);
    }

    static SpreadsheetFormatParserToken colorNumberFive() {
        return colorNumber(5);
    }

    static SpreadsheetFormatParserToken colorLiteral() {
        return SpreadsheetFormatParserToken.colorLiteralSymbol("COLOR", "COLOR");
    }

    static SpreadsheetFormatParserToken colorNumber(final int number) {
        return SpreadsheetFormatParserToken.colorNumber(number, String.valueOf(number));
    }

    static SpreadsheetFormatParserToken conditionEquals() {
        final List<ParserToken> list = Lists.of(
            bracketOpenSymbol(),
            whitespace3(),
            equalsSymbol(),
            conditionNumber(),
            bracketCloseSymbol()
        );
        return SpreadsheetFormatParserToken.equalsSpreadsheetFormatParserToken(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken conditionGreaterThanEquals() {
        final List<ParserToken> list = Lists.of(
            bracketOpenSymbol(),
            greaterThanEquals(),
            conditionNumber(),
            bracketCloseSymbol()
        );
        return SpreadsheetFormatParserToken.greaterThanEquals(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken conditionGreaterThan() {
        final List<ParserToken> list = Lists.of(
            bracketOpenSymbol(),
            greaterThan(),
            conditionNumber(),
            bracketCloseSymbol()
        );
        return SpreadsheetFormatParserToken.greaterThan(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken conditionLessThanEquals() {
        final List<ParserToken> list = Lists.of(
            bracketOpenSymbol(),
            lessThanEquals(),
            conditionNumber(),
            bracketCloseSymbol()
        );
        return SpreadsheetFormatParserToken.lessThanEquals(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken conditionLessThan() {
        final List<ParserToken> list = Lists.of(
            bracketOpenSymbol(),
            lessThan(),
            conditionNumber(),
            bracketCloseSymbol()
        );
        return SpreadsheetFormatParserToken.lessThan(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken conditionNotEquals() {
        final List<ParserToken> list = Lists.of(
            bracketOpenSymbol(),
            notEquals(),
            conditionNumber(),
            bracketCloseSymbol()
        );
        return SpreadsheetFormatParserToken.notEquals(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken conditionNumber() {
        final String text = "12.75";
        return SpreadsheetFormatParserToken.conditionNumber(
            new BigDecimal(text),
            text
        );
    }

    static SpreadsheetFormatParserToken currency() {
        return SpreadsheetFormatParserToken.currency("$", "$");
    }

    static DateSpreadsheetFormatParserToken date(final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);

        return SpreadsheetFormatParserToken.date(
            list,
            ParserToken.text(list)
        );
    }

    static DateTimeSpreadsheetFormatParserToken dateTime(final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);

        return SpreadsheetFormatParserToken.dateTime(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken day() {
        return day(1);
    }

    static SpreadsheetFormatParserToken day(final int count) {
        final String text = repeat('D', count);
        return SpreadsheetFormatParserToken.day(text, text);
    }

    static SpreadsheetFormatParserToken decimalPoint() {
        return SpreadsheetFormatParserToken.decimalPoint(".", ".");
    }

    static SpreadsheetFormatParserToken digit() {
        return SpreadsheetFormatParserToken.digit("#", "#");
    }

    static SpreadsheetFormatParserToken digitSpace() {
        return SpreadsheetFormatParserToken.digitSpace("?", "?");
    }

    static SpreadsheetFormatParserToken digitZero() {
        return SpreadsheetFormatParserToken.digitZero("0", "0");
    }

    static SpreadsheetFormatParserToken equalsSymbol() {
        return SpreadsheetFormatParserToken.equalsSymbol("=", "=");
    }

    static SpreadsheetFormatParserToken escape() {
        return SpreadsheetFormatParserToken.escape('A', "\\A");
    }

    static SpreadsheetFormatParserToken exponentPlusTokenSpaceZeroDigit(final SpreadsheetFormatParserToken token) {
        final List<ParserToken> tokens = Lists.of(
            exponentSymbolMinus(),
            token,
            digitSpace(),
            digitZero(),
            digit()
        );
        return SpreadsheetFormatParserToken.exponent(
            tokens,
            ParserToken.text(tokens)
        );
    }

    static SpreadsheetFormatParserToken exponentPlusDigitSpaceTokenZeroDigit(final SpreadsheetFormatParserToken token) {
        final List<ParserToken> tokens = Lists.of(
            exponentSymbolPlus(),
            digitSpace(),
            token,
            digitZero(),
            digit()
        );
        return SpreadsheetFormatParserToken.exponent(
            tokens,
            ParserToken.text(tokens)
        );
    }

    static SpreadsheetFormatParserToken exponentPlusSpaceZeroDigitToken(final SpreadsheetFormatParserToken token) {
        final List<ParserToken> tokens = Lists.of(
            exponentSymbolPlus(),
            digitSpace(),
            digitZero(),
            digit(),
            token
        );
        return SpreadsheetFormatParserToken.exponent(
            tokens,
            ParserToken.text(tokens)
        );
    }

    static SpreadsheetFormatParserToken exponentSymbolMinus() {
        return SpreadsheetFormatParserToken.exponentSymbol("E-", "E-");
    }

    static SpreadsheetFormatParserToken exponentSymbolPlus() {
        return SpreadsheetFormatParserToken.exponentSymbol("E+", "E+");
    }

    static SpreadsheetFormatParserToken fractionSymbol() {
        return SpreadsheetFormatParserToken.fractionSymbol("/", "/");
    }

    static SpreadsheetFormatParserToken general() {
        return general(
            generalSymbol()
        );
    }

    static SpreadsheetFormatParserToken general(final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> tokensList = Lists.of(tokens);

        return SpreadsheetFormatParserToken.general(
            tokensList,
            ParserToken.text(tokensList)
        );
    }

    static SpreadsheetFormatParserToken generalSymbol() {
        return SpreadsheetFormatParserToken.generalSymbol("GENERAL", "GENERAL");
    }

    static SpreadsheetFormatParserToken greaterThan() {
        return SpreadsheetFormatParserToken.greaterThanSymbol(">", ">");
    }

    static SpreadsheetFormatParserToken greaterThanEquals() {
        return SpreadsheetFormatParserToken.greaterThanEqualsSymbol(">=", ">=");
    }

    static SpreadsheetFormatParserToken groupSeparator() {
        return SpreadsheetFormatParserToken.groupSeparator(",", ",");
    }

    static SpreadsheetFormatParserToken hour() {
        return hour(1);
    }

    static SpreadsheetFormatParserToken hour(final int count) {
        final String text = repeat('H', count);
        return SpreadsheetFormatParserToken.hour(text, text);
    }

    static SpreadsheetFormatParserToken lessThan() {
        return SpreadsheetFormatParserToken.lessThanSymbol("<", "<");
    }

    static SpreadsheetFormatParserToken lessThanEquals() {
        return SpreadsheetFormatParserToken.lessThanEqualsSymbol("<=", "<=");
    }

    static SpreadsheetFormatParserToken minute() {
        return minute(1);
    }

    static SpreadsheetFormatParserToken minute(final int count) {
        final String text = repeat('M', count);
        return SpreadsheetFormatParserToken.minute(text, text);
    }

    static SpreadsheetFormatParserToken month() {
        return month(1);
    }

    static SpreadsheetFormatParserToken month(final int count) {
        final String text = repeat('M', count);
        return SpreadsheetFormatParserToken.month(text, text);
    }

    static SpreadsheetFormatParserToken notEquals() {
        return SpreadsheetFormatParserToken.notEqualsSymbol("<>", "<>");
    }

    static NumberSpreadsheetFormatParserToken number(final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);

        return SpreadsheetFormatParserToken.number(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken bracketOpenSymbol() {
        return SpreadsheetFormatParserToken.bracketOpenSymbol("[", "[");
    }

    static SpreadsheetFormatParserToken percentSymbol() {
        return SpreadsheetFormatParserToken.percent("%", "%");
    }

    static SpreadsheetFormatParserToken quotedText() {
        return SpreadsheetFormatParserToken.quotedText("HELLO!", "\"HELLO!\"");
    }

    static SpreadsheetFormatParserToken red() {
        return colorName("RED");
    }

    static SpreadsheetFormatParserToken second() {
        return second(1);
    }

    static SpreadsheetFormatParserToken second(final int count) {
        final String text = repeat('S', count);
        return SpreadsheetFormatParserToken.second(text, text);
    }

    static ParserToken sequence(final SpreadsheetFormatParserToken... tokens) {
        return sequence(
            Lists.of(tokens)
        );
    }

    static ParserToken sequence(final List<ParserToken> tokens) {
        return ParserTokens.sequence(
            tokens,
            ParserToken.text(tokens)
        );
    }

    static SpreadsheetFormatParserToken separator() {
        return SpreadsheetFormatParserToken.separatorSymbol(";", ";");
    }

    static SpreadsheetFormatParserToken star() {
        return SpreadsheetFormatParserToken.star('?', "*?");
    }

    static SpreadsheetFormatParserToken star2() {
        return SpreadsheetFormatParserToken.star('#', "*#");
    }

    static TextSpreadsheetFormatParserToken text(final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);

        return SpreadsheetFormatParserToken.text(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken textPlaceholder() {
        return SpreadsheetFormatParserToken.textPlaceholder("@", "@");
    }

    static SpreadsheetFormatParserToken textLiteralCloseParens() {
        return textLiteral(')');
    }

    static SpreadsheetFormatParserToken textLiteralColon() {
        return textLiteral(':');
    }

    static SpreadsheetFormatParserToken textLiteralComma() {
        return textLiteral(',');
    }

    static SpreadsheetFormatParserToken textLiteralDollar() {
        return textLiteral('$');
    }

    static SpreadsheetFormatParserToken textLiteralEquals() {
        return textLiteral('=');
    }

    static SpreadsheetFormatParserToken textLiteralGreaterThan() {
        return textLiteral('>');
    }

    static SpreadsheetFormatParserToken textLiteralGreaterThanEquals() {
        return textLiteral(">=");
    }

    static SpreadsheetFormatParserToken textLiteralGroupSeparator() {
        return textLiteral(",");
    }

    static SpreadsheetFormatParserToken textLiteralLessThan() {
        return textLiteral('<');
    }

    static SpreadsheetFormatParserToken textLiteralLessThanEquals() {
        return textLiteral("<=");
    }

    static SpreadsheetFormatParserToken textLiteralMinus() {
        return textLiteral('-');
    }

    static SpreadsheetFormatParserToken textLiteralNotEquals() {
        return textLiteral("<>");
    }

    static SpreadsheetFormatParserToken textLiteralOpenParens() {
        return textLiteral('(');
    }

    static SpreadsheetFormatParserToken textLiteralPlus() {
        return textLiteral('+');
    }

    static SpreadsheetFormatParserToken textLiteralPercent() {
        return textLiteral('%');
    }

    static SpreadsheetFormatParserToken textLiteralSlash() {
        return textLiteral('/');
    }

    static SpreadsheetFormatParserToken textLiteralSpace() {
        return textLiteral(' ');
    }

    static SpreadsheetFormatParserToken textLiteralSpaceSpaceSpace() {
        return textLiteral("   ");
    }

    static SpreadsheetFormatParserToken textLiteral(final char c) {
        return textLiteral("" + c);
    }

    static SpreadsheetFormatParserToken textLiteral(final String text) {
        return SpreadsheetFormatParserToken.textLiteral(
            text,
            text
        );
    }

    static TimeSpreadsheetFormatParserToken time(final SpreadsheetFormatParserToken... tokens) {
        final List<ParserToken> list = Lists.of(tokens);

        return SpreadsheetFormatParserToken.time(
            list,
            ParserToken.text(list)
        );
    }

    static SpreadsheetFormatParserToken underscore() {
        return SpreadsheetFormatParserToken.underscore('?', "_?");
    }

    static SpreadsheetFormatParserToken underscore2() {
        return SpreadsheetFormatParserToken.underscore('#', "_#");
    }

    static SpreadsheetFormatParserToken whitespace() {
        return SpreadsheetFormatParserToken.whitespace(
            " ",
            " "
        );
    }

    static SpreadsheetFormatParserToken whitespace3() {
        return SpreadsheetFormatParserToken.whitespace(
            "   ",
            "   "
        );
    }

    static SpreadsheetFormatParserToken year() {
        return year(1);
    }

    static SpreadsheetFormatParserToken year(final int count) {
        final String text = repeat('Y', count);
        return SpreadsheetFormatParserToken.year(text, text);
    }

    private static String repeat(final char c, final int count) {
        final char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }
}
