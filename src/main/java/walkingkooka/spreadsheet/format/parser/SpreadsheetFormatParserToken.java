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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Represents a token within the spreadsheet format grammar.
 */
public abstract class SpreadsheetFormatParserToken implements ParserToken {

    /**
     * {@see SpreadsheetFormatParserTokenPredicate}
     */
    public static Predicate<ParserToken> predicate(final Predicate<SpreadsheetFormatParserToken> predicate) {
        return SpreadsheetFormatParserTokenPredicate.with(predicate);
    }

    /**
     * {@see AmPmSpreadsheetFormatParserToken}
     */
    public static AmPmSpreadsheetFormatParserToken amPm(final String value, final String text) {
        return AmPmSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see BracketCloseSymbolSpreadsheetFormatParserToken}
     */
    public static BracketCloseSymbolSpreadsheetFormatParserToken bracketCloseSymbol(final String value, final String text) {
        return BracketCloseSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see BracketOpenSymbolSpreadsheetFormatParserToken}
     */
    public static BracketOpenSymbolSpreadsheetFormatParserToken bracketOpenSymbol(final String value, final String text) {
        return BracketOpenSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ColorSpreadsheetFormatParserToken}
     */
    public static ColorSpreadsheetFormatParserToken color(final List<ParserToken> value, final String text) {
        return ColorSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ColorLiteralSymbolSpreadsheetFormatParserToken}
     */
    public static ColorLiteralSymbolSpreadsheetFormatParserToken colorLiteralSymbol(final String value, final String text) {
        return ColorLiteralSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ColorNameSpreadsheetFormatParserToken}
     */
    public static ColorNameSpreadsheetFormatParserToken colorName(final String value, final String text) {
        return ColorNameSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ColorNumberSpreadsheetFormatParserToken}
     */
    public static ColorNumberSpreadsheetFormatParserToken colorNumber(final Integer value, final String text) {
        return ColorNumberSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ConditionNumberSpreadsheetFormatParserToken}
     */
    public static ConditionNumberSpreadsheetFormatParserToken conditionNumber(final BigDecimal value, final String text) {
        return ConditionNumberSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see CurrencySpreadsheetFormatParserToken}
     */
    public static CurrencySpreadsheetFormatParserToken currency(final String value, final String text) {
        return CurrencySpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see DateSpreadsheetFormatParserToken}
     */
    public static DateSpreadsheetFormatParserToken date(final List<ParserToken> value, final String text) {
        return DateSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see DateTimeSpreadsheetFormatParserToken}
     */
    public static DateTimeSpreadsheetFormatParserToken dateTime(final List<ParserToken> value, final String text) {
        return DateTimeSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see DaySpreadsheetFormatParserToken}
     */
    public static DaySpreadsheetFormatParserToken day(final String value, final String text) {
        return DaySpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see DecimalPointSpreadsheetFormatParserToken}
     */
    public static DecimalPointSpreadsheetFormatParserToken decimalPoint(final String value, final String text) {
        return DecimalPointSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see DigitSpreadsheetFormatParserToken}
     */
    public static DigitSpreadsheetFormatParserToken digit(final String value, final String text) {
        return DigitSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see DigitSpaceSpreadsheetFormatParserToken}
     */
    public static DigitSpaceSpreadsheetFormatParserToken digitSpace(final String value, final String text) {
        return DigitSpaceSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see DigitZeroSpreadsheetFormatParserToken}
     */
    public static DigitZeroSpreadsheetFormatParserToken digitZero(final String value, final String text) {
        return DigitZeroSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see EqualsSpreadsheetFormatParserToken}
     */
    public static EqualsSpreadsheetFormatParserToken equalsSpreadsheetFormatParserToken(final List<ParserToken> value, final String text) {
        return EqualsSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see EqualsSymbolSpreadsheetFormatParserToken}
     */
    public static EqualsSymbolSpreadsheetFormatParserToken equalsSymbol(final String value, final String text) {
        return EqualsSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see EscapeSpreadsheetFormatParserToken}
     */
    public static EscapeSpreadsheetFormatParserToken escape(final Character value, final String text) {
        return EscapeSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ExponentSpreadsheetFormatParserToken}
     */
    public static ExponentSpreadsheetFormatParserToken exponent(final List<ParserToken> value, final String text) {
        return ExponentSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ExponentSymbolSpreadsheetFormatParserToken}
     */
    public static ExponentSymbolSpreadsheetFormatParserToken exponentSymbol(final String value, final String text) {
        return ExponentSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see ExpressionSpreadsheetFormatParserToken}
     */
    public static ExpressionSpreadsheetFormatParserToken expression(final List<ParserToken> value, final String text) {
        return ExpressionSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see FractionSpreadsheetFormatParserToken}
     */
    public static FractionSpreadsheetFormatParserToken fraction(final List<ParserToken> value, final String text) {
        return FractionSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see FractionSymbolSpreadsheetFormatParserToken}
     */
    public static FractionSymbolSpreadsheetFormatParserToken fractionSymbol(final String value, final String text) {
        return FractionSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see GeneralSpreadsheetFormatParserToken}
     */
    public static GeneralSpreadsheetFormatParserToken general(final List<ParserToken> value, final String text) {
        return GeneralSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see GeneralSymbolSpreadsheetFormatParserToken}
     */
    public static GeneralSymbolSpreadsheetFormatParserToken generalSymbol(final String value, final String text) {
        return GeneralSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanSpreadsheetFormatParserToken}
     */
    public static GreaterThanSpreadsheetFormatParserToken greaterThan(final List<ParserToken> value, final String text) {
        return GreaterThanSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanSymbolSpreadsheetFormatParserToken}
     */
    public static GreaterThanSymbolSpreadsheetFormatParserToken greaterThanSymbol(final String value, final String text) {
        return GreaterThanSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanEqualsSpreadsheetFormatParserToken}
     */
    public static GreaterThanEqualsSpreadsheetFormatParserToken greaterThanEquals(final List<ParserToken> value, final String text) {
        return GreaterThanEqualsSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanEqualsSpreadsheetFormatParserToken}
     */
    public static GreaterThanEqualsSymbolSpreadsheetFormatParserToken greaterThanEqualsSymbol(final String value, final String text) {
        return GreaterThanEqualsSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see GroupSeparatorSpreadsheetFormatParserToken}
     */
    public static GroupSeparatorSpreadsheetFormatParserToken groupSeparator(final String value, final String text) {
        return GroupSeparatorSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see HourSpreadsheetFormatParserToken}
     */
    public static HourSpreadsheetFormatParserToken hour(final String value, final String text) {
        return HourSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see LessThanSpreadsheetFormatParserToken}
     */
    public static LessThanSpreadsheetFormatParserToken lessThan(final List<ParserToken> value, final String text) {
        return LessThanSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see LessThanSymbolSpreadsheetFormatParserToken}
     */
    public static LessThanSymbolSpreadsheetFormatParserToken lessThanSymbol(final String value, final String text) {
        return LessThanSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see LessThanEqualsSpreadsheetFormatParserToken}
     */
    public static LessThanEqualsSpreadsheetFormatParserToken lessThanEquals(final List<ParserToken> value, final String text) {
        return LessThanEqualsSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see LessThanEqualsSpreadsheetFormatParserToken}
     */
    public static LessThanEqualsSymbolSpreadsheetFormatParserToken lessThanEqualsSymbol(final String value, final String text) {
        return LessThanEqualsSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see MinuteSpreadsheetFormatParserToken}
     */
    public static MinuteSpreadsheetFormatParserToken minute(final String value, final String text) {
        return MinuteSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see MonthSpreadsheetFormatParserToken}
     */
    public static MonthSpreadsheetFormatParserToken month(final String value, final String text) {
        return MonthSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see NotEqualsSpreadsheetFormatParserToken}
     */
    public static NotEqualsSpreadsheetFormatParserToken notEquals(final List<ParserToken> value, final String text) {
        return NotEqualsSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see NotEqualsSymbolSpreadsheetFormatParserToken}
     */
    public static NotEqualsSymbolSpreadsheetFormatParserToken notEqualsSymbol(final String value, final String text) {
        return NotEqualsSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see NumberSpreadsheetFormatParserToken}
     */
    public static NumberSpreadsheetFormatParserToken number(final List<ParserToken> value, final String text) {
        return NumberSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see PercentSpreadsheetFormatParserToken}
     */
    public static PercentSpreadsheetFormatParserToken percent(final String value, final String text) {
        return PercentSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see QuotedTextSpreadsheetFormatParserToken}
     */
    public static QuotedTextSpreadsheetFormatParserToken quotedText(final String value, final String text) {
        return QuotedTextSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see SecondSpreadsheetFormatParserToken}
     */
    public static SecondSpreadsheetFormatParserToken second(final String value, final String text) {
        return SecondSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see SeparatorSymbolSpreadsheetFormatParserToken}
     */
    public static SeparatorSymbolSpreadsheetFormatParserToken separatorSymbol(final String value, final String text) {
        return SeparatorSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see StarSpreadsheetFormatParserToken}
     */
    public static StarSpreadsheetFormatParserToken star(final Character value, final String text) {
        return StarSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see TextSpreadsheetFormatParserToken}
     */
    public static TextSpreadsheetFormatParserToken text(final List<ParserToken> value, final String text) {
        return TextSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see TextLiteralSpreadsheetFormatParserToken}
     */
    public static TextLiteralSpreadsheetFormatParserToken textLiteral(final String value, final String text) {
        return TextLiteralSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see TextPlaceholderSpreadsheetFormatParserToken}
     */
    public static TextPlaceholderSpreadsheetFormatParserToken textPlaceholder(final String value, final String text) {
        return TextPlaceholderSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see TimeSpreadsheetFormatParserToken}
     */
    public static TimeSpreadsheetFormatParserToken time(final List<ParserToken> value, final String text) {
        return TimeSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see UnderscoreSpreadsheetFormatParserToken}
     */
    public static UnderscoreSpreadsheetFormatParserToken underscore(final Character value, final String text) {
        return UnderscoreSpreadsheetFormatParserToken.with(value, text);
    }

    /**
     * {@see WhitespaceSpreadsheetFormatParserToken}
     */
    public static WhitespaceSpreadsheetFormatParserToken whitespace(final String value, final String whitespace) {
        return WhitespaceSpreadsheetFormatParserToken.with(value, whitespace);
    }

    /**
     * {@see YearSpreadsheetFormatParserToken}
     */
    public static YearSpreadsheetFormatParserToken year(final String value, final String text) {
        return YearSpreadsheetFormatParserToken.with(value, text);
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
        return CharSequences.failIfNullOrEmpty(text, "text");
    }

    static String checkTextNotEmptyOrWhitespace(final String text) {
        return Whitespace.failIfNullOrEmptyOrWhitespace(text, "text");
    }

    /**
     * Package private ctor to limit subclassing.
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
     * Only {@link AmPmSpreadsheetFormatParserToken} return true
     */
    public final boolean isAmPm() {
        return this instanceof AmPmSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link NumberSpreadsheetFormatParserToken} return true
     */
    public final boolean isNumber() {
        return this instanceof NumberSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link BracketCloseSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isBracketCloseSymbol() {
        return this instanceof BracketCloseSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link BracketOpenSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isBracketOpenSymbol() {
        return this instanceof BracketOpenSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ColorSpreadsheetFormatParserToken} return true
     */
    public final boolean isColor() {
        return this instanceof ColorSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ColorLiteralSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isColorLiteralSymbol() {
        return this instanceof ColorLiteralSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ColorNameSpreadsheetFormatParserToken} return true
     */
    public final boolean isColorName() {
        return this instanceof ColorNameSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ColorNumberSpreadsheetFormatParserToken} return true
     */
    public final boolean isColorNumber() {
        return this instanceof ColorNumberSpreadsheetFormatParserToken;
    }

    /**
     * All subclasses of {@link ConditionSpreadsheetFormatParserToken} return true
     */
    public final boolean isCondition() {
        return this instanceof ConditionSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ConditionNumberSpreadsheetFormatParserToken} return true
     */
    public final boolean isConditionNumber() {
        return this instanceof ConditionNumberSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link CurrencySpreadsheetFormatParserToken} return true
     */
    public final boolean isCurrency() {
        return this instanceof CurrencySpreadsheetFormatParserToken;
    }

    /**
     * Only {@link DateSpreadsheetFormatParserToken} return true
     */
    public final boolean isDate() {
        return this instanceof DateSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link DateTimeSpreadsheetFormatParserToken} return true
     */
    public final boolean isDateTime() {
        return this instanceof DateTimeSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link DaySpreadsheetFormatParserToken} return true
     */
    public final boolean isDay() {
        return this instanceof DaySpreadsheetFormatParserToken;
    }

    /**
     * Only {@link DecimalPointSpreadsheetFormatParserToken} return true
     */
    public final boolean isDecimalPoint() {
        return this instanceof DecimalPointSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link DigitSpreadsheetFormatParserToken} return true
     */
    public final boolean isDigit() {
        return this instanceof DigitSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link DigitSpaceSpreadsheetFormatParserToken} return true
     */
    public final boolean isDigitSpace() {
        return this instanceof DigitSpaceSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link DigitZeroSpreadsheetFormatParserToken} return true
     */
    public final boolean isDigitZero() {
        return this instanceof DigitZeroSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link EqualsSpreadsheetFormatParserToken} return true
     */
    public final boolean isEquals() {
        return this instanceof EqualsSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link EqualsSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isEqualsSymbol() {
        return this instanceof EqualsSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link EscapeSpreadsheetFormatParserToken} return true
     */
    public final boolean isEscape() {
        return this instanceof EscapeSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ExponentSpreadsheetFormatParserToken} return true
     */
    public final boolean isExponent() {
        return this instanceof ExponentSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ExponentSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isExponentSymbol() {
        return this instanceof ExponentSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link ExpressionSpreadsheetFormatParserToken} return true
     */
    public final boolean isExpression() {
        return this instanceof ExpressionSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link FractionSpreadsheetFormatParserToken} return true
     */
    public final boolean isFraction() {
        return this instanceof FractionSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link FractionSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isFractionSymbol() {
        return this instanceof FractionSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link GeneralSpreadsheetFormatParserToken} return true
     */
    public final boolean isGeneral() {
        return this instanceof GeneralSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link GeneralSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isGeneralSymbol() {
        return this instanceof GeneralSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link GreaterThanSpreadsheetFormatParserToken} return true
     */
    public final boolean isGreaterThan() {
        return this instanceof GreaterThanSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link GreaterThanSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isGreaterThanSymbol() {
        return this instanceof GreaterThanSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link GreaterThanEqualsSpreadsheetFormatParserToken} return true
     */
    public final boolean isGreaterThanEquals() {
        return this instanceof GreaterThanEqualsSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link GreaterThanEqualsSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isGreaterThanEqualsSymbol() {
        return this instanceof GreaterThanEqualsSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link GroupSeparatorSpreadsheetFormatParserToken} return true
     */
    public final boolean isGroupSeparator() {
        return this instanceof GroupSeparatorSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link HourSpreadsheetFormatParserToken} return true
     */
    public final boolean isHour() {
        return this instanceof HourSpreadsheetFormatParserToken;
    }

    /**
     * Returns true for subclasses of {@link SpreadsheetFormatLeafParserToken}.
     */
    @Override
    public final boolean isLeaf() {
        return this instanceof SpreadsheetFormatLeafParserToken;
    }

    /**
     * Only {@link LessThanSpreadsheetFormatParserToken} return true
     */
    public final boolean isLessThan() {
        return this instanceof LessThanSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link LessThanSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isLessThanSymbol() {
        return this instanceof LessThanSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link LessThanEqualsSpreadsheetFormatParserToken} return true
     */
    public final boolean isLessThanEquals() {
        return this instanceof LessThanEqualsSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link LessThanEqualsSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isLessThanEqualsSymbol() {
        return this instanceof LessThanEqualsSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link MinuteSpreadsheetFormatParserToken} return true
     */
    public final boolean isMinute() {
        return this instanceof MinuteSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link MonthSpreadsheetFormatParserToken} return true
     */
    public final boolean isMonth() {
        return this instanceof MonthSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link SymbolSpreadsheetFormatParserToken} return true
     */
    @Override
    public final boolean isNoise() {
        return this instanceof SymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link NotEqualsSpreadsheetFormatParserToken} return true
     */
    public final boolean isNotEquals() {
        return this instanceof NotEqualsSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link NotEqualsSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isNotEqualsSymbol() {
        return this instanceof NotEqualsSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Returns true for subclasses of {@link ParentSpreadsheetFormatParserToken}.
     */
    @Override
    public final boolean isParent() {
        return this instanceof ParentSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link PercentSpreadsheetFormatParserToken} return true
     */
    public final boolean isPercent() {
        return this instanceof PercentSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link QuotedTextSpreadsheetFormatParserToken} return true
     */
    public final boolean isQuotedText() {
        return this instanceof QuotedTextSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link SecondSpreadsheetFormatParserToken} return true
     */
    public final boolean isSecond() {
        return this instanceof SecondSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link SeparatorSymbolSpreadsheetFormatParserToken} return true
     */
    public final boolean isSeparatorSymbol() {
        return this instanceof SeparatorSymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link StarSpreadsheetFormatParserToken} return true
     */
    public final boolean isStar() {
        return this instanceof StarSpreadsheetFormatParserToken;
    }

    /**
     * Only subclasses of {@link SymbolSpreadsheetFormatParserToken} return true
     */
    @Override
    public final boolean isSymbol() {
        return this instanceof SymbolSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link TextSpreadsheetFormatParserToken} return true
     */
    public final boolean isText() {
        return this instanceof TextSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link TextLiteralSpreadsheetFormatParserToken} return true
     */
    public final boolean isTextLiteral() {
        return this instanceof TextLiteralSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link TextPlaceholderSpreadsheetFormatParserToken} return true
     */
    public final boolean isTextPlaceholder() {
        return this instanceof TextPlaceholderSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link TimeSpreadsheetFormatParserToken} return true
     */
    public final boolean isTime() {
        return this instanceof TimeSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link UnderscoreSpreadsheetFormatParserToken} return true
     */
    public final boolean isUnderscore() {
        return this instanceof UnderscoreSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link WhitespaceSpreadsheetFormatParserToken} return true
     */
    @Override
    public final boolean isWhitespace() {
        return this instanceof WhitespaceSpreadsheetFormatParserToken;
    }

    /**
     * Only {@link YearSpreadsheetFormatParserToken} return true
     */
    public final boolean isYear() {
        return this instanceof YearSpreadsheetFormatParserToken;
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    /**
     * Returns the {@link SpreadsheetFormatParserTokenKind} for this token. Only leaf tokens will return something.
     */
    public abstract Optional<SpreadsheetFormatParserTokenKind> kind();

    final static Optional<SpreadsheetFormatParserTokenKind> EMPTY_KIND = Optional.empty();

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
    public final boolean equals(final Object other) {
        return this == other ||
            null != other && this.getClass() == other.getClass() && this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormatParserToken other) {
        return this.text.equals(other.text) &&
            this.value().equals(other.value());
    }

    @Override
    public final String toString() {
        return this.text();
    }

    // json.............................................................................................................

    // NonSymbolSpreadsheetFormatParserToken............................................................................

    static {
        registerLeafParserToken(
            AmPmSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallAmPm
        );

        registerLeafParserToken(
            ColorNameSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallColorName
        );

        registerLeafParserToken(
            ColorNumberSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallColorNumber
        );

        registerLeafParserToken(
            ConditionNumberSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallConditionNumber
        );

        registerLeafParserToken(
            CurrencySpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallCurrency
        );

        registerLeafParserToken(
            DaySpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallDay
        );

        registerLeafParserToken(
            DecimalPointSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallDecimalPoint
        );

        registerLeafParserToken(
            DigitSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallDigit
        );

        registerLeafParserToken(
            DigitSpaceSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallDigitSpace
        );

        registerLeafParserToken(
            DigitZeroSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallDigitZero
        );

        registerLeafParserToken(
            EscapeSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallEscape
        );

        registerLeafParserToken(
            GroupSeparatorSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallGroupSeparator
        );

        registerLeafParserToken(
            HourSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallHour
        );

        registerLeafParserToken(
            MinuteSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallMinute
        );

        registerLeafParserToken(
            MonthSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallMonth
        );

        registerLeafParserToken(
            PercentSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallPercent
        );

        registerLeafParserToken(
            QuotedTextSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallQuotedText
        );

        registerLeafParserToken(
            SecondSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallSecond
        );

        registerLeafParserToken(
            StarSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallStar
        );

        registerLeafParserToken(
            TextLiteralSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallTextLiteral
        );

        registerLeafParserToken(
            TextPlaceholderSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallTextPlaceholder
        );

        registerLeafParserToken(
            UnderscoreSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallUnderscore
        );

        registerLeafParserToken(
            YearSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallYear
        );
    }

    static AmPmSpreadsheetFormatParserToken unmarshallAmPm(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::amPm
        );
    }

    static ColorNameSpreadsheetFormatParserToken unmarshallColorName(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::colorName
        );
    }

    static ColorNumberSpreadsheetFormatParserToken unmarshallColorNumber(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
            node,
            Integer.class,
            context,
            SpreadsheetFormatParserToken::colorNumber
        );
    }

    static ConditionNumberSpreadsheetFormatParserToken unmarshallConditionNumber(final JsonNode node,
                                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
            node,
            BigDecimal.class,
            context,
            SpreadsheetFormatParserToken::conditionNumber
        );
    }

    static CurrencySpreadsheetFormatParserToken unmarshallCurrency(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
            node,
            String.class,
            context,
            SpreadsheetFormatParserToken::currency
        );
    }

    static DaySpreadsheetFormatParserToken unmarshallDay(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::day
        );
    }

    static DecimalPointSpreadsheetFormatParserToken unmarshallDecimalPoint(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::decimalPoint
        );
    }

    static DigitSpreadsheetFormatParserToken unmarshallDigit(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::digit
        );
    }

    static DigitSpaceSpreadsheetFormatParserToken unmarshallDigitSpace(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::digitSpace
        );
    }

    static DigitZeroSpreadsheetFormatParserToken unmarshallDigitZero(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::digitZero
        );
    }

    static EscapeSpreadsheetFormatParserToken unmarshallEscape(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallCharacterLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::escape
        );
    }

    static HourSpreadsheetFormatParserToken unmarshallHour(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::hour
        );
    }

    static MinuteSpreadsheetFormatParserToken unmarshallMinute(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::minute
        );
    }

    static MonthSpreadsheetFormatParserToken unmarshallMonth(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::month
        );
    }

    static PercentSpreadsheetFormatParserToken unmarshallPercent(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::percent
        );
    }

    static QuotedTextSpreadsheetFormatParserToken unmarshallQuotedText(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::quotedText
        );
    }

    static SecondSpreadsheetFormatParserToken unmarshallSecond(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::second
        );
    }

    static StarSpreadsheetFormatParserToken unmarshallStar(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallCharacterLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::star
        );
    }

    static TextLiteralSpreadsheetFormatParserToken unmarshallTextLiteral(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::textLiteral
        );
    }

    static TextPlaceholderSpreadsheetFormatParserToken unmarshallTextPlaceholder(final JsonNode node,
                                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::textPlaceholder
        );
    }

    static GroupSeparatorSpreadsheetFormatParserToken unmarshallGroupSeparator(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::groupSeparator
        );
    }

    static UnderscoreSpreadsheetFormatParserToken unmarshallUnderscore(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallCharacterLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::underscore
        );
    }

    static YearSpreadsheetFormatParserToken unmarshallYear(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::year
        );
    }

    // SpreadsheetSymbolParserToken.....................................................................................

    static {
        registerLeafParserToken(
            BracketCloseSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallBracketCloseSymbol
        );

        registerLeafParserToken(
            BracketOpenSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallBracketOpenSymbol
        );

        registerLeafParserToken(
            ColorLiteralSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallColorLiteralSymbol
        );

        registerLeafParserToken(
            EqualsSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallEqualsSymbol
        );

        registerLeafParserToken(
            ExponentSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallExponentSymbol
        );

        registerLeafParserToken(
            FractionSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallFractionSymbol
        );

        registerLeafParserToken(
            GeneralSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallGeneralSymbol
        );

        registerLeafParserToken(
            GreaterThanEqualsSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallGreaterThanEqualsSymbol
        );

        registerLeafParserToken(
            GreaterThanSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallGreaterThanSymbol
        );

        registerLeafParserToken(
            LessThanEqualsSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallLessThanEqualsSymbol
        );

        registerLeafParserToken(
            LessThanSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallLessThanSymbol
        );

        registerLeafParserToken(
            NotEqualsSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallNotEqualsSymbol
        );

        registerLeafParserToken(
            SeparatorSymbolSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallSeparatorSymbol
        );

        registerLeafParserToken(
            WhitespaceSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallWhitespace
        );
    }

    static BracketCloseSymbolSpreadsheetFormatParserToken unmarshallBracketCloseSymbol(final JsonNode node,
                                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::bracketCloseSymbol
        );
    }

    static BracketOpenSymbolSpreadsheetFormatParserToken unmarshallBracketOpenSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::bracketOpenSymbol
        );
    }

    static ColorLiteralSymbolSpreadsheetFormatParserToken unmarshallColorLiteralSymbol(final JsonNode node,
                                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::colorLiteralSymbol
        );
    }

    static EqualsSymbolSpreadsheetFormatParserToken unmarshallEqualsSymbol(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::equalsSymbol
        );
    }

    static ExponentSymbolSpreadsheetFormatParserToken unmarshallExponentSymbol(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::exponentSymbol
        );
    }

    static FractionSymbolSpreadsheetFormatParserToken unmarshallFractionSymbol(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::fractionSymbol
        );
    }

    static GeneralSymbolSpreadsheetFormatParserToken unmarshallGeneralSymbol(final JsonNode node,
                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::generalSymbol
        );
    }

    static GreaterThanEqualsSymbolSpreadsheetFormatParserToken unmarshallGreaterThanEqualsSymbol(final JsonNode node,
                                                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::greaterThanEqualsSymbol
        );
    }


    static GreaterThanSymbolSpreadsheetFormatParserToken unmarshallGreaterThanSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::greaterThanSymbol
        );
    }

    static LessThanEqualsSymbolSpreadsheetFormatParserToken unmarshallLessThanEqualsSymbol(final JsonNode node,
                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::lessThanEqualsSymbol
        );
    }

    static LessThanSymbolSpreadsheetFormatParserToken unmarshallLessThanSymbol(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::lessThanSymbol
        );
    }

    static NotEqualsSymbolSpreadsheetFormatParserToken unmarshallNotEqualsSymbol(final JsonNode node,
                                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::notEqualsSymbol
        );
    }

    static SeparatorSymbolSpreadsheetFormatParserToken unmarshallSeparatorSymbol(final JsonNode node,
                                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::separatorSymbol
        );
    }

    static WhitespaceSpreadsheetFormatParserToken unmarshallWhitespace(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::whitespace
        );
    }

    /**
     * Helper that knows how to unmarshall a subclass of {@link SpreadsheetFormatLeafParserToken}
     */
    static <T extends SymbolSpreadsheetFormatParserToken> T unmarshallSymbolParserToken(final JsonNode node,
                                                                                        final JsonNodeUnmarshallContext context,
                                                                                        final BiFunction<String, String, T> factory) {
        return unmarshallStringLeafParserToken(
            node,
            context,
            factory
        );
    }

    private static <T extends SpreadsheetFormatLeafParserToken<Character>> T unmarshallCharacterLeafParserToken(final JsonNode node,
                                                                                                                final JsonNodeUnmarshallContext context,
                                                                                                                final BiFunction<Character, String, T> factory) {
        return unmarshallLeafParserToken(
            node,
            Character.class,
            context,
            factory
        );
    }

    private static <T extends SpreadsheetFormatLeafParserToken<String>> T unmarshallStringLeafParserToken(final JsonNode node,
                                                                                                          final JsonNodeUnmarshallContext context,
                                                                                                          final BiFunction<String, String, T> factory) {
        return unmarshallLeafParserToken(
            node,
            String.class,
            context,
            factory
        );
    }

    /**
     * Helper that knows how to unmarshall a subclass of {@link SpreadsheetFormatLeafParserToken}
     */
    private static <V, T extends SpreadsheetFormatLeafParserToken<V>> T unmarshallLeafParserToken(final JsonNode node,
                                                                                                  final Class<V> valueType,
                                                                                                  final JsonNodeUnmarshallContext context,
                                                                                                  final BiFunction<V, String, T> factory) {
        V value = null;
        String text = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (name.value()) {
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshall(child, valueType);
                    break;
                case TEXT_PROPERTY_STRING:
                    try {
                        text = child.stringOrFail();
                    } catch (final JsonNodeException cause) {
                        throw new JsonNodeUnmarshallException("Node " + TEXT_PROPERTY + " is not a string=" + child, node);
                    }
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == value) {
            JsonNodeUnmarshallContext.missingProperty(VALUE_PROPERTY, node);
        }
        if (null == text) {
            JsonNodeUnmarshallContext.missingProperty(TEXT_PROPERTY, node);
        }

        return factory.apply(value, text);
    }

    private static <T extends SpreadsheetFormatLeafParserToken<?>> void registerLeafParserToken(final Class<T> type,
                                                                                                final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
            type,
            from,
            SpreadsheetFormatParserToken::marshallLeafParserToken
        );
    }

    // ParentSpreadsheetFormatParserToken...............................................................................

    static {
        registerParentParserToken(
            ColorSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallColor
        );

        registerParentParserToken(
            DateSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallDate
        );

        registerParentParserToken(
            DateTimeSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallDateTime
        );

        registerParentParserToken(
            EqualsSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallEquals
        );

        registerParentParserToken(
            ExponentSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallExponent
        );

        registerParentParserToken(
            ExpressionSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallExpression
        );

        registerParentParserToken(
            FractionSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallFraction
        );

        registerParentParserToken(
            GeneralSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallGeneral
        );

        registerParentParserToken(
            GreaterThanSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallGreaterThan
        );

        registerParentParserToken(
            GreaterThanEqualsSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallGreaterThanEquals
        );

        registerParentParserToken(
            LessThanSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallLessThan
        );

        registerParentParserToken(
            LessThanEqualsSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallLessThanEquals
        );

        registerParentParserToken(
            NotEqualsSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallNotEquals
        );

        registerParentParserToken(
            NumberSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallNumber
        );

        registerParentParserToken(
            TextSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallText
        );

        registerParentParserToken(
            TimeSpreadsheetFormatParserToken.class,
            SpreadsheetFormatParserToken::unmarshallTime
        );
    }

    static ColorSpreadsheetFormatParserToken unmarshallColor(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::color
        );
    }

    static DateSpreadsheetFormatParserToken unmarshallDate(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::date
        );
    }

    static DateTimeSpreadsheetFormatParserToken unmarshallDateTime(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::dateTime
        );
    }

    static EqualsSpreadsheetFormatParserToken unmarshallEquals(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::equalsSpreadsheetFormatParserToken
        );
    }

    static ExponentSpreadsheetFormatParserToken unmarshallExponent(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::exponent
        );
    }

    static ExpressionSpreadsheetFormatParserToken unmarshallExpression(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::expression
        );
    }

    static FractionSpreadsheetFormatParserToken unmarshallFraction(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::fraction
        );
    }

    static GeneralSpreadsheetFormatParserToken unmarshallGeneral(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::general
        );
    }

    static GreaterThanSpreadsheetFormatParserToken unmarshallGreaterThan(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::greaterThan
        );
    }

    static GreaterThanEqualsSpreadsheetFormatParserToken unmarshallGreaterThanEquals(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::greaterThanEquals
        );
    }

    static LessThanSpreadsheetFormatParserToken unmarshallLessThan(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::lessThan
        );
    }

    static LessThanEqualsSpreadsheetFormatParserToken unmarshallLessThanEquals(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::lessThanEquals
        );
    }

    static NotEqualsSpreadsheetFormatParserToken unmarshallNotEquals(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::notEquals
        );
    }

    static NumberSpreadsheetFormatParserToken unmarshallNumber(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::number
        );
    }

    static TextSpreadsheetFormatParserToken unmarshallText(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::text
        );
    }

    static TimeSpreadsheetFormatParserToken unmarshallTime(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
            node,
            context,
            SpreadsheetFormatParserToken::time
        );
    }

    /**
     * Helper that knows how to unmarshall a subclass of {@link SpreadsheetFormatLeafParserToken}
     */
    private static <T extends ParentSpreadsheetFormatParserToken> T unmarshallParentParserToken(final JsonNode node,
                                                                                                final JsonNodeUnmarshallContext context,
                                                                                                final BiFunction<List<ParserToken>, String, T> factory) {
        List<ParserToken> value = null;
        String text = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (name.value()) {
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallListWithType(child);
                    break;
                case TEXT_PROPERTY_STRING:
                    try {
                        text = child.stringOrFail();
                    } catch (final JsonNodeException cause) {
                        throw new JsonNodeUnmarshallException("Node " + TEXT_PROPERTY + " is not a string=" + child, node);
                    }
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == value) {
            JsonNodeUnmarshallContext.missingProperty(VALUE_PROPERTY, node);
        }
        if (null == text) {
            JsonNodeUnmarshallContext.missingProperty(TEXT_PROPERTY, node);
        }

        return factory.apply(value, text);
    }

    /**
     * Handles marshalling any {@link SpreadsheetFormatLeafParserToken}
     */
    private JsonNode marshallParentParserToken(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(
                VALUE_PROPERTY,
                context.marshallCollectionWithType(
                    Cast.to(this.value())
                )
            ) // unnecessary to include type.
            .set(
                TEXT_PROPERTY,
                JsonNode.string(this.text())
            );
    }

    private static <T extends ParentSpreadsheetFormatParserToken> void registerParentParserToken(final Class<T> type,
                                                                                                 final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
            type,
            from,
            SpreadsheetFormatParserToken::marshallParentParserToken
        );
    }

    private final static String VALUE_PROPERTY_STRING = "value";
    private final static String TEXT_PROPERTY_STRING = "text";

    // @VisibleForTesting

    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);
    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);


    /**
     * Handles marshalling any {@link SpreadsheetFormatLeafParserToken}
     */
    private JsonNode marshallLeafParserToken(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(VALUE_PROPERTY, context.marshall(this.value())) // unnecessary to include type.
            .set(TEXT_PROPERTY, JsonNode.string(this.text()));
    }

    private static <T extends SpreadsheetFormatParserToken> void register(final Class<T> type,
                                                                          final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from,
                                                                          final BiFunction<T, JsonNodeMarshallContext, JsonNode> to) {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(type),
            from,
            to,
            type
        );
    }
}
