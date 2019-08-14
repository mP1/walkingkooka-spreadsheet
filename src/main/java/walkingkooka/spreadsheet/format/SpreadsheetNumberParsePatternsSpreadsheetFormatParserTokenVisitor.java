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

package walkingkooka.spreadsheet.format;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatAmPmParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatCurrencyParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDayParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExponentSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatHourParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatMonthOrMinuteParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSecondParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatThousandsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatYearParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} used to convert a {@link SpreadsheetFormatNumberParserToken} to
 * {@link SpreadsheetNumberParsePatternsComponent}.
 */
final class SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor extends SpreadsheetParsePatternsSpreadsheetFormatParserTokenVisitor<SpreadsheetFormatNumberParserToken> {

    static SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor with() {
        return new SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor();
    }

    SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        this.addToken(token);

        this.components = Lists.array();
        this.firstDigit = true;
        this.decimal = false;
        this.lastDecimal = -1;

        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetFormatNumberParserToken token) {
        final List<SpreadsheetNumberParsePatternsComponent> components = this.components;

        final int lastDecimal = this.lastDecimal;
        if (-1 != lastDecimal) {
            components.set(lastDecimal, components.get(lastDecimal).lastDecimal());
        }

        this.patterns.add(components);
    }

    /**
     * Accumulates all the components for each and every pattern.
     */
    final List<List<SpreadsheetNumberParsePatternsComponent>> patterns = Lists.array();

    @Override
    protected void visit(final SpreadsheetFormatAmPmParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.advancePosition(token);
        this.addComponent(SpreadsheetNumberParsePatternsComponent.currency());
    }

    @Override
    protected void visit(final SpreadsheetFormatDayParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.advancePosition(token);
        this.addComponent(SpreadsheetNumberParsePatternsComponent.decimalSeparator());

        this.decimal = true;
        this.lastDecimal = -1;
    }

    /**
     * Hash is not a supported character, ? or 0 should be used instead.
     */
    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.advancePosition(token);
        this.addComponent(SpreadsheetNumberParsePatternsComponent.digit(this.digitMaxCount()));
        this.maybeUpdateLastDecimal();
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.advancePosition(token);
        this.addComponent(SpreadsheetNumberParsePatternsComponent.digitSpace(this.digitMaxCount()));
        this.maybeUpdateLastDecimal();
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.advancePosition(token);
        this.addComponent(SpreadsheetNumberParsePatternsComponent.digitZero(this.digitMaxCount()));
        this.maybeUpdateLastDecimal();
    }

    @Override
    protected void visit(final SpreadsheetFormatExponentSymbolParserToken token) {
        this.advancePosition(token);
        this.addComponent(SpreadsheetNumberParsePatternsComponent.exponent());

        this.firstDigit = true;
    }

    @Override
    protected void visit(final SpreadsheetFormatHourParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatMonthOrMinuteParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        this.advancePosition(token);
        this.addComponent(SpreadsheetNumberParsePatternsComponent.percentage());
    }

    @Override
    protected void visit(final SpreadsheetFormatSecondParserToken token) {
        this.failInvalid(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatThousandsParserToken token) {
        this.advancePosition(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatYearParserToken token) {
        this.failInvalid(token);
    }

    @Override
    void text(final String text) {
        this.addComponent(SpreadsheetNumberParsePatternsComponent.textLiteral(text));
    }

    /**
     * Adds a new {@link SpreadsheetNumberParsePatternsComponent} to the pattern being parsed.
     */
    private void addComponent(final SpreadsheetNumberParsePatternsComponent component) {
        this.components.add(component);
    }

    private int digitMaxCount() {
        final boolean first = this.firstDigit;
        this.firstDigit = false;
        return first ?
                Integer.MAX_VALUE :
                1;
    }

    /**
     * True for prior to the first onDigit component.
     */
    private boolean firstDigit;

    /**
     * Will be true when accepting components of a the decimal portion of a number. This becomes true when a decimal separator is encountered.
     */
    private boolean decimal;

    private void maybeUpdateLastDecimal() {
        if (this.decimal) {
            this.lastDecimal = this.components.size() - 1;
        }
    }

    /**
     * Holds the index of the last decimal digit in {@link #components}
     */
    private int lastDecimal;

    /**
     * Takes all the components for the pattern being visited.
     */
    List<SpreadsheetNumberParsePatternsComponent> components;
}
