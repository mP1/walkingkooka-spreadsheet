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
import walkingkooka.datetime.HasNow;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.AdditionSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.AmPmSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ApostropheSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.BetweenSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.CellRangeSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.CellReferenceSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ColumnReferenceSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ConditionRightEqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ConditionRightGreaterThanEqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ConditionRightGreaterThanSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ConditionRightLessThanEqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ConditionRightLessThanSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ConditionRightNotEqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.CurrencySymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DateSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DateTimeSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DayNameAbbreviationSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DayNameSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DayNumberSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DecimalSeparatorSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DigitsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DivideSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DivisionSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.DoubleQuoteSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.EqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.EqualsSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ErrorSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ExponentSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ExpressionSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.FunctionNameSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.FunctionParametersSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.GreaterThanEqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.GreaterThanEqualsSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.GreaterThanSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.GreaterThanSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.GroupSeparatorSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.GroupSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.HourSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.LabelNameSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.LessThanEqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.LessThanEqualsSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.LessThanSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.LessThanSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MillisecondSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MinusSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MinuteSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MonthNameAbbreviationSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MonthNameInitialSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MonthNameSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MonthNumberSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MultiplicationSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.MultiplySymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.NamedFunctionSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.NegativeSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.NotEqualsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.NotEqualsSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.NumberSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ParentSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ParenthesisCloseSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ParenthesisOpenSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.PercentSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.PlusSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.PowerSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.PowerSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.RowReferenceSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SecondsSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTokenVisitor;
import walkingkooka.spreadsheet.parser.SubtractionSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.TextLiteralSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.TextSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.TimeSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.ValueSeparatorSymbolSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.WhitespaceSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.YearSpreadsheetParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetParserTokenVisitor} that replaces tokens that are {@link Locale} sensitive, such as decimal-separator.
 */
final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    static SpreadsheetParserToken update(final SpreadsheetParserToken token,
                                         final SpreadsheetMetadata metadata,
                                         final HasNow now) {
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor visitor = new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor(
                metadata,
                now
        );
        visitor.accept(token);
        return visitor.children.get(0).cast(SpreadsheetParserToken.class);
    }

    SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetParserTokenVisitor(final SpreadsheetMetadata metadata,
                                                                                                final HasNow now) {
        super();
        this.metadata = metadata;
        this.now = now;
    }

    @Override
    protected Visiting startVisit(final AdditionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final AdditionSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::addition);
    }

    @Override
    protected Visiting startVisit(final CellRangeSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellRangeSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::cellRange);
    }

    @Override
    protected Visiting startVisit(final CellReferenceSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellReferenceSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::cellReference);
    }

    @Override
    protected Visiting startVisit(final ConditionRightEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightNotEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightNotEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::date);
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::dateTime);
    }

    @Override
    protected Visiting startVisit(final DivisionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DivisionSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::division);
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::equalsSpreadsheetParserToken);
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final ExpressionSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::expression);
    }

    @Override
    protected Visiting startVisit(final FunctionParametersSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final FunctionParametersSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::functionParameters);
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThan);
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThanEquals);
    }

    @Override
    protected Visiting startVisit(final GroupSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GroupSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::group);
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThan);
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThanEquals);
    }

    @Override
    protected Visiting startVisit(final MultiplicationSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final MultiplicationSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::multiplication);
    }

    @Override
    protected Visiting startVisit(final NamedFunctionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NamedFunctionSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::namedFunction);
    }

    @Override
    protected Visiting startVisit(final NegativeSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NegativeSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::negative);
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::notEquals);
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NumberSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::number);
    }

    @Override
    protected Visiting startVisit(final PowerSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final PowerSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::power);
    }

    @Override
    protected Visiting startVisit(final SubtractionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SubtractionSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::subtraction);
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final TextSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::text);
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final TimeSpreadsheetParserToken token) {
        this.exit(token, SpreadsheetParserToken::time);
    }

    // leaf ......................................................................................................

    @Override
    protected void visit(final AmPmSpreadsheetParserToken token) {
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
    protected void visit(final ApostropheSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final BetweenSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ColumnReferenceSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final CurrencySymbolSpreadsheetParserToken token) {
        this.leafString(
                SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL,
                SpreadsheetParserToken::currencySymbol
        );
    }

    @Override
    protected void visit(final DayNameSpreadsheetParserToken token) {
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
    protected void visit(final DayNameAbbreviationSpreadsheetParserToken token) {
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
    protected void visit(final DayNumberSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final DecimalSeparatorSymbolSpreadsheetParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
                SpreadsheetParserToken::decimalSeparatorSymbol
        );
    }

    @Override
    protected void visit(final DigitsSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final DivideSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final DoubleQuoteSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final EqualsSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ErrorSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetParserToken token) {
        this.leafString(
                SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL,
                SpreadsheetParserToken::exponentSymbol
        );
    }

    @Override
    protected void visit(final FunctionNameSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final GreaterThanSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final GreaterThanEqualsSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final GroupSeparatorSymbolSpreadsheetParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.GROUP_SEPARATOR,
                SpreadsheetParserToken::groupSeparatorSymbol
        );
    }

    @Override
    protected void visit(final HourSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final LabelNameSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final LessThanSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final LessThanEqualsSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MillisecondSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MinusSymbolSpreadsheetParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.NEGATIVE_SIGN,
                SpreadsheetParserToken::minusSymbol
        );
    }

    @Override
    protected void visit(final MinuteSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MonthNameSpreadsheetParserToken token) {
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
    protected void visit(final MonthNameAbbreviationSpreadsheetParserToken token) {
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
    protected void visit(final MonthNameInitialSpreadsheetParserToken token) {
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
    protected void visit(final MonthNumberSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MultiplySymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final NotEqualsSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ParenthesisCloseSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ParenthesisOpenSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final PercentSymbolSpreadsheetParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL,
                SpreadsheetParserToken::percentSymbol
        );
    }

    @Override
    protected void visit(final PlusSymbolSpreadsheetParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.POSITIVE_SIGN,
                SpreadsheetParserToken::plusSymbol
        );
    }

    @Override
    protected void visit(final PowerSymbolSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final RowReferenceSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SecondsSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ValueSeparatorSymbolSpreadsheetParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.VALUE_SEPARATOR,
                SpreadsheetParserToken::valueSeparatorSymbol
        );
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final YearSpreadsheetParserToken token) {
        this.leaf(token);
    }

    // helpers..........................................................................................................

    @SuppressWarnings("SameReturnValue")
    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();

        return Visiting.CONTINUE;
    }

    private <PP extends ParentSpreadsheetParserToken> void exit(final PP parent,
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

    private final HasNow now;

    private DateTimeContext dateTimeContext;

    @Override
    public String toString() {
        return this.metadata.toString();
    }
}
