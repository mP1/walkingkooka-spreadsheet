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

package walkingkooka.spreadsheet.store;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetAdditionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetAmPmParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetApostropheSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetBetweenSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetCellRangeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetConditionRightParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetCurrencySymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDayNameAbbreviationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDayNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDayNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDecimalSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDigitsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDivideSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDivisionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDoubleQuoteSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetErrorParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetExponentSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetExpressionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetFunctionNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetFunctionParametersParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGroupParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGroupSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetHourParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLabelNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMillisecondParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMinusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMinuteParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameAbbreviationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameInitialParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMultiplicationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMultiplySymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNamedFunctionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNegativeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNotEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNotEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParentParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParenthesisCloseSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParenthesisOpenSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTokenVisitor;
import walkingkooka.spreadsheet.parser.SpreadsheetPercentSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPlusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPowerParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPowerSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetSecondsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetSubtractionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTextLiteralParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTextParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetValueSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetWhitespaceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetYearParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A {@link SpreadsheetParserTokenVisitor} that replaces tokens that are {@link Locale} sensitive, such as decimal-separator.
 */
final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    static SpreadsheetParserToken update(final SpreadsheetParserToken token,
                                         final SpreadsheetMetadata metadata,
                                         final Supplier<LocalDateTime> now) {
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor visitor = new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor(
                metadata,
                now
        );
        visitor.accept(token);
        return visitor.children.get(0).cast(SpreadsheetParserToken.class);
    }

    SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor(final SpreadsheetMetadata metadata,
                                                                                                final Supplier<LocalDateTime> now) {
        super();
        this.metadata = metadata;
        this.now = now;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetAdditionParserToken token) {
        this.exit(token, SpreadsheetParserToken::addition);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetCellRangeParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetCellRangeParserToken token) {
        this.exit(token, SpreadsheetParserToken::cellRange);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.exit(token, SpreadsheetParserToken::cellReference);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetConditionRightParserToken token) {
        throw new UnsupportedOperationException(token.toString());
    }

    @Override
    protected void endVisit(final SpreadsheetConditionRightParserToken token) {
        throw new UnsupportedOperationException(token.toString());
    }

    @Override
    protected Visiting startVisit(final SpreadsheetDateParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetDateParserToken token) {
        this.exit(token, SpreadsheetParserToken::date);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetDateTimeParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetDateTimeParserToken token) {
        this.exit(token, SpreadsheetParserToken::dateTime);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetDivisionParserToken token) {
        this.exit(token, SpreadsheetParserToken::division);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::equalsParserToken);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetExpressionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetExpressionParserToken token) {
        this.exit(token, SpreadsheetParserToken::expression);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFunctionParametersParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetFunctionParametersParserToken token) {
        this.exit(token, SpreadsheetParserToken::functionParameters);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThan);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThanEquals);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGroupParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetGroupParserToken token) {
        this.exit(token, SpreadsheetParserToken::group);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThan);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThanEquals);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetMultiplicationParserToken token) {
        this.exit(token, SpreadsheetParserToken::multiplication);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNamedFunctionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetNamedFunctionParserToken token) {
        this.exit(token, SpreadsheetParserToken::namedFunction);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetNegativeParserToken token) {
        this.exit(token, SpreadsheetParserToken::negative);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetNotEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::notEquals);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNumberParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetNumberParserToken token) {
        this.exit(token, SpreadsheetParserToken::number);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetPowerParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetPowerParserToken token) {
        this.exit(token, SpreadsheetParserToken::power);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetSubtractionParserToken token) {
        this.exit(token, SpreadsheetParserToken::subtraction);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetTextParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetTextParserToken token) {
        this.exit(token, SpreadsheetParserToken::text);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetTimeParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetTimeParserToken token) {
        this.exit(token, SpreadsheetParserToken::time);
    }

    // leaf ......................................................................................................

    @Override
    protected void visit(final SpreadsheetAmPmParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetParserToken.amPm(
                        value,
                        this.dateTimeContext()
                                .ampm(value)
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetApostropheSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetBetweenSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetColumnReferenceParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetCurrencySymbolParserToken token) {
        this.leafString(
                SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL,
                SpreadsheetParserToken::currencySymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetDayNameParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetParserToken.dayName(
                        value,
                        this.dateTimeContext()
                                .weekDayName(value)
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetDayNameAbbreviationParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetParserToken.dayNameAbbreviation(
                        value,
                        this.dateTimeContext()
                                .weekDayNameAbbreviation(value)
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetDayNumberParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetDecimalSeparatorSymbolParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
                SpreadsheetParserToken::decimalSeparatorSymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetDigitsParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetDivideSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetDoubleQuoteSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetErrorParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetExponentSymbolParserToken token) {
        this.leafString(
                SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL,
                SpreadsheetParserToken::exponentSymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetFunctionNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetGreaterThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetGreaterThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetGroupSeparatorSymbolParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.GROUP_SEPARATOR,
                SpreadsheetParserToken::groupSeparatorSymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetHourParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetLabelNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetLessThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetLessThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetMillisecondParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetMinusSymbolParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.NEGATIVE_SIGN,
                SpreadsheetParserToken::minusSymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetMinuteParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetMonthNameParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetParserToken.monthName(
                        value,
                        this.dateTimeContext()
                                .monthName(value)
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetMonthNameAbbreviationParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetParserToken.monthNameAbbreviation(
                        value,
                        this.dateTimeContext()
                                .monthNameAbbreviation(value)
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetMonthNameInitialParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetParserToken.monthNameInitial(
                        value,
                        this.dateTimeContext()
                                .monthName(value).substring(0, 1)
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetMonthNumberParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetMultiplySymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetNotEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetParenthesisCloseSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetParenthesisOpenSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetPercentSymbolParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL,
                SpreadsheetParserToken::percentSymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetPlusSymbolParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.POSITIVE_SIGN,
                SpreadsheetParserToken::plusSymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetPowerSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetRowReferenceParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetSecondsParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetTextLiteralParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetValueSeparatorSymbolParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.VALUE_SEPARATOR,
                SpreadsheetParserToken::valueSeparatorSymbol
        );
    }

    @Override
    protected void visit(final SpreadsheetWhitespaceParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetYearParserToken token) {
        this.leaf(token);
    }

    // helpers..........................................................................................................

    @SuppressWarnings("SameReturnValue")
    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();

        return Visiting.CONTINUE;
    }

    private <PP extends SpreadsheetParentParserToken> void exit(final PP parent,
                                                                final BiFunction<List<ParserToken>, String, PP> factory) {
        final List<ParserToken> children = this.children;
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
        this.add(factory.apply(children, ParserToken.text(children)));
    }

    /**
     * Creates the {@link SpreadsheetParserToken} using the {@link Character} for the {@link SpreadsheetMetadataPropertyName}.
     */
    private void leafString(final SpreadsheetMetadataPropertyName<String> property,
                            final BiFunction<String, String, SpreadsheetParserToken> factory) {
        final String text = this.metadata.getOrFail(property);
        this.leaf(factory.apply(text, text));
    }

    /**
     * Creates the {@link SpreadsheetParserToken} using the {@link Character} for the {@link SpreadsheetMetadataPropertyName}.
     */
    private void leafCharacter(final SpreadsheetMetadataPropertyName<Character> property,
                               final BiFunction<String, String, SpreadsheetParserToken> factory) {
        final String text = Character.toString(this.metadata.getOrFail(property));
        this.leaf(factory.apply(text, text));
    }

    private void leaf(final ParserToken token) {
        this.add(token);
    }

    private void add(final ParserToken child) {
        Objects.requireNonNull(child, "child");
        this.children.add(child);
    }

    private Stack<List<ParserToken>> previousChildren = Stacks.arrayList();

    private List<ParserToken> children = Lists.array();

    private final SpreadsheetMetadata metadata;

    private DateTimeContext dateTimeContext() {
        if (null == this.dateTimeContext) {
            this.dateTimeContext = this.metadata.dateTimeContext(this.now);
        }
        return this.dateTimeContext;
    }

    private final Supplier<LocalDateTime> now;

    private DateTimeContext dateTimeContext;

    @Override
    public String toString() {
        return this.metadata.toString();
    }
}
