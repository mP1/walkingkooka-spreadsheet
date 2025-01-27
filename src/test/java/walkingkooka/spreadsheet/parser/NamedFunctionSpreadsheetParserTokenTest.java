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
import walkingkooka.spreadsheet.SpreadsheetExpressionFunctionNames;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class NamedFunctionSpreadsheetParserTokenTest extends FunctionSpreadsheetParserTokenTestCase<NamedFunctionSpreadsheetParserToken> {

    private final static String FUNCTION = "sum";

    @Test
    public void testWithMissingFunctionNameFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWith() {
        final String text = FUNCTION + "(" + NUMBER1 + ")";
        final FunctionNameSpreadsheetParserToken name = this.functionNameParserToken();
        final NamedFunctionSpreadsheetParserToken token = this.createToken(
                text,
                name,
                this.functionParameters(
                        this.number1()
                )
        );
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = FUNCTION + "(" + NUMBER1 + ")";

        final FunctionNameSpreadsheetParserToken name = this.functionNameParserToken();
        final FunctionParametersSpreadsheetParserToken parameters = this.functionParameters(
                this.openParenthesisSymbol(),
                this.number1(),
                this.closeParenthesisSymbol()
        );

        final NamedFunctionSpreadsheetParserToken token = this.createToken(
                text,
                name,
                parameters
        );
        this.textAndCheck(token, text);
        this.checkValue(token, name, parameters);
        this.checkFunctionName(token, this.functionName());
        this.checkParameters(token, parameters);
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final NamedFunctionSpreadsheetParserToken namedFunction = this.createToken();
        final SpreadsheetParserToken functionName = namedFunction.value()
                .get(0)
                .cast(SpreadsheetParserToken.class);
        final SpreadsheetParserToken parameters = namedFunction.value()
                .get(1)
                .cast(SpreadsheetParserToken.class);

        new FakeSpreadsheetParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetParserToken n) {
                b.append("1");
                visited.add(n);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetParserToken n) {
                b.append("2");
                visited.add(n);
            }

            @Override
            protected Visiting startVisit(final NamedFunctionSpreadsheetParserToken t) {
                assertSame(namedFunction, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final NamedFunctionSpreadsheetParserToken t) {
                assertSame(namedFunction, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final FunctionParametersSpreadsheetParserToken t) {
                b.append("5");
                visited.add(t);
                return Visiting.SKIP;
            }

            @Override
            protected void visit(final FunctionNameSpreadsheetParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void endVisit(final FunctionParametersSpreadsheetParserToken t) {
                b.append("7");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("8");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("9");
                visited.add(t);
            }
        }.accept(namedFunction);

        this.checkEquals(
                "81381629815729429",
                b.toString()
        );
        this.checkEquals(
                Lists.of(
                        namedFunction, namedFunction, namedFunction,
                        functionName, functionName, functionName, functionName, functionName,
                        parameters, parameters, parameters, parameters, parameters, parameters,
                        namedFunction, namedFunction, namedFunction
                ),
                visited,
                "visited"
        );
    }

    @Test
    public void testToExpression() {
        this.toExpressionAndCheck(
                Expression.call(
                        Expression.namedFunction(
                                ExpressionFunctionName.with(FUNCTION)
                                        .setCaseSensitivity(SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY)
                        ),
                        Lists.of(
                                Expression.value(
                                        this.expressionNumber(NUMBER1)
                                )
                        )
                )
        );
    }

    private void checkFunctionName(final NamedFunctionSpreadsheetParserToken function,
                                   final SpreadsheetFunctionName name) {
        this.checkEquals(
                name,
                function.functionName(),
                "functionName"
        );
    }

    private void checkParameters(final NamedFunctionSpreadsheetParserToken function,
                                 final FunctionParametersSpreadsheetParserToken parameters) {
        this.checkEquals(
                parameters,
                function.parameters(),
                "parameters"
        );
    }

    @Override
    NamedFunctionSpreadsheetParserToken createToken(final String text,
                                                    final List<ParserToken> tokens) {
        return SpreadsheetParserToken.namedFunction(tokens, text);
    }

    @Override
    public String text() {
        return FUNCTION + "(" + NUMBER1 + ")";
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(
                this.functionNameParserToken(),
                this.functionParameters(
                        this.openParenthesisSymbol(),
                        this.number1(),
                        this.closeParenthesisSymbol()
                )
        );
    }

    private FunctionNameSpreadsheetParserToken functionNameParserToken() {
        return functionNameParserToken(FUNCTION);
    }

    private FunctionNameSpreadsheetParserToken functionNameParserToken(final String name) {
        return SpreadsheetParserToken.functionName(
                SpreadsheetFunctionName.with(name),
                name
        );
    }

    private SpreadsheetFunctionName functionName() {
        return this.functionName(FUNCTION);
    }

    private SpreadsheetFunctionName functionName(final String name) {
        return SpreadsheetFunctionName.with(name);
    }

    private FunctionParametersSpreadsheetParserToken functionParameters(final SpreadsheetParserToken... tokens) {
        return SpreadsheetParserToken.functionParameters(
                Lists.of(
                        tokens
                ),
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
    }

    @Override
    public NamedFunctionSpreadsheetParserToken createDifferentToken() {
        return this.createToken(
                "avg()",
                this.functionNameParserToken("avg")
        );
    }

    @Override
    public Class<NamedFunctionSpreadsheetParserToken> type() {
        return NamedFunctionSpreadsheetParserToken.class;
    }

    @Override
    public NamedFunctionSpreadsheetParserToken unmarshall(final JsonNode from,
                                                          final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallNamedFunction(from, context);
    }
}
