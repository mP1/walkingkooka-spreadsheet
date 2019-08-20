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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatCurrencyParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExponentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatThousandsParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * Counts the number of pattern tokens for integerDigitSymbolCount, fractionDigitSymbolCount, thousands, and percentage symbols.
 */
final class NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatter3SpreadsheetFormatParserTokenVisitor {

    /**
     * Analyzes the given {@link SpreadsheetFormatParserToken}.
     */
    static NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor analyze(final SpreadsheetFormatParserToken token) {
        final NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = new NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        visitor.computeThousandsSeparatorAndCommaAdjust();
        return visitor;
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    // Visitor.....................................................................................................

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExponentParserToken token) {
        this.digitMode.exponent(token, this);
        return super.startVisit(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.add(NumberSpreadsheetFormatterComponent.currencySymbol());
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.add(NumberSpreadsheetFormatterComponent.decimalSeparator());
        this.digitMode.decimalPoint(this);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.addDigit(NumberSpreadsheetFormatterZero.HASH);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.addDigit(NumberSpreadsheetFormatterZero.QUESTION_MARK);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.addDigit(NumberSpreadsheetFormatterZero.ZERO);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.add(NumberSpreadsheetFormatterComponent.textLiteral(token.value().toString()));
    }

    void exponent(final SpreadsheetFormatExponentParserToken token) {
        this.add(NumberSpreadsheetFormatterComponent.exponentSymbol());
        this.digitMode = NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorDigitMode.EXPONENT;
        this.normalOrScientific = NumberSpreadsheetFormatterNormalOrScientific.SCENTIFIC;
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        if (!this.percentage) {
            this.percentage = true;
            this.decimalPlacesShift = this.decimalPlacesShift + 2; // x100
        }
        this.add(NumberSpreadsheetFormatterComponent.percentageSymbol());
    }

    /**
     * Used to only update the multiplier (by a factor of 100) once for percentage.
     */
    private boolean percentage = false;

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.add(NumberSpreadsheetFormatterComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.add(NumberSpreadsheetFormatterComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatThousandsParserToken token) {
        this.digitMode.thousands(this);
    }

    // misc ..................................................................................................

    /**
     * Keeps track of whether the digits are integerDigitSymbolCount, fractionDigitSymbolCount or exponentDigitSymbolCount
     */
    NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorDigitMode digitMode = NumberSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorDigitMode.INTEGER;

    /**
     * Defaults to {@link NumberSpreadsheetFormatterNormalOrScientific#NORMAL} or {@link NumberSpreadsheetFormatterNormalOrScientific#SCENTIFIC}
     * if an exponent appears in the pattern.
     */
    NumberSpreadsheetFormatterNormalOrScientific normalOrScientific = NumberSpreadsheetFormatterNormalOrScientific.NORMAL;

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
    private void computeThousandsSeparatorAndCommaAdjust() {
        this.thousandsSeparator = this.thousandsGrouping ?
                NumberSpreadsheetFormatterThousandsSeparator.INCLUDE :
                NumberSpreadsheetFormatterThousandsSeparator.NONE;

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
    boolean thousandsGrouping = false;

    /**
     * The computed {@link NumberSpreadsheetFormatterThousandsSeparator}.
     */
    NumberSpreadsheetFormatterThousandsSeparator thousandsSeparator;

    /**
     * A multiplier that is applied to the number before formatting.
     * This is increased when the thousands appear after the decimal point and percentage symbol.
     */
    //int multiplier = 0;

    //int divider = 0;

    /**
     * The number of decimal places to adjust, positive values multiply by 10, negative values divide by 10.
     */
    int decimalPlacesShift = 0;

    /**
     * Adds another component
     */
    void addDigit(final NumberSpreadsheetFormatterZero zero) {
        this.add(NumberSpreadsheetFormatterComponent.digit(this.digitMode.digitCounterAndIncrement(this), zero));
    }

    /**
     * Adds another component
     */
    void add(final NumberSpreadsheetFormatterComponent component) {
        this.components.add(component);
    }

    /**
     * Components represent each of the components of the original pattern.
     */
    final List<NumberSpreadsheetFormatterComponent> components = Lists.array();

    @Override
    public String toString() {
        return this.components.toString();
    }
}
