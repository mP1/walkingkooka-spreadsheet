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

package walkingkooka.spreadsheet.formula;

import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.parser.BinaryOperatorTransformer;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

final class SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformerBinaryOperatorTransformer implements BinaryOperatorTransformer {

    /**
     * Singleton
     */
    final static SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformerBinaryOperatorTransformer INSTANCE = new SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformerBinaryOperatorTransformer();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformerBinaryOperatorTransformer() {
        super();
    }

    @Override
    public int highestPriority() {
        return SpreadsheetFormulaParserToken.HIGHEST_PRIORITY;
    }

    @Override
    public int lowestPriority() {
        return SpreadsheetFormulaParserToken.LOWEST_PRIORITY;
    }

    @Override
    public int priority(final ParserToken token) {
        return token.cast(SpreadsheetFormulaParserToken.class)
            .operatorPriority();
    }

    @Override
    public ParserToken binaryOperand(final List<ParserToken> tokens,
                                     final String text,
                                     final ParserToken parent) {
        return parent.cast(SpreadsheetFormulaParserToken.class)
            .binaryOperand(tokens, text);
    }

    @Override
    public String toString() {
        return SpreadsheetFormulaParsersEbnfParserCombinatorGrammarTransformerBinaryOperatorTransformer.class.getSimpleName();
    }
}
