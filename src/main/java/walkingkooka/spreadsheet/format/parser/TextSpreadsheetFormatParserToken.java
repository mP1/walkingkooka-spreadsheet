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
import java.util.Objects;
import java.util.Optional;

/**
 * A token that contains a text formatting tokens.
 */
public final class TextSpreadsheetFormatParserToken extends ParentSpreadsheetFormatParserToken {

    /**
     * Factory that creates a new {@link TextSpreadsheetFormatParserToken}.
     */
    static TextSpreadsheetFormatParserToken with(final List<ParserToken> value, final String text) {
        final List<ParserToken> copy = copyAndCheckTokens(value);
        Objects.requireNonNull(text, "text");

        return new TextSpreadsheetFormatParserToken(copy, text);
    }

    /**
     * Private ctor use helper.
     */
    private TextSpreadsheetFormatParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public TextSpreadsheetFormatParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            TextSpreadsheetFormatParserToken::with
        );
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    public void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    @Override
    public Optional<SpreadsheetFormatParserTokenKind> kind() {
        return EMPTY_KIND;
    }
}
