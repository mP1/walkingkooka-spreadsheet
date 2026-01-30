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

import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;

/**
 * Unconditionally inserts the decimal point parse the {@link SpreadsheetFormatterContext}.
 */
final class SpreadsheetPatternSpreadsheetFormatterNumberComponentDecimalSeparator extends SpreadsheetPatternSpreadsheetFormatterNumberComponent {

    /**
     * Singleton
     */
    static final SpreadsheetPatternSpreadsheetFormatterNumberComponentDecimalSeparator INSTANCE = new SpreadsheetPatternSpreadsheetFormatterNumberComponentDecimalSeparator();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetPatternSpreadsheetFormatterNumberComponentDecimalSeparator() {
        super();
    }

    @Override
    void append(final SpreadsheetPatternSpreadsheetFormatterNumberContext context) {
        context.appendDecimalSeparator(context.fraction);
    }

    @Override
    public String toString() {
        return ".";
    }
}
