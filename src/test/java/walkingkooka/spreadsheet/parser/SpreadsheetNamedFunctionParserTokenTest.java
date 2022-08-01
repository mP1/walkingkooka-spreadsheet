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
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetNamedFunctionParserTokenTest extends SpreadsheetParentParserTokenTestCase<SpreadsheetNamedFunctionParserToken> {

    private final static String FUNCTION = "sum";

    @Test
    public void testWithMissingFunctionNameFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWith() {
        final String text = FUNCTION + "(" + NUMBER1 + ")";
        final SpreadsheetFunctionNameParserToken name = this.function();
        final SpreadsheetNamedFunctionParserToken token = this.createToken(text, name, this.number1());
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = FUNCTION + "(" + NUMBER1 + ")";

        final SpreadsheetFunctionNameParserToken name = this.function();
        final SpreadsheetParenthesisOpenSymbolParserToken left = this.openParenthesisSymbol();
        final SpreadsheetParserToken number = this.number1();
        final SpreadsheetParenthesisCloseSymbolParserToken right = this.closeParenthesisSymbol();

        final SpreadsheetNamedFunctionParserToken token = this.createToken(text, name, left, number, right);
        this.textAndCheck(token, text);
        this.checkValue(token, name, left, number, right);
        this.checkNamedFunction(token, this.functionName());
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

        final SpreadsheetNamedFunctionParserToken token = this.createToken(text, name, left, whitespace1, number, whitespace2, right);
        this.textAndCheck(token, text);
        this.checkValue(token, name, left, whitespace1, number, whitespace2, right);
        this.checkNamedFunction(token, this.functionName());
        this.checkParameters(token, number);
    }

    @Test
    public void testToExpression() {
        this.toExpressionAndCheck(
                Expression.call(
                        Expression.namedFunction(
                                FunctionExpressionName.with(FUNCTION)
                        ),
                        Lists.of(
                                Expression.value(
                                        this.expressionNumber(NUMBER1)
                                )
                        )
                )
        );
    }

    private void checkNamedFunction(final SpreadsheetNamedFunctionParserToken function,
                                    final SpreadsheetFunctionName name) {
        this.checkEquals(
                name,
                function.functionName(),
                "functionName"
        );
    }

    private void checkParameters(final SpreadsheetNamedFunctionParserToken function, final SpreadsheetParserToken... parameters) {
        this.checkEquals(Lists.of(parameters), function.parameters(), "parameters");
    }

    @Override
    SpreadsheetNamedFunctionParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.namedFunction(tokens, text);
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
    public SpreadsheetNamedFunctionParserToken createDifferentToken() {
        return this.createToken("avg()", this.function("avg"));
    }

    @Override
    public Class<SpreadsheetNamedFunctionParserToken> type() {
        return SpreadsheetNamedFunctionParserToken.class;
    }

    @Override
    public SpreadsheetNamedFunctionParserToken unmarshall(final JsonNode from,
                                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallNamedFunction(from, context);
    }
}
