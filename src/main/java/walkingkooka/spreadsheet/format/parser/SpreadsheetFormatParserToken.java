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

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Whitespace;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Represents a token within the spreadsheet format grammar.
 */
public abstract class SpreadsheetFormatParserToken implements ParserToken {

    /**
     * An empty list of {@link ParserToken tokens/values}.
     */
    public final static List<ParserToken> EMPTY = Lists.empty();

    /**
     * {@see SpreadsheetFormatAmPmParserToken}
     */
    public static SpreadsheetFormatAmPmParserToken amPm(final String value, final String text) {
        return SpreadsheetFormatAmPmParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatBracketCloseSymbolParserToken}
     */
    public static SpreadsheetFormatBracketCloseSymbolParserToken bracketCloseSymbol(final String value, final String text) {
        return SpreadsheetFormatBracketCloseSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatBracketOpenSymbolParserToken}
     */
    public static SpreadsheetFormatBracketOpenSymbolParserToken bracketOpenSymbol(final String value, final String text) {
        return SpreadsheetFormatBracketOpenSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatColorParserToken}
     */
    public static SpreadsheetFormatColorParserToken color(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatColorParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatColorLiteralSymbolParserToken}
     */
    public static SpreadsheetFormatColorLiteralSymbolParserToken colorLiteralSymbol(final String value, final String text) {
        return SpreadsheetFormatColorLiteralSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatColorNameParserToken}
     */
    public static SpreadsheetFormatColorNameParserToken colorName(final String value, final String text) {
        return SpreadsheetFormatColorNameParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatColorNumberParserToken}
     */
    public static SpreadsheetFormatColorNumberParserToken colorNumber(final Integer value, final String text) {
        return SpreadsheetFormatColorNumberParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatConditionNumberParserToken}
     */
    public static SpreadsheetFormatConditionNumberParserToken conditionNumber(final BigDecimal value, final String text) {
        return SpreadsheetFormatConditionNumberParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatCurrencyParserToken}
     */
    public static SpreadsheetFormatCurrencyParserToken currency(final String value, final String text) {
        return SpreadsheetFormatCurrencyParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatDateParserToken}
     */
    public static SpreadsheetFormatDateParserToken date(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatDateParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatDateTimeParserToken}
     */
    public static SpreadsheetFormatDateTimeParserToken dateTime(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatDateTimeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatDayParserToken}
     */
    public static SpreadsheetFormatDayParserToken day(final String value, final String text) {
        return SpreadsheetFormatDayParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatDecimalPointParserToken}
     */
    public static SpreadsheetFormatDecimalPointParserToken decimalPoint(final String value, final String text) {
        return SpreadsheetFormatDecimalPointParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatDigitParserToken}
     */
    public static SpreadsheetFormatDigitParserToken digit(final String value, final String text) {
        return SpreadsheetFormatDigitParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatDigitSpaceParserToken}
     */
    public static SpreadsheetFormatDigitSpaceParserToken digitSpace(final String value, final String text) {
        return SpreadsheetFormatDigitSpaceParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatDigitZeroParserToken}
     */
    public static SpreadsheetFormatDigitZeroParserToken digitZero(final String value, final String text) {
        return SpreadsheetFormatDigitZeroParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatEqualsParserToken}
     */
    public static SpreadsheetFormatEqualsParserToken equalsParserToken(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatEqualsSymbolParserToken}
     */
    public static SpreadsheetFormatEqualsSymbolParserToken equalsSymbol(final String value, final String text) {
        return SpreadsheetFormatEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatEscapeParserToken}
     */
    public static SpreadsheetFormatEscapeParserToken escape(final Character value, final String text) {
        return SpreadsheetFormatEscapeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatExponentParserToken}
     */
    public static SpreadsheetFormatExponentParserToken exponent(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatExponentParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatExponentSymbolParserToken}
     */
    public static SpreadsheetFormatExponentSymbolParserToken exponentSymbol(final String value, final String text) {
        return SpreadsheetFormatExponentSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatExpressionParserToken}
     */
    public static SpreadsheetFormatExpressionParserToken expression(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatExpressionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatFractionParserToken}
     */
    public static SpreadsheetFormatFractionParserToken fraction(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatFractionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatFractionSymbolParserToken}
     */
    public static SpreadsheetFormatFractionSymbolParserToken fractionSymbol(final String value, final String text) {
        return SpreadsheetFormatFractionSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatGeneralParserToken}
     */
    public static SpreadsheetFormatGeneralParserToken general(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatGeneralParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatGeneralSymbolParserToken}
     */
    public static SpreadsheetFormatGeneralSymbolParserToken generalSymbol(final String value, final String text) {
        return SpreadsheetFormatGeneralSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatGreaterThanParserToken}
     */
    public static SpreadsheetFormatGreaterThanParserToken greaterThan(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatGreaterThanParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatGreaterThanSymbolParserToken}
     */
    public static SpreadsheetFormatGreaterThanSymbolParserToken greaterThanSymbol(final String value, final String text) {
        return SpreadsheetFormatGreaterThanSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatGreaterThanEqualsParserToken}
     */
    public static SpreadsheetFormatGreaterThanEqualsParserToken greaterThanEquals(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatGreaterThanEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatGreaterThanEqualsParserToken}
     */
    public static SpreadsheetFormatGreaterThanEqualsSymbolParserToken greaterThanEqualsSymbol(final String value, final String text) {
        return SpreadsheetFormatGreaterThanEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatHourParserToken}
     */
    public static SpreadsheetFormatHourParserToken hour(final String value, final String text) {
        return SpreadsheetFormatHourParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatLessThanParserToken}
     */
    public static SpreadsheetFormatLessThanParserToken lessThan(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatLessThanParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatLessThanSymbolParserToken}
     */
    public static SpreadsheetFormatLessThanSymbolParserToken lessThanSymbol(final String value, final String text) {
        return SpreadsheetFormatLessThanSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatLessThanEqualsParserToken}
     */
    public static SpreadsheetFormatLessThanEqualsParserToken lessThanEquals(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatLessThanEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatLessThanEqualsParserToken}
     */
    public static SpreadsheetFormatLessThanEqualsSymbolParserToken lessThanEqualsSymbol(final String value, final String text) {
        return SpreadsheetFormatLessThanEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatMonthOrMinuteParserToken}
     */
    public static SpreadsheetFormatMonthOrMinuteParserToken monthOrMinute(final String value, final String text) {
        return SpreadsheetFormatMonthOrMinuteParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatNotEqualsParserToken}
     */
    public static SpreadsheetFormatNotEqualsParserToken notEquals(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatNotEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatNotEqualsSymbolParserToken}
     */
    public static SpreadsheetFormatNotEqualsSymbolParserToken notEqualsSymbol(final String value, final String text) {
        return SpreadsheetFormatNotEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatNumberParserToken}
     */
    public static SpreadsheetFormatNumberParserToken number(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatNumberParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatPercentParserToken}
     */
    public static SpreadsheetFormatPercentParserToken percent(final String value, final String text) {
        return SpreadsheetFormatPercentParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatQuotedTextParserToken}
     */
    public static SpreadsheetFormatQuotedTextParserToken quotedText(final String value, final String text) {
        return SpreadsheetFormatQuotedTextParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatSecondParserToken}
     */
    public static SpreadsheetFormatSecondParserToken second(final String value, final String text) {
        return SpreadsheetFormatSecondParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatSeparatorSymbolParserToken}
     */
    public static SpreadsheetFormatSeparatorSymbolParserToken separatorSymbol(final String value, final String text) {
        return SpreadsheetFormatSeparatorSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatStarParserToken}
     */
    public static SpreadsheetFormatStarParserToken star(final Character value, final String text) {
        return SpreadsheetFormatStarParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatTextParserToken}
     */
    public static SpreadsheetFormatTextParserToken text(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatTextParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatTextLiteralParserToken}
     */
    public static SpreadsheetFormatTextLiteralParserToken textLiteral(final String value, final String text) {
        return SpreadsheetFormatTextLiteralParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatTextPlaceholderParserToken}
     */
    public static SpreadsheetFormatTextPlaceholderParserToken textPlaceholder(final String value, final String text) {
        return SpreadsheetFormatTextPlaceholderParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatThousandsParserToken}
     */
    public static SpreadsheetFormatThousandsParserToken thousands(final String value, final String text) {
        return SpreadsheetFormatThousandsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatTimeParserToken}
     */
    public static SpreadsheetFormatTimeParserToken time(final List<ParserToken> value, final String text) {
        return SpreadsheetFormatTimeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatUnderscoreParserToken}
     */
    public static SpreadsheetFormatUnderscoreParserToken underscore(final Character value, final String text) {
        return SpreadsheetFormatUnderscoreParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFormatWhitespaceParserToken}
     */
    public static SpreadsheetFormatWhitespaceParserToken whitespace(final String value, final String whitespace) {
        return SpreadsheetFormatWhitespaceParserToken.with(value, whitespace);
    }

    /**
     * {@see SpreadsheetFormatYearParserToken}
     */
    public static SpreadsheetFormatYearParserToken year(final String value, final String text) {
        return SpreadsheetFormatYearParserToken.with(value, text);
    }

    // factory helpers..................................................................................................

    static List<ParserToken> copyAndCheckTokens(final List<ParserToken> tokens) {
        Objects.requireNonNull(tokens, "tokens");

        return Lists.immutable(tokens);
    }

    static List<ParserToken> copyAndCheckTokensFailIfEmpty(final List<ParserToken> tokens) {
        final List<ParserToken> copy = copyAndCheckTokens(tokens);

        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Tokens is empty");
        }
        return copy;
    }

    static String checkTextNotEmpty(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");
        return text;
    }

    static String checkTextNotEmptyOrWhitespace(final String text) {
        Whitespace.failIfNullOrEmptyOrWhitespace(text, "text");
        return text;
    }

    /**
     * Package private ctor to limit sub classing.
     */
    SpreadsheetFormatParserToken(final String text) {
        super();
        this.text = text;
    }

    @Override
    public final String text() {
        return this.text;
    }

    private final String text;

    /**
     * Getter that returns the scalar or child tokens.
     */
    abstract Object value();

    // isXXX ...........................................................................................................

    /**
     * Only {@link SpreadsheetFormatAmPmParserToken} return true
     */
    public final boolean isAmPm() {
        return this instanceof SpreadsheetFormatAmPmParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatNumberParserToken} return true
     */
    public final boolean isNumber(){
        return this instanceof SpreadsheetFormatNumberParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatBracketCloseSymbolParserToken} return true
     */
    public final boolean isBracketCloseSymbol(){
        return this instanceof SpreadsheetFormatBracketCloseSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatBracketOpenSymbolParserToken} return true
     */
    public final boolean isBracketOpenSymbol(){
        return this instanceof SpreadsheetFormatBracketOpenSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatColorParserToken} return true
     */
    public final boolean isColor(){
        return this instanceof SpreadsheetFormatColorParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatColorLiteralSymbolParserToken} return true
     */
    public final boolean isColorLiteralSymbol(){
        return this instanceof SpreadsheetFormatColorLiteralSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatColorNameParserToken} return true
     */
    public final boolean isColorName(){
        return this instanceof SpreadsheetFormatColorNameParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatColorNumberParserToken} return true
     */
    public final boolean isColorNumber(){
        return this instanceof SpreadsheetFormatColorNumberParserToken;
    }

    /**
     * All sub classes of {@link SpreadsheetFormatConditionParserToken} return true
     */
    public final boolean isCondition(){
        return this instanceof SpreadsheetFormatConditionParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatConditionNumberParserToken} return true
     */
    public final boolean isConditionNumber(){
        return this instanceof SpreadsheetFormatConditionNumberParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatCurrencyParserToken} return true
     */
    public final boolean isCurrency(){
        return this instanceof SpreadsheetFormatCurrencyParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatDateParserToken} return true
     */
    public final boolean isDate(){
        return this instanceof SpreadsheetFormatDateParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatDateTimeParserToken} return true
     */
    public final boolean isDateTime(){
        return this instanceof SpreadsheetFormatDateTimeParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatDayParserToken} return true
     */
    public final boolean isDay(){
        return this instanceof SpreadsheetFormatDayParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatDecimalPointParserToken} return true
     */
    public final boolean isDecimalPoint(){
        return this instanceof SpreadsheetFormatDecimalPointParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatDigitParserToken} return true
     */
    public final boolean isDigit(){
        return this instanceof SpreadsheetFormatDigitParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatDigitSpaceParserToken} return true
     */
    public final boolean isDigitSpace(){
        return this instanceof SpreadsheetFormatDigitSpaceParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatDigitZeroParserToken} return true
     */
    public final boolean isDigitZero(){
        return this instanceof SpreadsheetFormatDigitZeroParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatEqualsParserToken} return true
     */
    public final boolean isEquals(){
        return this instanceof SpreadsheetFormatEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatEqualsSymbolParserToken} return true
     */
    public final boolean isEqualsSymbol(){
        return this instanceof SpreadsheetFormatEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatEscapeParserToken} return true
     */
    public final boolean isEscape(){
        return this instanceof SpreadsheetFormatEscapeParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatExponentParserToken} return true
     */
    public final boolean isExponent(){
        return this instanceof SpreadsheetFormatExponentParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatExponentSymbolParserToken} return true
     */
    public final boolean isExponentSymbol(){
        return this instanceof SpreadsheetFormatExponentSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatExpressionParserToken} return true
     */
    public final boolean isExpression(){
        return this instanceof SpreadsheetFormatExpressionParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatFractionParserToken} return true
     */
    public final boolean isFraction(){
        return this instanceof SpreadsheetFormatFractionParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatFractionSymbolParserToken} return true
     */
    public final boolean isFractionSymbol(){
        return this instanceof SpreadsheetFormatFractionSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatGeneralParserToken} return true
     */
    public final boolean isGeneral(){
        return this instanceof SpreadsheetFormatGeneralParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatGeneralSymbolParserToken} return true
     */
    public final boolean isGeneralSymbol(){
        return this instanceof SpreadsheetFormatGeneralSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatGreaterThanParserToken} return true
     */
    public final boolean isGreaterThan(){
        return this instanceof SpreadsheetFormatGreaterThanParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatGreaterThanSymbolParserToken} return true
     */
    public final boolean isGreaterThanSymbol(){
        return this instanceof SpreadsheetFormatGreaterThanSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatGreaterThanEqualsParserToken} return true
     */
    public final boolean isGreaterThanEquals(){
        return this instanceof SpreadsheetFormatGreaterThanEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatGreaterThanEqualsSymbolParserToken} return true
     */
    public final boolean isGreaterThanEqualsSymbol(){
        return this instanceof SpreadsheetFormatGreaterThanEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatHourParserToken} return true
     */
    public final boolean isHour(){
        return this instanceof SpreadsheetFormatHourParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatLessThanParserToken} return true
     */
    public final boolean isLessThan(){
        return this instanceof SpreadsheetFormatLessThanParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatLessThanSymbolParserToken} return true
     */
    public final boolean isLessThanSymbol(){
        return this instanceof SpreadsheetFormatLessThanSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatLessThanEqualsParserToken} return true
     */
    public final boolean isLessThanEquals(){
        return this instanceof SpreadsheetFormatLessThanEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatLessThanEqualsSymbolParserToken} return true
     */
    public final boolean isLessThanEqualsSymbol(){
        return this instanceof SpreadsheetFormatLessThanEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatMonthOrMinuteParserToken} return true
     */
    public final boolean isMonthOrMinute(){
        return this instanceof SpreadsheetFormatMonthOrMinuteParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatSymbolParserToken} return true
     */
    public final boolean isNoise(){
        return this instanceof SpreadsheetFormatSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatNotEqualsParserToken} return true
     */
    public final boolean isNotEquals(){
        return this instanceof SpreadsheetFormatNotEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatNotEqualsSymbolParserToken} return true
     */
    public final boolean isNotEqualsSymbol(){
        return this instanceof SpreadsheetFormatNotEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatPercentParserToken} return true
     */
    public final boolean isPercent(){
        return this instanceof SpreadsheetFormatPercentParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatQuotedTextParserToken} return true
     */
    public final boolean isQuotedText(){
        return this instanceof SpreadsheetFormatQuotedTextParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatSecondParserToken} return true
     */
    public final boolean isSecond(){
        return this instanceof SpreadsheetFormatSecondParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatSeparatorSymbolParserToken} return true
     */
    public final boolean isSeparatorSymbol(){
        return this instanceof SpreadsheetFormatSeparatorSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatStarParserToken} return true
     */
    public final boolean isStar(){
        return this instanceof SpreadsheetFormatStarParserToken;
    }

    /**
     * Only sub classes of {@link SpreadsheetFormatSymbolParserToken} return true
     */
    public final boolean isSymbol(){
        return this instanceof SpreadsheetFormatSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatTextParserToken} return true
     */
    public final boolean isText(){
        return this instanceof SpreadsheetFormatTextParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatTextLiteralParserToken} return true
     */
    public final boolean isTextLiteral(){
        return this instanceof SpreadsheetFormatTextLiteralParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatTextPlaceholderParserToken} return true
     */
    public final boolean isTextPlaceholder(){
        return this instanceof SpreadsheetFormatTextPlaceholderParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatThousandsParserToken} return true
     */
    public final boolean isThousands(){
        return this instanceof SpreadsheetFormatThousandsParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatTimeParserToken} return true
     */
    public final boolean isTime(){
        return this instanceof SpreadsheetFormatTimeParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatUnderscoreParserToken} return true
     */
    public final boolean isUnderscore(){
        return this instanceof SpreadsheetFormatUnderscoreParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatWhitespaceParserToken} return true
     */
    public final boolean isWhitespace(){
        return this instanceof SpreadsheetFormatWhitespaceParserToken;
    }

    /**
     * Only {@link SpreadsheetFormatYearParserToken} return true
     */
    public final boolean isYear(){
        return this instanceof SpreadsheetFormatYearParserToken;
    }

    // Visitor ...........................................................................................................

    @Override
    public final void accept(final ParserTokenVisitor visitor) {
        if (visitor instanceof SpreadsheetFormatParserTokenVisitor) {
            final SpreadsheetFormatParserTokenVisitor visitor2 = (SpreadsheetFormatParserTokenVisitor) visitor;
            if (Visiting.CONTINUE == visitor2.startVisit(this)) {
                this.accept(visitor2);
            }
            visitor2.endVisit(this);
        }
    }

    abstract void accept(final SpreadsheetFormatParserTokenVisitor visitor);

    // Object ...........................................................................................................

    @Override
    public final int hashCode() {
        return this.text().hashCode();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final SpreadsheetFormatParserToken other) {
        return this.text.equals(other.text) &&
                this.value().equals(other.value());
    }

    @Override
    public final String toString() {
        return this.text();
    }
}
