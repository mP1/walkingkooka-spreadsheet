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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitLeadingSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitLeadingZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatThousandsParserToken;

import java.math.BigDecimal;
import java.util.List;

/**
 * Counts the number of pattern tokens for digits and percentage symbols and other components.
 */
final class BigDecimalFractionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor extends TextFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Analyzes the given {@link SpreadsheetFormatParserToken}.
     */
    static BigDecimalFractionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor analyze(final SpreadsheetFormatParserToken token) {
        final BigDecimalFractionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor visitor = new BigDecimalFractionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return visitor;
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    BigDecimalFractionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    // Visitor.....................................................................................................

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.add(BigDecimalFractionSpreadsheetTextFormatterComponent.currencySymbol());
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.addDigit(BigDecimalFractionSpreadsheetTextFormatterZero.HASH);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitLeadingSpaceParserToken token) {
        this.addDigit(BigDecimalFractionSpreadsheetTextFormatterZero.QUESTION_MARK);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitLeadingZeroParserToken token) {
        this.addDigit(BigDecimalFractionSpreadsheetTextFormatterZero.ZERO);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.add(BigDecimalFractionSpreadsheetTextFormatterComponent.textLiteral(token.value().toString()));
    }

    @Override
    protected void visit(final SpreadsheetFormatFractionSymbolParserToken token) {
        this.mode.slash(this);
        this.add(BigDecimalFractionSpreadsheetTextFormatterComponent.slashSymbol());
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentSymbolParserToken token) {
        if (!this.percentage) {
            this.percentage = true;
            this.multiplier = this.multiplier.scaleByPowerOfTen(2);// x100
        }
        this.add(BigDecimalFractionSpreadsheetTextFormatterComponent.percentageSymbol());
    }

    /**
     * Used to only update the multiplier (by a factor of 100) once for percentage.
     */
    private boolean percentage = false;

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.add(BigDecimalFractionSpreadsheetTextFormatterComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.add(BigDecimalFractionSpreadsheetTextFormatterComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatThousandsParserToken token) {
        this.multiplier = this.multiplier.scaleByPowerOfTen(-3); // divide by 1000
    }

    // misc ..................................................................................................

    /**
     * Tracks which number of the fraction is being processed.
     */
    BigDecimalFractionSpreadsheetTextFormatterMode mode = BigDecimalFractionSpreadsheetTextFormatterMode.NUMERATOR;

    /**
     * Counts the number of numerator digit symbols.
     */
    int numeratorDigitSymbolCount = 0;

    /**
     * Counts the number of denominator digit symbols.
     */
    int denominatorDigitSymbolCount = 0;

    /**
     * A multiplier that is applied to the number before formatting.
     * This is increased when the thousands appear after the decimal point and percentage symbol.
     */
    BigDecimal multiplier = BigDecimal.ONE;

    /**
     * Adds another digit component
     */
    private void addDigit(final BigDecimalFractionSpreadsheetTextFormatterZero zero) {
        this.add(BigDecimalFractionSpreadsheetTextFormatterComponent.digit(this.mode.digitCounterAndIncrement(this), zero));
    }

    /**
     * Adds another component
     */
    private void add(final BigDecimalFractionSpreadsheetTextFormatterComponent component) {
        this.components.add(component);
    }

    /**
     * Components represent each of the components of the original pattern.
     */
    final List<BigDecimalFractionSpreadsheetTextFormatterComponent> components = Lists.array();

    @Override
    public String toString() {
        return this.components.toString();
    }
}
