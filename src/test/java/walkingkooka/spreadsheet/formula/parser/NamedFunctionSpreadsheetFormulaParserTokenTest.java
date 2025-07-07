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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class NamedFunctionSpreadsheetFormulaParserTokenTest extends FunctionSpreadsheetFormulaParserTokenTestCase<NamedFunctionSpreadsheetFormulaParserToken> {

    private final static String FUNCTION = "sum";

    @Test
    public void testWithMissingFunctionNameFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWith() {
        final String text = FUNCTION + "(" + ParentSpreadsheetFormulaParserTokenTestCase.NUMBER1 + ")";
        final FunctionNameSpreadsheetFormulaParserToken name = this.functionNameParserToken();
        final NamedFunctionSpreadsheetFormulaParserToken token = this.createToken(
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
        final String text = FUNCTION + "(" + ParentSpreadsheetFormulaParserTokenTestCase.NUMBER1 + ")";

        final FunctionNameSpreadsheetFormulaParserToken name = this.functionNameParserToken();
        final FunctionParametersSpreadsheetFormulaParserToken parameters = this.functionParameters(
            this.openParenthesisSymbol(),
            this.number1(),
            this.closeParenthesisSymbol()
        );

        final NamedFunctionSpreadsheetFormulaParserToken token = this.createToken(
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

        final NamedFunctionSpreadsheetFormulaParserToken namedFunction = this.createToken();
        final SpreadsheetFormulaParserToken functionName = namedFunction.value()
            .get(0)
            .cast(SpreadsheetFormulaParserToken.class);
        final SpreadsheetFormulaParserToken parameters = namedFunction.value()
            .get(1)
            .cast(SpreadsheetFormulaParserToken.class);

        new FakeSpreadsheetFormulaParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetFormulaParserToken n) {
                b.append("1");
                visited.add(n);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormulaParserToken n) {
                b.append("2");
                visited.add(n);
            }

            @Override
            protected Visiting startVisit(final NamedFunctionSpreadsheetFormulaParserToken t) {
                assertSame(namedFunction, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final NamedFunctionSpreadsheetFormulaParserToken t) {
                assertSame(namedFunction, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final FunctionParametersSpreadsheetFormulaParserToken t) {
                b.append("5");
                visited.add(t);
                return Visiting.SKIP;
            }

            @Override
            protected void visit(final FunctionNameSpreadsheetFormulaParserToken token) {
                b.append("6");
                visited.add(token);
            }

            @Override
            protected void endVisit(final FunctionParametersSpreadsheetFormulaParserToken t) {
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
                    SpreadsheetExpressionFunctions.name(FUNCTION)
                ),
                Lists.of(
                    Expression.value(
                        this.expressionNumber(ParentSpreadsheetFormulaParserTokenTestCase.NUMBER1)
                    )
                )
            )
        );
    }

    private void checkFunctionName(final NamedFunctionSpreadsheetFormulaParserToken function,
                                   final SpreadsheetFunctionName name) {
        this.checkEquals(
            name,
            function.functionName(),
            "functionName"
        );
    }

    private void checkParameters(final NamedFunctionSpreadsheetFormulaParserToken function,
                                 final FunctionParametersSpreadsheetFormulaParserToken parameters) {
        this.checkEquals(
            parameters,
            function.parameters(),
            "parameters"
        );
    }

    @Override
    NamedFunctionSpreadsheetFormulaParserToken createToken(final String text,
                                                           final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.namedFunction(tokens, text);
    }

    @Override
    public String text() {
        return FUNCTION + "(" + ParentSpreadsheetFormulaParserTokenTestCase.NUMBER1 + ")";
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

    private FunctionNameSpreadsheetFormulaParserToken functionNameParserToken() {
        return functionNameParserToken(FUNCTION);
    }

    private FunctionNameSpreadsheetFormulaParserToken functionNameParserToken(final String name) {
        return SpreadsheetFormulaParserToken.functionName(
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

    private FunctionParametersSpreadsheetFormulaParserToken functionParameters(final SpreadsheetFormulaParserToken... tokens) {
        return SpreadsheetFormulaParserToken.functionParameters(
            Lists.of(
                tokens
            ),
            ParserToken.text(
                Lists.of(tokens)
            )
        );
    }

    @Override
    public NamedFunctionSpreadsheetFormulaParserToken createDifferentToken() {
        return this.createToken(
            "avg()",
            this.functionNameParserToken("avg")
        );
    }

    @Override
    public Class<NamedFunctionSpreadsheetFormulaParserToken> type() {
        return NamedFunctionSpreadsheetFormulaParserToken.class;
    }

    @Override
    public NamedFunctionSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallNamedFunction(from, context);
    }
}
