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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetLambdaFunctionParserTokenTest extends SpreadsheetFunctionParserTokenTestCase<SpreadsheetLambdaFunctionParserToken> {

    private final static String FUNCTION = "lambda";

    private final static String PARAMETER = "parameter1";

    @Test
    public void testWithMissingFunctionNameFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWith() {
        final String text = FUNCTION + "(" + PARAMETER + ")(" + NUMBER1 + ")";
        final SpreadsheetFunctionNameParserToken name = this.functionNameParserToken();
        final SpreadsheetLambdaFunctionParserToken token = this.createToken(
                text,
                name,
                this.functionParameters(
                        this.label(PARAMETER)
                ),
                this.functionParameters(
                        this.number1()
                )
        );
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = FUNCTION + "(" + PARAMETER + ")(" + NUMBER1 + ")";

        final SpreadsheetFunctionNameParserToken name = this.functionNameParserToken();

        final SpreadsheetFunctionParametersParserToken parameters = this.functionParameters(
                this.openParenthesisSymbol(),
                this.label(PARAMETER),
                this.closeParenthesisSymbol()
        );

        final SpreadsheetFunctionParametersParserToken parameterValues = this.functionParameters(
                this.openParenthesisSymbol(),
                this.number1(),
                this.closeParenthesisSymbol()
        );

        final SpreadsheetLambdaFunctionParserToken token = this.createToken(
                text,
                name,
                parameters,
                parameterValues
        );
        this.textAndCheck(token, text);
        this.checkValue(token, name, parameters, parameterValues);

        this.checkFunctionName(token, this.functionName());
        this.checkParameters(token, parameters);
        this.checkParameterValues(token, parameterValues);
    }

    @Test
    public void testToExpression() {
        this.toExpressionAndCheck(
                Expression.call(
                        Expression.call(
                                Expression.namedFunction(
                                        ExpressionFunctionName.with(FUNCTION)
                                                .setCaseSensitivity(SpreadsheetExpressionFunctionNames.CASE_SENSITIVITY)
                                ),
                                Lists.of(
                                        Expression.reference(
                                                SpreadsheetSelection.labelName(PARAMETER)
                                        )
                                )
                        ),
                        Lists.of(
                                Expression.value(
                                        this.expressionNumber(NUMBER1)
                                )
                        )
                )
        );
    }

    private void checkFunctionName(final SpreadsheetLambdaFunctionParserToken function,
                                   final SpreadsheetFunctionName name) {
        this.checkEquals(
                name,
                function.functionName(),
                "functionName"
        );
    }

    private void checkParameters(final SpreadsheetLambdaFunctionParserToken function,
                                 final SpreadsheetFunctionParametersParserToken parameters) {
        this.checkEquals(
                parameters,
                function.parameters(),
                "parameters"
        );
    }

    private void checkParameterValues(final SpreadsheetLambdaFunctionParserToken function,
                                      final SpreadsheetFunctionParametersParserToken parameterValues) {
        this.checkEquals(
                parameterValues,
                function.parameterValues(),
                "parameterValues"
        );
    }

    @Override
    SpreadsheetLambdaFunctionParserToken createToken(final String text,
                                                     final List<ParserToken> tokens) {
        return SpreadsheetParserToken.lambdaFunction(tokens, text);
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
                        this.label(PARAMETER),
                        this.closeParenthesisSymbol()
                ),
                this.functionParameters(
                        this.openParenthesisSymbol(),
                        this.number1(),
                        this.closeParenthesisSymbol()
                )
        );
    }

    private SpreadsheetFunctionNameParserToken functionNameParserToken() {
        return functionNameParserToken(FUNCTION);
    }

    private SpreadsheetFunctionNameParserToken functionNameParserToken(final String name) {
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

    private SpreadsheetFunctionParametersParserToken functionParameters(final SpreadsheetParserToken... tokens) {
        return SpreadsheetParserToken.functionParameters(
                Lists.of(
                        tokens
                ),
                ParserToken.text(
                        Lists.of(tokens)
                )
        );
    }

    private SpreadsheetLabelNameParserToken label(final String label) {
        return SpreadsheetParserToken.labelName(
                SpreadsheetSelection.labelName(label),
                label
        );
    }

    @Override
    public SpreadsheetLambdaFunctionParserToken createDifferentToken() {
        return this.createToken(
                "lambda()",
                this.functionNameParserToken("lambda"),
                this.functionParameters(
                        this.label("different")
                ),
                this.functionParameters(
                        this.number1()
                )
        );
    }

    @Override
    public Class<SpreadsheetLambdaFunctionParserToken> type() {
        return SpreadsheetLambdaFunctionParserToken.class;
    }

    @Override
    public SpreadsheetLambdaFunctionParserToken unmarshall(final JsonNode from,
                                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallLambdaFunction(from, context);
    }
}
