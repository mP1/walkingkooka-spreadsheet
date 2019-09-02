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

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Determines how individual digits with a number are handled.
 */
enum SpreadsheetNumberParsePatternsMode {

    /**
     * Digits before any decimal separator.
     */
    INTEGER {
        @Override
        void onDigit(final int digit,
                     final SpreadsheetNumberParsePatternsContext context) {
            final MathContext mathContext = context.context.mathContext();

            context.mantissa = context.mantissa.multiply(BigDecimal.TEN, mathContext)
                    .add(BigDecimal.valueOf(digit), mathContext);
        }

        @Override
        void onDecimalSeparator(final SpreadsheetNumberParsePatternsContext context) {
            context.mode = DECIMAL;
        }
    },

    /**
     * Digits after a decimal separator
     */
    DECIMAL {
        @Override
        void onDigit(final int digit,
                     final SpreadsheetNumberParsePatternsContext context) {
            final MathContext mathContext = context.context.mathContext();

            context.mantissa = context.mantissa.multiply(BigDecimal.TEN, mathContext)
                    .add(BigDecimal.valueOf(digit), mathContext);
            context.exponent--;
        }

        @Override
        void onDecimalSeparator(final SpreadsheetNumberParsePatternsContext context) {
            // ignored
        }
    },

    /**
     * Digits belong to an exponent.
     */
    EXPONENT {
        @Override
        void onDigit(final int digit,
                     final SpreadsheetNumberParsePatternsContext context) {
            context.exponent = context.exponent * 10 + digit;
        }

        @Override
        void onDecimalSeparator(final SpreadsheetNumberParsePatternsContext context) {
            // ignored
        }
    };

    /**
     * Handles a onDigit with a number
     */
    abstract void onDigit(final int digit,
                          final SpreadsheetNumberParsePatternsContext context);

    /**
     * Handles a decimal separator, possibly switching onDigit mode.
     */
    abstract void onDecimalSeparator(final SpreadsheetNumberParsePatternsContext context);
}
