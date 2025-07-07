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
 * The different types of zero formatting.
 */
enum SpreadsheetPatternSpreadsheetFormatterNumberZero {

    /**
     * format digit=#, leading zeros are omitted
     */
    HASH {
        @Override
        void append(final int textDigitPosition, final SpreadsheetPatternSpreadsheetFormatterNumberContext context) {
            // nop
        }

        @Override
        String pattern() {
            return "#";
        }
    },
    /**
     * format digit=? leading zeros are replaced with a space
     */
    QUESTION_MARK {
        @Override
        void append(final int textDigitPosition, final SpreadsheetPatternSpreadsheetFormatterNumberContext context) {
            context.appendDigit(' ', textDigitPosition);
        }

        @Override
        String pattern() {
            return "?";
        }
    },
    /**
     * format digit=0 leading zeroes are included as zeros.
     */
    ZERO {
        @Override
        void append(final int textDigitPosition, final SpreadsheetPatternSpreadsheetFormatterNumberContext context) {
            context.appendDigit(
                '0',
                textDigitPosition
            );
        }

        @Override
        String pattern() {
            return "0";
        }
    };

    abstract void append(final int textDigitPosition, final SpreadsheetPatternSpreadsheetFormatterNumberContext context);

    /**
     * Returns the pattern representation. This is used in building {@link #toString()} responses.
     */
    abstract String pattern();
}
