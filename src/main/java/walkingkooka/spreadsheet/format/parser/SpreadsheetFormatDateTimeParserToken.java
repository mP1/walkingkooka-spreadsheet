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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A token that contains date/time formatting tokens.
 */
public final class SpreadsheetFormatDateTimeParserToken extends SpreadsheetFormatParentParserToken {

    /**
     * Factory that creates a new {@link SpreadsheetFormatDateTimeParserToken}.
     */
    static SpreadsheetFormatDateTimeParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetFormatDateTimeParserToken(copyAndCheckTokensFailIfEmpty(value),
                checkTextNotEmpty(text));
    }

    /**
     * Private ctor use helper.
     */
    private SpreadsheetFormatDateTimeParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetFormatDateTimeParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetFormatDateTimeParserToken::with
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatDateTimeParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                               final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatDateTimeParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatDateTimeParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                          final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatDateTimeParserToken.class
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

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatDateTimeParserToken;
    }
}
