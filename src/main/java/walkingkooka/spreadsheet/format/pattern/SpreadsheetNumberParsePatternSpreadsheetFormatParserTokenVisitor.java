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

import walkingkooka.collect.list.Lists;
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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGroupSeparatorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMinuteParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMonthParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSecondParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatWhitespaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatYearParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} used to convert a {@link SpreadsheetFormatNumberParserToken} to
 * {@link SpreadsheetNumberParsePatternComponent}.
 */
final class SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor<SpreadsheetFormatNumberParserToken> {

    static List<List<SpreadsheetNumberParsePatternComponent>> patterns(final ParserToken token) {
        final SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor();
        visitor.startAccept(token);
        return visitor.patterns;
    }

    SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor() {
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
        this.components = Lists.array();
        this.lastDigit = -1;
        this.mode = SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN;

        return Visiting.CONTINUE;
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNumberParserToken token) {
        final List<SpreadsheetNumberParsePatternComponent> components = this.components;

        final int lastDigit = this.lastDigit;
        if (-1 != lastDigit) {
            components.set(
                    lastDigit,
                    components.get(lastDigit).lastDigit(this.mode)
            );
        }

        this.patterns.add(components);
    }

    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        return this.failInvalid();
    }

    /**
     * Accumulates all the textComponents for each and every pattern.
     */
    private final List<List<SpreadsheetNumberParsePatternComponent>> patterns = Lists.array();

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.currency());
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.decimalSeparator());

        this.mode = SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST;
        this.lastDigit = -1;
    }

    /**
     * Hash is not a supported character, ? or 0 should be used instead.
     */
    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.addComponent(
                SpreadsheetNumberParsePatternComponent.digit(
                        this.mode,
                        this.digitMaxCount()
                )
        );
        this.nextDigit();
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.addComponent(
                SpreadsheetNumberParsePatternComponent.digitSpace(
                        this.mode,
                        this.digitMaxCount()
                )
        );
        this.nextDigit();
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.addComponent(
                SpreadsheetNumberParsePatternComponent.digitZero(
                        this.mode,
                        this.digitMaxCount()
                )
        );
        this.nextDigit();
    }

    @Override
    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.exponent());

        this.mode = SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START;
    }

    @Override
    protected void visit(final SpreadsheetFormatGroupSeparatorParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.groupSeparator());
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
        this.addComponent(SpreadsheetNumberParsePatternComponent.percentage());
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.addComponent(
                SpreadsheetNumberParsePatternComponent.whitespace(
                        token.textLength()
                )
        );
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        this.failInvalid();
    }

    @Override
    void text(final String text) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.textLiteral(text));
    }

    /**
     * Tracks whether digits are the integer, decimal or exponent portion of a number
     */
    private SpreadsheetNumberParsePatternComponentDigitMode mode;

    /**
     * Adds a new {@link SpreadsheetNumberParsePatternComponent} to the pattern being parsed.
     */
    private void addComponent(final SpreadsheetNumberParsePatternComponent component) {
        this.components.add(component);
    }

    private int digitMaxCount() {
        return this.mode.isFirstDigit() ?
                Integer.MAX_VALUE :
                1;
    }

    private void nextDigit() {
        final SpreadsheetNumberParsePatternComponentDigitMode mode = this.mode;
        if (mode.isDecimal()) {
            this.lastDigit = this.components.size() - 1;
        }
        this.mode = mode.next();
    }

    /**
     * Holds the index of the last decimal digit in {@link #components}
     */
    private int lastDigit;

    /**
     * Takes all the textComponents for the pattern being visited.
     */
    private List<SpreadsheetNumberParsePatternComponent> components;
}
