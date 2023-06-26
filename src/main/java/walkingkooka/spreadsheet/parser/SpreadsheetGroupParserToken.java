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
package walkingkooka.spreadsheet.parser;

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Predicate;

/**
 * A wrapper around a grouping.
 */
public final class SpreadsheetGroupParserToken extends SpreadsheetParentParserToken {

    static SpreadsheetGroupParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetGroupParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetGroupParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetGroupParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetGroupParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public SpreadsheetGroupParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveFirstIf(
                this,
                predicate,
                SpreadsheetGroupParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetGroupParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetGroupParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetGroupParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                      final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetGroupParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetGroupParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                 final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetGroupParserToken.class
        );
    }
    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetGroupParserToken;
    }
}
