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
 * Handles inserting the integer digits into the formatted text.
 */
final class NumberSpreadsheetFormatterDigitsInteger extends NumberSpreadsheetFormatterDigitsExponentOrInteger {

    /**
     * Creates a new {@link NumberSpreadsheetFormatterDigitsInteger}
     */
    static NumberSpreadsheetFormatterDigitsInteger with(final NumberSpreadsheetFormatterMinusSign minusSign,
                                                        final String text,
                                                        final NumberSpreadsheetFormatterGroupingSeparator groupingSeparator) {
        return new NumberSpreadsheetFormatterDigitsInteger(minusSign, text, groupingSeparator);
    }

    /**
     * Private ctor use factory
     */
    private NumberSpreadsheetFormatterDigitsInteger(final NumberSpreadsheetFormatterMinusSign minusSign,
                                                    final String text,
                                                    final NumberSpreadsheetFormatterGroupingSeparator groupingSeparator) {
        super(minusSign, text);
        this.groupingSeparator = groupingSeparator;
    }

    @Override
    void append(final int digitSymbolPosition,
                final NumberSpreadsheetFormatterZero zero,
                final NumberSpreadsheetFormatterContext context) {
        final int digitSymbolLength = context.formatter.integerDigitSymbolCount;

        final String textDigits = context.integer.text;
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
    void groupingSeparator(final int numberDigitPosition, final NumberSpreadsheetFormatterContext context) {
        this.groupingSeparator.append(numberDigitPosition, context);
    }

    private final NumberSpreadsheetFormatterGroupingSeparator groupingSeparator;

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label(this.minusSign.symbol()).value(this.text);
    }
}
