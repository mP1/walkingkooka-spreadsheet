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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNotEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.visit.Visiting;

final class SpreadsheetPatternPrintTreeSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static void treePrint(final ParserToken token,
                          final IndentingPrinter printer) {
        new SpreadsheetPatternPrintTreeSpreadsheetFormatParserTokenVisitor(printer)
                .accept(token);
        printer.lineStart();
    }

    SpreadsheetPatternPrintTreeSpreadsheetFormatParserTokenVisitor(final IndentingPrinter printer) {
        this.printer = printer;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatColorParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatEqualsParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatFractionParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGeneralParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        this.printer.print(" ");
        this.printer.print(token.text());
    }

    private Visiting treePrint0(final SpreadsheetFormatParserToken token) {
        this.printer.lineStart();
        this.printer.print(
                CharSequences.quoteAndEscape(
                        token.text()
                )
        );

        return Visiting.SKIP;
    }

    private final IndentingPrinter printer;

    @Override
    public String toString() {
        return this.printer.toString();
    }
}
