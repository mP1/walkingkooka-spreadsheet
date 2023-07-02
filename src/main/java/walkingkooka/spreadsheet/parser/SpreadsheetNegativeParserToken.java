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
 * A wrapper around a numeric type that is also a percentage.
 */
public final class SpreadsheetNegativeParserToken extends SpreadsheetParentParserToken {

    static SpreadsheetNegativeParserToken with(final List<ParserToken> value,
                                               final String text) {
        return new SpreadsheetNegativeParserToken(
                copyAndCheckTokens(value),
                checkText(text)
        );
    }

    private SpreadsheetNegativeParserToken(final List<ParserToken> value,
                                           final String text) {
        super(value, text);

        final List<ParserToken> without = ParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (1 != count) {
            throw new IllegalArgumentException("Expected 1 token but got " + count + "=" + without);
        }
        this.parameter = without.get(0)
                .cast(SpreadsheetParserToken.class);
    }

    public SpreadsheetParserToken parameter() {
        return this.parameter;
    }

    private final SpreadsheetParserToken parameter;

    // children.........................................................................................................

    @Override
    public SpreadsheetNegativeParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetNegativeParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public SpreadsheetNegativeParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                SpreadsheetNegativeParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetNegativeParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetNegativeParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetNegativeParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                         final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetNegativeParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetNegativeParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                    final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetNegativeParserToken.class
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
        return other instanceof SpreadsheetNegativeParserToken;
    }
}
