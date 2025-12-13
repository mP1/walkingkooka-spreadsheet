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
package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.template.TemplateValueName;
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
public abstract class SpreadsheetFormulaParserToken implements ParserToken {

    /**
     * {@see AdditionSpreadsheetFormulaParserToken}
     */
    public static AdditionSpreadsheetFormulaParserToken addition(final List<ParserToken> value, final String text) {
        return AdditionSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see AmPmSpreadsheetFormulaParserToken}
     */
    public static AmPmSpreadsheetFormulaParserToken amPm(final int value, final String text) {
        return AmPmSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ApostropheSymbolSpreadsheetFormulaParserToken}
     */
    public static ApostropheSymbolSpreadsheetFormulaParserToken apostropheSymbol(final String value, final String text) {
        return ApostropheSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see BetweenSymbolSpreadsheetFormulaParserToken}
     */
    public static BetweenSymbolSpreadsheetFormulaParserToken betweenSymbol(final String value, final String text) {
        return BetweenSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see BooleanLiteralSpreadsheetFormulaParserToken}
     */
    public static BooleanLiteralSpreadsheetFormulaParserToken booleanLiteral(final boolean value,
                                                                             final String text) {
        return BooleanLiteralSpreadsheetFormulaParserToken.with(
            value,
            text
        );
    }

    /**
     * {@see BooleanLiteralSpreadsheetFormulaParserToken}
     */
    public static BooleanSpreadsheetFormulaParserToken booleanValue(final List<ParserToken> value,
                                                                    final String text) {
        return BooleanSpreadsheetFormulaParserToken.with(
            value,
            text
        );
    }

    /**
     * {@see CellRangeSpreadsheetFormulaParserToken}
     */
    public static CellRangeSpreadsheetFormulaParserToken cellRange(final List<ParserToken> value, final String text) {
        return CellRangeSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see CellSpreadsheetFormulaParserToken}
     */
    public static CellSpreadsheetFormulaParserToken cell(final List<ParserToken> value, final String text) {
        return CellSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ColumnSpreadsheetFormulaParserToken}
     */
    public static ColumnSpreadsheetFormulaParserToken column(final SpreadsheetColumnReference value, final String text) {
        return ColumnSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightEqualsSpreadsheetFormulaParserToken}
     */
    public static ConditionRightEqualsSpreadsheetFormulaParserToken conditionRightEquals(final List<ParserToken> value,
                                                                                         final String text) {
        return ConditionRightEqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightGreaterThanSpreadsheetFormulaParserToken}
     */
    public static ConditionRightGreaterThanSpreadsheetFormulaParserToken conditionRightGreaterThan(final List<ParserToken> value,
                                                                                                   final String text) {
        return ConditionRightGreaterThanSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken}
     */
    public static ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken conditionRightGreaterThanEquals(final List<ParserToken> value,
                                                                                                               final String text) {
        return ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightLessThanSpreadsheetFormulaParserToken}
     */
    public static ConditionRightLessThanSpreadsheetFormulaParserToken conditionRightLessThan(final List<ParserToken> value,
                                                                                             final String text) {
        return ConditionRightLessThanSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightLessThanEqualsSpreadsheetFormulaParserToken}
     */
    public static ConditionRightLessThanEqualsSpreadsheetFormulaParserToken conditionRightLessThanEquals(final List<ParserToken> value,
                                                                                                         final String text) {
        return ConditionRightLessThanEqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ConditionRightNotEqualsSpreadsheetFormulaParserToken}
     */
    public static ConditionRightNotEqualsSpreadsheetFormulaParserToken conditionRightNotEquals(final List<ParserToken> value,
                                                                                               final String text) {
        return ConditionRightNotEqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see CurrencySymbolSpreadsheetFormulaParserToken}
     */
    public static CurrencySymbolSpreadsheetFormulaParserToken currencySymbol(final String value, final String text) {
        return CurrencySymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DateSpreadsheetFormulaParserToken}
     */
    public static DateSpreadsheetFormulaParserToken date(final List<ParserToken> value, final String text) {
        return DateSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DateTimeSpreadsheetFormulaParserToken}
     */
    public static DateTimeSpreadsheetFormulaParserToken dateTime(final List<ParserToken> value, final String text) {
        return DateTimeSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DayNameSpreadsheetFormulaParserToken}
     */
    public static DayNameSpreadsheetFormulaParserToken dayName(final int value, final String text) {
        return DayNameSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DayNameAbbreviationSpreadsheetFormulaParserToken}
     */
    public static DayNameAbbreviationSpreadsheetFormulaParserToken dayNameAbbreviation(final int value, final String text) {
        return DayNameAbbreviationSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DayNumberSpreadsheetFormulaParserToken}
     */
    public static DayNumberSpreadsheetFormulaParserToken dayNumber(final int value, final String text) {
        return DayNumberSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DecimalSeparatorSymbolSpreadsheetFormulaParserToken}
     */
    public static DecimalSeparatorSymbolSpreadsheetFormulaParserToken decimalSeparatorSymbol(final String value, final String text) {
        return DecimalSeparatorSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DigitsSpreadsheetFormulaParserToken}
     */
    public static DigitsSpreadsheetFormulaParserToken digits(final String value, final String text) {
        return DigitsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DivideSymbolSpreadsheetFormulaParserToken}
     */
    public static DivideSymbolSpreadsheetFormulaParserToken divideSymbol(final String value, final String text) {
        return DivideSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DivisionSpreadsheetFormulaParserToken}
     */
    public static DivisionSpreadsheetFormulaParserToken division(final List<ParserToken> value, final String text) {
        return DivisionSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see DoubleQuoteSymbolSpreadsheetFormulaParserToken}
     */
    public static DoubleQuoteSymbolSpreadsheetFormulaParserToken doubleQuoteSymbol(final String value, final String text) {
        return DoubleQuoteSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see EqualsSpreadsheetFormulaParserToken}
     */
    public static EqualsSpreadsheetFormulaParserToken equalsSpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        return EqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see EqualsSymbolSpreadsheetFormulaParserToken}
     */
    public static EqualsSymbolSpreadsheetFormulaParserToken equalsSymbol(final String value, final String text) {
        return EqualsSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ErrorSpreadsheetFormulaParserToken}
     */
    public static ErrorSpreadsheetFormulaParserToken error(final SpreadsheetError value,
                                                           final String text) {
        return ErrorSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ExponentSymbolSpreadsheetFormulaParserToken}
     */
    public static ExponentSymbolSpreadsheetFormulaParserToken exponentSymbol(final String value, final String text) {
        return ExponentSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ExpressionSpreadsheetFormulaParserToken}
     */
    public static ExpressionSpreadsheetFormulaParserToken expression(final List<ParserToken> value, final String text) {
        return ExpressionSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see FunctionNameSpreadsheetFormulaParserToken}
     */
    public static FunctionNameSpreadsheetFormulaParserToken functionName(final SpreadsheetFunctionName value, final String text) {
        return FunctionNameSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see FunctionParametersSpreadsheetFormulaParserToken}
     */
    public static FunctionParametersSpreadsheetFormulaParserToken functionParameters(final List<ParserToken> value,
                                                                                     final String text) {
        return FunctionParametersSpreadsheetFormulaParserToken.with(
            value,
            text
        );
    }

    /**
     * {@see GreaterThanSpreadsheetFormulaParserToken}
     */
    public static GreaterThanSpreadsheetFormulaParserToken greaterThan(final List<ParserToken> value, final String text) {
        return GreaterThanSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanSymbolSpreadsheetFormulaParserToken}
     */
    public static GreaterThanSymbolSpreadsheetFormulaParserToken greaterThanSymbol(final String value, final String text) {
        return GreaterThanSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanEqualsSpreadsheetFormulaParserToken}
     */
    public static GreaterThanEqualsSpreadsheetFormulaParserToken greaterThanEquals(final List<ParserToken> value, final String text) {
        return GreaterThanEqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see GreaterThanEqualsSpreadsheetFormulaParserToken}
     */
    public static GreaterThanEqualsSymbolSpreadsheetFormulaParserToken greaterThanEqualsSymbol(final String value, final String text) {
        return GreaterThanEqualsSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see GroupSpreadsheetFormulaParserToken}
     */
    public static GroupSpreadsheetFormulaParserToken group(final List<ParserToken> value, final String text) {
        return GroupSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see GroupSeparatorSymbolSpreadsheetFormulaParserToken}
     */
    public static GroupSeparatorSymbolSpreadsheetFormulaParserToken groupSeparatorSymbol(final String value, final String text) {
        return GroupSeparatorSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see HourSpreadsheetFormulaParserToken}
     */
    public static HourSpreadsheetFormulaParserToken hour(final int value, final String text) {
        return HourSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see LabelSpreadsheetFormulaParserToken}
     */
    public static LabelSpreadsheetFormulaParserToken label(final SpreadsheetLabelName value, final String text) {
        return LabelSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see LambdaFunctionSpreadsheetFormulaParserToken}
     */
    public static LambdaFunctionSpreadsheetFormulaParserToken lambdaFunction(final List<ParserToken> value,
                                                                             final String text) {
        return LambdaFunctionSpreadsheetFormulaParserToken.with(
            value,
            text
        );
    }

    /**
     * {@see LessThanSpreadsheetFormulaParserToken}
     */
    public static LessThanSpreadsheetFormulaParserToken lessThan(final List<ParserToken> value, final String text) {
        return LessThanSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see LessThanSymbolSpreadsheetFormulaParserToken}
     */
    public static LessThanSymbolSpreadsheetFormulaParserToken lessThanSymbol(final String value, final String text) {
        return LessThanSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see LessThanEqualsSpreadsheetFormulaParserToken}
     */
    public static LessThanEqualsSpreadsheetFormulaParserToken lessThanEquals(final List<ParserToken> value, final String text) {
        return LessThanEqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see LessThanEqualsSpreadsheetFormulaParserToken}
     */
    public static LessThanEqualsSymbolSpreadsheetFormulaParserToken lessThanEqualsSymbol(final String value, final String text) {
        return LessThanEqualsSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MinusSymbolSpreadsheetFormulaParserToken}
     */
    public static MinusSymbolSpreadsheetFormulaParserToken minusSymbol(final String value, final String text) {
        return MinusSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MillisecondSpreadsheetFormulaParserToken}
     */
    public static MillisecondSpreadsheetFormulaParserToken millisecond(final int value, final String text) {
        return MillisecondSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MinuteSpreadsheetFormulaParserToken}
     */
    public static MinuteSpreadsheetFormulaParserToken minute(final int value, final String text) {
        return MinuteSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MonthNameSpreadsheetFormulaParserToken}
     */
    public static MonthNameSpreadsheetFormulaParserToken monthName(final int value, final String text) {
        return MonthNameSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MonthNameAbbreviationSpreadsheetFormulaParserToken}
     */
    public static MonthNameAbbreviationSpreadsheetFormulaParserToken monthNameAbbreviation(final int value, final String text) {
        return MonthNameAbbreviationSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MonthNameInitialSpreadsheetFormulaParserToken}
     */
    public static MonthNameInitialSpreadsheetFormulaParserToken monthNameInitial(final int value, final String text) {
        return MonthNameInitialSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MonthNumberSpreadsheetFormulaParserToken}
     */
    public static MonthNumberSpreadsheetFormulaParserToken monthNumber(final int value, final String text) {
        return MonthNumberSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MultiplicationSpreadsheetFormulaParserToken}
     */
    public static MultiplicationSpreadsheetFormulaParserToken multiplication(final List<ParserToken> value, final String text) {
        return MultiplicationSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see MultiplySymbolSpreadsheetFormulaParserToken}
     */
    public static MultiplySymbolSpreadsheetFormulaParserToken multiplySymbol(final String value, final String text) {
        return MultiplySymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see NamedFunctionSpreadsheetFormulaParserToken}
     */
    public static NamedFunctionSpreadsheetFormulaParserToken namedFunction(final List<ParserToken> value,
                                                                           final String text) {
        return NamedFunctionSpreadsheetFormulaParserToken.with(
            value,
            text
        );
    }

    /**
     * {@see NegativeSpreadsheetFormulaParserToken}
     */
    public static NegativeSpreadsheetFormulaParserToken negative(final List<ParserToken> value, final String text) {
        return NegativeSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see NotEqualsSpreadsheetFormulaParserToken}
     */
    public static NotEqualsSpreadsheetFormulaParserToken notEquals(final List<ParserToken> value, final String text) {
        return NotEqualsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see NotEqualsSymbolSpreadsheetFormulaParserToken}
     */
    public static NotEqualsSymbolSpreadsheetFormulaParserToken notEqualsSymbol(final String value, final String text) {
        return NotEqualsSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see NumberSpreadsheetFormulaParserToken}
     */
    public static NumberSpreadsheetFormulaParserToken number(final List<ParserToken> value, final String text) {
        return NumberSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ParenthesisCloseSymbolSpreadsheetFormulaParserToken}
     */
    public static ParenthesisCloseSymbolSpreadsheetFormulaParserToken parenthesisCloseSymbol(final String value, final String text) {
        return ParenthesisCloseSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ParenthesisOpenSymbolSpreadsheetFormulaParserToken}
     */
    public static ParenthesisOpenSymbolSpreadsheetFormulaParserToken parenthesisOpenSymbol(final String value, final String text) {
        return ParenthesisOpenSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see PercentSymbolSpreadsheetFormulaParserToken}
     */
    public static PercentSymbolSpreadsheetFormulaParserToken percentSymbol(final String value, final String text) {
        return PercentSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see PlusSymbolSpreadsheetFormulaParserToken}
     */
    public static PlusSymbolSpreadsheetFormulaParserToken plusSymbol(final String value, final String text) {
        return PlusSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see PowerSpreadsheetFormulaParserToken}
     */
    public static PowerSpreadsheetFormulaParserToken power(final List<ParserToken> value, final String text) {
        return PowerSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see PowerSymbolSpreadsheetFormulaParserToken}
     */
    public static PowerSymbolSpreadsheetFormulaParserToken powerSymbol(final String value, final String text) {
        return PowerSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see RowSpreadsheetFormulaParserToken}
     */
    public static RowSpreadsheetFormulaParserToken row(final SpreadsheetRowReference value, final String text) {
        return RowSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see SecondsSpreadsheetFormulaParserToken}
     */
    public static SecondsSpreadsheetFormulaParserToken seconds(final int value, final String text) {
        return SecondsSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see SubtractionSpreadsheetFormulaParserToken}
     */
    public static SubtractionSpreadsheetFormulaParserToken subtraction(final List<ParserToken> value, final String text) {
        return SubtractionSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see TemplateValueNameSpreadsheetFormulaParserToken}
     */
    public static TemplateValueNameSpreadsheetFormulaParserToken templateValueName(final TemplateValueName value,
                                                                                   final String text) {
        return TemplateValueNameSpreadsheetFormulaParserToken.with(
            value,
            text
        );
    }

    /**
     * {@see TextSpreadsheetFormulaParserToken}
     */
    public static TextSpreadsheetFormulaParserToken text(final List<ParserToken> value, final String text) {
        return TextSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see TextLiteralSpreadsheetFormulaParserToken}
     */
    public static TextLiteralSpreadsheetFormulaParserToken textLiteral(final String value, final String text) {
        return TextLiteralSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see TimeSpreadsheetFormulaParserToken}
     */
    public static TimeSpreadsheetFormulaParserToken time(final List<ParserToken> value, final String text) {
        return TimeSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see ValueSeparatorSymbolSpreadsheetFormulaParserToken}
     */
    public static ValueSeparatorSymbolSpreadsheetFormulaParserToken valueSeparatorSymbol(final String value, final String text) {
        return ValueSeparatorSymbolSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see WhitespaceSpreadsheetFormulaParserToken}
     */
    public static WhitespaceSpreadsheetFormulaParserToken whitespace(final String value, final String text) {
        return WhitespaceSpreadsheetFormulaParserToken.with(value, text);
    }

    /**
     * {@see YearSpreadsheetFormulaParserToken}
     */
    public static YearSpreadsheetFormulaParserToken year(final int value, final String text) {
        return YearSpreadsheetFormulaParserToken.with(value, text);
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
     * Package private ctor to limit subclassing.
     */
    SpreadsheetFormulaParserToken(final String text) {
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
     * Only {@link AdditionSpreadsheetFormulaParserToken} return true
     */
    public final boolean isAddition() {
        return this instanceof AdditionSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link AmPmSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isAmPm() {
        return this instanceof AmPmSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ApostropheSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isApostropheSymbol() {
        return this instanceof ApostropheSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ArithmeticSpreadsheetFormulaParserToken} return true
     */
    public final boolean isArithmetic() {
        return this instanceof ArithmeticSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link BetweenSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isBetweenSymbol() {
        return this instanceof BetweenSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link BooleanSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isBoolean() {
        return this instanceof BooleanSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link BooleanLiteralSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isBooleanLiteral() {
        return this instanceof BooleanLiteralSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link CellRangeSpreadsheetFormulaParserToken} return true
     */
    public final boolean isCellRange() {
        return this instanceof CellRangeSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link CellSpreadsheetFormulaParserToken} return true
     */
    public final boolean isCell() {
        return this instanceof CellSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ColumnSpreadsheetFormulaParserToken} return true
     */
    public final boolean isColumn() {
        return this instanceof ColumnSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionSpreadsheetFormulaParserToken} return true
     */
    public final boolean isCondition() {
        return this instanceof ConditionSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionRightSpreadsheetFormulaParserToken} return true
     */
    public final boolean isConditionRight() {
        return this instanceof ConditionRightSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionRightEqualsSpreadsheetFormulaParserToken} return true
     */
    public final boolean isConditionRightEquals() {
        return this instanceof ConditionRightEqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionRightGreaterThanSpreadsheetFormulaParserToken} return true
     */
    public final boolean isConditionRightGreaterThan() {
        return this instanceof ConditionRightGreaterThanSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken} return true
     */
    public final boolean isConditionRightGreaterThanEquals() {
        return this instanceof ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionRightLessThanSpreadsheetFormulaParserToken} return true
     */
    public final boolean isConditionRightLessThan() {
        return this instanceof ConditionRightLessThanSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionRightLessThanEqualsSpreadsheetFormulaParserToken} return true
     */
    public final boolean isConditionRightLessThanEquals() {
        return this instanceof ConditionRightLessThanEqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ConditionRightNotEqualsSpreadsheetFormulaParserToken} return true
     */
    public final boolean isConditionRightNotEquals() {
        return this instanceof ConditionRightNotEqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link CurrencySymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isCurrencySymbol() {
        return this instanceof CurrencySymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DateSpreadsheetFormulaParserToken} return true
     */
    public final boolean isDate() {
        return this instanceof DateSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DateTimeSpreadsheetFormulaParserToken} return true
     */
    public final boolean isDateTime() {
        return this instanceof DateTimeSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DayNameSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isDayName() {
        return this instanceof DayNameSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DayNameAbbreviationSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isDayNameAbbreviation() {
        return this instanceof DayNameAbbreviationSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DayNumberSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isDayNumber() {
        return this instanceof DayNumberSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DecimalSeparatorSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isDecimalSeparatorSymbol() {
        return this instanceof DecimalSeparatorSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DigitsSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isDigits() {
        return this instanceof DigitsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DivideSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isDivideSymbol() {
        return this instanceof DivideSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DivisionSpreadsheetFormulaParserToken} return true
     */
    public final boolean isDivision() {
        return this instanceof DivisionSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link DoubleQuoteSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isDoubleQuoteSymbol() {
        return this instanceof DoubleQuoteSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link EqualsSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isEquals() {
        return this instanceof EqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link EqualsSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isEqualsSymbol() {
        return this instanceof EqualsSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link EqualsSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isError() {
        return this instanceof ErrorSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ExponentSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isExponentSymbol() {
        return this instanceof ExponentSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ExpressionSpreadsheetFormulaParserToken} return true
     */
    public final boolean isExpression() {
        return this instanceof ExpressionSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link FunctionSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isFunction() {
        return this instanceof FunctionSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link FunctionNameSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isFunctionName() {
        return this instanceof FunctionNameSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link FunctionParametersSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isFunctionParameters() {
        return this instanceof FunctionParametersSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link GreaterThanSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isGreaterThan() {
        return this instanceof GreaterThanSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link GreaterThanSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isGreaterThanSymbol() {
        return this instanceof GreaterThanSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link GreaterThanEqualsSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isGreaterThanEquals() {
        return this instanceof GreaterThanEqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link GreaterThanEqualsSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isGreaterThanEqualsSymbol() {
        return this instanceof GreaterThanEqualsSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link GroupSpreadsheetFormulaParserToken} return true
     */
    public final boolean isGroup() {
        return this instanceof GroupSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link GroupSeparatorSymbolSpreadsheetFormulaParserToken} return true
     */
    public final boolean isGroupSeparatorSymbol() {
        return this instanceof GroupSeparatorSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link HourSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isHour() {
        return this instanceof HourSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link LabelSpreadsheetFormulaParserToken} return true
     */
    public final boolean isLabel() {
        return this instanceof LabelSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link LambdaFunctionSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isLambdaFunction() {
        return this instanceof LambdaFunctionSpreadsheetFormulaParserToken;
    }

    /**
     * Returns true for subclasses of {@link LeafSpreadsheetFormulaParserToken}.
     */
    @Override
    public final boolean isLeaf() {
        return this instanceof LeafSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link LessThanSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isLessThan() {
        return this instanceof LessThanSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link LessThanSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isLessThanSymbol() {
        return this instanceof LessThanSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link LessThanEqualsSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isLessThanEquals() {
        return this instanceof LessThanEqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link LessThanEqualsSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isLessThanEqualsSymbol() {
        return this instanceof LessThanEqualsSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MinusSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMinusSymbol() {
        return this instanceof MinusSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MillisecondSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMillisecond() {
        return this instanceof MillisecondSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MinuteSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMinute() {
        return this instanceof MinuteSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MonthNameSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMonthName() {
        return this instanceof MonthNameSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MonthNameAbbreviationSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMonthNameAbbreviation() {
        return this instanceof MonthNameAbbreviationSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MonthNameInitialSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMonthNameInitial() {
        return this instanceof MonthNameInitialSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MonthNumberSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMonthNumber() {
        return this instanceof MonthNumberSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MultiplicationSpreadsheetFormulaParserToken} return true
     */
    public final boolean isMultiplication() {
        return this instanceof MultiplicationSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link MultiplySymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isMultiplySymbol() {
        return this instanceof MultiplySymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link NamedFunctionSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isNamedFunction() {
        return this instanceof NamedFunctionSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link NegativeSpreadsheetFormulaParserToken} return true
     */
    public final boolean isNegative() {
        return this instanceof NegativeSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link SymbolSpreadsheetFormulaParserToken} return true
     */
    @Override
    public final boolean isNoise() {
        return this instanceof SymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link NotEqualsSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isNotEquals() {
        return this instanceof NotEqualsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link NotEqualsSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isNotEqualsSymbol() {
        return this instanceof NotEqualsSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link NumberSpreadsheetFormulaParserToken} return true
     */
    public final boolean isNumber() {
        return this instanceof NumberSpreadsheetFormulaParserToken;
    }

    /**
     * Returns true for subclasses of {@link ParentSpreadsheetFormulaParserToken}.
     */
    @Override
    public final boolean isParent() {
        return this instanceof ParentSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ParenthesisCloseSymbolSpreadsheetFormulaParserToken} return true
     */
    public final boolean isParenthesisCloseSymbol() {
        return this instanceof ParenthesisCloseSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ParenthesisOpenSymbolSpreadsheetFormulaParserToken} return true
     */
    public final boolean isParenthesisOpenSymbol() {
        return this instanceof ParenthesisOpenSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link PercentSymbolSpreadsheetFormulaParserToken} return true
     */
    public final boolean isPercentSymbol() {
        return this instanceof PercentSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link PlusSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isPlusSymbol() {
        return this instanceof PlusSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link PowerSpreadsheetFormulaParserToken} return true
     */
    public final boolean isPower() {
        return this instanceof PowerSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link PowerSymbolSpreadsheetFormulaParserToken} return true
     */
    public final boolean isPowerSymbol() {
        return this instanceof PowerSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link RowSpreadsheetFormulaParserToken} return true
     */
    public final boolean isRow() {
        return this instanceof RowSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link SecondsSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isSeconds() {
        return this instanceof SecondsSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link SubtractionSpreadsheetFormulaParserToken} return true
     */
    public final boolean isSubtraction() {
        return this instanceof SubtractionSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link SymbolSpreadsheetFormulaParserToken} return true
     */
    @Override
    public final boolean isSymbol() {
        return this instanceof SymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link TemplateValueNameSpreadsheetFormulaParserToken} return true
     */
    public final boolean isTemplateValueName() {
        return this instanceof TemplateValueNameSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link TextSpreadsheetFormulaParserToken} return true
     */
    public final boolean isText() {
        return this instanceof TextSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link TextLiteralSpreadsheetFormulaParserToken} return true
     */
    public final boolean isTextLiteral() {
        return this instanceof TextLiteralSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link TimeSpreadsheetFormulaParserToken} return true
     */
    public final boolean isTime() {
        return this instanceof TimeSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ValueSpreadsheetFormulaParserToken} return true
     */
    public final boolean isValue() {
        return this instanceof ValueSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link ValueSeparatorSymbolSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isValueSeparatorSymbol() {
        return this instanceof ValueSeparatorSymbolSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link WhitespaceSpreadsheetFormulaParserToken} return true
     */
    @Override
    public final boolean isWhitespace() {
        return this instanceof WhitespaceSpreadsheetFormulaParserToken;
    }

    /**
     * Only {@link YearSpreadsheetFormulaParserToken} returns true
     */
    public final boolean isYear() {
        return this instanceof YearSpreadsheetFormulaParserToken;
    }

    /**
     * The priority of this token, tokens with a value of zero are left in their original position.
     */
    abstract public int operatorPriority();

    final static int IGNORED = 0;

    public final static int LOWEST_PRIORITY = IGNORED + 1;
    final static int GREATER_THAN_LESS_THAN_PRIORITY = LOWEST_PRIORITY + 1;
    final static int ADDITION_SUBTRACTION_PRIORITY = GREATER_THAN_LESS_THAN_PRIORITY + 1;
    final static int MULTIPLY_DIVISION_PRIORITY = ADDITION_SUBTRACTION_PRIORITY + 1;
    final static int POWER_PRIORITY = MULTIPLY_DIVISION_PRIORITY + 1;
    final static int RANGE_BETWEEN_PRIORITY = POWER_PRIORITY + 1;

    public final static int HIGHEST_PRIORITY = RANGE_BETWEEN_PRIORITY;

    /**
     * Factory that creates the {@link BinarySpreadsheetFormulaParserToken} subclass using the provided tokens and text.
     */
    public abstract SpreadsheetFormulaParserToken binaryOperand(final List<ParserToken> tokens,
                                                                final String text);

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    public final void accept(final ParserTokenVisitor visitor) {
        if (visitor instanceof SpreadsheetFormulaParserTokenVisitor) {
            final SpreadsheetFormulaParserTokenVisitor visitor2 = (SpreadsheetFormulaParserTokenVisitor) visitor;
            if (Visiting.CONTINUE == visitor2.startVisit(this)) {
                this.accept(visitor2);
            }
            visitor2.endVisit(this);
        }
    }

    abstract void accept(final SpreadsheetFormulaParserTokenVisitor visitor);

    // HasExpression................................................................................................

    /**
     * Converts this token to its {@link Expression} equivalent. Token subclasses that represent a complete value
     * typically have a {@link Expression} equivalent, while those holding symbols or tokens such as a decimal-point
     * are not.
     */
    public final Optional<Expression> toExpression(final ExpressionEvaluationContext context) {
        return SpreadsheetFormulaParserTokenVisitorToExpression.toExpression(
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
    public final boolean equals(final Object other) {
        return this == other ||
            null != other && this.getClass() == other.getClass() && this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormulaParserToken other) {
        return this.text.equals(other.text) &&
            this.value().equals(other.value());
    }

    @Override
    public final String toString() {
        return this.text();
    }

    // json. ...........................................................................................................

    // NonSymbolSpreadsheetFormulaParserToken..................................................................................

    static {
        registerLeaf(
            AmPmSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallAmPm
        );

        registerLeaf(
            BooleanLiteralSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallBooleanLiteral
        );

        registerLeaf(
            ColumnSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallColumn
        );

        registerLeaf(
            DayNameSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDayName
        );

        registerLeaf(
            DayNameAbbreviationSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDayNameAbbreviation
        );

        registerLeaf(
            DayNumberSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDayNumber
        );

        registerLeaf(
            DigitsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDigits
        );

        registerLeaf(
            ErrorSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallError
        );

        registerLeaf(
            FunctionNameSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallFunctionName
        );

        registerLeaf(
            HourSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallHour
        );

        registerLeaf(
            LabelSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallLabel
        );

        registerLeaf(
            MillisecondSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMillisecond
        );

        registerLeaf(
            MinuteSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMinute
        );

        registerLeaf(
            MonthNameSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMonthName
        );

        registerLeaf(
            MonthNameAbbreviationSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMonthNameAbbreviation
        );

        registerLeaf(
            MonthNameInitialSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMonthNameInitial
        );

        registerLeaf(
            MonthNumberSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMonthNumber
        );

        registerLeaf(
            RowSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallRow
        );

        registerLeaf(
            SecondsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallSeconds
        );

        register(
            TemplateValueNameSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallTemplateValueName,
            SpreadsheetFormulaParserToken::marshallLeaf
        );

        registerLeaf(
            TextLiteralSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallTextLiteral
        );

        registerLeaf(
            YearSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallYear
        );
    }

    static AmPmSpreadsheetFormulaParserToken unmarshallAmPm(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::amPm
        );
    }

    static BooleanLiteralSpreadsheetFormulaParserToken unmarshallBooleanLiteral(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Boolean.class,
            context,
            SpreadsheetFormulaParserToken::booleanLiteral
        );
    }

    static ColumnSpreadsheetFormulaParserToken unmarshallColumn(final JsonNode node,
                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            SpreadsheetColumnReference.class,
            context,
            SpreadsheetFormulaParserToken::column
        );
    }

    static DayNameSpreadsheetFormulaParserToken unmarshallDayName(final JsonNode node,
                                                                  final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::dayName
        );
    }

    static DayNameAbbreviationSpreadsheetFormulaParserToken unmarshallDayNameAbbreviation(final JsonNode node,
                                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::dayNameAbbreviation
        );
    }

    static DayNumberSpreadsheetFormulaParserToken unmarshallDayNumber(final JsonNode node,
                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::dayNumber
        );
    }

    static DigitsSpreadsheetFormulaParserToken unmarshallDigits(final JsonNode node,
                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            String.class,
            context,
            SpreadsheetFormulaParserToken::digits
        );
    }

    static ErrorSpreadsheetFormulaParserToken unmarshallError(final JsonNode node,
                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            SpreadsheetError.class,
            context,
            SpreadsheetFormulaParserToken::error
        );
    }

    static FunctionNameSpreadsheetFormulaParserToken unmarshallFunctionName(final JsonNode node,
                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            SpreadsheetFunctionName.class,
            context,
            SpreadsheetFormulaParserToken::functionName
        );
    }

    static HourSpreadsheetFormulaParserToken unmarshallHour(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::hour
        );
    }

    static LabelSpreadsheetFormulaParserToken unmarshallLabel(final JsonNode node,
                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            SpreadsheetLabelName.class,
            context,
            SpreadsheetFormulaParserToken::label
        );
    }

    static MillisecondSpreadsheetFormulaParserToken unmarshallMillisecond(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::millisecond
        );
    }

    static MinuteSpreadsheetFormulaParserToken unmarshallMinute(final JsonNode node,
                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::minute
        );
    }

    static MonthNameSpreadsheetFormulaParserToken unmarshallMonthName(final JsonNode node,
                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::monthName
        );
    }

    static MonthNameAbbreviationSpreadsheetFormulaParserToken unmarshallMonthNameAbbreviation(final JsonNode node,
                                                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::monthNameAbbreviation
        );
    }

    static MonthNameInitialSpreadsheetFormulaParserToken unmarshallMonthNameInitial(final JsonNode node,
                                                                                    final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::monthNameInitial
        );
    }

    static MonthNumberSpreadsheetFormulaParserToken unmarshallMonthNumber(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::monthNumber
        );
    }

    static RowSpreadsheetFormulaParserToken unmarshallRow(final JsonNode node,
                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            SpreadsheetRowReference.class,
            context,
            SpreadsheetFormulaParserToken::row
        );
    }

    static SecondsSpreadsheetFormulaParserToken unmarshallSeconds(final JsonNode node,
                                                                  final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::seconds
        );
    }

    static TemplateValueNameSpreadsheetFormulaParserToken unmarshallTemplateValueName(final JsonNode node,
                                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            TemplateValueName.class,
            context,
            SpreadsheetFormulaParserToken::templateValueName
        );
    }

    static TextLiteralSpreadsheetFormulaParserToken unmarshallTextLiteral(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            String.class,
            context,
            SpreadsheetFormulaParserToken::textLiteral
        );
    }

    static YearSpreadsheetFormulaParserToken unmarshallYear(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallLeaf(
            node,
            Integer.class,
            context,
            SpreadsheetFormulaParserToken::year
        );
    }

    // SymbolSpreadsheetFormulaParserToken subclasses.........................................................................

    static {
        registerLeaf(
            ApostropheSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallApostropheSymbol
        );

        registerLeaf(
            BetweenSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallBetweenSymbol
        );

        registerLeaf(
            CurrencySymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallCurrencySymbol
        );

        registerLeaf(
            DecimalSeparatorSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDecimalSeparatorSymbol
        );

        registerLeaf(
            DivideSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDivideSymbol
        );

        registerLeaf(
            DoubleQuoteSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDoubleQuoteSymbol
        );

        registerLeaf(
            EqualsSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallEqualsSymbol
        );

        registerLeaf(
            ExponentSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallExponentSymbol
        );

        registerLeaf(
            GreaterThanEqualsSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallGreaterThanEqualsSymbol
        );

        registerLeaf(
            GreaterThanSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallGreaterThanSymbol
        );

        registerLeaf(
            GroupSeparatorSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallGroupSeparatorSymbol
        );

        registerLeaf(
            LessThanEqualsSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallLessThanEqualsSymbol
        );

        registerLeaf(
            LessThanSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallLessThanSymbol
        );

        registerLeaf(
            MinusSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMinusSymbol
        );

        registerLeaf(
            MultiplySymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMultiplySymbol
        );

        registerLeaf(
            NotEqualsSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallNotEqualsSymbol
        );

        registerLeaf(
            ParenthesisCloseSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallParenthesisCloseSymbol
        );

        registerLeaf(
            ParenthesisOpenSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallParenthesisOpenSymbol
        );

        registerLeaf(
            PercentSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallPercentSymbol
        );

        registerLeaf(
            PlusSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallPlusSymbol
        );

        registerLeaf(
            PowerSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallPowerSymbol
        );

        registerLeaf(
            ValueSeparatorSymbolSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallValueSeparatorSymbol
        );

        registerLeaf(
            WhitespaceSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallWhitespace
        );
    }

    static ApostropheSymbolSpreadsheetFormulaParserToken unmarshallApostropheSymbol(final JsonNode node,
                                                                                    final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::apostropheSymbol
        );
    }

    static BetweenSymbolSpreadsheetFormulaParserToken unmarshallBetweenSymbol(final JsonNode node,
                                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::betweenSymbol
        );
    }

    static CurrencySymbolSpreadsheetFormulaParserToken unmarshallCurrencySymbol(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::currencySymbol
        );
    }

    static DecimalSeparatorSymbolSpreadsheetFormulaParserToken unmarshallDecimalSeparatorSymbol(final JsonNode node,
                                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::decimalSeparatorSymbol
        );
    }

    static DivideSymbolSpreadsheetFormulaParserToken unmarshallDivideSymbol(final JsonNode node,
                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::divideSymbol
        );
    }

    static DoubleQuoteSymbolSpreadsheetFormulaParserToken unmarshallDoubleQuoteSymbol(final JsonNode node,
                                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::doubleQuoteSymbol
        );
    }

    static EqualsSymbolSpreadsheetFormulaParserToken unmarshallEqualsSymbol(final JsonNode node,
                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::equalsSymbol
        );
    }

    static ExponentSymbolSpreadsheetFormulaParserToken unmarshallExponentSymbol(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::exponentSymbol
        );
    }

    static GreaterThanEqualsSymbolSpreadsheetFormulaParserToken unmarshallGreaterThanEqualsSymbol(final JsonNode node,
                                                                                                  final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::greaterThanEqualsSymbol
        );
    }


    static GreaterThanSymbolSpreadsheetFormulaParserToken unmarshallGreaterThanSymbol(final JsonNode node,
                                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::greaterThanSymbol
        );
    }

    static GroupSeparatorSymbolSpreadsheetFormulaParserToken unmarshallGroupSeparatorSymbol(final JsonNode node,
                                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::groupSeparatorSymbol
        );
    }

    static LessThanEqualsSymbolSpreadsheetFormulaParserToken unmarshallLessThanEqualsSymbol(final JsonNode node,
                                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::lessThanEqualsSymbol
        );
    }

    static LessThanSymbolSpreadsheetFormulaParserToken unmarshallLessThanSymbol(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::lessThanSymbol
        );
    }

    static MinusSymbolSpreadsheetFormulaParserToken unmarshallMinusSymbol(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::minusSymbol
        );
    }

    static MultiplySymbolSpreadsheetFormulaParserToken unmarshallMultiplySymbol(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::multiplySymbol
        );
    }

    static NotEqualsSymbolSpreadsheetFormulaParserToken unmarshallNotEqualsSymbol(final JsonNode node,
                                                                                  final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::notEqualsSymbol
        );
    }

    static ParenthesisCloseSymbolSpreadsheetFormulaParserToken unmarshallParenthesisCloseSymbol(final JsonNode node,
                                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::parenthesisCloseSymbol
        );
    }

    static ParenthesisOpenSymbolSpreadsheetFormulaParserToken unmarshallParenthesisOpenSymbol(final JsonNode node,
                                                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::parenthesisOpenSymbol
        );
    }

    static PercentSymbolSpreadsheetFormulaParserToken unmarshallPercentSymbol(final JsonNode node,
                                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::percentSymbol
        );
    }

    static PlusSymbolSpreadsheetFormulaParserToken unmarshallPlusSymbol(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::plusSymbol
        );
    }

    static PowerSymbolSpreadsheetFormulaParserToken unmarshallPowerSymbol(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::powerSymbol
        );
    }

    static ValueSeparatorSymbolSpreadsheetFormulaParserToken unmarshallValueSeparatorSymbol(final JsonNode node,
                                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::valueSeparatorSymbol
        );
    }

    static WhitespaceSpreadsheetFormulaParserToken unmarshallWhitespace(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return unmarshallSymbol(
            node,
            context,
            SpreadsheetFormulaParserToken::whitespace
        );
    }

    /**
     * Helper that knows how to unmarshall a subclass of {@link LeafSpreadsheetFormulaParserToken}
     */
    static <T extends SymbolSpreadsheetFormulaParserToken> T unmarshallSymbol(final JsonNode node,
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
     * Helper that knows how to unmarshall a subclass of {@link SpreadsheetFormulaParserToken}
     */
    private static <V, T extends LeafSpreadsheetFormulaParserToken<V>> T unmarshallLeaf(final JsonNode node,
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

    private static <T extends LeafSpreadsheetFormulaParserToken<?>> void registerLeaf(final Class<T> type,
                                                                                      final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
            type,
            from,
            SpreadsheetFormulaParserToken::marshallLeaf
        );
    }

    // ParentSpreadsheetFormulaParserToken.....................................................................................

    static {
        registerParent(
            AdditionSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallAddition
        );

        registerParent(
            BooleanSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallBoolean
        );

        registerParent(
            CellSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallCell
        );

        registerParent(
            ConditionRightEqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallConditionRightEquals
        );

        registerParent(
            ConditionRightGreaterThanSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallConditionRightGreaterThan
        );

        registerParent(
            ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallConditionRightGreaterThanEquals
        );

        registerParent(
            ConditionRightLessThanSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallConditionRightLessThan
        );

        registerParent(
            ConditionRightLessThanEqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallConditionRightLessThanEquals
        );

        registerParent(
            ConditionRightNotEqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallConditionRightNotEquals
        );

        registerParent(
            DateSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDate
        );

        registerParent(
            DateTimeSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDateTime
        );

        registerParent(
            DivisionSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallDivision
        );

        registerParent(
            EqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallEquals
        );

        registerParent(
            ExpressionSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallExpression
        );

        registerParent(
            FunctionParametersSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallFunctionParameters
        );

        registerParent(
            GreaterThanEqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallGreaterThanEquals
        );

        registerParent(
            GreaterThanSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallGreaterThan
        );

        registerParent(
            GroupSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallGroup
        );

        registerParent(
            LambdaFunctionSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallLambdaFunction
        );

        registerParent(
            LessThanEqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallLessThanEquals
        );

        registerParent(
            LessThanSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallLessThan
        );

        registerParent(
            MultiplicationSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallMultiplication
        );

        registerParent(
            NamedFunctionSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallNamedFunction
        );

        registerParent(
            NegativeSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallNegative
        );

        registerParent(
            NotEqualsSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallNotEquals
        );

        registerParent(
            NumberSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallNumber
        );

        registerParent(
            PowerSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallPower
        );

        registerParent(
            CellRangeSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallCellRange
        );

        registerParent(
            SubtractionSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallSubtraction
        );

        registerParent(
            TimeSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallTime
        );

        registerParent(
            TextSpreadsheetFormulaParserToken.class,
            SpreadsheetFormulaParserToken::unmarshallText
        );
    }

    static AdditionSpreadsheetFormulaParserToken unmarshallAddition(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::addition
        );
    }

    static BooleanSpreadsheetFormulaParserToken unmarshallBoolean(final JsonNode node,
                                                                  final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::booleanValue
        );
    }

    static CellSpreadsheetFormulaParserToken unmarshallCell(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::cell
        );
    }

    static CellRangeSpreadsheetFormulaParserToken unmarshallCellRange(final JsonNode node,
                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::cellRange
        );
    }

    static ConditionRightEqualsSpreadsheetFormulaParserToken unmarshallConditionRightEquals(final JsonNode node,
                                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::conditionRightEquals
        );
    }

    static ConditionRightGreaterThanSpreadsheetFormulaParserToken unmarshallConditionRightGreaterThan(final JsonNode node,
                                                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::conditionRightGreaterThan
        );
    }

    static ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken unmarshallConditionRightGreaterThanEquals(final JsonNode node,
                                                                                                                  final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::conditionRightGreaterThanEquals
        );
    }

    static ConditionRightLessThanSpreadsheetFormulaParserToken unmarshallConditionRightLessThan(final JsonNode node,
                                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::conditionRightLessThan
        );
    }

    static ConditionRightLessThanEqualsSpreadsheetFormulaParserToken unmarshallConditionRightLessThanEquals(final JsonNode node,
                                                                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::conditionRightLessThanEquals
        );
    }

    static ConditionRightNotEqualsSpreadsheetFormulaParserToken unmarshallConditionRightNotEquals(final JsonNode node,
                                                                                                  final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::conditionRightNotEquals
        );
    }

    static DateSpreadsheetFormulaParserToken unmarshallDate(final JsonNode node,
                                                            final JsonNodeUnmarshallContext condate) {
        return unmarshallParent(
            node,
            condate,
            SpreadsheetFormulaParserToken::date
        );
    }

    static DateTimeSpreadsheetFormulaParserToken unmarshallDateTime(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext condateTime) {
        return unmarshallParent(
            node,
            condateTime,
            SpreadsheetFormulaParserToken::dateTime
        );
    }

    static DivisionSpreadsheetFormulaParserToken unmarshallDivision(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::division
        );
    }

    static EqualsSpreadsheetFormulaParserToken unmarshallEquals(final JsonNode node,
                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::equalsSpreadsheetFormulaParserToken
        );
    }

    static ExpressionSpreadsheetFormulaParserToken unmarshallExpression(final JsonNode node,
                                                                        final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::expression
        );
    }

    static FunctionParametersSpreadsheetFormulaParserToken unmarshallFunctionParameters(final JsonNode node,
                                                                                        final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::functionParameters
        );
    }

    static GreaterThanEqualsSpreadsheetFormulaParserToken unmarshallGreaterThanEquals(final JsonNode node,
                                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::greaterThanEquals
        );
    }

    static GreaterThanSpreadsheetFormulaParserToken unmarshallGreaterThan(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::greaterThan
        );
    }

    static GroupSpreadsheetFormulaParserToken unmarshallGroup(final JsonNode node,
                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::group
        );
    }

    static LambdaFunctionSpreadsheetFormulaParserToken unmarshallLambdaFunction(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::lambdaFunction
        );
    }

    static LessThanEqualsSpreadsheetFormulaParserToken unmarshallLessThanEquals(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::lessThanEquals
        );
    }

    static LessThanSpreadsheetFormulaParserToken unmarshallLessThan(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::lessThan
        );
    }

    static MultiplicationSpreadsheetFormulaParserToken unmarshallMultiplication(final JsonNode node,
                                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::multiplication
        );
    }

    static NamedFunctionSpreadsheetFormulaParserToken unmarshallNamedFunction(final JsonNode node,
                                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::namedFunction
        );
    }

    static NegativeSpreadsheetFormulaParserToken unmarshallNegative(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::negative
        );
    }

    static NotEqualsSpreadsheetFormulaParserToken unmarshallNotEquals(final JsonNode node,
                                                                      final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::notEquals
        );
    }

    static NumberSpreadsheetFormulaParserToken unmarshallNumber(final JsonNode node,
                                                                final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::number
        );
    }

    static PowerSpreadsheetFormulaParserToken unmarshallPower(final JsonNode node,
                                                              final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::power
        );
    }

    static SubtractionSpreadsheetFormulaParserToken unmarshallSubtraction(final JsonNode node,
                                                                          final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::subtraction
        );
    }

    static TextSpreadsheetFormulaParserToken unmarshallText(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::text
        );
    }

    static TimeSpreadsheetFormulaParserToken unmarshallTime(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return unmarshallParent(
            node,
            context,
            SpreadsheetFormulaParserToken::time
        );
    }

    /**
     * Helper that knows how to unmarshall a subclass of {@link ParentSpreadsheetFormulaParserToken}
     */
    private static <T extends ParentSpreadsheetFormulaParserToken> T unmarshallParent(final JsonNode node,
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
     * Handles marshalling any {@link LeafSpreadsheetFormulaParserToken}
     */
    private JsonNode marshallParent(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(
                VALUE_PROPERTY,
                context.marshallCollectionWithType(
                    Cast.to(this.value())
                )
            ) // unnecessary to include type.
            .set(
                TEXT_PROPERTY,
                this.text()
            );
    }

    private static <T extends ParentSpreadsheetFormulaParserToken> void registerParent(final Class<T> type,
                                                                                       final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
            type,
            from,
            SpreadsheetFormulaParserToken::marshallParent
        );
    }

    private static <T extends SpreadsheetFormulaParserToken> void register(final Class<T> type,
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
     * Handles marshalling any {@link LeafSpreadsheetFormulaParserToken}
     */
    private JsonNode marshallLeaf(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(VALUE_PROPERTY, context.marshall(this.value())) // unnecessary to include type.
            .set(TEXT_PROPERTY, JsonNode.string(this.text()));
    }
}
