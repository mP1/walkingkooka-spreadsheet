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
 * A substitute for tokens in the original pattern.
 */
abstract class NumberSpreadsheetFormatterComponent {

    /**
     * {@see NumberSpreadsheetFormatterCurrencySymbolComponent}
     */
    static NumberSpreadsheetFormatterComponent currencySymbol() {
        return NumberSpreadsheetFormatterCurrencySymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterDecimalPointSymbolComponent}
     */
    static NumberSpreadsheetFormatterComponent decimalPointSymbol() {
        return NumberSpreadsheetFormatterDecimalPointSymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterDigitComponent}
     */
    static NumberSpreadsheetFormatterComponent digit(final int position,
                                                     final NumberSpreadsheetFormatterZero zero) {
        return NumberSpreadsheetFormatterDigitComponent.with(position, zero);
    }

    /**
     * {@see NumberSpreadsheetFormatterExponentSymbolComponent}
     */
    static NumberSpreadsheetFormatterComponent exponentSymbol() {
        return NumberSpreadsheetFormatterExponentSymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterPercentageSymbolComponent}
     */
    static NumberSpreadsheetFormatterComponent percentageSymbol() {
        return NumberSpreadsheetFormatterPercentageSymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetFormatterLiteralComponent}
     */
    static NumberSpreadsheetFormatterComponent textLiteral(final String text) {
        return NumberSpreadsheetFormatterLiteralComponent.with(text);
    }

    NumberSpreadsheetFormatterComponent() {
        super();
    }

    /**
     * Invoked for each component which may then add zero or more characters or perform some action on the formatted text.
     */
    abstract void append(final NumberSpreadsheetFormatterComponentContext context);
}
