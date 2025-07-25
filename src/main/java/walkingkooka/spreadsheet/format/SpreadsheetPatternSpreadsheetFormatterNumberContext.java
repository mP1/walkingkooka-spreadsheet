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
 * The context that accompanies each {@link SpreadsheetPatternSpreadsheetFormatterNumberComponent}.
 */
final class SpreadsheetPatternSpreadsheetFormatterNumberContext implements Context {

    /**
     * Factory that creates a new context.
     */
    static SpreadsheetPatternSpreadsheetFormatterNumberContext with(final boolean currency,
                                                                    final SpreadsheetPatternSpreadsheetFormatterNumberDigits integer,
                                                                    final SpreadsheetPatternSpreadsheetFormatterNumberDigits fraction,
                                                                    final SpreadsheetPatternSpreadsheetFormatterNumberDigits exponent,
                                                                    final SpreadsheetPatternSpreadsheetFormatterNumber formatter,
                                                                    final SpreadsheetFormatterContext context) {
        return new SpreadsheetPatternSpreadsheetFormatterNumberContext(
            currency,
            integer,
            fraction,
            exponent,
            formatter,
            context
        );
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetPatternSpreadsheetFormatterNumberContext(final boolean currency,
                                                                final SpreadsheetPatternSpreadsheetFormatterNumberDigits integer,
                                                                final SpreadsheetPatternSpreadsheetFormatterNumberDigits fraction,
                                                                final SpreadsheetPatternSpreadsheetFormatterNumberDigits exponent,
                                                                final SpreadsheetPatternSpreadsheetFormatterNumber formatter,
                                                                final SpreadsheetFormatterContext context) {
        super();

        this.currency = currency;

        this.integer = integer;
        this.fraction = fraction;
        this.exponent = exponent;

        this.formatter = formatter;
        this.context = context;

        this.digits = integer;
    }

    void appendCurrencySymbol() {
        this.text.append(this.context.currencySymbol());
    }

    void appendDigit(final int symbolDigitPosition, final SpreadsheetPatternSpreadsheetFormatterNumberZero zero) {
        this.digits.append(symbolDigitPosition, zero, this);
    }

    void appendDigit(final char c,
                     final int numberDigitPosition) {
        this.digits.sign(this);
        this.text.append(c);
        this.digits.groupSeparator(numberDigitPosition, this);
    }

    void appendDecimalSeparator(final SpreadsheetPatternSpreadsheetFormatterNumberDigits next) {
        this.digits.sign(this);

        final SpreadsheetFormatterContext context = this.context;

        this.text.append(
            this.currency ?
                context.monetaryDecimalSeparator() :
                context.decimalSeparator()
        );
        this.digits = next;
    }

    void appendExponent() {
        this.text.append(this.context.exponentSymbol());
        this.digits = this.exponent;
    }

    void appendGroupSeparator() {
        this.text.append(this.context.groupSeparator());
    }

    void appendNegativeSign() {
        this.text.append(this.context.negativeSign());
    }

    void appendPercent() {
        this.text.append(this.context.percentSymbol());
    }

    void appendText(final String text) {
        this.text.append(text);
    }

    char zeroDigit() {
        return this.context.zeroDigit();
    }

    private final SpreadsheetFormatterContext context;

    private final boolean currency;

    private SpreadsheetPatternSpreadsheetFormatterNumberDigits digits;

    final SpreadsheetPatternSpreadsheetFormatterNumberDigits integer;
    final SpreadsheetPatternSpreadsheetFormatterNumberDigits fraction;
    final SpreadsheetPatternSpreadsheetFormatterNumberDigits exponent;
    final SpreadsheetPatternSpreadsheetFormatterNumber formatter;

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
            .labelSeparator("")
            .value(this.currency ? "currency " : "") // https://github.com/mP1/walkingkooka/issues/2525
            .value(this.integer)
            .value(this.fraction)
            .value(this.exponent)
            .label(" ")
            .value(this.text)
            .build();
    }
}
