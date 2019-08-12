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
 * THe thousands separator
 */
enum NumberSpreadsheetFormatterThousandsSeparator {

    /**
     * Only inserts the grouping or thousands separator if the textDigitPosition is a multiple of 3.
     */
    INCLUDE {
        @Override
        void append(final int numberDigitPosition, final NumberSpreadsheetFormatterComponentContext context) {
            if (numberDigitPosition > 0 && 0 == numberDigitPosition % 3) {
                context.appendGroupingSeparator();
            }
        }
    },
    /**
     * No separators are included in the formatted text.
     */
    NONE {
        @Override
        void append(final int numberDigitPosition, final NumberSpreadsheetFormatterComponentContext context) {
            // nop
        }
    };

    abstract void append(final int numberDigitPosition, final NumberSpreadsheetFormatterComponentContext context);
}
