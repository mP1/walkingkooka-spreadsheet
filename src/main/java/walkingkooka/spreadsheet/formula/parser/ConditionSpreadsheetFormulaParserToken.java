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

import java.util.List;

/**
 * Base class for any binary condition expression.
 */
abstract public class ConditionSpreadsheetFormulaParserToken extends BinarySpreadsheetFormulaParserToken {

    ConditionSpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    /**
     * Returns a subclass of {@link ConditionRightSpreadsheetFormulaParserToken} from the RHS argument of this condition.
     */
    public final ConditionRightSpreadsheetFormulaParserToken toConditionRightSpreadsheetFormulaParserToken() {
        // find symbol, return  the symbol and tokens following it to the factory method.
        final List<ParserToken> tokens = this.value();

        int symbolIndex = -1;
        int i = 0;
        for (final ParserToken token : tokens) {
            if (false == token.isWhitespace() && token.isSymbol()) {
                symbolIndex = i;
                break;
            }

            i++;
        }

        if (-1 == symbolIndex) {
            throw new IllegalArgumentException("Missing symbol");
        }

        final List<ParserToken> conditionRightTokens = tokens.subList(
            symbolIndex,
            tokens.size()
        );

        return this.toConditionRightSpreadsheetFormulaParserToken0(
            conditionRightTokens,
            ParserToken.text(conditionRightTokens)
        );
    }

    abstract ConditionRightSpreadsheetFormulaParserToken toConditionRightSpreadsheetFormulaParserToken0(final List<ParserToken> tokens,
                                                                                                        final String text);
}
