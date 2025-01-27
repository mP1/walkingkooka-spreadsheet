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

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.visit.Visiting;

public abstract class SpreadsheetParserTokenVisitor extends ParserTokenVisitor {

    // AdditionSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final AdditionSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final AdditionSpreadsheetParserToken token) {
        // nop
    }

    // CellRangeSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final CellRangeSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final CellRangeSpreadsheetParserToken token) {
        // nop
    }

    // CellReferenceSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final CellReferenceSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final CellReferenceSpreadsheetParserToken token) {
        // nop
    }

    // ConditionRightEqualsSpreadsheetParserToken.......................................................................

    protected Visiting startVisit(final ConditionRightEqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightEqualsSpreadsheetParserToken token) {
        // nop
    }

    // ConditionRightGreaterThanSpreadsheetParserToken..................................................................

    protected Visiting startVisit(final ConditionRightGreaterThanSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightGreaterThanSpreadsheetParserToken token) {
        // nop
    }

    // ConditionRightGreaterThanEqualsSpreadsheetParserToken............................................................

    protected Visiting startVisit(final ConditionRightGreaterThanEqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightGreaterThanEqualsSpreadsheetParserToken token) {
        // nop
    }

    // ConditionRightLessThanSpreadsheetParserToken.....................................................................

    protected Visiting startVisit(final ConditionRightLessThanSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightLessThanSpreadsheetParserToken token) {
        // nop
    }

    // ConditionRightLessThanEqualsSpreadsheetParserToken................................................................

    protected Visiting startVisit(final ConditionRightLessThanEqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightLessThanEqualsSpreadsheetParserToken token) {
        // nop
    }

    // ConditionRightNotEqualsSpreadsheetParserToken.....................................................................

    protected Visiting startVisit(final ConditionRightNotEqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConditionRightNotEqualsSpreadsheetParserToken token) {
        // nop
    }

    // DateSpreadsheetParserToken.......................................................................................

    protected Visiting startVisit(final DateSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DateSpreadsheetParserToken token) {
        // nop
    }

    // DateTimeSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final DateTimeSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DateTimeSpreadsheetParserToken token) {
        // nop
    }

    // DivisionSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final DivisionSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DivisionSpreadsheetParserToken token) {
        // nop
    }

    // EqualsSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final EqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final EqualsSpreadsheetParserToken token) {
        // nop
    }

    // ExpressionSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final ExpressionSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ExpressionSpreadsheetParserToken token) {
        // nop
    }

    // FunctionParametersSpreadsheetParserToken.........................................................................

    protected Visiting startVisit(final FunctionParametersSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final FunctionParametersSpreadsheetParserToken token) {
        // nop
    }

    // GreaterThanSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final GreaterThanSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GreaterThanSpreadsheetParserToken token) {
        // nop
    }

    // GreaterThanEqualsSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        // nop
    }

    // GroupSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final GroupSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GroupSpreadsheetParserToken token) {
        // nop
    }

    // LambdaFunctionSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final LambdaFunctionSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LambdaFunctionSpreadsheetParserToken token) {
        // nop
    }

    // LessThanSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final LessThanSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LessThanSpreadsheetParserToken token) {
        // nop
    }

    // LessThanEqualsSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final LessThanEqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LessThanEqualsSpreadsheetParserToken token) {
        // nop
    }

    // MultiplicationSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final MultiplicationSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final MultiplicationSpreadsheetParserToken token) {
        // nop
    }

    // NamedFunctionSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final NamedFunctionSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NamedFunctionSpreadsheetParserToken token) {
        // nop
    }

    // NegativeSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final NegativeSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NegativeSpreadsheetParserToken token) {
        // nop
    }

    // NotEqualsSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final NotEqualsSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NotEqualsSpreadsheetParserToken token) {
        // nop
    }

    // NumberSpreadsheetParserToken.....................................................................................

    protected Visiting startVisit(final NumberSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NumberSpreadsheetParserToken token) {
        // nop
    }

    // PowerSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final PowerSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final PowerSpreadsheetParserToken token) {
        // nop
    }

    // SubtractionSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final SubtractionSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SubtractionSpreadsheetParserToken token) {
        // nop
    }

    // TextSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final TextSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final TextSpreadsheetParserToken token) {
        // nop
    }

    // TimeSpreadsheetParserToken....................................................................................

    protected Visiting startVisit(final TimeSpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final TimeSpreadsheetParserToken token) {
        // nop
    }

    // LeafSpreadsheetParserToken ....................................................................................

    protected void visit(final AmPmSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final ApostropheSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final BetweenSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final ColumnReferenceSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final CurrencySymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final DayNameSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final DayNameAbbreviationSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final DayNumberSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final DecimalSeparatorSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final DigitsSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final DivideSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final DoubleQuoteSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final EqualsSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final ErrorSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final ExponentSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final FunctionNameSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final GreaterThanSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final GreaterThanEqualsSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final GroupSeparatorSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final HourSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final LabelNameSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final LessThanSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final LessThanEqualsSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MillisecondSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MinusSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MinuteSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MonthNameSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MonthNameAbbreviationSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MonthNameInitialSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MonthNumberSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final MultiplySymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final NotEqualsSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final ParenthesisCloseSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final ParenthesisOpenSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final PercentSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final PlusSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final PowerSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final RowReferenceSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final SecondsSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final TextLiteralSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final ValueSeparatorSymbolSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final WhitespaceSpreadsheetParserToken token) {
        // nop
    }

    protected void visit(final YearSpreadsheetParserToken token) {
        // nop
    }

    // ParserToken.......................................................................

    @Override
    protected Visiting startVisit(final ParserToken token) {
        return Visiting.CONTINUE;
    }

    @Override
    protected void endVisit(final ParserToken token) {
        // nop
    }

    // SpreadsheetParserToken.......................................................................

    protected Visiting startVisit(final SpreadsheetParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetParserToken token) {
        // nop
    }
}
