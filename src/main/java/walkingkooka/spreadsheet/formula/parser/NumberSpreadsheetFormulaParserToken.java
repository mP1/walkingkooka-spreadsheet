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
package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * Holds a {@link walkingkooka.tree.expression.ExpressionNumber} value along with the tokens and original text.
 */
public final class NumberSpreadsheetFormulaParserToken extends ValueSpreadsheetFormulaParserToken {

    static NumberSpreadsheetFormulaParserToken with(final List<ParserToken> value, final String text) {
        return new NumberSpreadsheetFormulaParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private NumberSpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    /**
     * Creates a {@link ExpressionNumber} parse the tokens in this {@link NumberSpreadsheetFormulaParserToken}.
     */
    public ExpressionNumber toNumber(final ExpressionNumberContext context) {
        return NumberSpreadsheetFormulaParserTokenToNumberSpreadsheetFormulaParserTokenVisitor.toExpressionNumber(
            this,
            context
        );
    }

    // children.........................................................................................................

    @Override
    public NumberSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            NumberSpreadsheetFormulaParserToken::with
        );
    }

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetFormulaParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }
}
