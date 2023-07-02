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
import java.util.function.Predicate;

/**
 * A token that contains up to 4 sub expressions.
 */
public final class SpreadsheetFormatExpressionParserToken extends SpreadsheetFormatParentParserToken {

    /**
     * Factory that creates a new {@link SpreadsheetFormatExpressionParserToken}.
     */
    static SpreadsheetFormatExpressionParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetFormatExpressionParserToken(copyAndCheckTokensFailIfEmpty(value),
                checkTextNotEmpty(text));
    }

    /**
     * Private ctor use helper.
     */
    private SpreadsheetFormatExpressionParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetFormatExpressionParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetFormatExpressionParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetFormatExpressionParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                SpreadsheetFormatExpressionParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetFormatExpressionParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetFormatExpressionParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatExpressionParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                 final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetFormatExpressionParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatExpressionParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                            final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetFormatExpressionParserToken.class
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
        return other instanceof SpreadsheetFormatExpressionParserToken;
    }

}
