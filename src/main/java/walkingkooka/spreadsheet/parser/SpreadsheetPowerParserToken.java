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
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a power operation with its parameters.
 */
public final class SpreadsheetPowerParserToken extends SpreadsheetBinaryParserToken {

    static SpreadsheetPowerParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetPowerParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetPowerParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetPowerParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetPowerParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetPowerParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                SpreadsheetPowerParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetPowerParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfParent(
                this,
                predicate,
                SpreadsheetPowerParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetPowerParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                      final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetPowerParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetPowerParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                 final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetPowerParserToken.class
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
        return other instanceof SpreadsheetPowerParserToken;
    }
}
