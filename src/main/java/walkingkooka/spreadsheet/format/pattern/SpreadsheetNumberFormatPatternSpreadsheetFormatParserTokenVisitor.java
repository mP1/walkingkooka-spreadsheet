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
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DaySpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.HourSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.MinuteSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.MonthSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SecondSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.YearSpreadsheetFormatParserToken;
import walkingkooka.visit.Visiting;

/**
 * A {@link SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor} that validates a number tokens within a pattern
 * fails when invalid tokens for number format are encountered.
 */
final class SpreadsheetNumberFormatPatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor {

    static SpreadsheetNumberFormatPatternSpreadsheetFormatParserTokenVisitor with() {
        return new SpreadsheetNumberFormatPatternSpreadsheetFormatParserTokenVisitor();
    }

    SpreadsheetNumberFormatPatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
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
    protected void visit(final DaySpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final HourSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final MinuteSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final MonthSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SecondSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final YearSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }
}
