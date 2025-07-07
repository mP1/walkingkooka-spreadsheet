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

import walkingkooka.Context;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;

/**
 * The context that accompanies each {@link SpreadsheetPatternSpreadsheetFormatterFractionComponent}.
 */
final class SpreadsheetPatternSpreadsheetFormatterFractionContext implements Context {

    /**
     * Factory that creates a new context.
     */
    static SpreadsheetPatternSpreadsheetFormatterFractionContext with(final SpreadsheetPatternSpreadsheetFormatterFractionNegativeSign numeratorSign,
                                                                      final SpreadsheetPatternSpreadsheetFormatterFractionDigitsNumerator numerator,
                                                                      final SpreadsheetPatternSpreadsheetFormatterFractionDigitsDenominator demonimator,
                                                                      final SpreadsheetPatternSpreadsheetFormatterFraction formatter,
                                                                      final SpreadsheetFormatterContext context) {
        return new SpreadsheetPatternSpreadsheetFormatterFractionContext(numeratorSign, numerator, demonimator, formatter, context);
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetPatternSpreadsheetFormatterFractionContext(final SpreadsheetPatternSpreadsheetFormatterFractionNegativeSign numeratorSign,
                                                                  final SpreadsheetPatternSpreadsheetFormatterFractionDigitsNumerator numerator,
                                                                  final SpreadsheetPatternSpreadsheetFormatterFractionDigitsDenominator demonimator,
                                                                  final SpreadsheetPatternSpreadsheetFormatterFraction formatter,
                                                                  final SpreadsheetFormatterContext context) {
        super();

        this.sign = numeratorSign;
        this.numerator = numerator;
        this.demonimator = demonimator;

        this.formatter = formatter;
        this.context = context;

        this.digits = numerator;
        this.digitSymbolCount = formatter.numeratorDigitSymbolCount;
    }

    void appendCurrencySymbol() {
        this.text.append(this.context.currencySymbol());
    }

    void appendDigit(final int symbolDigitPosition, final SpreadsheetPatternSpreadsheetFormatterFractionZero zero) {
        this.digits.append(symbolDigitPosition, zero, this);
    }

    void appendDigit(final char c) {
        this.digits.sign(this);
        this.text.append(c);
    }

    void appendMinusSign() {
        final SpreadsheetPatternSpreadsheetFormatterFractionNegativeSign sign = this.sign;
        if (sign.shouldAppendSymbol()) {
            this.text.append(this.context.negativeSign());
            this.sign = SpreadsheetPatternSpreadsheetFormatterFractionNegativeSign.NOT_REQUIRED;
        }
    }

    private SpreadsheetPatternSpreadsheetFormatterFractionNegativeSign sign;

    void appendPercentage() {
        this.text.append(this.context.percentSymbol());
    }

    void appendSlash() {
        this.appendMinusSign();
        this.text.append('/');
        this.digits = this.demonimator;
        this.digitSymbolCount = this.formatter.denominatorDigitSymbolCount;
    }

    void appendText(final String text) {
        this.text.append(text);
    }

    char zeroDigit() {
        return this.context.zeroDigit();
    }

    private final SpreadsheetFormatterContext context;

    SpreadsheetPatternSpreadsheetFormatterFractionDigits digits;
    int digitSymbolCount;

    private final SpreadsheetPatternSpreadsheetFormatterFractionDigits numerator;
    private final SpreadsheetPatternSpreadsheetFormatterFractionDigits demonimator;

    private final SpreadsheetPatternSpreadsheetFormatterFraction formatter;

    /**
     * Getter that returns the formatted text.
     */
    String formattedText() {
        return this.text.toString();
    }

    /**
     * Accumulates the formatted text.
     */
    private final StringBuilder text = new StringBuilder();

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .disable(ToStringBuilderOption.QUOTE)
            .separator("")
            .labelSeparator(this.sign.symbol())
            .value(this.numerator)
            .label("/")
            .value(this.demonimator)
            .label(" ")
            .value(this.text)
            .build();
    }
}
