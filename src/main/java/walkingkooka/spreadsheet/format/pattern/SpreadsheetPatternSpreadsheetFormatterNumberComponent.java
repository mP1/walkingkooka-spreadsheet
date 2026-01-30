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

/**
 * Individual tokens or tokens with an entire pattern.
 */
abstract class SpreadsheetPatternSpreadsheetFormatterNumberComponent {

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumberComponentCurrencySymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetPatternSpreadsheetFormatterNumberComponent currencySymbol() {
        return SpreadsheetPatternSpreadsheetFormatterNumberComponentCurrencySymbol.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumberComponentDecimalSeparator}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetPatternSpreadsheetFormatterNumberComponent decimalSeparator() {
        return SpreadsheetPatternSpreadsheetFormatterNumberComponentDecimalSeparator.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumberComponentDigit}
     */
    static SpreadsheetPatternSpreadsheetFormatterNumberComponent digit(final int position,
                                                                       final SpreadsheetPatternSpreadsheetFormatterNumberZero zero) {
        return SpreadsheetPatternSpreadsheetFormatterNumberComponentDigit.with(position, zero);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumberComponentExponentSymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetPatternSpreadsheetFormatterNumberComponent exponentSymbol() {
        return SpreadsheetPatternSpreadsheetFormatterNumberComponentExponentSymbol.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumberComponentPercentSymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetPatternSpreadsheetFormatterNumberComponent percentSymbol() {
        return SpreadsheetPatternSpreadsheetFormatterNumberComponentPercentSymbol.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumberComponentTextLiteral}
     */
    static SpreadsheetPatternSpreadsheetFormatterNumberComponent textLiteral(final String text) {
        return SpreadsheetPatternSpreadsheetFormatterNumberComponentTextLiteral.with(text);
    }

    SpreadsheetPatternSpreadsheetFormatterNumberComponent() {
        super();
    }

    /**
     * Invoked for each component which may then add zero or more characters or perform some action on the formatted text.
     */
    abstract void append(final SpreadsheetPatternSpreadsheetFormatterNumberContext context);
}
