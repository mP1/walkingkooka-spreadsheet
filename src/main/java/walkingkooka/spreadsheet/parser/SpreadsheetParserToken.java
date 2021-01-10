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
import walkingkooka.spreadsheet.function.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.Whitespace;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.HasExpression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.visit.Visiting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Represents a token within the grammar.
 */
public abstract class SpreadsheetParserToken implements ParserToken,
        HasExpression {

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
     * {@see SpreadsheetExpressionNumberParserToken}
     */
    public static SpreadsheetExpressionNumberParserToken expressionNumber(final ExpressionNumber value, final String text) {
        return SpreadsheetExpressionNumberParserToken.with(value, text);
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
    public static SpreadsheetPowerSymbolParserToken powerSymbol(final String value, final String text) {
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
     * Only {@link SpreadsheetBetweenSymbolParserToken} returns true
     */
    public final boolean isBetweenSymbol() {
        return this instanceof SpreadsheetBetweenSymbolParserToken;
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
     * Only {@link SpreadsheetExpressionNumberParserToken} return true
     */
    public final boolean isExpressionNumber() {
        return this instanceof SpreadsheetExpressionNumberParserToken;
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
     * Only {@link SpreadsheetFunctionParameterSeparatorSymbolParserToken} returns true
     */
    public final boolean isFunctionParameterSeparatorSymbol() {
        return this instanceof SpreadsheetFunctionParameterSeparatorSymbolParserToken;
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
     * Only {@link SpreadsheetLabelNameParserToken} return true
     */
    public final boolean isLabelName() {
        return this instanceof SpreadsheetLabelNameParserToken;
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
     * Only {@link SpreadsheetLocalDateParserToken} return true
     */
    public final boolean isLocalDate() {
        return this instanceof SpreadsheetLocalDateParserToken;
    }

    /**
     * Only {@link SpreadsheetLocalDateTimeParserToken} return true
     */
    public final boolean isLocalDateTime() {
        return this instanceof SpreadsheetLocalDateTimeParserToken;
    }

    /**
     * Only {@link SpreadsheetLocalTimeParserToken} return true
     */
    public final boolean isLocalTime() {
        return this instanceof SpreadsheetLocalTimeParserToken;
    }

    /**
     * Only {@link SpreadsheetMinusSymbolParserToken} returns true
     */
    public final boolean isMinusSymbol() {
        return this instanceof SpreadsheetMinusSymbolParserToken;
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
     * Only {@link SpreadsheetNegativeParserToken} return true
     */
    public final boolean isNegative() {
        return this instanceof SpreadsheetNegativeParserToken;
    }

    /**
     * Only {@link SpreadsheetSymbolParserToken} return true
     */
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
     * Only {@link SpreadsheetPercentageParserToken} return true
     */
    public final boolean isPercentage() {
        return this instanceof SpreadsheetPercentageParserToken;
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
     * Only {@link SpreadsheetRangeParserToken} return true
     */
    public final boolean isRange() {
        return this instanceof SpreadsheetRangeParserToken;
    }

    /**
     * Only {@link SpreadsheetRowReferenceParserToken} return true
     */
    public final boolean isRowReference() {
        return this instanceof SpreadsheetRowReferenceParserToken;
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
     * Only {@link SpreadsheetWhitespaceParserToken} return true
     */
    public final boolean isWhitespace() {
        return this instanceof SpreadsheetWhitespaceParserToken;
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
                this.accept((SpreadsheetParserTokenVisitor) visitor);
            }
            visitor2.endVisit(this);
        }
    }

    abstract void accept(final SpreadsheetParserTokenVisitor visitor);

    // HasExpression................................................................................................

    /**
     * Converts this token to its {@link Expression} equivalent.
     */
    @Override
    public final Optional<Expression> expression(final ExpressionNumberContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetParserTokenToExpressionSpreadsheetParserTokenVisitor.accept(
                this,
                context.expressionNumberKind()
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
                SpreadsheetColumnReferenceParserToken.class,
                SpreadsheetParserToken::unmarshallColumnReference
        );

        registerLeafParserToken(
                SpreadsheetExpressionNumberParserToken.class,
                SpreadsheetParserToken::unmarshallExpressionNumber
        );

        registerLeafParserToken(
                SpreadsheetFunctionNameParserToken.class,
                SpreadsheetParserToken::unmarshallFunctionName
        );

        registerLeafParserToken(
                SpreadsheetLabelNameParserToken.class,
                SpreadsheetParserToken::unmarshallLabelName
        );

        registerLeafParserToken(
                SpreadsheetLocalDateParserToken.class,
                SpreadsheetParserToken::unmarshallLocalDate
        );

        registerLeafParserToken(
                SpreadsheetLocalDateTimeParserToken.class,
                SpreadsheetParserToken::unmarshallLocalDateTime
        );

        registerLeafParserToken(
                SpreadsheetLocalTimeParserToken.class,
                SpreadsheetParserToken::unmarshallLocalTime
        );

        registerLeafParserToken(
                SpreadsheetRowReferenceParserToken.class,
                SpreadsheetParserToken::unmarshallRowReference
        );

        registerLeafParserToken(
                SpreadsheetTextParserToken.class,
                SpreadsheetParserToken::unmarshallText
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

    static SpreadsheetExpressionNumberParserToken unmarshallExpressionNumber(final JsonNode node,
                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                ExpressionNumber.class,
                context,
                SpreadsheetParserToken::expressionNumber
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

    static SpreadsheetLabelNameParserToken unmarshallLabelName(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                SpreadsheetLabelName.class,
                context,
                SpreadsheetParserToken::labelName
        );
    }

    static SpreadsheetLocalDateParserToken unmarshallLocalDate(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                LocalDate.class,
                context,
                SpreadsheetParserToken::localDate
        );
    }

    static SpreadsheetLocalDateTimeParserToken unmarshallLocalDateTime(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                LocalDateTime.class,
                context,
                SpreadsheetParserToken::localDateTime
        );
    }

    static SpreadsheetLocalTimeParserToken unmarshallLocalTime(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                LocalTime.class,
                context,
                SpreadsheetParserToken::localTime
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

    static SpreadsheetTextParserToken unmarshallText(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallLeafParserToken(
                node,
                String.class,
                context,
                SpreadsheetParserToken::text
        );
    }

    // SpreadsheetSymbolParserToken sub classes.........................................................................

    static {
        registerLeafParserToken(
                SpreadsheetBetweenSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallBetweenSymbol
        );

        registerLeafParserToken(
                SpreadsheetDivideSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallDivideSymbol
        );

        registerLeafParserToken(
                SpreadsheetEqualsSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallEqualsSymbol
        );

        registerLeafParserToken(
                SpreadsheetFunctionParameterSeparatorSymbolParserToken.class,
                SpreadsheetParserToken::unmarshallFunctionParameterSeparatorSymbol
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
                SpreadsheetWhitespaceParserToken.class,
                SpreadsheetParserToken::unmarshallWhitespace
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

    static SpreadsheetDivideSymbolParserToken unmarshallDivideSymbol(final JsonNode node,
                                                                     final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::divideSymbol
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

    static SpreadsheetFunctionParameterSeparatorSymbolParserToken unmarshallFunctionParameterSeparatorSymbol(final JsonNode node,
                                                                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallSymbolParserToken(
                node,
                context,
                SpreadsheetParserToken::functionParameterSeparatorSymbol
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
            }
        }

        if (null == value) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(VALUE_PROPERTY, node);
        }
        if (null == text) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(TEXT_PROPERTY, node);
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
                SpreadsheetDivisionParserToken.class,
                SpreadsheetParserToken::unmarshallDivision
        );

        registerParentParserToken(
                SpreadsheetEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallEquals
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
                SpreadsheetNotEqualsParserToken.class,
                SpreadsheetParserToken::unmarshallNotEquals
        );

        registerParentParserToken(
                SpreadsheetPowerParserToken.class,
                SpreadsheetParserToken::unmarshallPower
        );

        registerParentParserToken(
                SpreadsheetRangeParserToken.class,
                SpreadsheetParserToken::unmarshallRange
        );

        registerParentParserToken(
                SpreadsheetSubtractionParserToken.class,
                SpreadsheetParserToken::unmarshallSubtraction
        );

        registerParentParserToken(
                SpreadsheetCellReferenceParserToken.class,
                SpreadsheetParserToken::unmarshallCellReference
        );

        registerParentParserToken(
                SpreadsheetFunctionParserToken.class,
                SpreadsheetParserToken::unmarshallFunction
        );

        registerParentParserToken(
                SpreadsheetGroupParserToken.class,
                SpreadsheetParserToken::unmarshallGroup
        );

        registerParentParserToken(
                SpreadsheetNegativeParserToken.class,
                SpreadsheetParserToken::unmarshallNegative
        );

        registerParentParserToken(
                SpreadsheetPercentageParserToken.class,
                SpreadsheetParserToken::unmarshallPercentage
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

    static SpreadsheetNotEqualsParserToken unmarshallNotEquals(final JsonNode node,
                                                               final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::notEquals
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

    static SpreadsheetRangeParserToken unmarshallRange(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::range
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

    static SpreadsheetCellReferenceParserToken unmarshallCellReference(final JsonNode node,
                                                                       final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::cellReference
        );
    }

    static SpreadsheetFunctionParserToken unmarshallFunction(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::function
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

    static SpreadsheetNegativeParserToken unmarshallNegative(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::negative
        );
    }

    static SpreadsheetPercentageParserToken unmarshallPercentage(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return unmarshallParentParserToken(
                node,
                context,
                SpreadsheetParserToken::percentage
        );
    }

    /**
     * Helper that knows how to unmarshall a sub class of {@link SpreadsheetLeafParserToken}
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
            }
        }

        if (null == value) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(VALUE_PROPERTY, node);
        }
        if (null == text) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(TEXT_PROPERTY, node);
        }

        return factory.apply(value, text);
    }

    /**
     * Handles marshalling any {@link SpreadsheetLeafParserToken}
     */
    private JsonNode marshallParentParserToken(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(VALUE_PROPERTY, context.marshallWithTypeList(Cast.to(this.value()))) // unnecessary to include type.
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
