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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitSpaceParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDigitZeroParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGroupSeparatorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatPercentParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;

import java.math.BigDecimal;
import java.util.List;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that assembles a {@link SpreadsheetFormatter}. Much of the work is done
 * by counting the number of pattern tokens for digits and percentage symbols and other components.
 */
final class FractionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Analyzes the given {@link SpreadsheetFormatParserToken}.
     */
    static FractionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor analyze(final SpreadsheetFormatParserToken token) {
        final FractionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = new FractionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return visitor;
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    FractionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    // Visitor.....................................................................................................

    @Override
    protected void visit(final SpreadsheetFormatCurrencyParserToken token) {
        this.add(FractionSpreadsheetFormatterComponent.currencySymbol());
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitParserToken token) {
        this.addDigit(FractionSpreadsheetFormatterZero.HASH);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitSpaceParserToken token) {
        this.addDigit(FractionSpreadsheetFormatterZero.QUESTION_MARK);
    }

    @Override
    protected void visit(final SpreadsheetFormatDigitZeroParserToken token) {
        this.addDigit(FractionSpreadsheetFormatterZero.ZERO);
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.add(FractionSpreadsheetFormatterComponent.textLiteral(token.value().toString()));
    }

    @Override
    protected void visit(final SpreadsheetFormatFractionSymbolParserToken token) {
        this.mode.slash(this);
        this.add(FractionSpreadsheetFormatterComponent.slashSymbol());
    }

    @Override
    protected void visit(final SpreadsheetFormatGroupSeparatorParserToken token) {
        this.multiplier = this.multiplier.scaleByPowerOfTen(-3); // divide by 1000
    }

    @Override
    protected void visit(final SpreadsheetFormatPercentParserToken token) {
        if (!this.percentage) {
            this.percentage = true;
            this.multiplier = this.multiplier.scaleByPowerOfTen(2);// x100
        }
        this.add(FractionSpreadsheetFormatterComponent.percentageSymbol());
    }

    /**
     * Used to only update the multiplier (by a factor of 100) once for percentage.
     */
    private boolean percentage = false;

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.add(FractionSpreadsheetFormatterComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.add(FractionSpreadsheetFormatterComponent.textLiteral(token.value()));
    }

    // misc ..................................................................................................

    /**
     * Tracks which number of the fraction is being processed.
     */
    FractionSpreadsheetFormatterMode mode = FractionSpreadsheetFormatterMode.NUMERATOR;

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
     * This is increased when the groupSeparator appear after the decimal point and percentage symbol.
     */
    BigDecimal multiplier = BigDecimal.ONE;

    /**
     * Adds another digit component
     */
    private void addDigit(final FractionSpreadsheetFormatterZero zero) {
        this.add(FractionSpreadsheetFormatterComponent.digit(this.mode.digitCounterAndIncrement(this), zero));
    }

    /**
     * Adds another component
     */
    private void add(final FractionSpreadsheetFormatterComponent component) {
        this.components.add(component);
    }

    /**
     * Components represent each of the components of the original pattern.
     */
    final List<FractionSpreadsheetFormatterComponent> components = Lists.array();

    @Override
    public String toString() {
        return this.components.toString();
    }
}
