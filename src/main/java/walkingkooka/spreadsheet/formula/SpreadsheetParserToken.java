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
package walkingkooka.spreadsheet.formula;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Represents a token within the grammar.
 */
public abstract class SpreadsheetParserToken implements ParserToken {

    /**
     * {@see AdditionSpreadsheetParserToken}
     */
    public static AdditionSpreadsheetParserToken addition(final List<ParserToken> value, final String text) {
        return AdditionSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see AmPmSpreadsheetParserToken}
     */
    public static AmPmSpreadsheetParserToken amPm(final int value, final String text) {
        return AmPmSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ApostropheSymbolSpreadsheetParserToken}
     */
    public static ApostropheSymbolSpreadsheetParserToken apostropheSymbol(final String value, final String text) {
        return ApostropheSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see BetweenSymbolSpreadsheetParserToken}
     */
    public static BetweenSymbolSpreadsheetParserToken betweenSymbol(final String value, final String text) {
        return BetweenSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see CellRangeSpreadsheetParserToken}
     */
    public static CellRangeSpreadsheetParserToken cellRange(final List<ParserToken> value, final String text) {
        return CellRangeSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see CellReferenceSpreadsheetParserToken}
     */
    public static CellReferenceSpreadsheetParserToken cellReference(final List<ParserToken> value, final String text) {
        return CellReferenceSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ColumnReferenceSpreadsheetParserToken}
     */
    public static ColumnReferenceSpreadsheetParserToken columnReference(final SpreadsheetColumnReference value, final String text) {
        return ColumnReferenceSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightEqualsSpreadsheetParserToken}
     */
    public static ConditionRightEqualsSpreadsheetParserToken conditionRightEquals(final List<ParserToken> value,
                                                                                  final String text) {
        return ConditionRightEqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightGreaterThanSpreadsheetParserToken}
     */
    public static ConditionRightGreaterThanSpreadsheetParserToken conditionRightGreaterThan(final List<ParserToken> value,
                                                                                            final String text) {
        return ConditionRightGreaterThanSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightGreaterThanEqualsSpreadsheetParserToken}
     */
    public static ConditionRightGreaterThanEqualsSpreadsheetParserToken conditionRightGreaterThanEquals(final List<ParserToken> value,
                                                                                                        final String text) {
        return ConditionRightGreaterThanEqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightLessThanSpreadsheetParserToken}
     */
    public static ConditionRightLessThanSpreadsheetParserToken conditionRightLessThan(final List<ParserToken> value,
                                                                                      final String text) {
        return ConditionRightLessThanSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightLessThanEqualsSpreadsheetParserToken}
     */
    public static ConditionRightLessThanEqualsSpreadsheetParserToken conditionRightLessThanEquals(final List<ParserToken> value,
                                                                                                  final String text) {
        return ConditionRightLessThanEqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightNotEqualsSpreadsheetParserToken}
     */
    public static ConditionRightNotEqualsSpreadsheetParserToken conditionRightNotEquals(final List<ParserToken> value,
                                                                                        final String text) {
        return ConditionRightNotEqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see CurrencySymbolSpreadsheetParserToken}
     */
    public static CurrencySymbolSpreadsheetParserToken currencySymbol(final String value, final String text) {
        return CurrencySymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DateSpreadsheetParserToken}
     */
    public static DateSpreadsheetParserToken date(final List<ParserToken> value, final String text) {
        return DateSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DateTimeSpreadsheetParserToken}
     */
    public static DateTimeSpreadsheetParserToken dateTime(final List<ParserToken> value, final String text) {
        return DateTimeSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DayNameSpreadsheetParserToken}
     */
    public static DayNameSpreadsheetParserToken dayName(final int value, final String text) {
        return DayNameSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DayNameAbbreviationSpreadsheetParserToken}
     */
    public static DayNameAbbreviationSpreadsheetParserToken dayNameAbbreviation(final int value, final String text) {
        return DayNameAbbreviationSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DayNumberSpreadsheetParserToken}
     */
    public static DayNumberSpreadsheetParserToken dayNumber(final int value, final String text) {
        return DayNumberSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DecimalSeparatorSymbolSpreadsheetParserToken}
     */
    public static DecimalSeparatorSymbolSpreadsheetParserToken decimalSeparatorSymbol(final String value, final String text) {
        return DecimalSeparatorSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DigitsSpreadsheetParserToken}
     */
    public static DigitsSpreadsheetParserToken digits(final String value, final String text) {
        return DigitsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DivideSymbolSpreadsheetParserToken}
     */
    public static DivideSymbolSpreadsheetParserToken divideSymbol(final String value, final String text) {
        return DivideSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DivisionSpreadsheetParserToken}
     */
    public static DivisionSpreadsheetParserToken division(final List<ParserToken> value, final String text) {
        return DivisionSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see DoubleQuoteSymbolSpreadsheetParserToken}
     */
    public static DoubleQuoteSymbolSpreadsheetParserToken doubleQuoteSymbol(final String value, final String text) {
        return DoubleQuoteSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see EqualsSpreadsheetParserToken}
     */
    public static EqualsSpreadsheetParserToken equalsSpreadsheetParserToken(final List<ParserToken> value, final String text) {
        return EqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see EqualsSymbolSpreadsheetParserToken}
     */
    public static EqualsSymbolSpreadsheetParserToken equalsSymbol(final String value, final String text) {
        return EqualsSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ErrorSpreadsheetParserToken}
     */
    public static ErrorSpreadsheetParserToken error(final SpreadsheetError value,
                                                    final String text) {
        return ErrorSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ExponentSymbolSpreadsheetParserToken}
     */
    public static ExponentSymbolSpreadsheetParserToken exponentSymbol(final String value, final String text) {
        return ExponentSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ExpressionSpreadsheetParserToken}
     */
    public static ExpressionSpreadsheetParserToken expression(final List<ParserToken> value, final String text) {
        return ExpressionSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see FunctionNameSpreadsheetParserToken}
     */
    public static FunctionNameSpreadsheetParserToken functionName(final SpreadsheetFunctionName value, final String text) {
        return FunctionNameSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see FunctionParametersSpreadsheetParserToken}
     */
    public static FunctionParametersSpreadsheetParserToken functionParameters(final List<ParserToken> value,
                                                                              final String text) {
        return FunctionParametersSpreadsheetParserToken.with(
                value,
                text
        );
    }

    /**
     * {@see GreaterThanSpreadsheetParserToken}
     */
    public static GreaterThanSpreadsheetParserToken greaterThan(final List<ParserToken> value, final String text) {
        return GreaterThanSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanSymbolSpreadsheetParserToken}
     */
    public static GreaterThanSymbolSpreadsheetParserToken greaterThanSymbol(final String value, final String text) {
        return GreaterThanSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanEqualsSpreadsheetParserToken}
     */
    public static GreaterThanEqualsSpreadsheetParserToken greaterThanEquals(final List<ParserToken> value, final String text) {
        return GreaterThanEqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanEqualsSpreadsheetParserToken}
     */
    public static GreaterThanEqualsSymbolSpreadsheetParserToken greaterThanEqualsSymbol(final String value, final String text) {
        return GreaterThanEqualsSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see GroupSpreadsheetParserToken}
     */
    public static GroupSpreadsheetParserToken group(final List<ParserToken> value, final String text) {
        return GroupSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see GroupSeparatorSymbolSpreadsheetParserToken}
     */
    public static GroupSeparatorSymbolSpreadsheetParserToken groupSeparatorSymbol(final String value, final String text) {
        return GroupSeparatorSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see HourSpreadsheetParserToken}
     */
    public static HourSpreadsheetParserToken hour(final int value, final String text) {
        return HourSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see LabelNameSpreadsheetParserToken}
     */
    public static LabelNameSpreadsheetParserToken labelName(final SpreadsheetLabelName value, final String text) {
        return LabelNameSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see LambdaFunctionSpreadsheetParserToken}
     */
    public static LambdaFunctionSpreadsheetParserToken lambdaFunction(final List<ParserToken> value,
                                                                      final String text) {
        return LambdaFunctionSpreadsheetParserToken.with(
                value,
                text
        );
    }

    /**
     * {@see LessThanSpreadsheetParserToken}
     */
    public static LessThanSpreadsheetParserToken lessThan(final List<ParserToken> value, final String text) {
        return LessThanSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see LessThanSymbolSpreadsheetParserToken}
     */
    public static LessThanSymbolSpreadsheetParserToken lessThanSymbol(final String value, final String text) {
        return LessThanSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see LessThanEqualsSpreadsheetParserToken}
     */
    public static LessThanEqualsSpreadsheetParserToken lessThanEquals(final List<ParserToken> value, final String text) {
        return LessThanEqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see LessThanEqualsSpreadsheetParserToken}
     */
    public static LessThanEqualsSymbolSpreadsheetParserToken lessThanEqualsSymbol(final String value, final String text) {
        return LessThanEqualsSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MinusSymbolSpreadsheetParserToken}
     */
    public static MinusSymbolSpreadsheetParserToken minusSymbol(final String value, final String text) {
        return MinusSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MillisecondSpreadsheetParserToken}
     */
    public static MillisecondSpreadsheetParserToken millisecond(final int value, final String text) {
        return MillisecondSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MinuteSpreadsheetParserToken}
     */
    public static MinuteSpreadsheetParserToken minute(final int value, final String text) {
        return MinuteSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MonthNameSpreadsheetParserToken}
     */
    public static MonthNameSpreadsheetParserToken monthName(final int value, final String text) {
        return MonthNameSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MonthNameAbbreviationSpreadsheetParserToken}
     */
    public static MonthNameAbbreviationSpreadsheetParserToken monthNameAbbreviation(final int value, final String text) {
        return MonthNameAbbreviationSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MonthNameInitialSpreadsheetParserToken}
     */
    public static MonthNameInitialSpreadsheetParserToken monthNameInitial(final int value, final String text) {
        return MonthNameInitialSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MonthNumberSpreadsheetParserToken}
     */
    public static MonthNumberSpreadsheetParserToken monthNumber(final int value, final String text) {
        return MonthNumberSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MultiplicationSpreadsheetParserToken}
     */
    public static MultiplicationSpreadsheetParserToken multiplication(final List<ParserToken> value, final String text) {
        return MultiplicationSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see MultiplySymbolSpreadsheetParserToken}
     */
    public static MultiplySymbolSpreadsheetParserToken multiplySymbol(final String value, final String text) {
        return MultiplySymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see NamedFunctionSpreadsheetParserToken}
     */
    public static NamedFunctionSpreadsheetParserToken namedFunction(final List<ParserToken> value,
                                                                    final String text) {
        return NamedFunctionSpreadsheetParserToken.with(
                value,
                text
        );
    }

    /**
     * {@see NegativeSpreadsheetParserToken}
     */
    public static NegativeSpreadsheetParserToken negative(final List<ParserToken> value, final String text) {
        return NegativeSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see NotEqualsSpreadsheetParserToken}
     */
    public static NotEqualsSpreadsheetParserToken notEquals(final List<ParserToken> value, final String text) {
        return NotEqualsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see NotEqualsSymbolSpreadsheetParserToken}
     */
    public static NotEqualsSymbolSpreadsheetParserToken notEqualsSymbol(final String value, final String text) {
        return NotEqualsSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see NumberSpreadsheetParserToken}
     */
    public static NumberSpreadsheetParserToken number(final List<ParserToken> value, final String text) {
        return NumberSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ParenthesisCloseSymbolSpreadsheetParserToken}
     */
    public static ParenthesisCloseSymbolSpreadsheetParserToken parenthesisCloseSymbol(final String value, final String text) {
        return ParenthesisCloseSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ParenthesisOpenSymbolSpreadsheetParserToken}
     */
    public static ParenthesisOpenSymbolSpreadsheetParserToken parenthesisOpenSymbol(final String value, final String text) {
        return ParenthesisOpenSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see PercentSymbolSpreadsheetParserToken}
     */
    public static PercentSymbolSpreadsheetParserToken percentSymbol(final String value, final String text) {
        return PercentSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see PlusSymbolSpreadsheetParserToken}
     */
    public static PlusSymbolSpreadsheetParserToken plusSymbol(final String value, final String text) {
        return PlusSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see PowerSpreadsheetParserToken}
     */
    public static PowerSpreadsheetParserToken power(final List<ParserToken> value, final String text) {
        return PowerSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see PowerSymbolSpreadsheetParserToken}
     */
    public static PowerSymbolSpreadsheetParserToken powerSymbol(final String value, final String text) {
        return PowerSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see RowReferenceSpreadsheetParserToken}
     */
    public static RowReferenceSpreadsheetParserToken rowReference(final SpreadsheetRowReference value, final String text) {
        return RowReferenceSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see SecondsSpreadsheetParserToken}
     */
    public static SecondsSpreadsheetParserToken seconds(final int value, final String text) {
        return SecondsSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see SubtractionSpreadsheetParserToken}
     */
    public static SubtractionSpreadsheetParserToken subtraction(final List<ParserToken> value, final String text) {
        return SubtractionSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see TextSpreadsheetParserToken}
     */
    public static TextSpreadsheetParserToken text(final List<ParserToken> value, final String text) {
        return TextSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see TextLiteralSpreadsheetParserToken}
     */
    public static TextLiteralSpreadsheetParserToken textLiteral(final String value, final String text) {
        return TextLiteralSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see TimeSpreadsheetParserToken}
     */
    public static TimeSpreadsheetParserToken time(final List<ParserToken> value, final String text) {
        return TimeSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see ValueSeparatorSymbolSpreadsheetParserToken}
     */
    public static ValueSeparatorSymbolSpreadsheetParserToken valueSeparatorSymbol(final String value, final String text) {
        return ValueSeparatorSymbolSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see WhitespaceSpreadsheetParserToken}
     */
    public static WhitespaceSpreadsheetParserToken whitespace(final String value, final String text) {
        return WhitespaceSpreadsheetParserToken.with(value, text);
    }

    /**
     * {@see YearSpreadsheetParserToken}
     */
    public static YearSpreadsheetParserToken year(final int value, final String text) {
        return YearSpreadsheetParserToken.with(value, text);
    }

    static List<ParserToken> copyAndCheckTokens(final List<ParserToken> tokens) {
        Objects.requireNonNull(tokens, "tokens");

        final List<ParserToken> copy = Lists.immutable(tokens);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Tokens is empty");
        }
        return copy;
    }

    static String checkText(final String text) {
        return CharSequences.failIfNullOrEmpty(text, "text");
    }

    /**
     * Package private ctor to limit sub classing.
     */
    SpreadsheetParserToken(final String text) {
        super();
        this.text = text;
    }

    @Override
    public final String text() {
        return this.text;
    }

    private final String text;

    /**
     * Value getter which may be a scalar or list of child tokens.
     */
    abstract Object value();

    // isXXX............................................................................................................

    /**
     * Only {@link AdditionSpreadsheetParserToken} return true
     */
    public final boolean isAddition() {
        return this instanceof AdditionSpreadsheetParserToken;
    }

    /**
     * Only {@link AmPmSpreadsheetParserToken} returns true
     */
    public final boolean isAmPm() {
        return this instanceof AmPmSpreadsheetParserToken;
    }

    /**
     * Only {@link ApostropheSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isApostropheSymbol() {
        return this instanceof ApostropheSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link ArithmeticSpreadsheetParserToken} return true
     */
    public final boolean isArithmetic() {
        return this instanceof ArithmeticSpreadsheetParserToken;
    }

    /**
     * Only {@link BetweenSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isBetweenSymbol() {
        return this instanceof BetweenSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link CellRangeSpreadsheetParserToken} return true
     */
    public final boolean isCellRange() {
        return this instanceof CellRangeSpreadsheetParserToken;
    }

    /**
     * Only {@link CellReferenceSpreadsheetParserToken} return true
     */
    public final boolean isCellReference() {
        return this instanceof CellReferenceSpreadsheetParserToken;
    }

    /**
     * Only {@link ColumnReferenceSpreadsheetParserToken} return true
     */
    public final boolean isColumnReference() {
        return this instanceof ColumnReferenceSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionSpreadsheetParserToken} return true
     */
    public final boolean isCondition() {
        return this instanceof ConditionSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionRightSpreadsheetParserToken} return true
     */
    public final boolean isConditionRight() {
        return this instanceof ConditionRightSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionRightEqualsSpreadsheetParserToken} return true
     */
    public final boolean isConditionRightEquals() {
        return this instanceof ConditionRightEqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionRightGreaterThanSpreadsheetParserToken} return true
     */
    public final boolean isConditionRightGreaterThan() {
        return this instanceof ConditionRightGreaterThanSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionRightGreaterThanEqualsSpreadsheetParserToken} return true
     */
    public final boolean isConditionRightGreaterThanEquals() {
        return this instanceof ConditionRightGreaterThanEqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionRightLessThanSpreadsheetParserToken} return true
     */
    public final boolean isConditionRightLessThan() {
        return this instanceof ConditionRightLessThanSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionRightLessThanEqualsSpreadsheetParserToken} return true
     */
    public final boolean isConditionRightLessThanEquals() {
        return this instanceof ConditionRightLessThanEqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link ConditionRightNotEqualsSpreadsheetParserToken} return true
     */
    public final boolean isConditionRightNotEquals() {
        return this instanceof ConditionRightNotEqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link CurrencySymbolSpreadsheetParserToken} returns true
     */
    public final boolean isCurrencySymbol() {
        return this instanceof CurrencySymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link DateSpreadsheetParserToken} return true
     */
    public final boolean isDate() {
        return this instanceof DateSpreadsheetParserToken;
    }

    /**
     * Only {@link DateTimeSpreadsheetParserToken} return true
     */
    public final boolean isDateTime() {
        return this instanceof DateTimeSpreadsheetParserToken;
    }

    /**
     * Only {@link DayNameSpreadsheetParserToken} returns true
     */
    public final boolean isDayName() {
        return this instanceof DayNameSpreadsheetParserToken;
    }

    /**
     * Only {@link DayNameAbbreviationSpreadsheetParserToken} returns true
     */
    public final boolean isDayNameAbbreviation() {
        return this instanceof DayNameAbbreviationSpreadsheetParserToken;
    }

    /**
     * Only {@link DayNumberSpreadsheetParserToken} returns true
     */
    public final boolean isDayNumber() {
        return this instanceof DayNumberSpreadsheetParserToken;
    }

    /**
     * Only {@link DecimalSeparatorSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isDecimalSeparatorSymbol() {
        return this instanceof DecimalSeparatorSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link DigitsSpreadsheetParserToken} returns true
     */
    public final boolean isDigits() {
        return this instanceof DigitsSpreadsheetParserToken;
    }

    /**
     * Only {@link DivideSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isDivideSymbol() {
        return this instanceof DivideSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link DivisionSpreadsheetParserToken} return true
     */
    public final boolean isDivision() {
        return this instanceof DivisionSpreadsheetParserToken;
    }

    /**
     * Only {@link DoubleQuoteSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isDoubleQuoteSymbol() {
        return this instanceof DoubleQuoteSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link EqualsSpreadsheetParserToken} returns true
     */
    public final boolean isEquals() {
        return this instanceof EqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link EqualsSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isEqualsSymbol() {
        return this instanceof EqualsSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link EqualsSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isError() {
        return this instanceof ErrorSpreadsheetParserToken;
    }

    /**
     * Only {@link ExponentSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isExponentSymbol() {
        return this instanceof ExponentSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link ExpressionSpreadsheetParserToken} return true
     */
    public final boolean isExpression() {
        return this instanceof ExpressionSpreadsheetParserToken;
    }

    /**
     * Only {@link FunctionSpreadsheetParserToken} returns true
     */
    public final boolean isFunction() {
        return this instanceof FunctionSpreadsheetParserToken;
    }

    /**
     * Only {@link FunctionNameSpreadsheetParserToken} returns true
     */
    public final boolean isFunctionName() {
        return this instanceof FunctionNameSpreadsheetParserToken;
    }

    /**
     * Only {@link FunctionParametersSpreadsheetParserToken} returns true
     */
    public final boolean isFunctionParameters() {
        return this instanceof FunctionParametersSpreadsheetParserToken;
    }

    /**
     * Only {@link GreaterThanSpreadsheetParserToken} returns true
     */
    public final boolean isGreaterThan() {
        return this instanceof GreaterThanSpreadsheetParserToken;
    }

    /**
     * Only {@link GreaterThanSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isGreaterThanSymbol() {
        return this instanceof GreaterThanSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link GreaterThanEqualsSpreadsheetParserToken} returns true
     */
    public final boolean isGreaterThanEquals() {
        return this instanceof GreaterThanEqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link GreaterThanEqualsSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isGreaterThanEqualsSymbol() {
        return this instanceof GreaterThanEqualsSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link GroupSpreadsheetParserToken} return true
     */
    public final boolean isGroup() {
        return this instanceof GroupSpreadsheetParserToken;
    }

    /**
     * Only {@link GroupSeparatorSymbolSpreadsheetParserToken} return true
     */
    public final boolean isGroupSeparatorSymbol() {
        return this instanceof GroupSeparatorSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link HourSpreadsheetParserToken} returns true
     */
    public final boolean isHour() {
        return this instanceof HourSpreadsheetParserToken;
    }

    /**
     * Only {@link LabelNameSpreadsheetParserToken} return true
     */
    public final boolean isLabelName() {
        return this instanceof LabelNameSpreadsheetParserToken;
    }

    /**
     * Only {@link LambdaFunctionSpreadsheetParserToken} returns true
     */
    public final boolean isLambdaFunction() {
        return this instanceof LambdaFunctionSpreadsheetParserToken;
    }

    /**
     * Returns true for sub-classes of {@link LeafSpreadsheetParserToken}.
     */
    @Override
    public final boolean isLeaf() {
        return this instanceof LeafSpreadsheetParserToken;
    }

    /**
     * Only {@link LessThanSpreadsheetParserToken} returns true
     */
    public final boolean isLessThan() {
        return this instanceof LessThanSpreadsheetParserToken;
    }

    /**
     * Only {@link LessThanSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isLessThanSymbol() {
        return this instanceof LessThanSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link LessThanEqualsSpreadsheetParserToken} returns true
     */
    public final boolean isLessThanEquals() {
        return this instanceof LessThanEqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link LessThanEqualsSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isLessThanEqualsSymbol() {
        return this instanceof LessThanEqualsSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link MinusSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isMinusSymbol() {
        return this instanceof MinusSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link MillisecondSpreadsheetParserToken} returns true
     */
    public final boolean isMillisecond() {
        return this instanceof MillisecondSpreadsheetParserToken;
    }

    /**
     * Only {@link MinuteSpreadsheetParserToken} returns true
     */
    public final boolean isMinute() {
        return this instanceof MinuteSpreadsheetParserToken;
    }

    /**
     * Only {@link MonthNameSpreadsheetParserToken} returns true
     */
    public final boolean isMonthName() {
        return this instanceof MonthNameSpreadsheetParserToken;
    }

    /**
     * Only {@link MonthNameAbbreviationSpreadsheetParserToken} returns true
     */
    public final boolean isMonthNameAbbreviation() {
        return this instanceof MonthNameAbbreviationSpreadsheetParserToken;
    }

    /**
     * Only {@link MonthNameInitialSpreadsheetParserToken} returns true
     */
    public final boolean isMonthNameInitial() {
        return this instanceof MonthNameInitialSpreadsheetParserToken;
    }

    /**
     * Only {@link MonthNumberSpreadsheetParserToken} returns true
     */
    public final boolean isMonthNumber() {
        return this instanceof MonthNumberSpreadsheetParserToken;
    }

    /**
     * Only {@link MultiplicationSpreadsheetParserToken} return true
     */
    public final boolean isMultiplication() {
        return this instanceof MultiplicationSpreadsheetParserToken;
    }

    /**
     * Only {@link MultiplySymbolSpreadsheetParserToken} returns true
     */
    public final boolean isMultiplySymbol() {
        return this instanceof MultiplySymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link NamedFunctionSpreadsheetParserToken} returns true
     */
    public final boolean isNamedFunction() {
        return this instanceof NamedFunctionSpreadsheetParserToken;
    }

    /**
     * Only {@link NegativeSpreadsheetParserToken} return true
     */
    public final boolean isNegative() {
        return this instanceof NegativeSpreadsheetParserToken;
    }

    /**
     * Only {@link SymbolSpreadsheetParserToken} return true
     */
    @Override
    public final boolean isNoise() {
        return this instanceof SymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link NotEqualsSpreadsheetParserToken} returns true
     */
    public final boolean isNotEquals() {
        return this instanceof NotEqualsSpreadsheetParserToken;
    }

    /**
     * Only {@link NotEqualsSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isNotEqualsSymbol() {
        return this instanceof NotEqualsSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link NumberSpreadsheetParserToken} return true
     */
    public final boolean isNumber() {
        return this instanceof NumberSpreadsheetParserToken;
    }

    /**
     * Returns true for sub-classes of {@link ParentSpreadsheetParserToken}.
     */
    @Override
    public final boolean isParent() {
        return this instanceof ParentSpreadsheetParserToken;
    }

    /**
     * Only {@link ParenthesisCloseSymbolSpreadsheetParserToken} return true
     */
    public final boolean isParenthesisCloseSymbol() {
        return this instanceof ParenthesisCloseSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link ParenthesisOpenSymbolSpreadsheetParserToken} return true
     */
    public final boolean isParenthesisOpenSymbol() {
        return this instanceof ParenthesisOpenSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link PercentSymbolSpreadsheetParserToken} return true
     */
    public final boolean isPercentSymbol() {
        return this instanceof PercentSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link PlusSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isPlusSymbol() {
        return this instanceof PlusSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link PowerSpreadsheetParserToken} return true
     */
    public final boolean isPower() {
        return this instanceof PowerSpreadsheetParserToken;
    }

    /**
     * Only {@link PowerSymbolSpreadsheetParserToken} return true
     */
    public final boolean isPowerSymbol() {
        return this instanceof PowerSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link RowReferenceSpreadsheetParserToken} return true
     */
    public final boolean isRowReference() {
        return this instanceof RowReferenceSpreadsheetParserToken;
    }

    /**
     * Only {@link SecondsSpreadsheetParserToken} returns true
     */
    public final boolean isSeconds() {
        return this instanceof SecondsSpreadsheetParserToken;
    }

    /**
     * Only {@link SubtractionSpreadsheetParserToken} return true
     */
    public final boolean isSubtraction() {
        return this instanceof SubtractionSpreadsheetParserToken;
    }

    /**
     * Only {@link SymbolSpreadsheetParserToken} return true
     */
    @Override
    public final boolean isSymbol() {
        return this instanceof SymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link TextSpreadsheetParserToken} return true
     */
    public final boolean isText() {
        return this instanceof TextSpreadsheetParserToken;
    }

    /**
     * Only {@link TextLiteralSpreadsheetParserToken} return true
     */
    public final boolean isTextLiteral() {
        return this instanceof TextLiteralSpreadsheetParserToken;
    }

    /**
     * Only {@link TimeSpreadsheetParserToken} return true
     */
    public final boolean isTime() {
        return this instanceof TimeSpreadsheetParserToken;
    }

    /**
     * Only {@link ValueSpreadsheetParserToken} return true
     */
    public final boolean isValue() {
        return this instanceof ValueSpreadsheetParserToken;
    }

    /**
     * Only {@link ValueSeparatorSymbolSpreadsheetParserToken} returns true
     */
    public final boolean isValueSeparatorSymbol() {
        return this instanceof ValueSeparatorSymbolSpreadsheetParserToken;
    }

    /**
     * Only {@link WhitespaceSpreadsheetParserToken} return true
     */
    @Override
    public final boolean isWhitespace() {
        return this instanceof WhitespaceSpreadsheetParserToken;
    }

    /**
     * Only {@link YearSpreadsheetParserToken} returns true
     */
    public final boolean isYear() {
        return this instanceof YearSpreadsheetParserToken;
    }

    /**
     * The priority of this token, tokens with a value of zero are left in their original position.
     */
    abstract int operatorPriority();

    final static int IGNORED = 0;

    final static int LOWEST_PRIORITY = IGNORED + 1;
    final static int GREATER_THAN_LESS_THAN_PRIORITY = LOWEST_PRIORITY + 1;
    final static int ADDITION_SUBTRACTION_PRIORITY = GREATER_THAN_LESS_THAN_PRIORITY + 1;
    final static int MULTIPLY_DIVISION_PRIORITY = ADDITION_SUBTRACTION_PRIORITY + 1;
    final static int POWER_PRIORITY = MULTIPLY_DIVISION_PRIORITY + 1;
    final static int RANGE_BETWEEN_PRIORITY = POWER_PRIORITY + 1;

    final static int HIGHEST_PRIORITY = RANGE_BETWEEN_PRIORITY;

    /**
     * Factory that creates the {@link BinarySpreadsheetParserToken} sub class using the provided tokens and text.
     */
    abstract SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text);

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    public final void accept(final ParserTokenVisitor visitor) {
        if (visitor instanceof SpreadsheetParserTokenVisitor) {
            final SpreadsheetParserTokenVisitor visitor2 = (SpreadsheetParserTokenVisitor) visitor;
            if (Visiting.CONTINUE == visitor2.startVisit(this)) {
                this.accept(visitor2);
            }
            visitor2.endVisit(this);
        }
    }

    abstract void accept(final SpreadsheetParserTokenVisitor visitor);

    // HasExpression................................................................................................

    /**
     * Converts this token to its {@link Expression} equivalent. Token sub-classes that represent a complete value
     * typically have a {@link Expression} equivalent, while those holding symbols or tokens such as a decimal-point
     * are not.
     */
    public final Optional<Expression> toExpression(final ExpressionEvaluationContext context) {
        return SpreadsheetParserTokenVisitorToExpression.toExpression(
                this,
                context
        );
    }

    // Object ...........................................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.text, this.value());
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                null != other && this.getClass() == other.getClass() && this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserToken other) {
        return this.text.equals(other.text) &&
                this.value().equals(other.value());
    }

    @Override
    public final String toString() {
        return this.text();
    }

    // json. ...........................................................................................................

    // NonSymbolSpreadsheetParserToken..................................................................................

    static {
        registerLeaf(
                AmPmSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallAmPm
        );

        registerLeaf(
                ColumnReferenceSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallColumnReference
        );

        registerLeaf(
                DayNameSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDayName
        );

        registerLeaf(
                DayNameAbbreviationSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDayNameAbbreviation
        );

        registerLeaf(
                DayNumberSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDayNumber
        );

        registerLeaf(
                DigitsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDigits
        );

        registerLeaf(
                ErrorSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallError
        );

        registerLeaf(
                FunctionNameSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallFunctionName
        );

        registerLeaf(
                HourSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallHour
        );

        registerLeaf(
                LabelNameSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallLabelName
        );

        registerLeaf(
                MillisecondSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMillisecond
        );

        registerLeaf(
                MinuteSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMinute
        );

        registerLeaf(
                MonthNameSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMonthName
        );

        registerLeaf(
                MonthNameAbbreviationSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMonthNameAbbreviation
        );

        registerLeaf(
                MonthNameInitialSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMonthNameInitial
        );

        registerLeaf(
                MonthNumberSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMonthNumber
        );

        registerLeaf(
                RowReferenceSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallRowReference
        );

        registerLeaf(
                SecondsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallSeconds
        );

        registerLeaf(
                TextLiteralSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallTextLiteral
        );

        registerLeaf(
                YearSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallYear
        );
    }

    static AmPmSpreadsheetParserToken unmarshallAmPm(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::amPm
        );
    }

    static ColumnReferenceSpreadsheetParserToken unmarshallColumnReference(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                SpreadsheetColumnReference.class,
                context,
                SpreadsheetParserToken::columnReference
        );
    }

    static DayNameSpreadsheetParserToken unmarshallDayName(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::dayName
        );
    }

    static DayNameAbbreviationSpreadsheetParserToken unmarshallDayNameAbbreviation(final JsonNode node,
                                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::dayNameAbbreviation
        );
    }

    static DayNumberSpreadsheetParserToken unmarshallDayNumber(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::dayNumber
        );
    }

    static DigitsSpreadsheetParserToken unmarshallDigits(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                String.class,
                context,
                SpreadsheetParserToken::digits
        );
    }

    static ErrorSpreadsheetParserToken unmarshallError(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                SpreadsheetError.class,
                context,
                SpreadsheetParserToken::error
        );
    }

    static FunctionNameSpreadsheetParserToken unmarshallFunctionName(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                SpreadsheetFunctionName.class,
                context,
                SpreadsheetParserToken::functionName
        );
    }

    static HourSpreadsheetParserToken unmarshallHour(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::hour
        );
    }

    static LabelNameSpreadsheetParserToken unmarshallLabelName(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                SpreadsheetLabelName.class,
                context,
                SpreadsheetParserToken::labelName
        );
    }

    static MillisecondSpreadsheetParserToken unmarshallMillisecond(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::millisecond
        );
    }

    static MinuteSpreadsheetParserToken unmarshallMinute(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::minute
        );
    }

    static MonthNameSpreadsheetParserToken unmarshallMonthName(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthName
        );
    }

    static MonthNameAbbreviationSpreadsheetParserToken unmarshallMonthNameAbbreviation(final JsonNode node,
                                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthNameAbbreviation
        );
    }

    static MonthNameInitialSpreadsheetParserToken unmarshallMonthNameInitial(final JsonNode node,
                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthNameInitial
        );
    }

    static MonthNumberSpreadsheetParserToken unmarshallMonthNumber(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthNumber
        );
    }

    static RowReferenceSpreadsheetParserToken unmarshallRowReference(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                SpreadsheetRowReference.class,
                context,
                SpreadsheetParserToken::rowReference
        );
    }

    static SecondsSpreadsheetParserToken unmarshallSeconds(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::seconds
        );
    }

    static TextLiteralSpreadsheetParserToken unmarshallTextLiteral(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                String.class,
                context,
                SpreadsheetParserToken::textLiteral
        );
    }

    static YearSpreadsheetParserToken unmarshallYear(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::year
        );
    }

    // SymbolSpreadsheetParserToken sub-classes.........................................................................

    static {
        registerLeaf(
                ApostropheSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallApostropheSymbol
        );

        registerLeaf(
                BetweenSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallBetweenSymbol
        );

        registerLeaf(
                CurrencySymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallCurrencySymbol
        );

        registerLeaf(
                DecimalSeparatorSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDecimalSeparatorSymbol
        );

        registerLeaf(
                DivideSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDivideSymbol
        );

        registerLeaf(
                DoubleQuoteSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDoubleQuoteSymbol
        );

        registerLeaf(
                EqualsSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallEqualsSymbol
        );

        registerLeaf(
                ExponentSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallExponentSymbol
        );

        registerLeaf(
                GreaterThanEqualsSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThanEqualsSymbol
        );

        registerLeaf(
                GreaterThanSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThanSymbol
        );

        registerLeaf(
                GroupSeparatorSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallGroupSeparatorSymbol
        );

        registerLeaf(
                LessThanEqualsSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallLessThanEqualsSymbol
        );

        registerLeaf(
                LessThanSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallLessThanSymbol
        );

        registerLeaf(
                MinusSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMinusSymbol
        );

        registerLeaf(
                MultiplySymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMultiplySymbol
        );

        registerLeaf(
                NotEqualsSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallNotEqualsSymbol
        );

        registerLeaf(
                ParenthesisCloseSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallParenthesisCloseSymbol
        );

        registerLeaf(
                ParenthesisOpenSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallParenthesisOpenSymbol
        );

        registerLeaf(
                PercentSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallPercentSymbol
        );

        registerLeaf(
                PlusSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallPlusSymbol
        );

        registerLeaf(
                PowerSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallPowerSymbol
        );

        registerLeaf(
                ValueSeparatorSymbolSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallValueSeparatorSymbol
        );

        registerLeaf(
                WhitespaceSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallWhitespace
        );
    }

    static ApostropheSymbolSpreadsheetParserToken unmarshallApostropheSymbol(final JsonNode node,
                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::apostropheSymbol
        );
    }

    static BetweenSymbolSpreadsheetParserToken unmarshallBetweenSymbol(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::betweenSymbol
        );
    }

    static CurrencySymbolSpreadsheetParserToken unmarshallCurrencySymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::currencySymbol
        );
    }

    static DecimalSeparatorSymbolSpreadsheetParserToken unmarshallDecimalSeparatorSymbol(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::decimalSeparatorSymbol
        );
    }

    static DivideSymbolSpreadsheetParserToken unmarshallDivideSymbol(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::divideSymbol
        );
    }

    static DoubleQuoteSymbolSpreadsheetParserToken unmarshallDoubleQuoteSymbol(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::doubleQuoteSymbol
        );
    }

    static EqualsSymbolSpreadsheetParserToken unmarshallEqualsSymbol(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::equalsSymbol
        );
    }

    static ExponentSymbolSpreadsheetParserToken unmarshallExponentSymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::exponentSymbol
        );
    }

    static GreaterThanEqualsSymbolSpreadsheetParserToken unmarshallGreaterThanEqualsSymbol(final JsonNode node,
                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::greaterThanEqualsSymbol
        );
    }


    static GreaterThanSymbolSpreadsheetParserToken unmarshallGreaterThanSymbol(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::greaterThanSymbol
        );
    }

    static GroupSeparatorSymbolSpreadsheetParserToken unmarshallGroupSeparatorSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::groupSeparatorSymbol
        );
    }

    static LessThanEqualsSymbolSpreadsheetParserToken unmarshallLessThanEqualsSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::lessThanEqualsSymbol
        );
    }

    static LessThanSymbolSpreadsheetParserToken unmarshallLessThanSymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::lessThanSymbol
        );
    }

    static MinusSymbolSpreadsheetParserToken unmarshallMinusSymbol(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::minusSymbol
        );
    }

    static MultiplySymbolSpreadsheetParserToken unmarshallMultiplySymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::multiplySymbol
        );
    }

    static NotEqualsSymbolSpreadsheetParserToken unmarshallNotEqualsSymbol(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::notEqualsSymbol
        );
    }

    static ParenthesisCloseSymbolSpreadsheetParserToken unmarshallParenthesisCloseSymbol(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::parenthesisCloseSymbol
        );
    }

    static ParenthesisOpenSymbolSpreadsheetParserToken unmarshallParenthesisOpenSymbol(final JsonNode node,
                                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::parenthesisOpenSymbol
        );
    }

    static PercentSymbolSpreadsheetParserToken unmarshallPercentSymbol(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::percentSymbol
        );
    }

    static PlusSymbolSpreadsheetParserToken unmarshallPlusSymbol(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::plusSymbol
        );
    }

    static PowerSymbolSpreadsheetParserToken unmarshallPowerSymbol(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::powerSymbol
        );
    }

    static ValueSeparatorSymbolSpreadsheetParserToken unmarshallValueSeparatorSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::valueSeparatorSymbol
        );
    }

    static WhitespaceSpreadsheetParserToken unmarshallWhitespace(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
                node,
                context,
                SpreadsheetParserToken::whitespace
        );
    }

    /**
     * Helper that knows how to unmarshall a sub class of {@link LeafSpreadsheetParserToken}
     */
    static <T extends SymbolSpreadsheetParserToken> T unmarshallSymbol(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context,
                                                                       final BiFunction<String, String, T> factory) {
        return unmarshallLeaf(
                node,
                String.class,
                context,
                factory
        );
    }

    /**
     * Helper that knows how to unmarshall a sub class of {@link SpreadsheetParserToken}
     */
    private static <V, T extends LeafSpreadsheetParserToken<V>> T unmarshallLeaf(final JsonNode node,
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

    private static <T extends LeafSpreadsheetParserToken<?>> void registerLeaf(final Class<T> type,
                                                                               final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
                type,
                from,
                SpreadsheetParserToken::marshallLeaf
        );
    }

    // ParentSpreadsheetParserToken.....................................................................................

    static {
        registerParent(
                AdditionSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallAddition
        );

        registerParent(
                CellReferenceSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallCellReference
        );

        registerParent(
                ConditionRightEqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightEquals
        );

        registerParent(
                ConditionRightGreaterThanSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightGreaterThan
        );

        registerParent(
                ConditionRightGreaterThanEqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightGreaterThanEquals
        );

        registerParent(
                ConditionRightLessThanSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightLessThan
        );

        registerParent(
                ConditionRightLessThanEqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightLessThanEquals
        );

        registerParent(
                ConditionRightNotEqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightNotEquals
        );

        registerParent(
                DateSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDate
        );

        registerParent(
                DateTimeSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDateTime
        );

        registerParent(
                DivisionSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallDivision
        );

        registerParent(
                EqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallEquals
        );

        registerParent(
                ExpressionSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallExpression
        );

        registerParent(
                FunctionParametersSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallFunctionParameters
        );

        registerParent(
                GreaterThanEqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThanEquals
        );

        registerParent(
                GreaterThanSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThan
        );

        registerParent(
                GroupSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallGroup
        );

        registerParent(
                LambdaFunctionSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallLambdaFunction
        );

        registerParent(
                LessThanEqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallLessThanEquals
        );

        registerParent(
                LessThanSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallLessThan
        );

        registerParent(
                MultiplicationSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallMultiplication
        );

        registerParent(
                NamedFunctionSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallNamedFunction
        );

        registerParent(
                NegativeSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallNegative
        );

        registerParent(
                NotEqualsSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallNotEquals
        );

        registerParent(
                NumberSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallNumber
        );

        registerParent(
                PowerSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallPower
        );

        registerParent(
                CellRangeSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallCellRange
        );

        registerParent(
                SubtractionSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallSubtraction
        );

        registerParent(
                TimeSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallTime
        );

        registerParent(
                TextSpreadsheetParserToken.class,
                SpreadsheetParserToken::unmarshallText
        );
    }

    static AdditionSpreadsheetParserToken unmarshallAddition(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::addition
        );
    }

    static CellReferenceSpreadsheetParserToken unmarshallCellReference(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::cellReference
        );
    }

    static CellRangeSpreadsheetParserToken unmarshallCellRange(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::cellRange
        );
    }

    static ConditionRightEqualsSpreadsheetParserToken unmarshallConditionRightEquals(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::conditionRightEquals
        );
    }

    static ConditionRightGreaterThanSpreadsheetParserToken unmarshallConditionRightGreaterThan(final JsonNode node,
                                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::conditionRightGreaterThan
        );
    }

    static ConditionRightGreaterThanEqualsSpreadsheetParserToken unmarshallConditionRightGreaterThanEquals(final JsonNode node,
                                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::conditionRightGreaterThanEquals
        );
    }

    static ConditionRightLessThanSpreadsheetParserToken unmarshallConditionRightLessThan(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::conditionRightLessThan
        );
    }

    static ConditionRightLessThanEqualsSpreadsheetParserToken unmarshallConditionRightLessThanEquals(final JsonNode node,
                                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::conditionRightLessThanEquals
        );
    }

    static ConditionRightNotEqualsSpreadsheetParserToken unmarshallConditionRightNotEquals(final JsonNode node,
                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::conditionRightNotEquals
        );
    }

    static DateSpreadsheetParserToken unmarshallDate(final JsonNode node,
                                                     final JsonNodeUnmarshallContext condate) {
        return unmarshallParent(
                node,
                condate,
                SpreadsheetParserToken::date
        );
    }

    static DateTimeSpreadsheetParserToken unmarshallDateTime(final JsonNode node,
                                                             final JsonNodeUnmarshallContext condateTime) {
        return unmarshallParent(
                node,
                condateTime,
                SpreadsheetParserToken::dateTime
        );
    }

    static DivisionSpreadsheetParserToken unmarshallDivision(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::division
        );
    }

    static EqualsSpreadsheetParserToken unmarshallEquals(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::equalsSpreadsheetParserToken
        );
    }

    static ExpressionSpreadsheetParserToken unmarshallExpression(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::expression
        );
    }

    static FunctionParametersSpreadsheetParserToken unmarshallFunctionParameters(final JsonNode node,
                                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::functionParameters
        );
    }

    static GreaterThanEqualsSpreadsheetParserToken unmarshallGreaterThanEquals(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::greaterThanEquals
        );
    }

    static GreaterThanSpreadsheetParserToken unmarshallGreaterThan(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::greaterThan
        );
    }

    static GroupSpreadsheetParserToken unmarshallGroup(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::group
        );
    }

    static LambdaFunctionSpreadsheetParserToken unmarshallLambdaFunction(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::lambdaFunction
        );
    }

    static LessThanEqualsSpreadsheetParserToken unmarshallLessThanEquals(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::lessThanEquals
        );
    }

    static LessThanSpreadsheetParserToken unmarshallLessThan(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::lessThan
        );
    }

    static MultiplicationSpreadsheetParserToken unmarshallMultiplication(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::multiplication
        );
    }

    static NamedFunctionSpreadsheetParserToken unmarshallNamedFunction(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::namedFunction
        );
    }

    static NegativeSpreadsheetParserToken unmarshallNegative(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::negative
        );
    }

    static NotEqualsSpreadsheetParserToken unmarshallNotEquals(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::notEquals
        );
    }

    static NumberSpreadsheetParserToken unmarshallNumber(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::number
        );
    }

    static PowerSpreadsheetParserToken unmarshallPower(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::power
        );
    }

    static SubtractionSpreadsheetParserToken unmarshallSubtraction(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::subtraction
        );
    }

    static TextSpreadsheetParserToken unmarshallText(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::text
        );
    }

    static TimeSpreadsheetParserToken unmarshallTime(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
                node,
                context,
                SpreadsheetParserToken::time
        );
    }

    /**
     * Helper that knows how to unmarshall a sub class of {@link ParentSpreadsheetParserToken}
     */
    private static <T extends ParentSpreadsheetParserToken> T unmarshallParent(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context,
                                                                               final BiFunction<List<ParserToken>, String, T> factory) {
        List<ParserToken> value = null;
        String text = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (name.value()) {
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithTypeList(child);
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
     * Handles marshalling any {@link LeafSpreadsheetParserToken}
     */
    private JsonNode marshallParent(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(VALUE_PROPERTY, context.marshallWithTypeCollection(Cast.to(this.value()))) // unnecessary to include type.
                .set(TEXT_PROPERTY, JsonNode.string(this.text()));
    }

    private static <T extends ParentSpreadsheetParserToken> void registerParent(final Class<T> type,
                                                                                final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
                type,
                from,
                SpreadsheetParserToken::marshallParent
        );
    }

    private static <T extends SpreadsheetParserToken> void register(final Class<T> type,
                                                                    final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from,
                                                                    final BiFunction<T, JsonNodeMarshallContext, JsonNode> to) {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(type),
                from,
                to,
                type
        );
    }

    private final static String VALUE_PROPERTY_STRING = "value";
    private final static String TEXT_PROPERTY_STRING = "text";

    // @VisibleForTesting

    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);
    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);


    /**
     * Handles marshalling any {@link LeafSpreadsheetParserToken}
     */
    private JsonNode marshallLeaf(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(VALUE_PROPERTY, context.marshall(this.value())) // unnecessary to include type.
                .set(TEXT_PROPERTY, JsonNode.string(this.text()));
    }
}
