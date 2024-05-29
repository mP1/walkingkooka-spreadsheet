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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNameParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatCurrencyParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExponentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGroupSeparatorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatWhitespaceParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that assembles a {@link SpreadsheetFormatter} that handles numbers.
 * Most of this is achieved by counting parser tokens for integerDigitSymbolCount, fractionDigitSymbolCount, thousands,
 * and percentage symbols.
 */
final class SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Analyzes the given {@link SpreadsheetFormatParserToken}.
     */
    static SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor analyze(final SpreadsheetFormatParserToken token) {
        final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        visitor.computeGroupSeparatorAndCommaAdjust();
        return visitor;
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    // Visitor.....................................................................................................

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExponentParserToken token) {
        this.digitMode.exponent(token, this);
        return super.startVisit(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNameParserToken token) {
        this.color = token.colorName();
    }

    @Override
    protected void visit(final SpreadsheetFormatColorNumberParserToken token) {
        this.color = token.value();
    }

    // the color name or color number
    Object color = null;

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.currencySymbol());
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.decimalSeparator());
        this.digitMode.decimalPoint(this);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.addDigit(SpreadsheetPatternSpreadsheetFormatterNumberZero.HASH);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.addDigit(SpreadsheetPatternSpreadsheetFormatterNumberZero.QUESTION_MARK);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.addDigit(SpreadsheetPatternSpreadsheetFormatterNumberZero.ZERO);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.textLiteral(token.value().toString()));
    }

    void exponent(final SpreadsheetFormatExponentParserToken token) {
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.exponentSymbol());
        this.digitMode = SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitorDigitMode.EXPONENT;
        this.normalOrScientific = SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific.SCENTIFIC;
    }

    @Override
    protected void visit(final SpreadsheetFormatGroupSeparatorParserToken token) {
        this.digitMode.groupSeparator(this);
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        if (!this.percentage) {
            this.percentage = true;
            this.decimalPlacesShift = this.decimalPlacesShift + 2; // x100
        }
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.percentageSymbol());
    }

    /**
     * Used to only update the multiplier (by a factor of 100) once for percentage.
     */
    private boolean percentage = false;

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatWhitespaceParserToken token) {
        this.add(
                walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.textLiteral(
                        CharSequences.repeating(
                                ' ',
                                token.value().length()
                        ).toString()
                )
        );
    }

    // misc ..................................................................................................

    /**
     * Keeps track of whether the digits are integerDigitSymbolCount, fractionDigitSymbolCount or exponentDigitSymbolCount
     */
    SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitorDigitMode digitMode = SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitorDigitMode.INTEGER;

    /**
     * Defaults to {@link SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific#NORMAL} or {@link SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific#SCENTIFIC}
     * if an exponent appears in the pattern.
     */
    SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific normalOrScientific = SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific.NORMAL;

    /**
     * Counts the number of integer digit symbols.
     */
    int integerDigitSymbolCount = 0;

    /**
     * Counts the number of fractionDigitSymbolCount digit symbols.
     */
    int fractionDigitSymbolCount = 0;

    /**
     * Counts the number of exponentDigitSymbolCount digit symbols.
     */
    int exponentDigitSymbolCount = 0;

    /**
     * If the comma count is greater than one update the {@link #decimalPlacesShift}.
     */
    private void computeGroupSeparatorAndCommaAdjust() {
        this.groupSeparator = this.thousandsDivider ?
                SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE :
                SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.NONE;

        final int comma = this.comma;
        if (comma > 0) {
            this.decimalPlacesShift = this.decimalPlacesShift - (comma * 3); // divide by 1000 for each "comma".
        }
    }

    /**
     * Whenever a comma in the INTEGER portion is found this is incremented. Eventually this might cause
     * the {@link #decimalPlacesShift} to be divided.
     */
    int comma = 0;

    /**
     * A flag that is set whenever a comma is found that isnt a thousands divider.
     */
    boolean thousandsDivider = false;

    /**
     * The computed {@link SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator}.
     */
    SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator groupSeparator;

    /**
     * The number of decimal places to adjust, positive values multiply by 10, negative values divide by 10.
     */
    int decimalPlacesShift = 0;

    /**
     * Adds another component
     */
    private void addDigit(final SpreadsheetPatternSpreadsheetFormatterNumberZero zero) {
        this.add(walkingkooka.spreadsheet.format.SpreadsheetPatternSpreadsheetFormatterNumberComponent.digit(this.digitMode.digitCounterAndIncrement(this), zero));
    }

    /**
     * Adds another component
     */
    private void add(final SpreadsheetPatternSpreadsheetFormatterNumberComponent component) {
        this.components.add(component);
    }

    /**
     * Components represent each of the components of the original pattern.
     */
    final List<SpreadsheetPatternSpreadsheetFormatterNumberComponent> components = Lists.array();

    @Override
    public String toString() {
        return this.components.toString();
    }
}
