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

import walkingkooka.spreadsheet.formula.parser.MillisecondSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;

import java.time.LocalTime;

/**
 * A {@link Parser} that matches the milliseconds and returns a {@link MillisecondSpreadsheetFormulaParserToken}
 */
final class SpreadsheetNonNumberParsePatternParserMilliseconds extends SpreadsheetNonNumberParsePatternParser {

    /**
     * Singleton instance
     */
    static SpreadsheetNonNumberParsePatternParserMilliseconds with(final String pattern) {
        return new SpreadsheetNonNumberParsePatternParserMilliseconds(pattern);
    }

    private SpreadsheetNonNumberParsePatternParserMilliseconds(final String pattern) {
        super();
        this.pattern = pattern;
    }

    @Override
    MillisecondSpreadsheetFormulaParserToken parseNotEmpty0(final TextCursor cursor,
                                                            final SpreadsheetParserContext context,
                                                            final TextCursorSavePoint start) {
        MillisecondSpreadsheetFormulaParserToken token;
        double digitValue = FIRST_DIGIT;
        double value = 0;


        for (; ; ) {
            final char c = cursor.at();
            final int digit = context.digit(c);
            if (-1 == digit) {
                token = digitValue != FIRST_DIGIT ?
                    token(value, start) :
                    null;
                break;
            }
            value += digit * digitValue;

            cursor.next();
            if (cursor.isEmpty()) {
                token = token(value, start);
                break;
            }

            digitValue = digitValue * 0.1f;
        }

        return token;
    }

    private final static long FIRST_DIGIT = LocalTime.of(0, 0, 1).toNanoOfDay() / 10;

    private static MillisecondSpreadsheetFormulaParserToken token(final double value,
                                                                  final TextCursorSavePoint start) {
        return SpreadsheetFormulaParserToken.millisecond(
            (int) Math.round(value), // shouldnt overload
            start.textBetween().toString()
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpreadsheetNonNumberParsePatternParserMilliseconds && this.equals0((SpreadsheetNonNumberParsePatternParserMilliseconds) other);
    }

    private boolean equals0(final SpreadsheetNonNumberParsePatternParserMilliseconds other) {
        return this.pattern.equals(other.pattern);
    }

    @Override
    public String toString() {
        return this.pattern;
    }

    /**
     * Pattern ignored during parsing
     */
    private final String pattern;
}
