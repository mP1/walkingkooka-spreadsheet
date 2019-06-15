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
import walkingkooka.tree.visit.Visiting;

public abstract class SpreadsheetParserTokenVisitor extends ParserTokenVisitor {

    // SpreadsheetAdditionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetAdditionParserToken token) {
        // nop
    }

    // SpreadsheetCellReferenceParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetCellReferenceParserToken token) {
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

    // SpreadsheetFunctionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetFunctionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFunctionParserToken token) {
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

    // SpreadsheetPercentageParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetPercentageParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetPercentageParserToken token) {
        // nop
    }

    // SpreadsheetPowerParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetPowerParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetPowerParserToken token) {
        // nop
    }

    // SpreadsheetRangeParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetRangeParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetRangeParserToken token) {
        // nop
    }

    // SpreadsheetSubtractionParserToken....................................................................................

    protected Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetSubtractionParserToken token) {
        // nop
    }

    // SpreadsheetLeafParserToken ....................................................................................

    protected void visit(final SpreadsheetBetweenSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetBigDecimalParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetBigIntegerParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetColumnReferenceParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDivideSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetDoubleParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFunctionNameParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFunctionParameterSeparatorSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetGreaterThanSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetGreaterThanEqualsSymbolParserToken token) {
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

    protected void visit(final SpreadsheetLocalDateParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetLocalDateTimeParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetLocalTimeParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetLongParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetMinusSymbolParserToken token) {
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

    protected void visit(final SpreadsheetTextParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetWhitespaceParserToken token) {
        // nop
    }

    // ParserToken.......................................................................

    protected Visiting startVisit(final ParserToken token) {
        return Visiting.CONTINUE;
    }

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
