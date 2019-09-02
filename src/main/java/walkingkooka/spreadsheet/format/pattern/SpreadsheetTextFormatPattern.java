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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;

/**
 * Holds a valid {@link SpreadsheetPattern} to format {@link String text}.
 */
public final class SpreadsheetTextFormatPattern extends SpreadsheetFormatPattern<SpreadsheetFormatTextParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetTextFormatPattern} from the given token.
     */
    static SpreadsheetTextFormatPattern with(final SpreadsheetFormatTextParserToken token) {
        SpreadsheetTextFormatPatternSpreadsheetFormatParserTokenVisitor.with()
                .startAccept(token);

        return new SpreadsheetTextFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTextFormatPattern(final SpreadsheetFormatTextParserToken token) {
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
        return true;
    }

    @Override
    public boolean isTime() {
        return false;
    }

    // HasSpreadsheetFormatter..........................................................................................

    /**
     * Factory that lazily creates a {@link SpreadsheetFormatter}
     */
    @Override
    SpreadsheetFormatter createFormatter() {
        return SpreadsheetFormatters.text(this.value);
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTextFormatPattern;
    }
}
