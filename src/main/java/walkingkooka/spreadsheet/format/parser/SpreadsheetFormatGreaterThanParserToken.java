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

import walkingkooka.compare.ComparisonRelation;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a greater than test with its parameters.
 */
public final class SpreadsheetFormatGreaterThanParserToken extends SpreadsheetFormatConditionParserToken {

    static SpreadsheetFormatGreaterThanParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetFormatGreaterThanParserToken(copyAndCheckTokensFailIfEmpty(value),
                checkTextNotEmptyOrWhitespace(text));
    }

    private SpreadsheetFormatGreaterThanParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    @Override
    public ComparisonRelation relation() {
        return ComparisonRelation.GT;
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetFormatGreaterThanParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetFormatGreaterThanParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public SpreadsheetFormatGreaterThanParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                SpreadsheetFormatGreaterThanParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetFormatGreaterThanParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetFormatGreaterThanParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatGreaterThanParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                  final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetFormatGreaterThanParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatGreaterThanParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                             final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetFormatGreaterThanParserToken.class
        );
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatGreaterThanParserToken;
    }

}
