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
import walkingkooka.spreadsheet.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetFunctionParserTokenTest extends SpreadsheetParentParserTokenTestCase<SpreadsheetFunctionParserToken> {

    private final static String FUNCTION = "sum";

    @Test
    public void testWithMissingFunctionNameFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWith() {
        final String text = FUNCTION + "(" + NUMBER1 + ")";
        final SpreadsheetFunctionNameParserToken name = this.function();
        final SpreadsheetFunctionParserToken token = this.createToken(text, name, this.number1());
        this.checkText(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = FUNCTION + "(" + NUMBER1 + ")";

        final SpreadsheetFunctionNameParserToken name = this.function();
        final SpreadsheetParenthesisOpenSymbolParserToken left = this.openParenthesisSymbol();
        final SpreadsheetParserToken number = this.number1();
        final SpreadsheetParenthesisCloseSymbolParserToken right = this.closeParenthesisSymbol();

        final SpreadsheetFunctionParserToken token = this.createToken(text, name, left, number, right);
        this.checkText(token, text);
        this.checkValue(token, name, left, number, right);
        this.checkFunction(token, this.functionName());
        this.checkParameters(token, number);
    }

    @Test
    public void testWithSymbols2() {
        final String text = FUNCTION + "( " + NUMBER1 + " )";

        final SpreadsheetFunctionNameParserToken name = this.function();
        final SpreadsheetParenthesisOpenSymbolParserToken left = this.openParenthesisSymbol();
        final SpreadsheetWhitespaceParserToken whitespace1 = this.whitespace();
        final SpreadsheetParserToken number = this.number1();
        final SpreadsheetWhitespaceParserToken whitespace2 = this.whitespace();
        final SpreadsheetParenthesisCloseSymbolParserToken right = this.closeParenthesisSymbol();

        final SpreadsheetFunctionParserToken token = this.createToken(text, name, left, whitespace1, number, whitespace2, right);
        this.checkText(token, text);
        this.checkValue(token, name, left, whitespace1, number, whitespace2, right);
        this.checkFunction(token, this.functionName());
        this.checkParameters(token, number);
    }

    @Test
    public void testToExpressionNode() {
        this.toExpressionNodeAndCheck(ExpressionNode.function(
                ExpressionNodeName.with(FUNCTION),
                Lists.of(ExpressionNode.bigInteger(new BigInteger(NUMBER1, 10)))));
    }

    private void checkFunction(final SpreadsheetFunctionParserToken function, final SpreadsheetFunctionName name) {
        assertEquals(name, function.functionName(), "functionName");
    }

    private void checkParameters(final SpreadsheetFunctionParserToken function, final SpreadsheetParserToken... parameters) {
        assertEquals(Lists.of(parameters), function.parameters(), "parameters");
    }

    @Override
    SpreadsheetFunctionParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.function(tokens, text);
    }

    @Override
    public String text() {
        return FUNCTION + "(" + NUMBER1 + ")";
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.function(), this.openParenthesisSymbol(), this.number1(), this.closeParenthesisSymbol());
    }

    private SpreadsheetFunctionNameParserToken function() {
        return function(FUNCTION);
    }

    private SpreadsheetFunctionNameParserToken function(final String name) {
        return SpreadsheetParserToken.functionName(this.functionName(name), name);
    }

    private SpreadsheetFunctionName functionName() {
        return this.functionName(FUNCTION);
    }

    private SpreadsheetFunctionName functionName(final String name) {
        return SpreadsheetFunctionName.with(name);
    }

    @Override
    public SpreadsheetFunctionParserToken createDifferentToken() {
        return this.createToken("avg()", this.function("avg"));
    }

    @Override
    public Class<SpreadsheetFunctionParserToken> type() {
        return SpreadsheetFunctionParserToken.class;
    }
}
