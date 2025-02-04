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
import walkingkooka.spreadsheet.formula.AdditionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.AmPmSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ApostropheSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.BetweenSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.BooleanLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.BooleanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.CellRangeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.CellReferenceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ColumnReferenceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ConditionRightEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ConditionRightGreaterThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ConditionRightLessThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ConditionRightLessThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ConditionRightNotEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.CurrencySymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DateTimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DayNameAbbreviationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DayNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DayNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DecimalSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DigitsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DivideSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DivisionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.DoubleQuoteSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.EqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.EqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ErrorSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ExponentSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ExpressionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.FunctionNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.FunctionParametersSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.GreaterThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.GreaterThanEqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.GreaterThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.GreaterThanSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.GroupSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.GroupSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.HourSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.LabelNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.LessThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.LessThanEqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.LessThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.LessThanSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MillisecondSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MinusSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MinuteSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MonthNameAbbreviationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MonthNameInitialSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MonthNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MonthNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MultiplicationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.MultiplySymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.NamedFunctionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.NegativeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.NotEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.NotEqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ParentSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ParenthesisCloseSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ParenthesisOpenSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.PercentSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.PlusSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.PowerSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.PowerSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.RowReferenceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.SecondsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParserTokenVisitor;
import walkingkooka.spreadsheet.formula.SubtractionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.TemplateValueNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.TextLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.TextSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.ValueSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.WhitespaceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.YearSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetFormulaParserTokenVisitor} that replaces tokens that are {@link Locale} sensitive, such as decimal-separator.
 */
final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor extends SpreadsheetFormulaParserTokenVisitor {

    static SpreadsheetFormulaParserToken update(final SpreadsheetFormulaParserToken token,
                                                final SpreadsheetMetadata metadata,
                                                final HasNow now) {
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor visitor = new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor(
                metadata,
                now
        );
        visitor.accept(token);
        return visitor.children.get(0).cast(SpreadsheetFormulaParserToken.class);
    }

    SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor(final SpreadsheetMetadata metadata,
                                                                                                       final HasNow now) {
        super();
        this.metadata = metadata;
        this.now = now;
    }

    @Override
    protected Visiting startVisit(final AdditionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final AdditionSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::addition);
    }

    @Override
    protected Visiting startVisit(final BooleanSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final BooleanSpreadsheetFormulaParserToken token) {
        this.exit(
                token,
                SpreadsheetFormulaParserToken::booleanValue
        );
    }

    @Override
    protected Visiting startVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::cellRange);
    }

    @Override
    protected Visiting startVisit(final CellReferenceSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellReferenceSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::cellReference);
    }

    @Override
    protected Visiting startVisit(final ConditionRightEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightNotEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightNotEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::date);
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::dateTime);
    }

    @Override
    protected Visiting startVisit(final DivisionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DivisionSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::division);
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::equalsSpreadsheetFormulaParserToken);
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::expression);
    }

    @Override
    protected Visiting startVisit(final FunctionParametersSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final FunctionParametersSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::functionParameters);
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::greaterThan);
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::greaterThanEquals);
    }

    @Override
    protected Visiting startVisit(final GroupSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GroupSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::group);
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::lessThan);
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::lessThanEquals);
    }

    @Override
    protected Visiting startVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::multiplication);
    }

    @Override
    protected Visiting startVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::namedFunction);
    }

    @Override
    protected Visiting startVisit(final NegativeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NegativeSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::negative);
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::notEquals);
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NumberSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::number);
    }

    @Override
    protected Visiting startVisit(final PowerSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final PowerSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::power);
    }

    @Override
    protected Visiting startVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::subtraction);
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final TextSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::text);
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final TimeSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::time);
    }

    // leaf ......................................................................................................

    @Override
    protected void visit(final AmPmSpreadsheetFormulaParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetFormulaParserToken.amPm(
                        value,
                        this.dateTimeContext()
                                .ampm(value)
                )
        );
    }

    @Override
    protected void visit(final ApostropheSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final BetweenSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final BooleanLiteralSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ColumnReferenceSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final CurrencySymbolSpreadsheetFormulaParserToken token) {
        this.leafString(
                SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL,
                SpreadsheetFormulaParserToken::currencySymbol
        );
    }

    @Override
    protected void visit(final DayNameSpreadsheetFormulaParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetFormulaParserToken.dayName(
                        value,
                        this.dateTimeContext()
                                .weekDayName(value)
                )
        );
    }

    @Override
    protected void visit(final DayNameAbbreviationSpreadsheetFormulaParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetFormulaParserToken.dayNameAbbreviation(
                        value,
                        this.dateTimeContext()
                                .weekDayNameAbbreviation(value)
                )
        );
    }

    @Override
    protected void visit(final DayNumberSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final DecimalSeparatorSymbolSpreadsheetFormulaParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
                SpreadsheetFormulaParserToken::decimalSeparatorSymbol
        );
    }

    @Override
    protected void visit(final DigitsSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final DivideSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final DoubleQuoteSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final EqualsSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ErrorSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetFormulaParserToken token) {
        this.leafString(
                SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL,
                SpreadsheetFormulaParserToken::exponentSymbol
        );
    }

    @Override
    protected void visit(final FunctionNameSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final GreaterThanSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final GroupSeparatorSymbolSpreadsheetFormulaParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.GROUP_SEPARATOR,
                SpreadsheetFormulaParserToken::groupSeparatorSymbol
        );
    }

    @Override
    protected void visit(final HourSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final LabelNameSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final LessThanSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final LessThanEqualsSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MillisecondSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MinusSymbolSpreadsheetFormulaParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.NEGATIVE_SIGN,
                SpreadsheetFormulaParserToken::minusSymbol
        );
    }

    @Override
    protected void visit(final MinuteSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MonthNameSpreadsheetFormulaParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetFormulaParserToken.monthName(
                        value,
                        this.dateTimeContext()
                                .monthName(value)
                )
        );
    }

    @Override
    protected void visit(final MonthNameAbbreviationSpreadsheetFormulaParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetFormulaParserToken.monthNameAbbreviation(
                        value,
                        this.dateTimeContext()
                                .monthNameAbbreviation(value)
                )
        );
    }

    @Override
    protected void visit(final MonthNameInitialSpreadsheetFormulaParserToken token) {
        final int value = token.value();

        this.leaf(
                SpreadsheetFormulaParserToken.monthNameInitial(
                        value,
                        this.dateTimeContext()
                                .monthName(value).substring(0, 1)
                )
        );
    }

    @Override
    protected void visit(final MonthNumberSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final MultiplySymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final NotEqualsSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ParenthesisCloseSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ParenthesisOpenSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final PercentSymbolSpreadsheetFormulaParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL,
                SpreadsheetFormulaParserToken::percentSymbol
        );
    }

    @Override
    protected void visit(final PlusSymbolSpreadsheetFormulaParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.POSITIVE_SIGN,
                SpreadsheetFormulaParserToken::plusSymbol
        );
    }

    @Override
    protected void visit(final PowerSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final RowReferenceSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final SecondsSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final TemplateValueNameSpreadsheetFormulaParserToken token) {
        throw new IllegalStateException("Cells should never have a " + TemplateValueNameSpreadsheetFormulaParserToken.class.getSimpleName());
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final ValueSeparatorSymbolSpreadsheetFormulaParserToken token) {
        this.leafCharacter(
                SpreadsheetMetadataPropertyName.VALUE_SEPARATOR,
                SpreadsheetFormulaParserToken::valueSeparatorSymbol
        );
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final YearSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    // helpers..........................................................................................................

    @SuppressWarnings("SameReturnValue")
    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();

        return Visiting.CONTINUE;
    }

    private <PP extends ParentSpreadsheetFormulaParserToken> void exit(final PP parent,
                                                                       final BiFunction<List<ParserToken>, String, PP> factory) {
        final List<ParserToken> children = this.children;
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
        this.add(factory.apply(children, ParserToken.text(children)));
    }

    /**
     * Creates the {@link SpreadsheetFormulaParserToken} using the {@link Character} for the {@link SpreadsheetMetadataPropertyName}.
     */
    private void leafString(final SpreadsheetMetadataPropertyName<String> property,
                            final BiFunction<String, String, SpreadsheetFormulaParserToken> factory) {
        final String text = this.metadata.getOrFail(property);
        this.leaf(factory.apply(text, text));
    }

    /**
     * Creates the {@link SpreadsheetFormulaParserToken} using the {@link Character} for the {@link SpreadsheetMetadataPropertyName}.
     */
    private void leafCharacter(final SpreadsheetMetadataPropertyName<Character> property,
                               final BiFunction<String, String, SpreadsheetFormulaParserToken> factory) {
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
