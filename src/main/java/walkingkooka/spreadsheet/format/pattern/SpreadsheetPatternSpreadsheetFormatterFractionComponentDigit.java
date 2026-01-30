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
 * Represents a placeholder for a digit.
 */
final class SpreadsheetPatternSpreadsheetFormatterFractionComponentDigit extends SpreadsheetPatternSpreadsheetFormatterFractionComponent {

    /**
     * Factory that creates a {@link SpreadsheetPatternSpreadsheetFormatterFractionComponentDigit}.
     */
    static SpreadsheetPatternSpreadsheetFormatterFractionComponentDigit with(final int position,
                                                                             final SpreadsheetPatternSpreadsheetFormatterFractionZero zero) {
        return new SpreadsheetPatternSpreadsheetFormatterFractionComponentDigit(position, zero);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetPatternSpreadsheetFormatterFractionComponentDigit(final int position,
                                                                         final SpreadsheetPatternSpreadsheetFormatterFractionZero zero) {
        super();

        this.position = position;
        this.zero = zero;
    }

    @Override
    void append(final SpreadsheetPatternSpreadsheetFormatterFractionContext context) {
        context.appendDigit(this.position, this.zero);
    }

    private final int position;

    private final SpreadsheetPatternSpreadsheetFormatterFractionZero zero;

    @Override
    public String toString() {
        return this.zero.pattern();
    }
}
