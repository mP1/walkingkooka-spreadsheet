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
abstract class NumberSpreadsheetTextFormatterComponent {

    /**
     * {@see NumberSpreadsheetTextFormatterCurrencySymbolComponent}
     */
    static NumberSpreadsheetTextFormatterComponent currencySymbol() {
        return NumberSpreadsheetTextFormatterCurrencySymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetTextFormatterDecimalPointSymbolComponent}
     */
    static NumberSpreadsheetTextFormatterComponent decimalPointSymbol() {
        return NumberSpreadsheetTextFormatterDecimalPointSymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetTextFormatterDigitComponent}
     */
    static NumberSpreadsheetTextFormatterComponent digit(final int position,
                                                         final NumberSpreadsheetTextFormatterZero zero) {
        return NumberSpreadsheetTextFormatterDigitComponent.with(position, zero);
    }

    /**
     * {@see NumberSpreadsheetTextFormatterExponentSymbolComponent}
     */
    static NumberSpreadsheetTextFormatterComponent exponentSymbol() {
        return NumberSpreadsheetTextFormatterExponentSymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetTextFormatterPercentageSymbolComponent}
     */
    static NumberSpreadsheetTextFormatterComponent percentageSymbol() {
        return NumberSpreadsheetTextFormatterPercentageSymbolComponent.INSTANCE;
    }

    /**
     * {@see NumberSpreadsheetTextFormatterTextLiteralComponent}
     */
    static NumberSpreadsheetTextFormatterComponent textLiteral(final String text) {
        return NumberSpreadsheetTextFormatterTextLiteralComponent.with(text);
    }

    NumberSpreadsheetTextFormatterComponent() {
        super();
    }

    /**
     * Invoked for each component which may then add zero or more characters or perform some action on the formatted text.
     */
    abstract void append(final NumberSpreadsheetTextFormatterComponentContext context);
}
