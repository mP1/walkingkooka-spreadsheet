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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a subtraction operation with its parameters.
 */
public final class SpreadsheetSubtractionParserToken extends SpreadsheetBinaryParserToken {

    static SpreadsheetSubtractionParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetSubtractionParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetSubtractionParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetSubtractionParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetSubtractionParserToken::with
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetSubtractionParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                            final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetSubtractionParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetSubtractionParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                       final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetSubtractionParserToken.class
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
        return other instanceof SpreadsheetSubtractionParserToken;
    }
}
