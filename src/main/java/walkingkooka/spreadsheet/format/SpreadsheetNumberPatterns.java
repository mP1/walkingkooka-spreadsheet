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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetNumberPatterns}.
 */
public final class SpreadsheetNumberPatterns extends SpreadsheetPatterns<SpreadsheetFormatNumberParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetNumberPatterns} from the given tokens.
     */
    static SpreadsheetNumberPatterns withNumber0(final List<SpreadsheetFormatNumberParserToken> value) {
        return new SpreadsheetNumberPatterns(copyAndCheck(value, SpreadsheetNumberPatternsSpreadsheetFormatParserTokenVisitor.with()));
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetNumberPatterns(final List<SpreadsheetFormatNumberParserToken> value) {
        super(value);
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
        return other instanceof SpreadsheetNumberPatterns;
    }
}
