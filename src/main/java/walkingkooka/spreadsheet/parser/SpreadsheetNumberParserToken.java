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
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Predicate;

/**
 * Holds a {@link walkingkooka.tree.expression.ExpressionNumber} value along with the components and original text.
 */
public final class SpreadsheetNumberParserToken extends SpreadsheetParentParserToken {

    static SpreadsheetNumberParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetNumberParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetNumberParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    /**
     * Creates a {@link ExpressionNumber} from the components in this {@link SpreadsheetNumberParserToken}.
     */
    public ExpressionNumber toNumber(final ExpressionNumberContext context) {
        return SpreadsheetParserTokenVisitorExpressionNumber.toExpressionNumber(
                this,
                context
        );
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetNumberParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetNumberParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public SpreadsheetNumberParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveFirstIf(
                this,
                predicate,
                SpreadsheetNumberParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetNumberParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetNumberParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetNumberParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                       final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetNumberParserToken.class
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
        return other instanceof SpreadsheetNumberParserToken;
    }
}
