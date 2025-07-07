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
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * Represents a greater than equals test with its parameters.
 */
public final class GreaterThanEqualsSpreadsheetFormulaParserToken extends ConditionSpreadsheetFormulaParserToken {

    static GreaterThanEqualsSpreadsheetFormulaParserToken with(final List<ParserToken> value, final String text) {
        return new GreaterThanEqualsSpreadsheetFormulaParserToken(copyAndCheckTokens(value),
            checkText(text));
    }

    private GreaterThanEqualsSpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    @Override
    ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken toConditionRightSpreadsheetFormulaParserToken0(final List<ParserToken> tokens,
                                                                                                                final String text) {

        return SpreadsheetFormulaParserToken.conditionRightGreaterThanEquals(
            tokens,
            text
        );
    }

    // children.........................................................................................................

    @Override
    public GreaterThanEqualsSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            GreaterThanEqualsSpreadsheetFormulaParserToken::with
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
