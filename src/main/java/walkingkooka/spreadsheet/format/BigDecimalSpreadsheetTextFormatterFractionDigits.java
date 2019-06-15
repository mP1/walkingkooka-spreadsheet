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

import walkingkooka.build.tostring.ToStringBuilder;

/**
 * Handles copy fraction digits including adding zero or spaces for trailing "zero" digits.
 */
final class BigDecimalSpreadsheetTextFormatterFractionDigits extends BigDecimalSpreadsheetTextFormatterDigits {

    /**
     * Creates a new {@link BigDecimalSpreadsheetTextFormatterFractionDigits}
     */
    static BigDecimalSpreadsheetTextFormatterFractionDigits with(final String text) {

        // remove any trailing zeros...
        int end = text.length();
        while (end > 0) {
            if (text.charAt(end - 1) != '0') {
                break;
            }
            end--;
        }

        return new BigDecimalSpreadsheetTextFormatterFractionDigits(text.substring(0, end));
    }

    /**
     * Private ctor use factory
     */
    private BigDecimalSpreadsheetTextFormatterFractionDigits(final String text) {
        super(text);
    }

    @Override
    void append(final int numberDigitPosition,
                final BigDecimalSpreadsheetTextFormatterZero zero,
                final BigDecimalSpreadsheetTextFormatterComponentContext context) {
        final String textDigits = context.fraction.text;

        if (numberDigitPosition < textDigits.length()) {
            context.appendDigit(textDigits.charAt(numberDigitPosition), numberDigitPosition);
        } else {
            zero.append(numberDigitPosition, context);
        }
    }

    @Override
    void sign(final BigDecimalSpreadsheetTextFormatterComponentContext context) {
        // never append sign inside a fraction
    }

    final void thousandsSeparator(final int numberDigitPosition, final BigDecimalSpreadsheetTextFormatterComponentContext context) {
        // nop
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label(".").value(this.text);
    }
}
