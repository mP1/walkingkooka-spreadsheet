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
 * Handles inserting the minus sign when required.
 */
enum SpreadsheetPatternSpreadsheetFormatterNumberMinusSign {
    /**
     * unconditionally adds a minus sign
     */
    REQUIRED {
        @Override
        boolean shouldAppendSymbol() {
            return true;
        }

        @Override
        String symbol() {
            return "-";
        }
    },

    /**
     * doesnt add a minus.
     */
    NOT_REQUIRED {
        @Override
        boolean shouldAppendSymbol() {
            return false;
        }

        @Override
        String symbol() {
            return "";
        }
    };

    abstract boolean shouldAppendSymbol();

    abstract String symbol();

    static SpreadsheetPatternSpreadsheetFormatterNumberMinusSign fromSignum(final int value) {
        return value < 0 ?
            REQUIRED :
            NOT_REQUIRED;
    }
}
