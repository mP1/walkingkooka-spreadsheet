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

/**
 * Handles inserting the exponent digits into the formatted text output.
 */
final class NumberSpreadsheetTextFormatterExponentDigits extends NumberSpreadsheetTextFormatterExponentOrIntegerDigits {

    /**
     * Factory creates a new {@link NumberSpreadsheetTextFormatterExponentDigits}
     */
    static NumberSpreadsheetTextFormatterExponentDigits with(final NumberSpreadsheetTextFormatterMinusSign minusSign,
                                                             final String text) {
        return new NumberSpreadsheetTextFormatterExponentDigits(minusSign, text);
    }

    /**
     * Private ctor use factory
     */
    private NumberSpreadsheetTextFormatterExponentDigits(final NumberSpreadsheetTextFormatterMinusSign minusSign,
                                                         final String text) {
        super(minusSign, text);
    }

    @Override
    void append(final int digitSymbolPosition,
                final NumberSpreadsheetTextFormatterZero zero,
                final NumberSpreadsheetTextFormatterComponentContext context) {
        final int digitSymbolLength = context.formatter.exponentDigitSymbolCount;

        final String textDigits = context.exponent.text;
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

    @Override
    void thousandsSeparator(int numberDigitPosition, NumberSpreadsheetTextFormatterComponentContext context) {
        // exponents never have thousands separator
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label("E" + this.minusSign.symbol()).value(this.text);
    }
}
