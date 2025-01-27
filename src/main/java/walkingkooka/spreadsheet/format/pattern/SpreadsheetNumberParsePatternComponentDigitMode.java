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
import walkingkooka.spreadsheet.formula.SpreadsheetParserToken;
import walkingkooka.text.cursor.TextCursor;

/**
 * Determines how individual digits with a number are handled.
 */
enum SpreadsheetNumberParsePatternComponentDigitMode {

    /**
     * The sign or first digit for the integer portion of a number, before any decimal separator or exponent.
     */
    INTEGER_OR_SIGN {
        @Override
        SpreadsheetNumberParsePatternComponentDigitMode next() {
            return INTEGER;
        }
    },

    /**
     * Digits before any decimal separator.
     */
    INTEGER {
        @Override
        SpreadsheetNumberParsePatternComponentDigitMode next() {
            return INTEGER;
        }
    },

    /**
     * The first digit after a decimal separator
     */
    DECIMAL_FIRST {
        @Override
        SpreadsheetNumberParsePatternComponentDigitMode next() {
            return DECIMAL_NOT_FIRST;
        }

    },

    /**
     * Any digits but not the first after a decimal separataor
     */
    DECIMAL_NOT_FIRST {
        @Override
        SpreadsheetNumberParsePatternComponentDigitMode next() {
            return DECIMAL_NOT_FIRST;
        }
    },

    /**
     * THe start of the exponent portion of a number which may be a sign or digit
     */
    EXPONENT_START {
        @Override
        SpreadsheetNumberParsePatternComponentDigitMode next() {
            return EXPONENT;
        }
    },

    /**
     * Digits belong to an exponent.
     */
    EXPONENT {
        @Override
        SpreadsheetNumberParsePatternComponentDigitMode next() {
            return EXPONENT;
        }
    };

    final boolean isDecimal() {
        return this == DECIMAL_FIRST || this == DECIMAL_NOT_FIRST;
    }

    /**
     * Returns true if this is the first digit in sequence.
     */
    final boolean isFirstDigit() {
        return this.isSign() || this == DECIMAL_FIRST;
    }

    /**
     * Returns true if this is an integer mode.
     */
    final boolean isInteger() {
        return this == INTEGER_OR_SIGN || this == INTEGER;
    }

    final boolean isSign() {
        return this == INTEGER_OR_SIGN || this == EXPONENT_START;
    }

    final void tryParseSign(final TextCursor cursor,
                            final SpreadsheetNumberParsePatternRequest request) {
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
     * Advances a mode to continue processing more characters.
     */
    abstract SpreadsheetNumberParsePatternComponentDigitMode next();

    /**
     * Handles a decimal separator, possibly switching onDigit mode.
     */
    final void onDecimalSeparator(final SpreadsheetNumberParsePatternRequest context) {
        if (this == INTEGER_OR_SIGN || this == INTEGER) {
            context.setDigitMode(SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST);
        }
    }
}
