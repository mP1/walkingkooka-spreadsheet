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

import walkingkooka.io.printer.IndentingPrinters;
import walkingkooka.io.printer.Printers;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.BigIntegerParserToken;
import walkingkooka.text.cursor.parser.CharacterParserToken;
import walkingkooka.text.cursor.parser.DoubleParserToken;
import walkingkooka.text.cursor.parser.DoubleQuotedParserToken;
import walkingkooka.text.cursor.parser.LocalDateParserToken;
import walkingkooka.text.cursor.parser.LocalDateTimeParserToken;
import walkingkooka.text.cursor.parser.LocalTimeParserToken;
import walkingkooka.text.cursor.parser.LongParserToken;
import walkingkooka.text.cursor.parser.OffsetDateTimeParserToken;
import walkingkooka.text.cursor.parser.OffsetTimeParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RepeatedParserToken;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.text.cursor.parser.SignParserToken;
import walkingkooka.text.cursor.parser.SingleQuotedParserToken;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ZonedDateTimeParserToken;
import walkingkooka.tree.visit.Visiting;
import walkingkooka.tree.visit.VisitorPrettyPrinter;

final class SpreadsheetFormatParserPrettySpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static String toString(final ParserToken token) {
        final StringBuilder b = new StringBuilder();

        new SpreadsheetFormatParserPrettySpreadsheetFormatParserTokenVisitor(VisitorPrettyPrinter.with(
                IndentingPrinters.printer(Printers.stringBuilder(b, LineEnding.NL)),
                Indentation.with("  "),
                SpreadsheetFormatParserPrettySpreadsheetFormatParserTokenVisitor::tokenName)).accept(token);
        return b.toString();
    }

    private static String tokenName(final ParserToken token) {
        return VisitorPrettyPrinter.computeFromClassSimpleName(token, "SpreadsheetFormat", ParserToken.class.getSimpleName());
    }

    private SpreadsheetFormatParserPrettySpreadsheetFormatParserTokenVisitor(final VisitorPrettyPrinter<ParserToken> printer) {
        this.printer = printer;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatBigDecimalParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatBigDecimalParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatColorParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatColorParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatDateParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatEqualsParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatEqualsParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExponentParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatExponentParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExpressionParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatExpressionParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatFractionParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatFractionParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatTextParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatTimeParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatBracketCloseSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatBracketOpenSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorLiteralSymbolParserToken token) {
        super.visit(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNameParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNumberParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatConditionNumberParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitLeadingSpaceParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitLeadingZeroParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatEqualsSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatFractionSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatGreaterThanEqualsSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatGreaterThanSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatLessThanEqualsSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatLessThanSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatMonthOrMinuteParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatNotEqualsSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatStarParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatTextPlaceholderParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatThousandsParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatUnderscoreParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected Visiting startVisit(final RepeatedParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final RepeatedParserToken token) {
        this.printer.exit(token);
    }

    @Override
    protected Visiting startVisit(final SequenceParserToken token) {
        this.printer.enter(token);
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SequenceParserToken token) {
        this.printer.exit(token);
        super.endVisit(token);
    }

    @Override
    protected void visit(final BigDecimalParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final BigIntegerParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final CharacterParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final DoubleParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final DoubleQuotedParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final LocalDateParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final LocalDateTimeParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final LocalTimeParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final LongParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final OffsetDateTimeParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final OffsetTimeParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SingleQuotedParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final SignParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final StringParserToken token) {
        this.printer.leaf(token);
    }

    @Override
    protected void visit(final ZonedDateTimeParserToken token) {
        this.printer.leaf(token);
    }

    private final VisitorPrettyPrinter<ParserToken> printer;

    @Override
    public String toString() {
        return this.printer.toString();
    }
}
