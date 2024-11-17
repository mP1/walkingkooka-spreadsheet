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

    // SpreadsheetAdditionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetAdditionParserToken token) {
        // nop
    }

    // SpreadsheetCellRangeParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetCellRangeParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetCellRangeParserToken token) {
        // nop
    }

    // SpreadsheetCellReferenceParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetCellReferenceParserToken token) {
        // nop
    }

    // SpreadsheetConditionRightEqualsParserToken.......................................................................

    protected Visiting startVisit(final SpreadsheetConditionRightEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetConditionRightEqualsParserToken token) {
        // nop
    }

    // SpreadsheetConditionRightGreaterThanParserToken..................................................................

    protected Visiting startVisit(final SpreadsheetConditionRightGreaterThanParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetConditionRightGreaterThanParserToken token) {
        // nop
    }

    // SpreadsheetConditionRightGreaterThanEqualsParserToken............................................................

    protected Visiting startVisit(final SpreadsheetConditionRightGreaterThanEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetConditionRightGreaterThanEqualsParserToken token) {
        // nop
    }

    // SpreadsheetConditionRightLessThanParserToken.....................................................................

    protected Visiting startVisit(final SpreadsheetConditionRightLessThanParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetConditionRightLessThanParserToken token) {
        // nop
    }

    // SpreadsheetConditionRightLessThanEqualsParserToken................................................................

    protected Visiting startVisit(final SpreadsheetConditionRightLessThanEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetConditionRightLessThanEqualsParserToken token) {
        // nop
    }

    // SpreadsheetConditionRightNotEqualsParserToken.....................................................................

    protected Visiting startVisit(final SpreadsheetConditionRightNotEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetConditionRightNotEqualsParserToken token) {
        // nop
    }

    // SpreadsheetDateParserToken.......................................................................................

    protected Visiting startVisit(final SpreadsheetDateParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetDateParserToken token) {
        // nop
    }

    // SpreadsheetDateTimeParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetDateTimeParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetDateTimeParserToken token) {
        // nop
    }

    // SpreadsheetDivisionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetDivisionParserToken token) {
        // nop
    }

    // SpreadsheetEqualsParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetEqualsParserToken token) {
        // nop
    }

    // SpreadsheetExpressionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetExpressionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetExpressionParserToken token) {
        // nop
    }

    // SpreadsheetFunctionParametersParserToken.........................................................................

    protected Visiting startVisit(final SpreadsheetFunctionParametersParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFunctionParametersParserToken token) {
        // nop
    }

    // SpreadsheetGreaterThanParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetGreaterThanParserToken token) {
        // nop
    }

    // SpreadsheetGreaterThanEqualsParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        // nop
    }

    // SpreadsheetGroupParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetGroupParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetGroupParserToken token) {
        // nop
    }

    // SpreadsheetLambdaFunctionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetLambdaFunctionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetLambdaFunctionParserToken token) {
        // nop
    }

    // SpreadsheetLessThanParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetLessThanParserToken token) {
        // nop
    }

    // SpreadsheetLessThanEqualsParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        // nop
    }

    // SpreadsheetMultiplicationParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetMultiplicationParserToken token) {
        // nop
    }

    // SpreadsheetNamedFunctionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetNamedFunctionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetNamedFunctionParserToken token) {
        // nop
    }

    // SpreadsheetNegativeParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetNegativeParserToken token) {
        // nop
    }

    // SpreadsheetNotEqualsParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetNotEqualsParserToken token) {
        // nop
    }

    // SpreadsheetNumberParserToken.....................................................................................

    protected Visiting startVisit(final SpreadsheetNumberParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetNumberParserToken token) {
        // nop
    }

    // SpreadsheetPowerParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetPowerParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetPowerParserToken token) {
        // nop
    }

    // SpreadsheetSubtractionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetSubtractionParserToken token) {
        // nop
    }

    // SpreadsheetTextParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetTextParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetTextParserToken token) {
        // nop
    }

    // SpreadsheetTimeParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetTimeParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetTimeParserToken token) {
        // nop
    }

    // SpreadsheetLeafParserToken ....................................................................................

    protected void visit(final SpreadsheetAmPmParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetApostropheSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetBetweenSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetColumnReferenceParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetCurrencySymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDayNameParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDayNameAbbreviationParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDayNumberParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDecimalSeparatorSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDigitsParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDivideSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDoubleQuoteSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetErrorParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetExponentSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFunctionNameParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetGreaterThanSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetGreaterThanEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetGroupSeparatorSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetHourParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetLabelNameParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetLessThanSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetLessThanEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMillisecondParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMinusSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMinuteParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMonthNameParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMonthNameAbbreviationParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMonthNameInitialParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMonthNumberParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMultiplySymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetNotEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetParenthesisCloseSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetParenthesisOpenSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetPercentSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetPlusSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetPowerSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetRowReferenceParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetSecondsParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetTextLiteralParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetValueSeparatorSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetWhitespaceParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetYearParserToken token) {
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
