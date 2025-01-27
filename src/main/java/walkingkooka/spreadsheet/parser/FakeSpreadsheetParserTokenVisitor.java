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

import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

public class FakeSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor implements Fake {

    protected FakeSpreadsheetParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final AdditionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final AdditionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final CellReferenceSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final CellReferenceSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final DateSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DivisionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final DivisionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ExpressionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final FunctionParametersSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final FunctionParametersSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final GroupSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final GroupSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final LambdaFunctionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final LambdaFunctionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final MultiplicationSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final MultiplicationSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NamedFunctionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NamedFunctionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NegativeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NegativeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final NumberSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final PowerSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final PowerSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final CellRangeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final CellRangeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SubtractionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SubtractionSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final TextSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final TimeSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final AmPmSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ApostropheSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final BetweenSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ColumnReferenceSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final CurrencySymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DayNameSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DayNameAbbreviationSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DecimalSeparatorSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DigitsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DivideSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DoubleQuoteSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final EqualsSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ErrorSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final FunctionNameSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final GreaterThanSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final GreaterThanEqualsSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final GroupSeparatorSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final HourSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LabelNameSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LessThanSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final LessThanEqualsSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MillisecondSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MinusSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MinuteSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MonthNameSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MonthNameAbbreviationSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MonthNumberSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final MultiplySymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final NotEqualsSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final DayNumberSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ParenthesisCloseSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ParenthesisOpenSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final PercentSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final PlusSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final PowerSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final RowReferenceSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SecondsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final ValueSeparatorSymbolSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final YearSpreadsheetParserToken token) {
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
    protected Visiting startVisit(final SpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }
}
