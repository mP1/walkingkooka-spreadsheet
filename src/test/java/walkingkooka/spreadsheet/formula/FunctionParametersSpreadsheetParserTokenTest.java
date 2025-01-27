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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class FunctionParametersSpreadsheetParserTokenTest extends ParentSpreadsheetParserTokenTestCase<FunctionParametersSpreadsheetParserToken> {

    @Test
    public void testWithMissingFunctionNameFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWith() {
        final String text = "(" + NUMBER1 + ")";
        final FunctionParametersSpreadsheetParserToken token = this.createToken(
                text,
                this.number1()
        );
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = "(" + NUMBER1 + ")";

        final ParenthesisOpenSymbolSpreadsheetParserToken left = this.openParenthesisSymbol();
        final SpreadsheetParserToken number = this.number1();
        final ParenthesisCloseSymbolSpreadsheetParserToken right = this.closeParenthesisSymbol();

        final FunctionParametersSpreadsheetParserToken token = this.createToken(text, left, number, right);
        this.textAndCheck(token, text);
        this.checkValue(token, left, number, right);
        this.checkParameters(token, number);
    }

    @Test
    public void testWithSymbols2() {
        final String text = "( " + NUMBER1 + " )";

        final ParenthesisOpenSymbolSpreadsheetParserToken left = this.openParenthesisSymbol();
        final WhitespaceSpreadsheetParserToken whitespace1 = this.whitespace();
        final SpreadsheetParserToken number = this.number1();
        final WhitespaceSpreadsheetParserToken whitespace2 = this.whitespace();
        final ParenthesisCloseSymbolSpreadsheetParserToken right = this.closeParenthesisSymbol();

        final FunctionParametersSpreadsheetParserToken token = this.createToken(
                text,
                left,
                whitespace1,
                number,
                whitespace2,
                right
        );
        this.textAndCheck(token, text);
        this.checkValue(token, left, whitespace1, number, whitespace2, right);
        this.checkParameters(token, number);
    }

    private void checkParameters(final FunctionParametersSpreadsheetParserToken function,
                                 final SpreadsheetParserToken... parameters) {
        this.checkEquals(
                Lists.of(parameters),
                function.parameters(),
                "parameters"
        );
    }

    @Override
    FunctionParametersSpreadsheetParserToken createToken(final String text,
                                                         final List<ParserToken> tokens) {
        return SpreadsheetParserToken.functionParameters(tokens, text);
    }

    @Override
    public String text() {
        return "(" + NUMBER1 + ")";
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(
                this.openParenthesisSymbol(),
                this.number1(),
                this.closeParenthesisSymbol()
        );
    }

    @Override
    public FunctionParametersSpreadsheetParserToken createDifferentToken() {
        return FunctionParametersSpreadsheetParserToken.with(
                Lists.of(
                        this.openParenthesisSymbol(),
                        this.number("99", "99"),
                        this.closeParenthesisSymbol()
                ),
                "(99)"
        );
    }

    @Override
    public Class<FunctionParametersSpreadsheetParserToken> type() {
        return FunctionParametersSpreadsheetParserToken.class;
    }

    @Override
    public FunctionParametersSpreadsheetParserToken unmarshall(final JsonNode from,
                                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallFunctionParameters(from, context);
    }
}
