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
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class AdditionSpreadsheetFormulaParserTokenTest extends ArithmeticSpreadsheetFormulaParserTokenTestCase<AdditionSpreadsheetFormulaParserToken> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final BinarySpreadsheetFormulaParserToken binary = this.createToken();
        final SpreadsheetFormulaParserToken left = binary.left();
        final SpreadsheetFormulaParserToken right = binary.right();
        final SpreadsheetFormulaParserToken symbol = operatorSymbol();

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
            protected Visiting startVisit(final AdditionSpreadsheetFormulaParserToken t) {
                assertSame(binary, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final AdditionSpreadsheetFormulaParserToken t) {
                assertSame(binary, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final NumberSpreadsheetFormulaParserToken t) {
                b.append("5");
                visited.add(t);
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final NumberSpreadsheetFormulaParserToken t) {
                b.append("6");
                visited.add(t);
            }

            @Override
            protected void visit(final PlusSymbolSpreadsheetFormulaParserToken t) {
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
                b.append("8");
                visited.add(t);
            }
        }.accept(binary);
        this.checkEquals("81381562881728815628428", b.toString());
        this.checkEquals(Lists.of(binary, binary, binary,
                left, left, left, left, left, left,
                symbol, symbol, symbol, symbol, symbol,
                right, right, right, right, right, right,
                binary, binary, binary),
            visited,
            "visited");
    }

    @Override
    AdditionSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.addition(tokens, text);
    }

    @Override
    SpreadsheetFormulaParserToken operatorSymbol() {
        return SpreadsheetFormulaParserToken.plusSymbol("+", "+");
    }

    @Override
    Expression expressionNode(final Expression left, final Expression right) {
        return Expression.add(left, right);
    }

    @Override
    public Class<AdditionSpreadsheetFormulaParserToken> type() {
        return AdditionSpreadsheetFormulaParserToken.class;
    }

    @Override
    public AdditionSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallAddition(from, context);
    }
}
