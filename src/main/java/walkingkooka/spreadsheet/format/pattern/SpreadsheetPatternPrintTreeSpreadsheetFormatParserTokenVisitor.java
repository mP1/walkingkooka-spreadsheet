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

import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.FractionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GeneralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NotEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SeparatorSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
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
    protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final FractionSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final GeneralSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        return this.treePrint0(token);
    }

    @Override
    protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
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
