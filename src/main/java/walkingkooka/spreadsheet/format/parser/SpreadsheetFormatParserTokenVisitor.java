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

    protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ColorSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DateSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final DateTimeSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final EqualsSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final EqualsSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final ExponentSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ExponentSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final ExpressionSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ExpressionSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final FractionSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final FractionSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final GeneralSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GeneralSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final LessThanSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final LessThanSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final NumberSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final TextSpreadsheetFormatParserToken token) {
        // nop
    }

    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final TimeSpreadsheetFormatParserToken token) {
        // nop
    }

    // SpreadsheetFormatLeafParserToken ....................................................................................

    protected void visit(final AmPmSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final ColorLiteralSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final ColorNumberSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final CurrencySpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final DaySpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final DecimalPointSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final DigitSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final DigitSpaceSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final EqualsSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final EscapeSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final ExponentSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final FractionSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final GeneralSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final GreaterThanSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final GroupSeparatorSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final HourSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final LessThanEqualsSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final LessThanSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final MinuteSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final MonthSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final NotEqualsSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final PercentSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final QuotedTextSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final SecondSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final StarSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final TextLiteralSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final TextPlaceholderSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final UnderscoreSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final WhitespaceSpreadsheetFormatParserToken token) {
        // nop
    }

    protected void visit(final YearSpreadsheetFormatParserToken token) {
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
