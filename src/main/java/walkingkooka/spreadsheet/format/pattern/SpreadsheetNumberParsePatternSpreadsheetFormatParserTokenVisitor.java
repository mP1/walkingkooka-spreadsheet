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
import walkingkooka.spreadsheet.format.parser.AmPmSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.CurrencySpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DaySpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DecimalPointSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitSpaceSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitZeroSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ExponentSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GroupSeparatorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.HourSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.MinuteSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.MonthSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.PercentSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SecondSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.WhitespaceSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.YearSpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} used to convert a {@link NumberSpreadsheetFormatParserToken} to
 * {@link SpreadsheetNumberParsePatternComponent}.
 */
final class SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor<NumberSpreadsheetFormatParserToken> {

    static List<List<SpreadsheetNumberParsePatternComponent>> patterns(final ParserToken token) {
        final SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor();
        visitor.startAccept(token);
        return visitor.patterns;
    }

    SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor() {
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
    protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
        this.components = Lists.array();
        this.lastDigit = -1;
        this.mode = SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN;

        return Visiting.CONTINUE;
    }

    @Override
    protected void endVisit(final NumberSpreadsheetFormatParserToken token) {
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

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
        return this.failInvalid();
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        return this.failInvalid();
    }

    /**
     * Accumulates all the tokens for each and every pattern.
     */
    private final List<List<SpreadsheetNumberParsePatternComponent>> patterns = Lists.array();

    @Override
    protected void visit(final AmPmSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final CurrencySpreadsheetFormatParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.currency());
    }

    @Override
    protected void visit(final DaySpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final DecimalPointSpreadsheetFormatParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.decimalSeparator());

        this.mode = SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST;
        this.lastDigit = -1;
    }

    /**
     * Hash is not a supported character, ? or 0 should be used instead.
     */
    @Override
    protected void visit(final DigitSpreadsheetFormatParserToken token) {
        this.addComponent(
            SpreadsheetNumberParsePatternComponent.digit(
                this.mode,
                this.digitMaxCount()
            )
        );
        this.nextDigit();
    }

    @Override
    protected void visit(final DigitSpaceSpreadsheetFormatParserToken token) {
        this.addComponent(
            SpreadsheetNumberParsePatternComponent.digitSpace(
                this.mode,
                this.digitMaxCount()
            )
        );
        this.nextDigit();
    }

    @Override
    protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
        this.addComponent(
            SpreadsheetNumberParsePatternComponent.digitZero(
                this.mode,
                this.digitMaxCount()
            )
        );
        this.nextDigit();
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetFormatParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.exponent());

        this.mode = SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START;
    }

    @Override
    protected void visit(final GroupSeparatorSpreadsheetFormatParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.groupSeparator());
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
    protected void visit(final PercentSpreadsheetFormatParserToken token) {
        this.addComponent(SpreadsheetNumberParsePatternComponent.percentage());
    }

    @Override
    protected void visit(final SecondSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetFormatParserToken token) {
        this.addComponent(
            SpreadsheetNumberParsePatternComponent.whitespace(
                token.textLength()
            )
        );
    }

    @Override
    protected void visit(final YearSpreadsheetFormatParserToken token) {
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
     * Takes all the tokens for the pattern being visited.
     */
    private List<SpreadsheetNumberParsePatternComponent> components;
}
