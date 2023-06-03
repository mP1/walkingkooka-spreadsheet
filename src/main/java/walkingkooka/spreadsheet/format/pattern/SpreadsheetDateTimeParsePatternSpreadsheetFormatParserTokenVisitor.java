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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatCurrencyParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExponentSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGroupingParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.visit.Visiting;

final class SpreadsheetDateTimeParsePatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor<SpreadsheetFormatDateTimeParserToken> {

    static SpreadsheetDateTimeParsePatternSpreadsheetFormatParserTokenVisitor with() {
        return new SpreadsheetDateTimeParsePatternSpreadsheetFormatParserTokenVisitor();
    }

    SpreadsheetDateTimeParsePatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected void endVisit(final SpreadsheetFormatDateTimeParserToken token) {
        // nop
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
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
    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatGroupingParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        this.failInvalid();
    }

    @Override
    void text(final String text) {
        // nop
    }
}
