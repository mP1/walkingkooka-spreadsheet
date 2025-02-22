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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.spreadsheet.format.parser.AmPmSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.CurrencySpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DecimalPointSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitSpaceSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitZeroSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ExponentSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GroupSeparatorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.HourSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.PercentSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SecondSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
import walkingkooka.visit.Visiting;

final class SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor<DateSpreadsheetFormatParserToken> {

    static SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor with() {
        return new SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor();
    }

    SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected void visit(final AmPmSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final CurrencySpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final DecimalPointSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final DigitSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final DigitSpaceSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final GroupSeparatorSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final HourSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final PercentSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SecondSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    void text(final String text) {
        // nop
    }
}
