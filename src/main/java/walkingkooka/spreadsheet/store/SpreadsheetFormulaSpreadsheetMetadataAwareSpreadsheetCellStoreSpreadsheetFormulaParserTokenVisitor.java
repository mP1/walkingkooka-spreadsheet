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
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.formula.parser.AdditionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.AmPmSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ApostropheSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.BetweenSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.BooleanLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.BooleanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.CellRangeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ConditionRightEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ConditionRightGreaterThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ConditionRightLessThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ConditionRightLessThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ConditionRightNotEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.CurrencySymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DateTimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DayNameAbbreviationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DayNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DayNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DecimalSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DigitsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DivideSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DivisionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.DoubleQuoteSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.EqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.EqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ErrorSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ExponentSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ExpressionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.FunctionNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.FunctionParametersSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GreaterThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GreaterThanEqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GreaterThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GreaterThanSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GroupSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.GroupSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.HourSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LabelSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LessThanEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LessThanEqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LessThanSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.LessThanSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MillisecondSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MinusSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MinuteSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNameAbbreviationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNameInitialSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MonthNumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MultiplicationSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.MultiplySymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NamedFunctionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NegativeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NotEqualsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NotEqualsSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ParentSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ParenthesisCloseSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ParenthesisOpenSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.PercentSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.PlusSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.PowerSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.PowerSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SecondsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserTokenVisitor;
import walkingkooka.spreadsheet.formula.parser.SubtractionSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TemplateValueNameSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TextLiteralSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TextSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.TimeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ValueSeparatorSymbolSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.WhitespaceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.YearSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link SpreadsheetFormulaParserTokenVisitor} that replaces tokens that are {@link Locale} sensitive, such as decimal-separator.
 */
final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor extends SpreadsheetFormulaParserTokenVisitor {

    static SpreadsheetFormulaParserToken update(final SpreadsheetCell cell,
                                                final SpreadsheetFormulaParserToken token,
                                                final SpreadsheetMetadata metadata,
                                                final HasNow now,
                                                final LocaleContext localeContext) {
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor visitor = new SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor(
            cell,
            metadata,
            now,
            localeContext
        );
        visitor.accept(token);
        return visitor.children.get(0).cast(SpreadsheetFormulaParserToken.class);
    }

    SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreSpreadsheetFormulaParserTokenVisitor(final SpreadsheetCell cell,
                                                                                                       final SpreadsheetMetadata metadata,
                                                                                                       final HasNow now,
                                                                                                       final LocaleContext localeContext) {
        super();

        this.cell = cell;
        this.metadata = metadata;
        this.now = now;
        this.localeContext = localeContext;
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
    protected Visiting startVisit(final CellSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellSpreadsheetFormulaParserToken token) {
        this.exit(token, SpreadsheetFormulaParserToken::cell);
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
    protected void visit(final ColumnSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final CurrencySymbolSpreadsheetFormulaParserToken token) {
        this.decimalNumberSymbols(
            DecimalNumberSymbols::currencySymbol,
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
        this.decimalNumberSymbols(
            DecimalNumberSymbols::decimalSeparator,
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
        this.decimalNumberSymbols(
            DecimalNumberSymbols::exponentSymbol,
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
        this.decimalNumberSymbols(
            DecimalNumberSymbols::groupSeparator,
            SpreadsheetFormulaParserToken::groupSeparatorSymbol
        );
    }

    @Override
    protected void visit(final HourSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final LabelSpreadsheetFormulaParserToken token) {
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
        this.decimalNumberSymbols(
            DecimalNumberSymbols::negativeSign,
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
        this.decimalNumberSymbols(
            DecimalNumberSymbols::percentSymbol,
            SpreadsheetFormulaParserToken::percentSymbol
        );
    }

    @Override
    protected void visit(final PlusSymbolSpreadsheetFormulaParserToken token) {
        this.decimalNumberSymbols(
            DecimalNumberSymbols::positiveSign,
            SpreadsheetFormulaParserToken::plusSymbol
        );
    }

    @Override
    protected void visit(final PowerSymbolSpreadsheetFormulaParserToken token) {
        this.leaf(token);
    }

    @Override
    protected void visit(final RowSpreadsheetFormulaParserToken token) {
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
        final String text = Character.toString(
            this.metadata.getOrFail(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR)
        );
        this.leaf(
            SpreadsheetFormulaParserToken.valueSeparatorSymbol(
                text,
                text
            )
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

    private void decimalNumberSymbols(final Function<DecimalNumberSymbols, ?> decimalNumberSymbolsGetter,
                                      final BiFunction<String, String, SpreadsheetFormulaParserToken> parserTokenFactory) {
        if (null == this.decimalNumberSymbols) {
            this.decimalNumberSymbols = this.metadata.getOrFail(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS);
        }

        final String text = decimalNumberSymbolsGetter.apply(this.decimalNumberSymbols)
            .toString();

        this.leaf(
            parserTokenFactory.apply(
                text,
                text
            )
        );
    }

    private DecimalNumberSymbols decimalNumberSymbols;

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
            this.dateTimeContext = this.metadata.dateTimeContext(
                Optional.of(this.cell),
                this.now,
                this.localeContext
            );
        }
        return this.dateTimeContext;
    }

    private DateTimeContext dateTimeContext;

    private final SpreadsheetCell cell;

    private final HasNow now;

    private final LocaleContext localeContext;

    @Override
    public String toString() {
        return this.metadata.toString();
    }
}
