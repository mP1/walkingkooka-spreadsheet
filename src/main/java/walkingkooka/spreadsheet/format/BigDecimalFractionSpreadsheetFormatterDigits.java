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

import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;

/**
 * Handles formatting of both the denominator and numerator.
 */
abstract class BigDecimalFractionSpreadsheetFormatterDigits implements UsesToStringBuilder {

    /**
     * {@see BigDecimalFractionSpreadsheetFormatterDigits}
     */
    static BigDecimalFractionSpreadsheetFormatterDenominatorDigits denominator(final String text) {
        return BigDecimalFractionSpreadsheetFormatterDenominatorDigits.with(text);
    }

    /**
     * {@see BigDecimalFractionSpreadsheetFormatterNumeratorDigits}
     */
    static BigDecimalFractionSpreadsheetFormatterNumeratorDigits numerator(final String text) {
        return BigDecimalFractionSpreadsheetFormatterNumeratorDigits.with(text);
    }

    /**
     * Package private to limit sub classing.
     */
    BigDecimalFractionSpreadsheetFormatterDigits(final String text) {
        super();
        this.text = "0".equals(text) ?
                "" :
                text;
    }

    /**
     * Appends a digit, and possibly sign if necessary.
     */
    final void append(final int digitSymbolPosition,
                      final BigDecimalFractionSpreadsheetFormatterZero zero,
                      final BigDecimalFractionSpreadsheetFormatterComponentContext context) {
        final int digitSymbolLength = context.digitSymbolCount;
        final String textDigits = context.digits.text;
        final int textDigitLength = textDigits.length();

        final int textDigitPosition = textDigitLength - digitSymbolLength + digitSymbolPosition;
        final int numberDigitPosition = digitSymbolLength - digitSymbolPosition;

        if (textDigitPosition >= 0) {
            this.addDigits(0 == digitSymbolPosition ? 0 : textDigitPosition,
                    textDigitPosition,
                    textDigits,
                    context);
        } else {
            zero.append(numberDigitPosition, context);
        }
    }

    private void addDigits(final int start,
                           final int end,
                           final String textDigits,
                           final BigDecimalFractionSpreadsheetFormatterComponentContext context) {
        int numberDigitPosition = textDigits.length() - start - 1;
        for (int i = start; i <= end; i++) {
            context.appendDigit(textDigits.charAt(i), numberDigitPosition--);
        }
    }

    abstract void sign(final BigDecimalFractionSpreadsheetFormatterComponentContext context);

    /**
     * Text that holds the individual digit characters.
     */
    final String text;

    @Override
    public final String toString() {
        return ToStringBuilder.buildFrom(this);
    }
}
