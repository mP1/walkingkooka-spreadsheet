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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class ExpressionSpreadsheetFormulaParserTokenTest extends ParentSpreadsheetFormulaParserTokenTestCase<ExpressionSpreadsheetFormulaParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWithTwoTokensFails() {
        this.createToken("" + NUMBER1 + NUMBER2, this.number1(), this.number2());
    }

    @Test
    public void testWith() {
        final String text = "(" + NUMBER1 + ")";
        final ExpressionSpreadsheetFormulaParserToken token = this.createToken(text, this.number1());
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = "(" + NUMBER1 + ")";

        final ParenthesisOpenSymbolSpreadsheetFormulaParserToken left = openParenthesisSymbol();
        final SpreadsheetFormulaParserToken number = this.number1();
        final ParenthesisCloseSymbolSpreadsheetFormulaParserToken right = closeParenthesisSymbol();

        final ExpressionSpreadsheetFormulaParserToken token = this.createToken(text, left, number, right);
        this.textAndCheck(token, text);
        this.checkValue(token, left, number, right);
    }

    @Test
    public void testWithSymbols2() {
        final String text = "( " + NUMBER1 + " )";

        final ParenthesisOpenSymbolSpreadsheetFormulaParserToken left = this.openParenthesisSymbol();
        final WhitespaceSpreadsheetFormulaParserToken whitespace1 = this.whitespace();
        final SpreadsheetFormulaParserToken number = this.number1();
        final WhitespaceSpreadsheetFormulaParserToken whitespace2 = this.whitespace();
        final ParenthesisCloseSymbolSpreadsheetFormulaParserToken right = this.closeParenthesisSymbol();

        final ExpressionSpreadsheetFormulaParserToken token = this.createToken(text, left, whitespace1, number, whitespace2, right);
        this.textAndCheck(token, text);
        this.checkValue(token, left, whitespace1, number, whitespace2, right);
    }

    @Test
    public void testToExpression() {
        this.toExpressionAndCheck(
            Expression.value(
                this.expressionNumber(NUMBER1)
            )
        );
    }

    @Test
    public void testToExpressionAddition() {
        this.toExpressionAndCheck(
            ExpressionSpreadsheetFormulaParserToken.with(
                Lists.of(
                    equalsSymbol(),
                    SpreadsheetFormulaParserToken.addition(
                        Lists.of(
                            number1(),
                            SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                            number2()
                        ),
                        "1+2"
                    )
                ),
                "=1+2"
            ),
            Expression.add(
                Expression.value(
                    this.expressionNumber(NUMBER1)
                ),
                Expression.value(
                    this.expressionNumber(NUMBER2)
                )
            )
        );
    }

    @Test
    public void testToExpressionWhitespaceAddition() {
        this.toExpressionAndCheck(
            ExpressionSpreadsheetFormulaParserToken.with(
                Lists.of(
                    equalsSymbol(),
                    whitespace(),
                    SpreadsheetFormulaParserToken.addition(
                        Lists.of(
                            number1(),
                            SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                            number2()
                        ),
                        "1+2"
                    )
                ),
                "= 1+2"
            ),
            Expression.add(
                Expression.value(
                    this.expressionNumber(NUMBER1)
                ),
                Expression.value(
                    this.expressionNumber(NUMBER2)
                )
            )
        );
    }

    @Override
    ExpressionSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.expression(tokens, text);
    }

    @Override
    public String text() {
        return "=" + NUMBER1;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.equalsSymbol(), this.number1());
    }

    @Override
    public ExpressionSpreadsheetFormulaParserToken createDifferentToken() {
        return this.createToken("=" + NUMBER2, this.equalsSymbol(), this.number2());
    }

    @Override
    public Class<ExpressionSpreadsheetFormulaParserToken> type() {
        return ExpressionSpreadsheetFormulaParserToken.class;
    }

    @Override
    public ExpressionSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallExpression(from, context);
    }
}
