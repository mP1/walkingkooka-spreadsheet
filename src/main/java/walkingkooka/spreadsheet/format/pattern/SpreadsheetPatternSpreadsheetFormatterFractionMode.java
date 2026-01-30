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
 * Keeps track of the two number modes, the numerator and denominator.
 */
enum SpreadsheetPatternSpreadsheetFormatterFractionMode {

    /**
     * Any digit symbols belong to the numerator portion of a fraction.
     */
    NUMERATOR {
        @Override
        int digitCounterAndIncrement(final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor) {
            return visitor.numeratorDigitSymbolCount++;
        }

        @Override
        void slash(final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.mode = DENOMINATOR;
        }
    },

    /**
     * Any digit symbols belong to the denominator portion of a fraction.
     */
    DENOMINATOR {
        @Override
        int digitCounterAndIncrement(final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor) {
            return visitor.denominatorDigitSymbolCount++;
        }

        @Override
        void slash(final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor) {
            // nop
        }
    };

    abstract int digitCounterAndIncrement(final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor);

    abstract void slash(final SpreadsheetPatternSpreadsheetFormatterFractionSpreadsheetFormatParserTokenVisitor visitor);
}
