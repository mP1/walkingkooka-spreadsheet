/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.format;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatCurrencyParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDecimalPointParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitLeadingSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitLeadingZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExponentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatThousandsParserToken;
import walkingkooka.tree.visit.Visiting;

import java.math.BigDecimal;
import java.util.List;

/**
 * Counts the number of pattern tokens for integerDigitSymbolCount, fractionDigitSymbolCount, thousands, and percentage symbols.
 */
final class BigDecimalSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor extends TextFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Analyzes the given {@link SpreadsheetFormatParserToken}.
     */
    static BigDecimalSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor analyze(final SpreadsheetFormatParserToken token) {
        final BigDecimalSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor visitor = new BigDecimalSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        visitor.computeThousandsSeparatorAndMultiplier();
        return visitor;
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    BigDecimalSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    // Visitor.....................................................................................................

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExponentParserToken token) {
        this.mode.exponent(token, this);
        return super.startVisit(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.add(BigDecimalSpreadsheetTextFormatterComponent.currencySymbol());
    }

    @Override
    protected void visit(final SpreadsheetFormatDecimalPointParserToken token) {
        this.add(BigDecimalSpreadsheetTextFormatterComponent.decimalPointSymbol());
        this.mode.decimalPoint(this);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.addDigit(BigDecimalSpreadsheetTextFormatterZero.HASH);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitLeadingSpaceParserToken token) {
        this.addDigit(BigDecimalSpreadsheetTextFormatterZero.QUESTION_MARK);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitLeadingZeroParserToken token) {
        this.addDigit(BigDecimalSpreadsheetTextFormatterZero.ZERO);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.add(BigDecimalSpreadsheetTextFormatterComponent.textLiteral(token.value().toString()));
    }

    void exponent(final SpreadsheetFormatExponentParserToken token) {
        this.add(BigDecimalSpreadsheetTextFormatterComponent.exponentSymbol());
        this.mode = BigDecimalSpreadsheetTextFormatterMode.EXPONENT;
        this.format = BigDecimalSpreadsheetTextFormatterFormat.SCENTIFIC;
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentSymbolParserToken token) {
        if (!this.percentage) {
            this.percentage = true;
            this.multiplier = this.multiplier.scaleByPowerOfTen(2);// x100
        }
        this.add(BigDecimalSpreadsheetTextFormatterComponent.percentageSymbol());
    }

    /**
     * Used to only update the multiplier (by a factor of 100) once for percentage.
     */
    private boolean percentage = false;

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.add(BigDecimalSpreadsheetTextFormatterComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.add(BigDecimalSpreadsheetTextFormatterComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatThousandsParserToken token) {
        this.mode.thousands(this);
    }

    // misc ..................................................................................................

    /**
     * Keeps track of whether the digits are integerDigitSymbolCount, fractionDigitSymbolCount or exponentDigitSymbolCount
     */
    BigDecimalSpreadsheetTextFormatterMode mode = BigDecimalSpreadsheetTextFormatterMode.INTEGER;

    /**
     * Defaults to {@link BigDecimalSpreadsheetTextFormatterFormat#NORMAL} or {@link BigDecimalSpreadsheetTextFormatterFormat#SCENTIFIC}
     * if an exponent appears in the pattern.
     */
    BigDecimalSpreadsheetTextFormatterFormat format = BigDecimalSpreadsheetTextFormatterFormat.NORMAL;

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
     * If the comma count is greater than one update the multiplier and
     */
    private void computeThousandsSeparatorAndMultiplier() {
        this.thousandsSeparator = this.thousandsGrouping ?
                BigDecimalSpreadsheetTextFormatterThousandsSeparator.INCLUDE :
                BigDecimalSpreadsheetTextFormatterThousandsSeparator.NONE;

        final int comma = this.comma;
        if (comma > 0) {
            this.multiplier = this.multiplier.scaleByPowerOfTen(comma * -3); // divide by 1000 for each "comma".
        }
    }

    /**
     * Whenever a comma in the INTEGER portion is found this is incremented. Eventually this might cause
     * the {@link #multiplier} to be divided.
     */
    int comma = 0;

    /**
     * A flag that is set whenever a comma is found that isnt a thousands divider.
     */
    boolean thousandsGrouping = false;

    /**
     * The computed {@link BigDecimalSpreadsheetTextFormatterThousandsSeparator}.
     */
    BigDecimalSpreadsheetTextFormatterThousandsSeparator thousandsSeparator;

    /**
     * A multiplier that is applied to the number before formatting.
     * This is increased when the thousands appear after the decimal point and percentage symbol.
     */
    BigDecimal multiplier = BigDecimal.ONE;

    /**
     * Adds another component
     */
    void addDigit(final BigDecimalSpreadsheetTextFormatterZero zero) {
        this.add(BigDecimalSpreadsheetTextFormatterComponent.digit(this.mode.digitCounterAndIncrement(this), zero));
    }

    /**
     * Adds another component
     */
    void add(final BigDecimalSpreadsheetTextFormatterComponent component) {
        this.components.add(component);
    }

    /**
     * Components represent each of the components of the original pattern.
     */
    final List<BigDecimalSpreadsheetTextFormatterComponent> components = Lists.array();

    @Override
    public String toString() {
        return this.components.toString();
    }
}
