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
 * Individual components or tokens with an entire pattern.
 */
abstract class NumberSpreadsheetFormatterComponent {

    /**
     * {@see NumberSpreadsheetFormatterComponentCurrencySymbol}
     */
    static NumberSpreadsheetFormatterComponent currencySymbol() {
        return NumberSpreadsheetFormatterComponentCurrencySymbol.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterComponentDecimalSeparator}
     */
    static NumberSpreadsheetFormatterComponent decimalSeparator() {
        return NumberSpreadsheetFormatterComponentDecimalSeparator.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterComponentDigit}
     */
    static NumberSpreadsheetFormatterComponent digit(final int position,
                                                     final NumberSpreadsheetFormatterZero zero) {
        return NumberSpreadsheetFormatterComponentDigit.with(position, zero);
    }

    /**
     * {@see NumberSpreadsheetFormatterComponentExponentSymbol}
     */
    static NumberSpreadsheetFormatterComponent exponentSymbol() {
        return NumberSpreadsheetFormatterComponentExponentSymbol.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterComponentPercentageSymbol}
     */
    static NumberSpreadsheetFormatterComponent percentageSymbol() {
        return NumberSpreadsheetFormatterComponentPercentageSymbol.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterComponentTextLiteral}
     */
    static NumberSpreadsheetFormatterComponent textLiteral(final String text) {
        return NumberSpreadsheetFormatterComponentTextLiteral.with(text);
    }

    NumberSpreadsheetFormatterComponent() {
        super();
    }

    /**
     * Invoked for each component which may then add zero or more characters or perform some action on the formatted text.
     */
    abstract void append(final NumberSpreadsheetFormatterComponentContext context);
}
