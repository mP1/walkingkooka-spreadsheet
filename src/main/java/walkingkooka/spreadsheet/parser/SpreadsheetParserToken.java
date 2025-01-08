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
     * {@see SpreadsheetAdditionParserToken}
     */
    public static SpreadsheetAdditionParserToken addition(final List<ParserToken> value, final String text) {
        return SpreadsheetAdditionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetAmPmParserToken}
     */
    public static SpreadsheetAmPmParserToken amPm(final int value, final String text) {
        return SpreadsheetAmPmParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetApostropheSymbolParserToken}
     */
    public static SpreadsheetApostropheSymbolParserToken apostropheSymbol(final String value, final String text) {
        return SpreadsheetApostropheSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetBetweenSymbolParserToken}
     */
    public static SpreadsheetBetweenSymbolParserToken betweenSymbol(final String value, final String text) {
        return SpreadsheetBetweenSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetCellRangeParserToken}
     */
    public static SpreadsheetCellRangeParserToken cellRange(final List<ParserToken> value, final String text) {
        return SpreadsheetCellRangeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetCellReferenceParserToken}
     */
    public static SpreadsheetCellReferenceParserToken cellReference(final List<ParserToken> value, final String text) {
        return SpreadsheetCellReferenceParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetColumnReferenceParserToken}
     */
    public static SpreadsheetColumnReferenceParserToken columnReference(final SpreadsheetColumnReference value, final String text) {
        return SpreadsheetColumnReferenceParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetConditionRightEqualsParserToken}
     */
    public static SpreadsheetConditionRightEqualsParserToken conditionRightEquals(final List<ParserToken> value,
                                                                                  final String text) {
        return SpreadsheetConditionRightEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetConditionRightGreaterThanParserToken}
     */
    public static SpreadsheetConditionRightGreaterThanParserToken conditionRightGreaterThan(final List<ParserToken> value,
                                                                                            final String text) {
        return SpreadsheetConditionRightGreaterThanParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetConditionRightGreaterThanEqualsParserToken}
     */
    public static SpreadsheetConditionRightGreaterThanEqualsParserToken conditionRightGreaterThanEquals(final List<ParserToken> value,
                                                                                                        final String text) {
        return SpreadsheetConditionRightGreaterThanEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetConditionRightLessThanParserToken}
     */
    public static SpreadsheetConditionRightLessThanParserToken conditionRightLessThan(final List<ParserToken> value,
                                                                                      final String text) {
        return SpreadsheetConditionRightLessThanParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetConditionRightLessThanEqualsParserToken}
     */
    public static SpreadsheetConditionRightLessThanEqualsParserToken conditionRightLessThanEquals(final List<ParserToken> value,
                                                                                                  final String text) {
        return SpreadsheetConditionRightLessThanEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetConditionRightNotEqualsParserToken}
     */
    public static SpreadsheetConditionRightNotEqualsParserToken conditionRightNotEquals(final List<ParserToken> value,
                                                                                        final String text) {
        return SpreadsheetConditionRightNotEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetCurrencySymbolParserToken}
     */
    public static SpreadsheetCurrencySymbolParserToken currencySymbol(final String value, final String text) {
        return SpreadsheetCurrencySymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDateParserToken}
     */
    public static SpreadsheetDateParserToken date(final List<ParserToken> value, final String text) {
        return SpreadsheetDateParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDateTimeParserToken}
     */
    public static SpreadsheetDateTimeParserToken dateTime(final List<ParserToken> value, final String text) {
        return SpreadsheetDateTimeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDayNameParserToken}
     */
    public static SpreadsheetDayNameParserToken dayName(final int value, final String text) {
        return SpreadsheetDayNameParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDayNameAbbreviationParserToken}
     */
    public static SpreadsheetDayNameAbbreviationParserToken dayNameAbbreviation(final int value, final String text) {
        return SpreadsheetDayNameAbbreviationParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDayNumberParserToken}
     */
    public static SpreadsheetDayNumberParserToken dayNumber(final int value, final String text) {
        return SpreadsheetDayNumberParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDecimalSeparatorSymbolParserToken}
     */
    public static SpreadsheetDecimalSeparatorSymbolParserToken decimalSeparatorSymbol(final String value, final String text) {
        return SpreadsheetDecimalSeparatorSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDigitsParserToken}
     */
    public static SpreadsheetDigitsParserToken digits(final String value, final String text) {
        return SpreadsheetDigitsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDivideSymbolParserToken}
     */
    public static SpreadsheetDivideSymbolParserToken divideSymbol(final String value, final String text) {
        return SpreadsheetDivideSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDivisionParserToken}
     */
    public static SpreadsheetDivisionParserToken division(final List<ParserToken> value, final String text) {
        return SpreadsheetDivisionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetDoubleQuoteSymbolParserToken}
     */
    public static SpreadsheetDoubleQuoteSymbolParserToken doubleQuoteSymbol(final String value, final String text) {
        return SpreadsheetDoubleQuoteSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetEqualsParserToken}
     */
    public static SpreadsheetEqualsParserToken equalsParserToken(final List<ParserToken> value, final String text) {
        return SpreadsheetEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetEqualsSymbolParserToken}
     */
    public static SpreadsheetEqualsSymbolParserToken equalsSymbol(final String value, final String text) {
        return SpreadsheetEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetErrorParserToken}
     */
    public static SpreadsheetErrorParserToken error(final SpreadsheetError value,
                                                    final String text) {
        return SpreadsheetErrorParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetExponentSymbolParserToken}
     */
    public static SpreadsheetExponentSymbolParserToken exponentSymbol(final String value, final String text) {
        return SpreadsheetExponentSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetExpressionParserToken}
     */
    public static SpreadsheetExpressionParserToken expression(final List<ParserToken> value, final String text) {
        return SpreadsheetExpressionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFunctionNameParserToken}
     */
    public static SpreadsheetFunctionNameParserToken functionName(final SpreadsheetFunctionName value, final String text) {
        return SpreadsheetFunctionNameParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFunctionParametersParserToken}
     */
    public static SpreadsheetFunctionParametersParserToken functionParameters(final List<ParserToken> value,
                                                                              final String text) {
        return SpreadsheetFunctionParametersParserToken.with(
                value,
                text
        );
    }

    /**
     * {@see SpreadsheetGreaterThanParserToken}
     */
    public static SpreadsheetGreaterThanParserToken greaterThan(final List<ParserToken> value, final String text) {
        return SpreadsheetGreaterThanParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetGreaterThanSymbolParserToken}
     */
    public static SpreadsheetGreaterThanSymbolParserToken greaterThanSymbol(final String value, final String text) {
        return SpreadsheetGreaterThanSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetGreaterThanEqualsParserToken}
     */
    public static SpreadsheetGreaterThanEqualsParserToken greaterThanEquals(final List<ParserToken> value, final String text) {
        return SpreadsheetGreaterThanEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetGreaterThanEqualsParserToken}
     */
    public static SpreadsheetGreaterThanEqualsSymbolParserToken greaterThanEqualsSymbol(final String value, final String text) {
        return SpreadsheetGreaterThanEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetGroupParserToken}
     */
    public static SpreadsheetGroupParserToken group(final List<ParserToken> value, final String text) {
        return SpreadsheetGroupParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetGroupSeparatorSymbolParserToken}
     */
    public static SpreadsheetGroupSeparatorSymbolParserToken groupSeparatorSymbol(final String value, final String text) {
        return SpreadsheetGroupSeparatorSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetHourParserToken}
     */
    public static SpreadsheetHourParserToken hour(final int value, final String text) {
        return SpreadsheetHourParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLabelNameParserToken}
     */
    public static SpreadsheetLabelNameParserToken labelName(final SpreadsheetLabelName value, final String text) {
        return SpreadsheetLabelNameParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLambdaFunctionParserToken}
     */
    public static SpreadsheetLambdaFunctionParserToken lambdaFunction(final List<ParserToken> value,
                                                                      final String text) {
        return SpreadsheetLambdaFunctionParserToken.with(
                value,
                text
        );
    }

    /**
     * {@see SpreadsheetLessThanParserToken}
     */
    public static SpreadsheetLessThanParserToken lessThan(final List<ParserToken> value, final String text) {
        return SpreadsheetLessThanParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLessThanSymbolParserToken}
     */
    public static SpreadsheetLessThanSymbolParserToken lessThanSymbol(final String value, final String text) {
        return SpreadsheetLessThanSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLessThanEqualsParserToken}
     */
    public static SpreadsheetLessThanEqualsParserToken lessThanEquals(final List<ParserToken> value, final String text) {
        return SpreadsheetLessThanEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLessThanEqualsParserToken}
     */
    public static SpreadsheetLessThanEqualsSymbolParserToken lessThanEqualsSymbol(final String value, final String text) {
        return SpreadsheetLessThanEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMinusSymbolParserToken}
     */
    public static SpreadsheetMinusSymbolParserToken minusSymbol(final String value, final String text) {
        return SpreadsheetMinusSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMillisecondParserToken}
     */
    public static SpreadsheetMillisecondParserToken millisecond(final int value, final String text) {
        return SpreadsheetMillisecondParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMinuteParserToken}
     */
    public static SpreadsheetMinuteParserToken minute(final int value, final String text) {
        return SpreadsheetMinuteParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMonthNameParserToken}
     */
    public static SpreadsheetMonthNameParserToken monthName(final int value, final String text) {
        return SpreadsheetMonthNameParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMonthNameAbbreviationParserToken}
     */
    public static SpreadsheetMonthNameAbbreviationParserToken monthNameAbbreviation(final int value, final String text) {
        return SpreadsheetMonthNameAbbreviationParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMonthNameInitialParserToken}
     */
    public static SpreadsheetMonthNameInitialParserToken monthNameInitial(final int value, final String text) {
        return SpreadsheetMonthNameInitialParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMonthNumberParserToken}
     */
    public static SpreadsheetMonthNumberParserToken monthNumber(final int value, final String text) {
        return SpreadsheetMonthNumberParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMultiplicationParserToken}
     */
    public static SpreadsheetMultiplicationParserToken multiplication(final List<ParserToken> value, final String text) {
        return SpreadsheetMultiplicationParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMultiplySymbolParserToken}
     */
    public static SpreadsheetMultiplySymbolParserToken multiplySymbol(final String value, final String text) {
        return SpreadsheetMultiplySymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetNamedFunctionParserToken}
     */
    public static SpreadsheetNamedFunctionParserToken namedFunction(final List<ParserToken> value,
                                                                    final String text) {
        return SpreadsheetNamedFunctionParserToken.with(
                value,
                text
        );
    }

    /**
     * {@see SpreadsheetNegativeParserToken}
     */
    public static SpreadsheetNegativeParserToken negative(final List<ParserToken> value, final String text) {
        return SpreadsheetNegativeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetNotEqualsParserToken}
     */
    public static SpreadsheetNotEqualsParserToken notEquals(final List<ParserToken> value, final String text) {
        return SpreadsheetNotEqualsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetNotEqualsSymbolParserToken}
     */
    public static SpreadsheetNotEqualsSymbolParserToken notEqualsSymbol(final String value, final String text) {
        return SpreadsheetNotEqualsSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetNumberParserToken}
     */
    public static SpreadsheetNumberParserToken number(final List<ParserToken> value, final String text) {
        return SpreadsheetNumberParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetParenthesisCloseSymbolParserToken}
     */
    public static SpreadsheetParenthesisCloseSymbolParserToken parenthesisCloseSymbol(final String value, final String text) {
        return SpreadsheetParenthesisCloseSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetParenthesisOpenSymbolParserToken}
     */
    public static SpreadsheetParenthesisOpenSymbolParserToken parenthesisOpenSymbol(final String value, final String text) {
        return SpreadsheetParenthesisOpenSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetPercentSymbolParserToken}
     */
    public static SpreadsheetPercentSymbolParserToken percentSymbol(final String value, final String text) {
        return SpreadsheetPercentSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetPlusSymbolParserToken}
     */
    public static SpreadsheetPlusSymbolParserToken plusSymbol(final String value, final String text) {
        return SpreadsheetPlusSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetPowerParserToken}
     */
    public static SpreadsheetPowerParserToken power(final List<ParserToken> value, final String text) {
        return SpreadsheetPowerParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetPowerSymbolParserToken}
     */
    public static SpreadsheetPowerSymbolParserToken powerSymbol(final String value, final String text) {
        return SpreadsheetPowerSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetRowReferenceParserToken}
     */
    public static SpreadsheetRowReferenceParserToken rowReference(final SpreadsheetRowReference value, final String text) {
        return SpreadsheetRowReferenceParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetSecondsParserToken}
     */
    public static SpreadsheetSecondsParserToken seconds(final int value, final String text) {
        return SpreadsheetSecondsParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetSubtractionParserToken}
     */
    public static SpreadsheetSubtractionParserToken subtraction(final List<ParserToken> value, final String text) {
        return SpreadsheetSubtractionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetTextParserToken}
     */
    public static SpreadsheetTextParserToken text(final List<ParserToken> value, final String text) {
        return SpreadsheetTextParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetTextLiteralParserToken}
     */
    public static SpreadsheetTextLiteralParserToken textLiteral(final String value, final String text) {
        return SpreadsheetTextLiteralParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetTimeParserToken}
     */
    public static SpreadsheetTimeParserToken time(final List<ParserToken> value, final String text) {
        return SpreadsheetTimeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetValueSeparatorSymbolParserToken}
     */
    public static SpreadsheetValueSeparatorSymbolParserToken valueSeparatorSymbol(final String value, final String text) {
        return SpreadsheetValueSeparatorSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetWhitespaceParserToken}
     */
    public static SpreadsheetWhitespaceParserToken whitespace(final String value, final String text) {
        return SpreadsheetWhitespaceParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetYearParserToken}
     */
    public static SpreadsheetYearParserToken year(final int value, final String text) {
        return SpreadsheetYearParserToken.with(value, text);
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
     * Only {@link SpreadsheetAdditionParserToken} return true
     */
    public final boolean isAddition() {
        return this instanceof SpreadsheetAdditionParserToken;
    }

    /**
     * Only {@link SpreadsheetAmPmParserToken} returns true
     */
    public final boolean isAmPm() {
        return this instanceof SpreadsheetAmPmParserToken;
    }

    /**
     * Only {@link SpreadsheetApostropheSymbolParserToken} returns true
     */
    public final boolean isApostropheSymbol() {
        return this instanceof SpreadsheetApostropheSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetArithmeticParserToken} return true
     */
    public final boolean isArithmetic() {
        return this instanceof SpreadsheetArithmeticParserToken;
    }

    /**
     * Only {@link SpreadsheetBetweenSymbolParserToken} returns true
     */
    public final boolean isBetweenSymbol() {
        return this instanceof SpreadsheetBetweenSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetCellRangeParserToken} return true
     */
    public final boolean isCellRange() {
        return this instanceof SpreadsheetCellRangeParserToken;
    }

    /**
     * Only {@link SpreadsheetCellReferenceParserToken} return true
     */
    public final boolean isCellReference() {
        return this instanceof SpreadsheetCellReferenceParserToken;
    }

    /**
     * Only {@link SpreadsheetColumnReferenceParserToken} return true
     */
    public final boolean isColumnReference() {
        return this instanceof SpreadsheetColumnReferenceParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionParserToken} return true
     */
    public final boolean isCondition() {
        return this instanceof SpreadsheetConditionParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionRightParserToken} return true
     */
    public final boolean isConditionRight() {
        return this instanceof SpreadsheetConditionRightParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionRightEqualsParserToken} return true
     */
    public final boolean isConditionRightEquals() {
        return this instanceof SpreadsheetConditionRightEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionRightGreaterThanParserToken} return true
     */
    public final boolean isConditionRightGreaterThan() {
        return this instanceof SpreadsheetConditionRightGreaterThanParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionRightGreaterThanEqualsParserToken} return true
     */
    public final boolean isConditionRightGreaterThanEquals() {
        return this instanceof SpreadsheetConditionRightGreaterThanEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionRightLessThanParserToken} return true
     */
    public final boolean isConditionRightLessThan() {
        return this instanceof SpreadsheetConditionRightLessThanParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionRightLessThanEqualsParserToken} return true
     */
    public final boolean isConditionRightLessThanEquals() {
        return this instanceof SpreadsheetConditionRightLessThanEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetConditionRightNotEqualsParserToken} return true
     */
    public final boolean isConditionRightNotEquals() {
        return this instanceof SpreadsheetConditionRightNotEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetCurrencySymbolParserToken} returns true
     */
    public final boolean isCurrencySymbol() {
        return this instanceof SpreadsheetCurrencySymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetDateParserToken} return true
     */
    public final boolean isDate() {
        return this instanceof SpreadsheetDateParserToken;
    }

    /**
     * Only {@link SpreadsheetDateTimeParserToken} return true
     */
    public final boolean isDateTime() {
        return this instanceof SpreadsheetDateTimeParserToken;
    }

    /**
     * Only {@link SpreadsheetDayNameParserToken} returns true
     */
    public final boolean isDayName() {
        return this instanceof SpreadsheetDayNameParserToken;
    }

    /**
     * Only {@link SpreadsheetDayNameAbbreviationParserToken} returns true
     */
    public final boolean isDayNameAbbreviation() {
        return this instanceof SpreadsheetDayNameAbbreviationParserToken;
    }

    /**
     * Only {@link SpreadsheetDayNumberParserToken} returns true
     */
    public final boolean isDayNumber() {
        return this instanceof SpreadsheetDayNumberParserToken;
    }

    /**
     * Only {@link SpreadsheetDecimalSeparatorSymbolParserToken} returns true
     */
    public final boolean isDecimalSeparatorSymbol() {
        return this instanceof SpreadsheetDecimalSeparatorSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetDigitsParserToken} returns true
     */
    public final boolean isDigits() {
        return this instanceof SpreadsheetDigitsParserToken;
    }

    /**
     * Only {@link SpreadsheetDivideSymbolParserToken} returns true
     */
    public final boolean isDivideSymbol() {
        return this instanceof SpreadsheetDivideSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetDivisionParserToken} return true
     */
    public final boolean isDivision() {
        return this instanceof SpreadsheetDivisionParserToken;
    }

    /**
     * Only {@link SpreadsheetDoubleQuoteSymbolParserToken} returns true
     */
    public final boolean isDoubleQuoteSymbol() {
        return this instanceof SpreadsheetDoubleQuoteSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetEqualsParserToken} returns true
     */
    public final boolean isEquals() {
        return this instanceof SpreadsheetEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetEqualsSymbolParserToken} returns true
     */
    public final boolean isEqualsSymbol() {
        return this instanceof SpreadsheetEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetEqualsSymbolParserToken} returns true
     */
    public final boolean isError() {
        return this instanceof SpreadsheetErrorParserToken;
    }

    /**
     * Only {@link SpreadsheetExponentSymbolParserToken} returns true
     */
    public final boolean isExponentSymbol() {
        return this instanceof SpreadsheetExponentSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetExpressionParserToken} return true
     */
    public final boolean isExpression() {
        return this instanceof SpreadsheetExpressionParserToken;
    }

    /**
     * Only {@link SpreadsheetFunctionParserToken} returns true
     */
    public final boolean isFunction() {
        return this instanceof SpreadsheetFunctionParserToken;
    }

    /**
     * Only {@link SpreadsheetFunctionNameParserToken} returns true
     */
    public final boolean isFunctionName() {
        return this instanceof SpreadsheetFunctionNameParserToken;
    }

    /**
     * Only {@link SpreadsheetFunctionParametersParserToken} returns true
     */
    public final boolean isFunctionParameters() {
        return this instanceof SpreadsheetFunctionParametersParserToken;
    }

    /**
     * Only {@link SpreadsheetGreaterThanParserToken} returns true
     */
    public final boolean isGreaterThan() {
        return this instanceof SpreadsheetGreaterThanParserToken;
    }

    /**
     * Only {@link SpreadsheetGreaterThanSymbolParserToken} returns true
     */
    public final boolean isGreaterThanSymbol() {
        return this instanceof SpreadsheetGreaterThanSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetGreaterThanEqualsParserToken} returns true
     */
    public final boolean isGreaterThanEquals() {
        return this instanceof SpreadsheetGreaterThanEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetGreaterThanEqualsSymbolParserToken} returns true
     */
    public final boolean isGreaterThanEqualsSymbol() {
        return this instanceof SpreadsheetGreaterThanEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetGroupParserToken} return true
     */
    public final boolean isGroup() {
        return this instanceof SpreadsheetGroupParserToken;
    }

    /**
     * Only {@link SpreadsheetGroupSeparatorSymbolParserToken} return true
     */
    public final boolean isGroupSeparatorSymbol() {
        return this instanceof SpreadsheetGroupSeparatorSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetHourParserToken} returns true
     */
    public final boolean isHour() {
        return this instanceof SpreadsheetHourParserToken;
    }

    /**
     * Only {@link SpreadsheetLabelNameParserToken} return true
     */
    public final boolean isLabelName() {
        return this instanceof SpreadsheetLabelNameParserToken;
    }

    /**
     * Only {@link SpreadsheetLambdaFunctionParserToken} returns true
     */
    public final boolean isLambdaFunction() {
        return this instanceof SpreadsheetLambdaFunctionParserToken;
    }

    /**
     * Returns true for sub-classes of {@link SpreadsheetLeafParserToken}.
     */
    @Override
    public final boolean isLeaf() {
        return this instanceof SpreadsheetLeafParserToken;
    }

    /**
     * Only {@link SpreadsheetLessThanParserToken} returns true
     */
    public final boolean isLessThan() {
        return this instanceof SpreadsheetLessThanParserToken;
    }

    /**
     * Only {@link SpreadsheetLessThanSymbolParserToken} returns true
     */
    public final boolean isLessThanSymbol() {
        return this instanceof SpreadsheetLessThanSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetLessThanEqualsParserToken} returns true
     */
    public final boolean isLessThanEquals() {
        return this instanceof SpreadsheetLessThanEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetLessThanEqualsSymbolParserToken} returns true
     */
    public final boolean isLessThanEqualsSymbol() {
        return this instanceof SpreadsheetLessThanEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetMinusSymbolParserToken} returns true
     */
    public final boolean isMinusSymbol() {
        return this instanceof SpreadsheetMinusSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetMillisecondParserToken} returns true
     */
    public final boolean isMillisecond() {
        return this instanceof SpreadsheetMillisecondParserToken;
    }

    /**
     * Only {@link SpreadsheetMinuteParserToken} returns true
     */
    public final boolean isMinute() {
        return this instanceof SpreadsheetMinuteParserToken;
    }

    /**
     * Only {@link SpreadsheetMonthNameParserToken} returns true
     */
    public final boolean isMonthName() {
        return this instanceof SpreadsheetMonthNameParserToken;
    }

    /**
     * Only {@link SpreadsheetMonthNameAbbreviationParserToken} returns true
     */
    public final boolean isMonthNameAbbreviation() {
        return this instanceof SpreadsheetMonthNameAbbreviationParserToken;
    }

    /**
     * Only {@link SpreadsheetMonthNameInitialParserToken} returns true
     */
    public final boolean isMonthNameInitial() {
        return this instanceof SpreadsheetMonthNameInitialParserToken;
    }

    /**
     * Only {@link SpreadsheetMonthNumberParserToken} returns true
     */
    public final boolean isMonthNumber() {
        return this instanceof SpreadsheetMonthNumberParserToken;
    }

    /**
     * Only {@link SpreadsheetMultiplicationParserToken} return true
     */
    public final boolean isMultiplication() {
        return this instanceof SpreadsheetMultiplicationParserToken;
    }

    /**
     * Only {@link SpreadsheetMultiplySymbolParserToken} returns true
     */
    public final boolean isMultiplySymbol() {
        return this instanceof SpreadsheetMultiplySymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetNamedFunctionParserToken} returns true
     */
    public final boolean isNamedFunction() {
        return this instanceof SpreadsheetNamedFunctionParserToken;
    }

    /**
     * Only {@link SpreadsheetNegativeParserToken} return true
     */
    public final boolean isNegative() {
        return this instanceof SpreadsheetNegativeParserToken;
    }

    /**
     * Only {@link SpreadsheetSymbolParserToken} return true
     */
    @Override
    public final boolean isNoise() {
        return this instanceof SpreadsheetSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetNotEqualsParserToken} returns true
     */
    public final boolean isNotEquals() {
        return this instanceof SpreadsheetNotEqualsParserToken;
    }

    /**
     * Only {@link SpreadsheetNotEqualsSymbolParserToken} returns true
     */
    public final boolean isNotEqualsSymbol() {
        return this instanceof SpreadsheetNotEqualsSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetNumberParserToken} return true
     */
    public final boolean isNumber() {
        return this instanceof SpreadsheetNumberParserToken;
    }

    /**
     * Returns true for sub-classes of {@link SpreadsheetParentParserToken}.
     */
    @Override
    public final boolean isParent() {
        return this instanceof SpreadsheetParentParserToken;
    }

    /**
     * Only {@link SpreadsheetParenthesisCloseSymbolParserToken} return true
     */
    public final boolean isParenthesisCloseSymbol() {
        return this instanceof SpreadsheetParenthesisCloseSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetParenthesisOpenSymbolParserToken} return true
     */
    public final boolean isParenthesisOpenSymbol() {
        return this instanceof SpreadsheetParenthesisOpenSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetPercentSymbolParserToken} return true
     */
    public final boolean isPercentSymbol() {
        return this instanceof SpreadsheetPercentSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetPlusSymbolParserToken} returns true
     */
    public final boolean isPlusSymbol() {
        return this instanceof SpreadsheetPlusSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetPowerParserToken} return true
     */
    public final boolean isPower() {
        return this instanceof SpreadsheetPowerParserToken;
    }

    /**
     * Only {@link SpreadsheetPowerSymbolParserToken} return true
     */
    public final boolean isPowerSymbol() {
        return this instanceof SpreadsheetPowerSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetRowReferenceParserToken} return true
     */
    public final boolean isRowReference() {
        return this instanceof SpreadsheetRowReferenceParserToken;
    }

    /**
     * Only {@link SpreadsheetSecondsParserToken} returns true
     */
    public final boolean isSeconds() {
        return this instanceof SpreadsheetSecondsParserToken;
    }

    /**
     * Only {@link SpreadsheetSubtractionParserToken} return true
     */
    public final boolean isSubtraction() {
        return this instanceof SpreadsheetSubtractionParserToken;
    }

    /**
     * Only {@link SpreadsheetSymbolParserToken} return true
     */
    @Override
    public final boolean isSymbol() {
        return this instanceof SpreadsheetSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetTextParserToken} return true
     */
    public final boolean isText() {
        return this instanceof SpreadsheetTextParserToken;
    }

    /**
     * Only {@link SpreadsheetTextLiteralParserToken} return true
     */
    public final boolean isTextLiteral() {
        return this instanceof SpreadsheetTextLiteralParserToken;
    }

    /**
     * Only {@link SpreadsheetTimeParserToken} return true
     */
    public final boolean isTime() {
        return this instanceof SpreadsheetTimeParserToken;
    }

    /**
     * Only {@link SpreadsheetValueParserToken} return true
     */
    public final boolean isValue() {
        return this instanceof SpreadsheetValueParserToken;
    }

    /**
     * Only {@link SpreadsheetValueSeparatorSymbolParserToken} returns true
     */
    public final boolean isValueSeparatorSymbol() {
        return this instanceof SpreadsheetValueSeparatorSymbolParserToken;
    }

    /**
     * Only {@link SpreadsheetWhitespaceParserToken} return true
     */
    @Override
    public final boolean isWhitespace() {
        return this instanceof SpreadsheetWhitespaceParserToken;
    }

    /**
     * Only {@link SpreadsheetYearParserToken} returns true
     */
    public final boolean isYear() {
        return this instanceof SpreadsheetYearParserToken;
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
     * Factory that creates the {@link SpreadsheetBinaryParserToken} sub class using the provided tokens and text.
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
                this.canBeEqual(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final SpreadsheetParserToken other) {
        return this.text.equals(other.text) &&
                this.value().equals(other.value());
    }

    @Override
    public final String toString() {
        return this.text();
    }

    // json. ...........................................................................................................

    // SpreadsheetNonSymbolParserToken..................................................................................

    static {
        registerLeafParserToken(
                SpreadsheetAmPmParserToken.class,
                SpreadsheetParserToken::unmarshallAmPm
        );

        registerLeafParserToken(
                SpreadsheetColumnReferenceParserToken.class,
                SpreadsheetParserToken::unmarshallColumnReference
        );

        registerLeafParserToken(
                SpreadsheetDayNameParserToken.class,
                SpreadsheetParserToken::unmarshallDayName
        );

        registerLeafParserToken(
                SpreadsheetDayNameAbbreviationParserToken.class,
                SpreadsheetParserToken::unmarshallDayNameAbbreviation
        );

        registerLeafParserToken(
                SpreadsheetDayNumberParserToken.class,
                SpreadsheetParserToken::unmarshallDayNumber
        );

        registerLeafParserToken(
                SpreadsheetDigitsParserToken.class,
                SpreadsheetParserToken::unmarshallDigits
        );

        registerLeafParserToken(
                SpreadsheetErrorParserToken.class,
                SpreadsheetParserToken::unmarshallError
        );

        registerLeafParserToken(
                SpreadsheetFunctionNameParserToken.class,
                SpreadsheetParserToken::unmarshallFunctionName
        );

        registerLeafParserToken(
                SpreadsheetHourParserToken.class,
                SpreadsheetParserToken::unmarshallHour
        );

        registerLeafParserToken(
                SpreadsheetLabelNameParserToken.class,
                SpreadsheetParserToken::unmarshallLabelName
        );

        registerLeafParserToken(
                SpreadsheetMillisecondParserToken.class,
                SpreadsheetParserToken::unmarshallMillisecond
        );

        registerLeafParserToken(
                SpreadsheetMinuteParserToken.class,
                SpreadsheetParserToken::unmarshallMinute
        );

        registerLeafParserToken(
                SpreadsheetMonthNameParserToken.class,
                SpreadsheetParserToken::unmarshallMonthName
        );

        registerLeafParserToken(
                SpreadsheetMonthNameAbbreviationParserToken.class,
                SpreadsheetParserToken::unmarshallMonthNameAbbreviation
        );

        registerLeafParserToken(
                SpreadsheetMonthNameInitialParserToken.class,
                SpreadsheetParserToken::unmarshallMonthNameInitial
        );

        registerLeafParserToken(
                SpreadsheetMonthNumberParserToken.class,
                SpreadsheetParserToken::unmarshallMonthNumber
        );

        registerLeafParserToken(
                SpreadsheetRowReferenceParserToken.class,
                SpreadsheetParserToken::unmarshallRowReference
        );

        registerLeafParserToken(
                SpreadsheetSecondsParserToken.class,
                SpreadsheetParserToken::unmarshallSeconds
        );

        registerLeafParserToken(
                SpreadsheetTextLiteralParserToken.class,
                SpreadsheetParserToken::unmarshallTextLiteral
        );

        registerLeafParserToken(
                SpreadsheetYearParserToken.class,
                SpreadsheetParserToken::unmarshallYear
        );
    }

    static SpreadsheetAmPmParserToken unmarshallAmPm(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::amPm
        );
    }

    static SpreadsheetColumnReferenceParserToken unmarshallColumnReference(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                SpreadsheetColumnReference.class,
                context,
                SpreadsheetParserToken::columnReference
        );
    }

    static SpreadsheetDayNameParserToken unmarshallDayName(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::dayName
        );
    }

    static SpreadsheetDayNameAbbreviationParserToken unmarshallDayNameAbbreviation(final JsonNode node,
                                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::dayNameAbbreviation
        );
    }

    static SpreadsheetDayNumberParserToken unmarshallDayNumber(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::dayNumber
        );
    }

    static SpreadsheetDigitsParserToken unmarshallDigits(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                String.class,
                context,
                SpreadsheetParserToken::digits
        );
    }

    static SpreadsheetErrorParserToken unmarshallError(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                SpreadsheetError.class,
                context,
                SpreadsheetParserToken::error
        );
    }

    static SpreadsheetFunctionNameParserToken unmarshallFunctionName(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                SpreadsheetFunctionName.class,
                context,
                SpreadsheetParserToken::functionName
        );
    }

    static SpreadsheetHourParserToken unmarshallHour(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::hour
        );
    }

    static SpreadsheetLabelNameParserToken unmarshallLabelName(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                SpreadsheetLabelName.class,
                context,
                SpreadsheetParserToken::labelName
        );
    }

    static SpreadsheetMillisecondParserToken unmarshallMillisecond(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::millisecond
        );
    }

    static SpreadsheetMinuteParserToken unmarshallMinute(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::minute
        );
    }

    static SpreadsheetMonthNameParserToken unmarshallMonthName(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthName
        );
    }

    static SpreadsheetMonthNameAbbreviationParserToken unmarshallMonthNameAbbreviation(final JsonNode node,
                                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthNameAbbreviation
        );
    }

    static SpreadsheetMonthNameInitialParserToken unmarshallMonthNameInitial(final JsonNode node,
                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthNameInitial
        );
    }

    static SpreadsheetMonthNumberParserToken unmarshallMonthNumber(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::monthNumber
        );
    }

    static SpreadsheetRowReferenceParserToken unmarshallRowReference(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                SpreadsheetRowReference.class,
                context,
                SpreadsheetParserToken::rowReference
        );
    }

    static SpreadsheetSecondsParserToken unmarshallSeconds(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::seconds
        );
    }

    static SpreadsheetTextLiteralParserToken unmarshallTextLiteral(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                String.class,
                context,
                SpreadsheetParserToken::textLiteral
        );
    }

    static SpreadsheetYearParserToken unmarshallYear(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                Integer.class,
                context,
                SpreadsheetParserToken::year
        );
    }

    // SpreadsheetSymbolParserToken sub-classes.........................................................................

    static {
        registerLeafParserToken(
                SpreadsheetApostropheSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallApostropheSymbol
        );

        registerLeafParserToken(
                SpreadsheetBetweenSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallBetweenSymbol
        );

        registerLeafParserToken(
                SpreadsheetCurrencySymbolParserToken.class,
                SpreadsheetParserToken::unmarshallCurrencySymbol
        );

        registerLeafParserToken(
                SpreadsheetDecimalSeparatorSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallDecimalSeparatorSymbol
        );

        registerLeafParserToken(
                SpreadsheetDivideSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallDivideSymbol
        );

        registerLeafParserToken(
                SpreadsheetDoubleQuoteSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallDoubleQuoteSymbol
        );

        registerLeafParserToken(
                SpreadsheetEqualsSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallEqualsSymbol
        );

        registerLeafParserToken(
                SpreadsheetExponentSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallExponentSymbol
        );

        registerLeafParserToken(
                SpreadsheetGreaterThanEqualsSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThanEqualsSymbol
        );

        registerLeafParserToken(
                SpreadsheetGreaterThanSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThanSymbol
        );

        registerLeafParserToken(
                SpreadsheetGroupSeparatorSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallGroupSeparatorSymbol
        );

        registerLeafParserToken(
                SpreadsheetLessThanEqualsSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallLessThanEqualsSymbol
        );

        registerLeafParserToken(
                SpreadsheetLessThanSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallLessThanSymbol
        );

        registerLeafParserToken(
                SpreadsheetMinusSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallMinusSymbol
        );

        registerLeafParserToken(
                SpreadsheetMultiplySymbolParserToken.class,
                SpreadsheetParserToken::unmarshallMultiplySymbol
        );

        registerLeafParserToken(
                SpreadsheetNotEqualsSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallNotEqualsSymbol
        );

        registerLeafParserToken(
                SpreadsheetParenthesisCloseSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallParenthesisCloseSymbol
        );

        registerLeafParserToken(
                SpreadsheetParenthesisOpenSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallParenthesisOpenSymbol
        );

        registerLeafParserToken(
                SpreadsheetPercentSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallPercentSymbol
        );

        registerLeafParserToken(
                SpreadsheetPlusSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallPlusSymbol
        );

        registerLeafParserToken(
                SpreadsheetPowerSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallPowerSymbol
        );

        registerLeafParserToken(
                SpreadsheetValueSeparatorSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallValueSeparatorSymbol
        );

        registerLeafParserToken(
                SpreadsheetWhitespaceParserToken.class,
                SpreadsheetParserToken::unmarshallWhitespace
        );
    }

    static SpreadsheetApostropheSymbolParserToken unmarshallApostropheSymbol(final JsonNode node,
                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::apostropheSymbol
        );
    }

    static SpreadsheetBetweenSymbolParserToken unmarshallBetweenSymbol(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::betweenSymbol
        );
    }

    static SpreadsheetCurrencySymbolParserToken unmarshallCurrencySymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::currencySymbol
        );
    }

    static SpreadsheetDecimalSeparatorSymbolParserToken unmarshallDecimalSeparatorSymbol(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::decimalSeparatorSymbol
        );
    }

    static SpreadsheetDivideSymbolParserToken unmarshallDivideSymbol(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::divideSymbol
        );
    }

    static SpreadsheetDoubleQuoteSymbolParserToken unmarshallDoubleQuoteSymbol(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::doubleQuoteSymbol
        );
    }

    static SpreadsheetEqualsSymbolParserToken unmarshallEqualsSymbol(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::equalsSymbol
        );
    }

    static SpreadsheetExponentSymbolParserToken unmarshallExponentSymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::exponentSymbol
        );
    }

    static SpreadsheetGreaterThanEqualsSymbolParserToken unmarshallGreaterThanEqualsSymbol(final JsonNode node,
                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::greaterThanEqualsSymbol
        );
    }


    static SpreadsheetGreaterThanSymbolParserToken unmarshallGreaterThanSymbol(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::greaterThanSymbol
        );
    }

    static SpreadsheetGroupSeparatorSymbolParserToken unmarshallGroupSeparatorSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::groupSeparatorSymbol
        );
    }

    static SpreadsheetLessThanEqualsSymbolParserToken unmarshallLessThanEqualsSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::lessThanEqualsSymbol
        );
    }

    static SpreadsheetLessThanSymbolParserToken unmarshallLessThanSymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::lessThanSymbol
        );
    }

    static SpreadsheetMinusSymbolParserToken unmarshallMinusSymbol(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::minusSymbol
        );
    }

    static SpreadsheetMultiplySymbolParserToken unmarshallMultiplySymbol(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::multiplySymbol
        );
    }

    static SpreadsheetNotEqualsSymbolParserToken unmarshallNotEqualsSymbol(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::notEqualsSymbol
        );
    }

    static SpreadsheetParenthesisCloseSymbolParserToken unmarshallParenthesisCloseSymbol(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::parenthesisCloseSymbol
        );
    }

    static SpreadsheetParenthesisOpenSymbolParserToken unmarshallParenthesisOpenSymbol(final JsonNode node,
                                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::parenthesisOpenSymbol
        );
    }

    static SpreadsheetPercentSymbolParserToken unmarshallPercentSymbol(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::percentSymbol
        );
    }

    static SpreadsheetPlusSymbolParserToken unmarshallPlusSymbol(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::plusSymbol
        );
    }

    static SpreadsheetPowerSymbolParserToken unmarshallPowerSymbol(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::powerSymbol
        );
    }

    static SpreadsheetValueSeparatorSymbolParserToken unmarshallValueSeparatorSymbol(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::valueSeparatorSymbol
        );
    }

    static SpreadsheetWhitespaceParserToken unmarshallWhitespace(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::whitespace
        );
    }

    /**
     * Helper that knows how to unmarshall a sub class of {@link SpreadsheetLeafParserToken}
     */
    static <T extends SpreadsheetSymbolParserToken> T unmarshallSymbolParserToken(final JsonNode node,
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
     * Helper that knows how to unmarshall a sub class of {@link SpreadsheetParserToken}
     */
    private static <V, T extends SpreadsheetLeafParserToken<V>> T unmarshallLeafParserToken(final JsonNode node,
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

    private static <T extends SpreadsheetLeafParserToken<?>> void registerLeafParserToken(final Class<T> type,
                                                                                          final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
                type,
                from,
                SpreadsheetParserToken::marshallLeafParserToken
        );
    }

    // SpreadsheetParentParserToken.....................................................................................

    static {
        registerParentParserToken(
                SpreadsheetAdditionParserToken.class,
                SpreadsheetParserToken::unmarshallAddition
        );

        registerParentParserToken(
                SpreadsheetCellReferenceParserToken.class,
                SpreadsheetParserToken::unmarshallCellReference
        );

        registerParentParserToken(
                SpreadsheetConditionRightEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightEquals
        );

        registerParentParserToken(
                SpreadsheetConditionRightGreaterThanParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightGreaterThan
        );

        registerParentParserToken(
                SpreadsheetConditionRightGreaterThanEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightGreaterThanEquals
        );

        registerParentParserToken(
                SpreadsheetConditionRightLessThanParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightLessThan
        );

        registerParentParserToken(
                SpreadsheetConditionRightLessThanEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightLessThanEquals
        );

        registerParentParserToken(
                SpreadsheetConditionRightNotEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallConditionRightNotEquals
        );

        registerParentParserToken(
                SpreadsheetDateParserToken.class,
                SpreadsheetParserToken::unmarshallDate
        );

        registerParentParserToken(
                SpreadsheetDateTimeParserToken.class,
                SpreadsheetParserToken::unmarshallDateTime
        );

        registerParentParserToken(
                SpreadsheetDivisionParserToken.class,
                SpreadsheetParserToken::unmarshallDivision
        );

        registerParentParserToken(
                SpreadsheetEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallEquals
        );

        registerParentParserToken(
                SpreadsheetExpressionParserToken.class,
                SpreadsheetParserToken::unmarshallExpression
        );

        registerParentParserToken(
                SpreadsheetFunctionParametersParserToken.class,
                SpreadsheetParserToken::unmarshallFunctionParameters
        );

        registerParentParserToken(
                SpreadsheetGreaterThanEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThanEquals
        );

        registerParentParserToken(
                SpreadsheetGreaterThanParserToken.class,
                SpreadsheetParserToken::unmarshallGreaterThan
        );

        registerParentParserToken(
                SpreadsheetGroupParserToken.class,
                SpreadsheetParserToken::unmarshallGroup
        );

        registerParentParserToken(
                SpreadsheetLambdaFunctionParserToken.class,
                SpreadsheetParserToken::unmarshallLambdaFunction
        );

        registerParentParserToken(
                SpreadsheetLessThanEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallLessThanEquals
        );

        registerParentParserToken(
                SpreadsheetLessThanParserToken.class,
                SpreadsheetParserToken::unmarshallLessThan
        );

        registerParentParserToken(
                SpreadsheetMultiplicationParserToken.class,
                SpreadsheetParserToken::unmarshallMultiplication
        );

        registerParentParserToken(
                SpreadsheetNamedFunctionParserToken.class,
                SpreadsheetParserToken::unmarshallNamedFunction
        );

        registerParentParserToken(
                SpreadsheetNegativeParserToken.class,
                SpreadsheetParserToken::unmarshallNegative
        );

        registerParentParserToken(
                SpreadsheetNotEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallNotEquals
        );

        registerParentParserToken(
                SpreadsheetNumberParserToken.class,
                SpreadsheetParserToken::unmarshallNumber
        );

        registerParentParserToken(
                SpreadsheetPowerParserToken.class,
                SpreadsheetParserToken::unmarshallPower
        );

        registerParentParserToken(
                SpreadsheetCellRangeParserToken.class,
                SpreadsheetParserToken::unmarshallCellRange
        );

        registerParentParserToken(
                SpreadsheetSubtractionParserToken.class,
                SpreadsheetParserToken::unmarshallSubtraction
        );

        registerParentParserToken(
                SpreadsheetTimeParserToken.class,
                SpreadsheetParserToken::unmarshallTime
        );

        registerParentParserToken(
                SpreadsheetTextParserToken.class,
                SpreadsheetParserToken::unmarshallText
        );
    }

    static SpreadsheetAdditionParserToken unmarshallAddition(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::addition
        );
    }

    static SpreadsheetCellReferenceParserToken unmarshallCellReference(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::cellReference
        );
    }

    static SpreadsheetCellRangeParserToken unmarshallCellRange(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::cellRange
        );
    }

    static SpreadsheetConditionRightEqualsParserToken unmarshallConditionRightEquals(final JsonNode node,
                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::conditionRightEquals
        );
    }

    static SpreadsheetConditionRightGreaterThanParserToken unmarshallConditionRightGreaterThan(final JsonNode node,
                                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::conditionRightGreaterThan
        );
    }

    static SpreadsheetConditionRightGreaterThanEqualsParserToken unmarshallConditionRightGreaterThanEquals(final JsonNode node,
                                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::conditionRightGreaterThanEquals
        );
    }

    static SpreadsheetConditionRightLessThanParserToken unmarshallConditionRightLessThan(final JsonNode node,
                                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::conditionRightLessThan
        );
    }

    static SpreadsheetConditionRightLessThanEqualsParserToken unmarshallConditionRightLessThanEquals(final JsonNode node,
                                                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::conditionRightLessThanEquals
        );
    }

    static SpreadsheetConditionRightNotEqualsParserToken unmarshallConditionRightNotEquals(final JsonNode node,
                                                                                           final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::conditionRightNotEquals
        );
    }

    static SpreadsheetDateParserToken unmarshallDate(final JsonNode node,
                                                     final JsonNodeUnmarshallContext condate) {
        return unmarshallParentParserToken(
                node,
                condate,
                SpreadsheetParserToken::date
        );
    }

    static SpreadsheetDateTimeParserToken unmarshallDateTime(final JsonNode node,
                                                             final JsonNodeUnmarshallContext condateTime) {
        return unmarshallParentParserToken(
                node,
                condateTime,
                SpreadsheetParserToken::dateTime
        );
    }

    static SpreadsheetDivisionParserToken unmarshallDivision(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::division
        );
    }

    static SpreadsheetEqualsParserToken unmarshallEquals(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::equalsParserToken
        );
    }

    static SpreadsheetExpressionParserToken unmarshallExpression(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::expression
        );
    }

    static SpreadsheetFunctionParametersParserToken unmarshallFunctionParameters(final JsonNode node,
                                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::functionParameters
        );
    }

    static SpreadsheetGreaterThanEqualsParserToken unmarshallGreaterThanEquals(final JsonNode node,
                                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::greaterThanEquals
        );
    }

    static SpreadsheetGreaterThanParserToken unmarshallGreaterThan(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::greaterThan
        );
    }

    static SpreadsheetGroupParserToken unmarshallGroup(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::group
        );
    }

    static SpreadsheetLambdaFunctionParserToken unmarshallLambdaFunction(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::lambdaFunction
        );
    }

    static SpreadsheetLessThanEqualsParserToken unmarshallLessThanEquals(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::lessThanEquals
        );
    }

    static SpreadsheetLessThanParserToken unmarshallLessThan(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::lessThan
        );
    }

    static SpreadsheetMultiplicationParserToken unmarshallMultiplication(final JsonNode node,
                                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::multiplication
        );
    }

    static SpreadsheetNamedFunctionParserToken unmarshallNamedFunction(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::namedFunction
        );
    }

    static SpreadsheetNegativeParserToken unmarshallNegative(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::negative
        );
    }

    static SpreadsheetNotEqualsParserToken unmarshallNotEquals(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::notEquals
        );
    }

    static SpreadsheetNumberParserToken unmarshallNumber(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::number
        );
    }

    static SpreadsheetPowerParserToken unmarshallPower(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::power
        );
    }

    static SpreadsheetSubtractionParserToken unmarshallSubtraction(final JsonNode node,
                                                                   final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::subtraction
        );
    }

    static SpreadsheetTextParserToken unmarshallText(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::text
        );
    }

    static SpreadsheetTimeParserToken unmarshallTime(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::time
        );
    }

    /**
     * Helper that knows how to unmarshall a sub class of {@link SpreadsheetParentParserToken}
     */
    private static <T extends SpreadsheetParentParserToken> T unmarshallParentParserToken(final JsonNode node,
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
     * Handles marshalling any {@link SpreadsheetLeafParserToken}
     */
    private JsonNode marshallParentParserToken(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(VALUE_PROPERTY, context.marshallWithTypeCollection(Cast.to(this.value()))) // unnecessary to include type.
                .set(TEXT_PROPERTY, JsonNode.string(this.text()));
    }

    private static <T extends SpreadsheetParentParserToken> void registerParentParserToken(final Class<T> type,
                                                                                           final BiFunction<JsonNode, JsonNodeUnmarshallContext, T> from) {
        register(
                type,
                from,
                SpreadsheetParserToken::marshallParentParserToken
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
     * Handles marshalling any {@link SpreadsheetLeafParserToken}
     */
    private JsonNode marshallLeafParserToken(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(VALUE_PROPERTY, context.marshall(this.value())) // unnecessary to include type.
                .set(TEXT_PROPERTY, JsonNode.string(this.text()));
    }
}
