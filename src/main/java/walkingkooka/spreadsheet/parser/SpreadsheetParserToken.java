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
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.text.Whitespace;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.HasExpressionNode;
import walkingkooka.tree.visit.Visiting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a token within the grammar.
 */
public abstract class SpreadsheetParserToken implements ParserToken, HasExpressionNode {

    /**
     * {@see SpreadsheetAdditionParserToken}
     */
    public static SpreadsheetAdditionParserToken addition(final List<ParserToken> value, final String text) {
        return SpreadsheetAdditionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetBetweenSymbolParserToken}
     */
    public static SpreadsheetBetweenSymbolParserToken betweenSymbol(final String value, final String text) {
        return SpreadsheetBetweenSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetBigDecimalParserToken}
     */
    public static SpreadsheetBigDecimalParserToken bigDecimal(final BigDecimal value, final String text) {
        return SpreadsheetBigDecimalParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetBigIntegerParserToken}
     */
    public static SpreadsheetBigIntegerParserToken bigInteger(final BigInteger value, final String text) {
        return SpreadsheetBigIntegerParserToken.with(value, text);
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
     * {@see SpreadsheetDoubleParserToken}
     */
    public static SpreadsheetDoubleParserToken doubleParserToken(final double value, final String text) {
        return SpreadsheetDoubleParserToken.with(value, text);
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
     * {@see SpreadsheetFunctionParserToken}
     */
    public static SpreadsheetFunctionParserToken function(final List<ParserToken> value, final String text) {
        return SpreadsheetFunctionParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFunctionNameParserToken}
     */
    public static SpreadsheetFunctionNameParserToken functionName(final SpreadsheetFunctionName value, final String text) {
        return SpreadsheetFunctionNameParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetFunctionParameterSeparatorSymbolParserToken}
     */
    public static SpreadsheetFunctionParameterSeparatorSymbolParserToken functionParameterSeparatorSymbol(final String value, final String text) {
        return SpreadsheetFunctionParameterSeparatorSymbolParserToken.with(value, text);
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
     * {@see SpreadsheetLabelNameParserToken}
     */
    public static SpreadsheetLabelNameParserToken labelName(final SpreadsheetLabelName value, final String text) {
        return SpreadsheetLabelNameParserToken.with(value, text);
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
     * {@see SpreadsheetLocalDateParserToken}
     */
    public static SpreadsheetLocalDateParserToken localDate(final LocalDate value, final String text) {
        return SpreadsheetLocalDateParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLocalDateTimeParserToken}
     */
    public static SpreadsheetLocalDateTimeParserToken localDateTime(final LocalDateTime value, final String text) {
        return SpreadsheetLocalDateTimeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLocalTimeParserToken}
     */
    public static SpreadsheetLocalTimeParserToken localTime(final LocalTime value, final String text) {
        return SpreadsheetLocalTimeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetLongParserToken}
     */
    public static SpreadsheetLongParserToken longParserToken(final long value, final String text) {
        return SpreadsheetLongParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetMinusSymbolParserToken}
     */
    public static SpreadsheetMinusSymbolParserToken minusSymbol(final String value, final String text) {
        return SpreadsheetMinusSymbolParserToken.with(value, text);
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
     * {@see SpreadsheetPercentageParserToken}
     */
    public static SpreadsheetPercentageParserToken percentage(final List<ParserToken> value, final String text) {
        return SpreadsheetPercentageParserToken.with(value, text);
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
    public static SpreadsheetSymbolParserToken powerSymbol(final String value, final String text) {
        return SpreadsheetPowerSymbolParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetRangeParserToken}
     */
    public static SpreadsheetRangeParserToken range(final List<ParserToken> value, final String text) {
        return SpreadsheetRangeParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetRowReferenceParserToken}
     */
    public static SpreadsheetRowReferenceParserToken rowReference(final SpreadsheetRowReference value, final String text) {
        return SpreadsheetRowReferenceParserToken.with(value, text);
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
    public static SpreadsheetTextParserToken text(final String value, final String text) {
        return SpreadsheetTextParserToken.with(value, text);
    }

    /**
     * {@see SpreadsheetWhitespaceParserToken}
     */
    public static SpreadsheetWhitespaceParserToken whitespace(final String value, final String text) {
        return SpreadsheetWhitespaceParserToken.with(value, text);
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
        Whitespace.failIfNullOrEmptyOrWhitespace(text, "text");
        return text;
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

    final String text;

    /**
     * Value getter which may be a scalar or list of child tokens.
     */
    abstract Object value();

    /**
     * Returns a copy without any symbols or whitespace tokens. The original text form will still contain
     * those tokens as text, but the tokens themselves will be removed.
     */
    abstract public Optional<SpreadsheetParserToken> withoutSymbols();

    // isXXX............................................................................................................

    /**
     * Only {@link SpreadsheetAdditionParserToken} return true
     */
    public abstract boolean isAddition();

    /**
     * Only {@link SpreadsheetBetweenSymbolParserToken} returns true
     */
    public abstract boolean isBetweenSymbol();

    /**
     * Only {@link SpreadsheetBigDecimalParserToken} return true
     */
    public abstract boolean isBigDecimal();

    /**
     * Only {@link SpreadsheetBigIntegerParserToken} return true
     */
    public abstract boolean isBigInteger();

    /**
     * Only {@link SpreadsheetCellReferenceParserToken} return true
     */
    public abstract boolean isCellReference();

    /**
     * Only {@link SpreadsheetColumnReferenceParserToken} return true
     */
    public abstract boolean isColumnReference();

    /**
     * Only {@link SpreadsheetDivideSymbolParserToken} returns true
     */
    public abstract boolean isDivideSymbol();

    /**
     * Only {@link SpreadsheetDoubleParserToken} return true
     */
    public abstract boolean isDouble();

    /**
     * Only {@link SpreadsheetDivisionParserToken} return true
     */
    public abstract boolean isDivision();

    /**
     * Only {@link SpreadsheetEqualsParserToken} returns true
     */
    public abstract boolean isEquals();

    /**
     * Only {@link SpreadsheetEqualsSymbolParserToken} returns true
     */
    public abstract boolean isEqualsSymbol();

    /**
     * Only {@link SpreadsheetFunctionParserToken} return true
     */
    public abstract boolean isFunction();

    /**
     * Only {@link SpreadsheetFunctionNameParserToken} return true
     */
    public abstract boolean isFunctionName();

    /**
     * Only {@link SpreadsheetFunctionParameterSeparatorSymbolParserToken} returns true
     */
    public abstract boolean isFunctionParameterSeparatorSymbol();

    /**
     * Only {@link SpreadsheetGreaterThanParserToken} returns true
     */
    public abstract boolean isGreaterThan();

    /**
     * Only {@link SpreadsheetGreaterThanSymbolParserToken} returns true
     */
    public abstract boolean isGreaterThanSymbol();

    /**
     * Only {@link SpreadsheetGreaterThanEqualsParserToken} returns true
     */
    public abstract boolean isGreaterThanEquals();

    /**
     * Only {@link SpreadsheetGreaterThanEqualsSymbolParserToken} returns true
     */
    public abstract boolean isGreaterThanEqualsSymbol();

    /**
     * Only {@link SpreadsheetGroupParserToken} return true
     */
    public abstract boolean isGroup();

    /**
     * Only {@link SpreadsheetLabelNameParserToken} return true
     */
    public abstract boolean isLabelName();

    /**
     * Only {@link SpreadsheetLessThanParserToken} returns true
     */
    public abstract boolean isLessThan();

    /**
     * Only {@link SpreadsheetLessThanSymbolParserToken} returns true
     */
    public abstract boolean isLessThanSymbol();

    /**
     * Only {@link SpreadsheetLessThanEqualsParserToken} returns true
     */
    public abstract boolean isLessThanEquals();

    /**
     * Only {@link SpreadsheetLessThanEqualsSymbolParserToken} returns true
     */
    public abstract boolean isLessThanEqualsSymbol();

    /**
     * Only {@link SpreadsheetLocalDateParserToken} return true
     */
    public abstract boolean isLocalDate();

    /**
     * Only {@link SpreadsheetLocalDateTimeParserToken} return true
     */
    public abstract boolean isLocalDateTime();

    /**
     * Only {@link SpreadsheetLocalTimeParserToken} return true
     */
    public abstract boolean isLocalTime();

    /**
     * Only {@link SpreadsheetLongParserToken} return true
     */
    public abstract boolean isLong();

    /**
     * Only {@link SpreadsheetMinusSymbolParserToken} returns true
     */
    public abstract boolean isMinusSymbol();

    /**
     * Only {@link SpreadsheetMultiplicationParserToken} return true
     */
    public abstract boolean isMultiplication();

    /**
     * Only {@link SpreadsheetMultiplySymbolParserToken} returns true
     */
    public abstract boolean isMultiplySymbol();

    /**
     * Only {@link SpreadsheetNegativeParserToken} return true
     */
    public abstract boolean isNegative();

    /**
     * Only {@link SpreadsheetNotEqualsParserToken} returns true
     */
    public abstract boolean isNotEquals();

    /**
     * Only {@link SpreadsheetNotEqualsSymbolParserToken} returns true
     */
    public abstract boolean isNotEqualsSymbol();

    /**
     * Only {@link SpreadsheetParenthesisCloseSymbolParserToken} return true
     */
    public abstract boolean isParenthesisCloseSymbol();

    /**
     * Only {@link SpreadsheetParenthesisOpenSymbolParserToken} return true
     */
    public abstract boolean isParenthesisOpenSymbol();

    /**
     * Only {@link SpreadsheetPercentageParserToken} return true
     */
    public abstract boolean isPercentage();

    /**
     * Only {@link SpreadsheetPercentSymbolParserToken} return true
     */
    public abstract boolean isPercentSymbol();

    /**
     * Only {@link SpreadsheetPlusSymbolParserToken} returns true
     */
    public abstract boolean isPlusSymbol();

    /**
     * Only {@link SpreadsheetPowerParserToken} return true
     */
    public abstract boolean isPower();

    /**
     * Only {@link SpreadsheetPowerSymbolParserToken} return true
     */
    public abstract boolean isPowerSymbol();

    /**
     * Only {@link SpreadsheetRangeParserToken} return true
     */
    public abstract boolean isRange();

    /**
     * Only {@link SpreadsheetRowReferenceParserToken} return true
     */
    public abstract boolean isRowReference();

    /**
     * Only {@link SpreadsheetSubtractionParserToken} return true
     */
    public abstract boolean isSubtraction();

    /**
     * Only {@link SpreadsheetSymbolParserToken} return true
     */
    public abstract boolean isSymbol();

    /**
     * Only {@link SpreadsheetTextParserToken} return true
     */
    public abstract boolean isText();

    /**
     * The priority of this token, tokens with a value of zero are left in their original position.
     */
    abstract int operatorPriority();

    final static int LOWEST_PRIORITY = 0;
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

    public final void accept(final ParserTokenVisitor visitor) {
        final SpreadsheetParserTokenVisitor ebnfParserTokenVisitor = Cast.to(visitor);
        final SpreadsheetParserToken token = this;

        if (Visiting.CONTINUE == ebnfParserTokenVisitor.startVisit(token)) {
            this.accept(SpreadsheetParserTokenVisitor.class.cast(visitor));
        }
        ebnfParserTokenVisitor.endVisit(token);
    }

    abstract public void accept(final SpreadsheetParserTokenVisitor visitor);

    // HasExpressionNode................................................................................................

    /**
     * Converts this token to its {@link ExpressionNode} equivalent.
     */
    public final Optional<ExpressionNode> expressionNode() {
        return SpreadsheetParserTokenToExpressionNodeSpreadsheetParserTokenVisitor.accept(this);
    }

    // Object ...........................................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.text, this.value());
    }

    @Override
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
}
