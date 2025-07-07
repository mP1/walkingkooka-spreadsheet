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
 * The parameters including the open/close parenthesis for a function.
 * <br>
 * (A10:A20)
 */
public final class FunctionParametersSpreadsheetFormulaParserToken extends ParentSpreadsheetFormulaParserToken {

    static FunctionParametersSpreadsheetFormulaParserToken with(final List<ParserToken> value,
                                                                final String text) {
        return new FunctionParametersSpreadsheetFormulaParserToken(
            copyAndCheckTokens(value),
            checkText(text)
        );
    }

    private FunctionParametersSpreadsheetFormulaParserToken(final List<ParserToken> value,
                                                            final String text) {
        super(value, text);

        this.parameters = ParserToken.filterWithoutNoise(value);
    }

    public List<ParserToken> parameters() {
        return this.parameters;
    }

    private final List<ParserToken> parameters;

    // children.........................................................................................................

    @Override
    public FunctionParametersSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            FunctionParametersSpreadsheetFormulaParserToken::with
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
