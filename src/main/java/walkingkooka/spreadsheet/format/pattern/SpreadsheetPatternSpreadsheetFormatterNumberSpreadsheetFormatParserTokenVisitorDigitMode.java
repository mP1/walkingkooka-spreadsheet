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

import walkingkooka.spreadsheet.format.parser.ExponentSpreadsheetFormatParserToken;

/**
 * Keeps track of the three modes or phases of a number, the integer, fractionDigitSymbolCount and exponentDigitSymbolCount digits.
 */
enum SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitorDigitMode {

    /**
     * Any digit symbols belong to the INTEGER portion of a number.
     */
    INTEGER {
        @Override
        void decimalPoint(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.digitMode = FRACTION;
        }

        @Override
        int digitCounterAndIncrement(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            if (visitor.comma > 0) {
                visitor.thousandsDivider = true;
                visitor.comma = 0;
            }
            return visitor.integerDigitSymbolCount++;
        }

        @Override
        void exponent(final ExponentSpreadsheetFormatParserToken token,
                      final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.exponent(token);
        }

        @Override
        void groupSeparator(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.comma++;
        }
    },

    /**
     * Any digit symbols belong to the FRACTION portion of a number.
     */
    FRACTION {
        @Override
        void decimalPoint(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.digitMode = EXPONENT;
        }

        @Override
        int digitCounterAndIncrement(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            return visitor.fractionDigitSymbolCount++;
        }

        @Override
        void exponent(final ExponentSpreadsheetFormatParserToken token,
                      final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.exponent(token);
        }

        @Override
        void groupSeparator(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.thousandsDivider = true;
        }
    },

    /**
     * Any digit symbols belong to the FRACTION portion of a number.
     */
    EXPONENT {
        @Override
        void decimalPoint(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            //nop
        }

        @Override
        int digitCounterAndIncrement(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            return visitor.exponentDigitSymbolCount++;
        }

        @Override
        void exponent(final ExponentSpreadsheetFormatParserToken token,
                      final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            // impossible parser doesnt support
        }

        @Override
        void groupSeparator(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor) {
            visitor.thousandsDivider = true;
        }
    };

    abstract void decimalPoint(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor);

    abstract int digitCounterAndIncrement(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor);

    abstract void exponent(final ExponentSpreadsheetFormatParserToken token,
                           final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor);

    abstract void groupSeparator(final SpreadsheetPatternSpreadsheetFormatterNumberSpreadsheetFormatParserTokenVisitor visitor);
}
