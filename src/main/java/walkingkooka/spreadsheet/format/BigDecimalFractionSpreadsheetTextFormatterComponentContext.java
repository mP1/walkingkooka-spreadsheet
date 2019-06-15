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

import walkingkooka.Context;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.build.tostring.ToStringBuilderOption;

/**
 * The context that accompanies each {@link BigDecimalFractionSpreadsheetTextFormatterComponent}.
 */
final class BigDecimalFractionSpreadsheetTextFormatterComponentContext implements Context {

    /**
     * Factory that creates a new context.
     */
    static BigDecimalFractionSpreadsheetTextFormatterComponentContext with(final BigDecimalFractionSpreadsheetTextFormatterMinusSign numeratorSign,
                                                                           final BigDecimalFractionSpreadsheetTextFormatterNumeratorDigits numerator,
                                                                           final BigDecimalFractionSpreadsheetTextFormatterDenominatorDigits demonimator,
                                                                           final BigDecimalFractionSpreadsheetTextFormatter formatter,
                                                                           final SpreadsheetTextFormatContext context) {
        return new BigDecimalFractionSpreadsheetTextFormatterComponentContext(numeratorSign, numerator, demonimator, formatter, context);
    }

    /**
     * Private ctor use factory.
     */
    private BigDecimalFractionSpreadsheetTextFormatterComponentContext(final BigDecimalFractionSpreadsheetTextFormatterMinusSign numeratorSign,
                                                                       final BigDecimalFractionSpreadsheetTextFormatterNumeratorDigits numerator,
                                                                       final BigDecimalFractionSpreadsheetTextFormatterDenominatorDigits demonimator,
                                                                       final BigDecimalFractionSpreadsheetTextFormatter formatter,
                                                                       final SpreadsheetTextFormatContext context) {
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

    void appendDigit(final int symbolDigitPosition, final BigDecimalFractionSpreadsheetTextFormatterZero zero) {
        this.digits.append(symbolDigitPosition, zero, this);
    }

    void appendDigit(final char c, final int numberDigitPosition) {
        this.digits.sign(this);
        this.text.append(c);
    }

    void appendMinusSign() {
        final BigDecimalFractionSpreadsheetTextFormatterMinusSign sign = this.sign;
        if (sign.shouldAppendSymbol()) {
            this.text.append(this.context.minusSign());
            this.sign = BigDecimalFractionSpreadsheetTextFormatterMinusSign.NOT_REQUIRED;
        }
    }

    private BigDecimalFractionSpreadsheetTextFormatterMinusSign sign;

    void appendPercentage() {
        this.text.append(this.context.percentageSymbol());
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

    private final SpreadsheetTextFormatContext context;

    BigDecimalFractionSpreadsheetTextFormatterDigits digits;
    int digitSymbolCount;

    private final BigDecimalFractionSpreadsheetTextFormatterDigits numerator;
    private final BigDecimalFractionSpreadsheetTextFormatterDigits demonimator;

    final BigDecimalFractionSpreadsheetTextFormatter formatter;

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
