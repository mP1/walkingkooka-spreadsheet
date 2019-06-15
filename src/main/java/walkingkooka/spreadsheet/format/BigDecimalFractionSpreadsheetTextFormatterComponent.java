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

/**
 * A substitute for tokens in the original pattern.
 */
abstract class BigDecimalFractionSpreadsheetTextFormatterComponent {

    /**
     * {@see BigDecimalFractionSpreadsheetTextFormatterCurrencySymbolComponent}
     */
    static BigDecimalFractionSpreadsheetTextFormatterComponent currencySymbol() {
        return BigDecimalFractionSpreadsheetTextFormatterCurrencySymbolComponent.INSTANCE;
    }

    /**
     * {@see BigDecimalFractionSpreadsheetTextFormatterDigitComponent}
     */
    static BigDecimalFractionSpreadsheetTextFormatterComponent digit(final int position,
                                                                     final BigDecimalFractionSpreadsheetTextFormatterZero zero) {
        return BigDecimalFractionSpreadsheetTextFormatterDigitComponent.with(position, zero);
    }

    /**
     * {@see BigDecimalFractionSpreadsheetTextFormatterSlashSymbolComponent}
     */
    static BigDecimalFractionSpreadsheetTextFormatterComponent slashSymbol() {
        return BigDecimalFractionSpreadsheetTextFormatterSlashSymbolComponent.INSTANCE;
    }

    /**
     * {@see BigDecimalFractionSpreadsheetTextFormatterPercentageSymbolComponent}
     */
    static BigDecimalFractionSpreadsheetTextFormatterComponent percentageSymbol() {
        return BigDecimalFractionSpreadsheetTextFormatterPercentageSymbolComponent.INSTANCE;
    }

    /**
     * {@see BigDecimalFractionSpreadsheetTextFormatterTextLiteralComponent}
     */
    static BigDecimalFractionSpreadsheetTextFormatterComponent textLiteral(final String text) {
        return BigDecimalFractionSpreadsheetTextFormatterTextLiteralComponent.with(text);
    }

    BigDecimalFractionSpreadsheetTextFormatterComponent() {
        super();
    }

    /**
     * Invoked for each component which may then add zero or more characters or perform some action on the formatted text.
     */
    abstract void append(final BigDecimalFractionSpreadsheetTextFormatterComponentContext context);
}
