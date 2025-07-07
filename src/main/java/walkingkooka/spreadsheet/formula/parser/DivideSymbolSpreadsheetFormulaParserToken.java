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
 * Represents a divide symbol token.
 */
public final class DivideSymbolSpreadsheetFormulaParserToken extends SymbolSpreadsheetFormulaParserToken {

    static DivideSymbolSpreadsheetFormulaParserToken with(final String value,
                                                          final String text) {
        return new DivideSymbolSpreadsheetFormulaParserToken(
            checkValue(value),
            checkText(text)
        );
    }

    private DivideSymbolSpreadsheetFormulaParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    public int operatorPriority() {
        return MULTIPLY_DIVISION_PRIORITY;
    }

    @Override
    public SpreadsheetFormulaParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        return division(tokens, text);
    }

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetFormulaParserTokenVisitor visitor) {
        visitor.visit(this);
    }
}
