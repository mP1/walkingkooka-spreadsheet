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

import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

public class FakeSpreadsheetFormulaParserTokenVisitor extends SpreadsheetFormulaParserTokenVisitor implements Fake {

    protected FakeSpreadsheetFormulaParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final AdditionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final AdditionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final CellReferenceSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final CellReferenceSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final DateSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DivisionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final DivisionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final FunctionParametersSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final FunctionParametersSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final GroupSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final GroupSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final LambdaFunctionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final LambdaFunctionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NegativeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NegativeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NumberSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final PowerSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final PowerSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final TextSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final TimeSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final AmPmSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ApostropheSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final BetweenSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ColumnReferenceSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final CurrencySymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DayNameSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DayNameAbbreviationSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DecimalSeparatorSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DigitsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DivideSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DoubleQuoteSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final EqualsSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ErrorSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final FunctionNameSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final GreaterThanSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final GroupSeparatorSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final HourSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LabelNameSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LessThanSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LessThanEqualsSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MillisecondSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MinusSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MinuteSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MonthNameSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MonthNameAbbreviationSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MonthNumberSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MultiplySymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final NotEqualsSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DayNumberSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ParenthesisCloseSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ParenthesisOpenSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final PercentSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final PlusSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final PowerSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final RowReferenceSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SecondsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ValueSeparatorSymbolSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final YearSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(ParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(ParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }
}
