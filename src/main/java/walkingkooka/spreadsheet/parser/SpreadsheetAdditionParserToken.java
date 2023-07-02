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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a addition operation with its parameters.
 */
public final class SpreadsheetAdditionParserToken extends SpreadsheetBinaryParserToken {

    static SpreadsheetAdditionParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetAdditionParserToken(Lists.immutable(value), checkText(text));
    }

    private SpreadsheetAdditionParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetAdditionParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetAdditionParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetAdditionParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                SpreadsheetAdditionParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetAdditionParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfParent(
                this,
                predicate,
                SpreadsheetAdditionParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetAdditionParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                         final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetAdditionParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetAdditionParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                    final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetAdditionParserToken.class
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
        return other instanceof SpreadsheetAdditionParserToken;
    }
}
