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
    protected Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetAdditionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetCellReferenceParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetDivisionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFunctionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetFunctionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGroupParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetGroupParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetMultiplicationParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetNegativeParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetNotEqualsParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetPercentageParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetPercentageParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetPowerParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetPowerParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetRangeParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetRangeParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetSubtractionParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetBetweenSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetBigDecimalParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetBigIntegerParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetColumnReferenceParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetDivideSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetDoubleParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetEqualsSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetFunctionNameParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetFunctionParameterSeparatorSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetGreaterThanSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetGreaterThanEqualsSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLabelNameParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLessThanSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLessThanEqualsSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLocalDateParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLocalDateTimeParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLocalTimeParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLongParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetMinusSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetMultiplySymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetNotEqualsSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetParenthesisCloseSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetParenthesisOpenSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetPercentSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetPlusSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetPowerSymbolParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetRowReferenceParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetTextParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetWhitespaceParserToken token) {
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
