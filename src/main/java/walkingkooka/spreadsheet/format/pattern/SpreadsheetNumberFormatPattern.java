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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;

/**
 * Holds a valid {@link SpreadsheetNumberFormatPattern}.
 */
public final class SpreadsheetNumberFormatPattern extends SpreadsheetFormatPattern<SpreadsheetFormatNumberParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetNumberFormatPattern} from the given token.
     */
    static SpreadsheetNumberFormatPattern with(final SpreadsheetFormatNumberParserToken token) {
        SpreadsheetNumberFormatPatternSpreadsheetFormatParserTokenVisitor.with()
                .startAccept(token);

        return new SpreadsheetNumberFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetNumberFormatPattern(final SpreadsheetFormatNumberParserToken token) {
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
        return true;
    }

    @Override
    public boolean isTime() {
        return false;
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetNumberFormatPattern;
    }
}
