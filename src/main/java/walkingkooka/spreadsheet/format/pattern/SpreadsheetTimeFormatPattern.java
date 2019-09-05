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

import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;

import java.time.LocalTime;

/**
 * Holds a valid {@link SpreadsheetTimeFormatPattern}.
 */
public final class SpreadsheetTimeFormatPattern extends SpreadsheetFormatPattern<SpreadsheetFormatTimeParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetTimeFormatPattern} from the given token.
     */
    static SpreadsheetTimeFormatPattern with(final SpreadsheetFormatTimeParserToken token) {
        SpreadsheetTimeFormatPatternSpreadsheetFormatParserTokenVisitor.with().startAccept(token);

        return new SpreadsheetTimeFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTimeFormatPattern(final SpreadsheetFormatTimeParserToken token) {
        super(token);
    }

    @Override
    public boolean isDate() {
        return false;
    }

    @Override
    public boolean isDateTime() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isText() {
        return false;
    }

    @Override
    public boolean isTime() {
        return true;
    }

    // HasSpreadsheetFormatter..........................................................................................

    @Override
    SpreadsheetFormatter createFormatter() {
        final SpreadsheetFormatTimeParserToken time = this.value;

        return SpreadsheetFormatters.dateTime(SpreadsheetFormatParserToken.dateTime(time.value(), time.text()), LocalTime.class);
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTimeFormatPattern;
    }
}
