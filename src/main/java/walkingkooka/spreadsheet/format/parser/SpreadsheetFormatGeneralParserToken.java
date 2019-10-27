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

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * A token that includes a general literal and possibly other tokens such as whitespace.
 */
public final class SpreadsheetFormatGeneralParserToken extends SpreadsheetFormatParentParserToken {

    /**
     * Factory that creates a new {@link SpreadsheetFormatGeneralParserToken}.
     */
    static SpreadsheetFormatGeneralParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetFormatGeneralParserToken(copyAndCheckTokensFailIfEmpty(value),
                checkTextNotEmptyOrWhitespace(text));
    }

    /**
     * Private ctor use helper.
     */
    private SpreadsheetFormatGeneralParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    public void accept(SpreadsheetFormatParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatGeneralParserToken;
    }

}
