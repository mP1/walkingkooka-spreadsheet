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
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class GroupSpreadsheetParserTokenTest extends ParentSpreadsheetParserTokenTestCase<GroupSpreadsheetParserToken> {

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
        final GroupSpreadsheetParserToken token = this.createToken(text, this.number1());
        this.textAndCheck(token, text);
    }

    @Test
    public void testWithSymbols() {
        final String text = "(" + NUMBER1 + ")";

        final ParenthesisOpenSymbolSpreadsheetParserToken left = openParenthesisSymbol();
        final SpreadsheetParserToken number = this.number1();
        final ParenthesisCloseSymbolSpreadsheetParserToken right = closeParenthesisSymbol();

        final GroupSpreadsheetParserToken token = this.createToken(text, left, number, right);
        this.textAndCheck(token, text);
        this.checkValue(token, left, number, right);
    }

    @Test
    public void testWithSymbols2() {
        final String text = "( " + NUMBER1 + " )";

        final ParenthesisOpenSymbolSpreadsheetParserToken left = this.openParenthesisSymbol();
        final WhitespaceSpreadsheetParserToken whitespace1 = this.whitespace();
        final SpreadsheetParserToken number = this.number1();
        final WhitespaceSpreadsheetParserToken whitespace2 = this.whitespace();
        final ParenthesisCloseSymbolSpreadsheetParserToken right = this.closeParenthesisSymbol();

        final GroupSpreadsheetParserToken token = this.createToken(text, left, whitespace1, number, whitespace2, right);
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

    @Override
    GroupSpreadsheetParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.group(tokens, text);
    }

    @Override
    public String text() {
        return "(" + NUMBER1 + ")";
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.openParenthesisSymbol(), this.number1(), this.closeParenthesisSymbol());
    }

    @Override
    public GroupSpreadsheetParserToken createDifferentToken() {
        return this.createToken("" + NUMBER2, this.number2());
    }

    @Override
    public Class<GroupSpreadsheetParserToken> type() {
        return GroupSpreadsheetParserToken.class;
    }

    @Override
    public GroupSpreadsheetParserToken unmarshall(final JsonNode from,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallGroup(from, context);
    }
}
