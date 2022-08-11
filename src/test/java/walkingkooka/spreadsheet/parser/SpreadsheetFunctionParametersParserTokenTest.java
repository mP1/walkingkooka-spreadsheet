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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetFunctionParametersParserTokenTest extends SpreadsheetParentParserTokenTestCase<SpreadsheetFunctionParametersParserToken> {

    @Test
    public void testWithMissingFunctionNameFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWith() {
        final String text = "(" + NUMBER1 + ")";
        final SpreadsheetFunctionParametersParserToken token = this.createToken(
                text,
                this.number1()
        );
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = "(" + NUMBER1 + ")";

        final SpreadsheetParenthesisOpenSymbolParserToken left = this.openParenthesisSymbol();
        final SpreadsheetParserToken number = this.number1();
        final SpreadsheetParenthesisCloseSymbolParserToken right = this.closeParenthesisSymbol();

        final SpreadsheetFunctionParametersParserToken token = this.createToken(text, left, number, right);
        this.textAndCheck(token, text);
        this.checkValue(token, left, number, right);
        this.checkParameters(token, number);
    }

    @Test
    public void testWithSymbols2() {
        final String text = "( " + NUMBER1 + " )";

        final SpreadsheetParenthesisOpenSymbolParserToken left = this.openParenthesisSymbol();
        final SpreadsheetWhitespaceParserToken whitespace1 = this.whitespace();
        final SpreadsheetParserToken number = this.number1();
        final SpreadsheetWhitespaceParserToken whitespace2 = this.whitespace();
        final SpreadsheetParenthesisCloseSymbolParserToken right = this.closeParenthesisSymbol();

        final SpreadsheetFunctionParametersParserToken token = this.createToken(
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

    private void checkParameters(final SpreadsheetFunctionParametersParserToken function,
                                 final SpreadsheetParserToken... parameters) {
        this.checkEquals(
                Lists.of(parameters),
                function.parameters(),
                "parameters"
        );
    }

    @Override
    SpreadsheetFunctionParametersParserToken createToken(final String text,
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
    public SpreadsheetFunctionParametersParserToken createDifferentToken() {
        return SpreadsheetFunctionParametersParserToken.with(
                Lists.of(
                        this.openParenthesisSymbol(),
                        this.number("99", "99"),
                        this.closeParenthesisSymbol()
                ),
                "(99)"
        );
    }

    @Override
    public Class<SpreadsheetFunctionParametersParserToken> type() {
        return SpreadsheetFunctionParametersParserToken.class;
    }

    @Override
    public SpreadsheetFunctionParametersParserToken unmarshall(final JsonNode from,
                                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallFunctionParameters(from, context);
    }
}
