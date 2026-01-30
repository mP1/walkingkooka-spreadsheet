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
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.parser.CurrencySpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitSpaceSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DigitZeroSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EscapeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.FractionSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GroupSeparatorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.PercentSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.QuotedTextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.TextLiteralSpreadsheetFormatParserToken;

import java.math.BigDecimal;
import java.util.List;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that assembles a {@link SpreadsheetFormatter}. Much of the work is done
 * by counting the number of pattern tokens for digits and percentage symbols and other tokens.
 */
final class SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Analyzes the given {@link SpreadsheetFormatParserToken}.
     */
    static SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor analyze(final SpreadsheetFormatParserToken token) {
        final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return visitor;
    }

    /**
     * Private ctor use static method.
     */
    // @VisibleForTesting
    SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    // Visitor.....................................................................................................

    @Override
    protected void visit(final CurrencySpreadsheetFormatParserToken token) {
        this.add(SpreadsheetPatternSpreadsheetFormatterFractionComponent.currencySymbol());
    }

    @Override
    protected void visit(final DigitSpreadsheetFormatParserToken token) {
        this.addDigit(SpreadsheetPatternSpreadsheetFormatterFractionZero.HASH);
    }

    @Override
    protected void visit(final DigitSpaceSpreadsheetFormatParserToken token) {
        this.addDigit(SpreadsheetPatternSpreadsheetFormatterFractionZero.QUESTION_MARK);
    }

    @Override
    protected void visit(final DigitZeroSpreadsheetFormatParserToken token) {
        this.addDigit(SpreadsheetPatternSpreadsheetFormatterFractionZero.ZERO);
    }

    @Override
    protected void visit(final EscapeSpreadsheetFormatParserToken token) {
        this.add(SpreadsheetPatternSpreadsheetFormatterFractionComponent.textLiteral(token.value().toString()));
    }

    @Override
    protected void visit(final FractionSymbolSpreadsheetFormatParserToken token) {
        this.mode.slash(this);
        this.add(SpreadsheetPatternSpreadsheetFormatterFractionComponent.slashSymbol());
    }

    @Override
    protected void visit(final GroupSeparatorSpreadsheetFormatParserToken token) {
        this.multiplier = this.multiplier.scaleByPowerOfTen(-3); // divide by 1000
    }

    @Override
    protected void visit(final PercentSpreadsheetFormatParserToken token) {
        if (!this.percentage) {
            this.percentage = true;
            this.multiplier = this.multiplier.scaleByPowerOfTen(2);// x100
        }
        this.add(SpreadsheetPatternSpreadsheetFormatterFractionComponent.percentSymbol());
    }

    /**
     * Used to only update the multiplier (by a factor of 100) once for percentage.
     */
    private boolean percentage = false;

    @Override
    protected void visit(final QuotedTextSpreadsheetFormatParserToken token) {
        this.add(SpreadsheetPatternSpreadsheetFormatterFractionComponent.textLiteral(token.value()));
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetFormatParserToken token) {
        this.add(SpreadsheetPatternSpreadsheetFormatterFractionComponent.textLiteral(token.value()));
    }

    // misc ..................................................................................................

    /**
     * Tracks which number of the fraction is being processed.
     */
    SpreadsheetPatternSpreadsheetFormatterFractionMode mode = SpreadsheetPatternSpreadsheetFormatterFractionMode.NUMERATOR;

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
    private void addDigit(final SpreadsheetPatternSpreadsheetFormatterFractionZero zero) {
        this.add(SpreadsheetPatternSpreadsheetFormatterFractionComponent.digit(this.mode.digitCounterAndIncrement(this), zero));
    }

    /**
     * Adds another component
     */
    private void add(final SpreadsheetPatternSpreadsheetFormatterFractionComponent component) {
        this.components.add(component);
    }

    /**
     * Components represent each of the tokens of the original pattern.
     */
    final List<SpreadsheetPatternSpreadsheetFormatterFractionComponent> components = Lists.array();

    @Override
    public String toString() {
        return this.components.toString();
    }
}
