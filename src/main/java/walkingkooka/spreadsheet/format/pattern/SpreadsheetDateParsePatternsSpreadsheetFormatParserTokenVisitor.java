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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExponentSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSecondParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatThousandsParserToken;
import walkingkooka.visit.Visiting;

final class SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor extends SpreadsheetParsePatternsSpreadsheetFormatParserTokenVisitor<SpreadsheetFormatDateParserToken> {

    static SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor with() {
        return new SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor();
    }

    SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        this.addToken(token);
        return Visiting.CONTINUE;
    }

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatThousandsParserToken token) {
        this.failInvalid(token);
    }

    @Override
    void text(final String text) {
        // nop
    }
}
