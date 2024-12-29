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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

/**
 * A {@link Visitor} for all {@link SpreadsheetFormatParserToken}.
 */
public abstract class SpreadsheetFormatParserTokenVisitor extends ParserTokenVisitor {

    protected SpreadsheetFormatParserTokenVisitor() {
        super();
    }

    protected Visiting startVisit(final SpreadsheetFormatColorParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatColorParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatDateParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatDateTimeParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatEqualsParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatExponentParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatExponentParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatExpressionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatExpressionParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatFractionParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatFractionParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatGeneralParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatGeneralParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatLessThanParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatLessThanParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatNumberParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatTextParserToken token) {
        // nop
    }

    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatTimeParserToken token) {
        // nop
    }

    // SpreadsheetFormatLeafParserToken ....................................................................................

    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatBracketCloseSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatBracketOpenSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatColorLiteralSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatColorNameParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatColorNumberParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatConditionNumberParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatDayParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatFractionSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatGeneralSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatGreaterThanEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatGreaterThanSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatGroupSeparatorParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatHourParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatLessThanEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatLessThanSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatMinuteParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatMonthParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatNotEqualsSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatStarParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatTextPlaceholderParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatUnderscoreParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        // nop
    }

    protected void visit(final SpreadsheetFormatYearParserToken token) {
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

    // SpreadsheetFormatParserToken.......................................................................

    protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatParserToken token) {
        // nop
    }
}
