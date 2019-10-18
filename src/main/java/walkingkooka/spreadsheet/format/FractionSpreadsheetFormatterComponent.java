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
abstract class FractionSpreadsheetFormatterComponent {

    /**
     * {@see FractionSpreadsheetFormatterComponentCurrencySymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static FractionSpreadsheetFormatterComponent currencySymbol() {
        return FractionSpreadsheetFormatterComponentCurrencySymbol.INSTANCE;
    }

    /**
     * {@see FractionSpreadsheetFormatterComponentDigit}
     */
    static FractionSpreadsheetFormatterComponent digit(final int position,
                                                       final FractionSpreadsheetFormatterZero zero) {
        return FractionSpreadsheetFormatterComponentDigit.with(position, zero);
    }

    /**
     * {@see FractionSpreadsheetFormatterComponentSlashSymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static FractionSpreadsheetFormatterComponent slashSymbol() {
        return FractionSpreadsheetFormatterComponentSlashSymbol.INSTANCE;
    }

    /**
     * {@see FractionSpreadsheetFormatterComponentPercentageSymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static FractionSpreadsheetFormatterComponent percentageSymbol() {
        return FractionSpreadsheetFormatterComponentPercentageSymbol.INSTANCE;
    }

    /**
     * {@see FractionSpreadsheetFormatterComponentTextLiteral}
     */
    static FractionSpreadsheetFormatterComponent textLiteral(final String text) {
        return FractionSpreadsheetFormatterComponentTextLiteral.with(text);
    }

    FractionSpreadsheetFormatterComponent() {
        super();
    }

    /**
     * Invoked for each component which may then add zero or more characters or perform some action on the formatted text.
     */
    abstract void append(final FractionSpreadsheetFormatterContext context);
}
