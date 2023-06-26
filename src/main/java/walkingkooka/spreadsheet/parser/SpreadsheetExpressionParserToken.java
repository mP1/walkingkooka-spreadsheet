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
 * A top level parent that holds an equals sign and the following expression.
 */
public final class SpreadsheetExpressionParserToken extends SpreadsheetParentParserToken {

    static SpreadsheetExpressionParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetExpressionParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetExpressionParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetExpressionParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetExpressionParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public SpreadsheetExpressionParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveFirstIf(
                this,
                predicate,
                SpreadsheetExpressionParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetExpressionParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetExpressionParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetExpressionParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                           final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetExpressionParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetExpressionParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                      final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetExpressionParserToken.class
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
        return other instanceof SpreadsheetExpressionParserToken;
    }
}
