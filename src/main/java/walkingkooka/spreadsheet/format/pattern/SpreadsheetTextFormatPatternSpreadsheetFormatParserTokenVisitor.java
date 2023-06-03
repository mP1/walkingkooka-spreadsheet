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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatCurrencyParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDayParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExponentSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGroupSeparatorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMinuteParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMonthParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSecondParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatYearParserToken;
import walkingkooka.visit.Visiting;

/**
 * A {@link SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor} that validates a text tokens within a pattern
 * fails when invalid tokens for number format are encountered.
 */
final class SpreadsheetTextFormatPatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor {

    static SpreadsheetTextFormatPatternSpreadsheetFormatParserTokenVisitor with() {
        return new SpreadsheetTextFormatPatternSpreadsheetFormatParserTokenVisitor();
    }

    SpreadsheetTextFormatPatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatFractionSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatGroupSeparatorParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatMinuteParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatMonthParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        this.failInvalid();
    }
}
