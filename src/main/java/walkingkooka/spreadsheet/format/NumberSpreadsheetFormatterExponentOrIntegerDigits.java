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

/**
 * Base class for both integer and exponent digits. These share several similarities including inserting the sign
 * and adding digits.
 */
abstract class NumberSpreadsheetFormatterExponentOrIntegerDigits extends NumberSpreadsheetFormatterDigits {

    NumberSpreadsheetFormatterExponentOrIntegerDigits(final NumberSpreadsheetFormatterMinusSign minusSign,
                                                      final String text) {
        super(text);
        this.minusSign = minusSign;
    }

    final void addDigits(final int start,
                         final int end,
                         final String textDigits,
                         final NumberSpreadsheetFormatterComponentContext context) {
        int numberDigitPosition = textDigits.length() - start - 1;
        for (int i = start; i <= end; i++) {
            context.appendDigit(textDigits.charAt(i), numberDigitPosition--);
        }
    }

    @Override
    final void sign(final NumberSpreadsheetFormatterComponentContext context) {
        if (this.minusSign.shouldAppendSymbol()) {
            context.appendNegativeSign();
            this.minusSign = NumberSpreadsheetFormatterMinusSign.NOT_REQUIRED;
        }
    }

    /**
     * This will after the initial sign is inserted on demand.
     */
    NumberSpreadsheetFormatterMinusSign minusSign;
}
