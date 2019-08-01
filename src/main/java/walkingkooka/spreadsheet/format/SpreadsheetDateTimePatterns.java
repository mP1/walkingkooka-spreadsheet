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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetDateTimePatterns}.
 */
final class SpreadsheetDateTimePatterns extends SpreadsheetPatterns<SpreadsheetFormatDateTimeParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetDateTimePatterns} from the given tokens.
     */
    static SpreadsheetDateTimePatterns withDateTime0(final List<SpreadsheetFormatDateTimeParserToken> value) {
        return new SpreadsheetDateTimePatterns(copyAndNotEmptyCheck(value));
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateTimePatterns(final List<SpreadsheetFormatDateTimeParserToken> value) {
        super(value);
    }

    @Override
    public boolean isDate() {
        return false;
    }

    @Override
    public boolean isDateTime() {
        return true;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDateTimePatterns;
    }
}
