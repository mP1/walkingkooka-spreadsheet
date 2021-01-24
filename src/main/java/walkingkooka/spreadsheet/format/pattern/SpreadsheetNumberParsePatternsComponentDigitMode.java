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

import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.TextCursor;

/**
 * Determines how individual digits with a number are handled.
 */
enum SpreadsheetNumberParsePatternsComponentDigitMode {

    /**
     * Digits before any decimal separator.
     */
    INTEGER_OR_SIGN {

        @Override
        SpreadsheetNumberParsePatternsComponentDigitMode next() {
            return INTEGER;
        }
    },

    /**
     * Digits before any decimal separator.
     */
    INTEGER {

        @Override
        SpreadsheetNumberParsePatternsComponentDigitMode next() {
            return INTEGER;
        }
    },

    /**
     * Digits after a decimal separator
     */
    DECIMAL_FIRST {

        @Override
        SpreadsheetNumberParsePatternsComponentDigitMode next() {
            return DECIMAL;
        }

    },

    /**
     * Digits after a decimal separator
     */
    DECIMAL {

        @Override
        SpreadsheetNumberParsePatternsComponentDigitMode next() {
            return DECIMAL;
        }
    },

    /**
     * Digits or sign
     */
    EXPONENT_OR_SIGN {

        @Override
        SpreadsheetNumberParsePatternsComponentDigitMode next() {
            return EXPONENT;
        }
    },

    /**
     * Digits belong to an exponent.
     */
    EXPONENT {

        @Override
        SpreadsheetNumberParsePatternsComponentDigitMode next() {
            return EXPONENT;
        }
    };

    /**
     * Returns true if this is the first digit in sequence.
     */
    final boolean isFirst() {
        return this.isSign() || this == DECIMAL_FIRST;
    }

    boolean isSign() {
        return this == INTEGER_OR_SIGN || this == EXPONENT_OR_SIGN;
    }

    final boolean isDecimal() {
        return this == DECIMAL_FIRST || this == DECIMAL;
    }

    final void tryParseSign(final TextCursor cursor,
                            final SpreadsheetNumberParsePatternsRequest request) {
        if (this.isSign()) {
            final DecimalNumberContext context = request.context;

            final char c = cursor.at();
            if (context.positiveSign() == c) {
                final String plusSign = Character.toString(c);
                request.add(SpreadsheetParserToken.plusSymbol(plusSign, plusSign));
                request.setDigitMode(this.next());
                cursor.next();
            } else {
                if (context.negativeSign() == c) {
                    final String minusSign = Character.toString(c);
                    request.add(SpreadsheetParserToken.minusSymbol(minusSign, minusSign));
                    request.setDigitMode(this.next());
                    cursor.next();
                }
            }
        }
    }


    /**
     * Advances a mode to the unsigned form.
     */
    abstract SpreadsheetNumberParsePatternsComponentDigitMode next();

    /**
     * Handles a decimal separator, possibly switching onDigit mode.
     */
    final void onDecimalSeparator(final SpreadsheetNumberParsePatternsRequest context) {
        if(this == INTEGER_OR_SIGN || this == INTEGER) {
            context.setDigitMode(SpreadsheetNumberParsePatternsComponentDigitMode.DECIMAL_FIRST);
        }
    }
}
