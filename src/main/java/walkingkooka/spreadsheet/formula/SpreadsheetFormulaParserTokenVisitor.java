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

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.visit.Visiting;

public abstract class SpreadsheetFormulaParserTokenVisitor extends ParserTokenVisitor {

    // AdditionSpreadsheetFormulaParserToken............................................................................

    protected Visiting startVisit(final AdditionSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final AdditionSpreadsheetFormulaParserToken token) {
        // nop
    }

    // BooleanSpreadsheetFormulaParserToken.............................................................................

    protected Visiting startVisit(final BooleanSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final BooleanSpreadsheetFormulaParserToken token) {
        // nop
    }

    // CellRangeSpreadsheetFormulaParserToken...........................................................................

    protected Visiting startVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        // nop
    }

    // CellSpreadsheetFormulaParserToken.......................................................................

    protected Visiting startVisit(final CellSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final CellSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ConditionRightEqualsSpreadsheetFormulaParserToken................................................................

    protected Visiting startVisit(final ConditionRightEqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightEqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ConditionRightGreaterThanSpreadsheetFormulaParserToken...........................................................

    protected Visiting startVisit(final ConditionRightGreaterThanSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightGreaterThanSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken.....................................................

    protected Visiting startVisit(final ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ConditionRightLessThanSpreadsheetFormulaParserToken..............................................................

    protected Visiting startVisit(final ConditionRightLessThanSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightLessThanSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ConditionRightLessThanEqualsSpreadsheetFormulaParserToken........................................................

    protected Visiting startVisit(final ConditionRightLessThanEqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightLessThanEqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ConditionRightNotEqualsSpreadsheetFormulaParserToken.............................................................

    protected Visiting startVisit(final ConditionRightNotEqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightNotEqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // DateSpreadsheetFormulaParserToken................................................................................

    protected Visiting startVisit(final DateSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DateSpreadsheetFormulaParserToken token) {
        // nop
    }

    // DateTimeSpreadsheetFormulaParserToken............................................................................

    protected Visiting startVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        // nop
    }

    // DivisionSpreadsheetFormulaParserToken............................................................................

    protected Visiting startVisit(final DivisionSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DivisionSpreadsheetFormulaParserToken token) {
        // nop
    }

    // EqualsSpreadsheetFormulaParserToken..............................................................................

    protected Visiting startVisit(final EqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final EqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ExpressionSpreadsheetFormulaParserToken..........................................................................

    protected Visiting startVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        // nop
    }

    // FunctionParametersSpreadsheetFormulaParserToken..................................................................

    protected Visiting startVisit(final FunctionParametersSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final FunctionParametersSpreadsheetFormulaParserToken token) {
        // nop
    }

    // GreaterThanSpreadsheetFormulaParserToken....................................................................................

    protected Visiting startVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        // nop
    }

    // GreaterThanEqualsSpreadsheetFormulaParserToken...................................................................

    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // GroupSpreadsheetFormulaParserToken...............................................................................

    protected Visiting startVisit(final GroupSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GroupSpreadsheetFormulaParserToken token) {
        // nop
    }

    // LambdaFunctionSpreadsheetFormulaParserToken......................................................................

    protected Visiting startVisit(final LambdaFunctionSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LambdaFunctionSpreadsheetFormulaParserToken token) {
        // nop
    }

    // LessThanSpreadsheetFormulaParserToken............................................................................

    protected Visiting startVisit(final LessThanSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LessThanSpreadsheetFormulaParserToken token) {
        // nop
    }

    // LessThanEqualsSpreadsheetFormulaParserToken......................................................................

    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // MultiplicationSpreadsheetFormulaParserToken......................................................................

    protected Visiting startVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        // nop
    }

    // NamedFunctionSpreadsheetFormulaParserToken........................................................................

    protected Visiting startVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
        // nop
    }

    // NegativeSpreadsheetFormulaParserToken............................................................................

    protected Visiting startVisit(final NegativeSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NegativeSpreadsheetFormulaParserToken token) {
        // nop
    }

    // NotEqualsSpreadsheetFormulaParserToken...........................................................................

    protected Visiting startVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        // nop
    }

    // NumberSpreadsheetFormulaParserToken..............................................................................

    protected Visiting startVisit(final NumberSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NumberSpreadsheetFormulaParserToken token) {
        // nop
    }

    // PowerSpreadsheetFormulaParserToken...............................................................................

    protected Visiting startVisit(final PowerSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final PowerSpreadsheetFormulaParserToken token) {
        // nop
    }

    // SubtractionSpreadsheetFormulaParserToken.........................................................................
    protected Visiting startVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        // nop
    }

    // TextSpreadsheetFormulaParserToken................................................................................

    protected Visiting startVisit(final TextSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final TextSpreadsheetFormulaParserToken token) {
        // nop
    }

    // TimeSpreadsheetFormulaParserToken................................................................................

    protected Visiting startVisit(final TimeSpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final TimeSpreadsheetFormulaParserToken token) {
        // nop
    }

    // LeafSpreadsheetFormulaParserToken ...............................................................................

    protected void visit(final AmPmSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final ApostropheSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final BetweenSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final BooleanLiteralSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final ColumnSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final CurrencySymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final DayNameSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final DayNameAbbreviationSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final DayNumberSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final DecimalSeparatorSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final DigitsSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final DivideSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final DoubleQuoteSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final EqualsSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final ErrorSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final ExponentSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final FunctionNameSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final GreaterThanSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final GroupSeparatorSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final HourSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final LabelSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final LessThanSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final LessThanEqualsSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MillisecondSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MinusSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MinuteSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MonthNameSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MonthNameAbbreviationSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MonthNameInitialSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MonthNumberSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final MultiplySymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final NotEqualsSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final ParenthesisCloseSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final ParenthesisOpenSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final PercentSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final PlusSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final PowerSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final RowSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final SecondsSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final TemplateValueNameSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final TextLiteralSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final ValueSeparatorSymbolSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final WhitespaceSpreadsheetFormulaParserToken token) {
        // nop
    }

    protected void visit(final YearSpreadsheetFormulaParserToken token) {
        // nop
    }

    // ParserToken.......................................................................................................

    @Override
    protected Visiting startVisit(final ParserToken token) {
        return Visiting.CONTINUE;
    }

    @Override
    protected void endVisit(final ParserToken token) {
        // nop
    }

    // SpreadsheetFormulaParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetFormulaParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormulaParserToken token) {
        // nop
    }
}
