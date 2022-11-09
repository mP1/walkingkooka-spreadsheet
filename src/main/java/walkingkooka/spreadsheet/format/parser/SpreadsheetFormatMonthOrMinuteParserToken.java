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
package walkingkooka.spreadsheet.format.parser;

import java.util.Optional;

/**
 * Represents a month or minute token both of which use 'm' using context decide which is meant.
 */
public final class SpreadsheetFormatMonthOrMinuteParserToken extends SpreadsheetFormatNonSymbolParserToken<String> {

    static SpreadsheetFormatMonthOrMinuteParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatMonthOrMinuteParserToken(value, text);
    }

    private SpreadsheetFormatMonthOrMinuteParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    @Override
    public Optional<SpreadsheetFormatParserTokenKind> kind(final boolean minute) {
        final int textLength = this.textLength();

        return (
                minute ?
                        kindForMinute(textLength) :
                        kindForMonth(textLength)
        ).asOptional;
    }

    private static SpreadsheetFormatParserTokenKind kindForMinute(final int patternLength) {
        final SpreadsheetFormatParserTokenKind kind;

        switch (patternLength) {
            case 1:
                kind = SpreadsheetFormatParserTokenKind.MINUTES_WITHOUT_LEADING_ZERO;
                break;
            default:
                kind = SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO;
                break;
        }

        return kind;
    }

    private static SpreadsheetFormatParserTokenKind kindForMonth(final int patternLength) {
        final SpreadsheetFormatParserTokenKind kind;

        switch (patternLength) {
            case 1:
                kind = SpreadsheetFormatParserTokenKind.MONTH_WITHOUT_LEADING_ZERO;
                break;
            case 2:
                kind = SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO;
                break;
            case 3:
                kind = SpreadsheetFormatParserTokenKind.MONTH_NAME_ABBREVIATION;
                break;
            case 4:
                kind = SpreadsheetFormatParserTokenKind.MONTH_NAME_FULL;
                break;
            default:
                // https://www.myonlinetraininghub.com/excel-date-and-time-formatting
                kind = SpreadsheetFormatParserTokenKind.MONTH_NAME_INITIAL;
                break;
        }

        return kind;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatMonthOrMinuteParserToken;
    }

}
