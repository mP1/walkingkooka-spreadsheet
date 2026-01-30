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
 * A substitute for tokens in the original pattern.
 */
abstract class SpreadsheetPatternSpreadsheetFormatterFractionComponent {

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFractionComponentCurrencySymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetPatternSpreadsheetFormatterFractionComponent currencySymbol() {
        return SpreadsheetPatternSpreadsheetFormatterFractionComponentCurrencySymbol.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFractionComponentDigit}
     */
    static SpreadsheetPatternSpreadsheetFormatterFractionComponent digit(final int position,
                                                                         final SpreadsheetPatternSpreadsheetFormatterFractionZero zero) {
        return SpreadsheetPatternSpreadsheetFormatterFractionComponentDigit.with(position, zero);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFractionComponentSlashSymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetPatternSpreadsheetFormatterFractionComponent slashSymbol() {
        return SpreadsheetPatternSpreadsheetFormatterFractionComponentSlashSymbol.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFractionComponentPercentSymbol}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetPatternSpreadsheetFormatterFractionComponent percentSymbol() {
        return SpreadsheetPatternSpreadsheetFormatterFractionComponentPercentSymbol.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFractionComponentTextLiteral}
     */
    static SpreadsheetPatternSpreadsheetFormatterFractionComponent textLiteral(final String text) {
        return SpreadsheetPatternSpreadsheetFormatterFractionComponentTextLiteral.with(text);
    }

    SpreadsheetPatternSpreadsheetFormatterFractionComponent() {
        super();
    }

    /**
     * Invoked for each component which may then add zero or more characters or perform some action on the formatted text.
     */
    abstract void append(final SpreadsheetPatternSpreadsheetFormatterFractionContext context);
}
