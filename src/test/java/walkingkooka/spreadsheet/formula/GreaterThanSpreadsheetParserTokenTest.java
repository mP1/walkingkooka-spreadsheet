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
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class GreaterThanSpreadsheetParserTokenTest extends ConditionSpreadsheetParserTokenTestCase<GreaterThanSpreadsheetParserToken> {

    // toConditionRightSpreadsheetParserToken...........................................................................

    @Test
    public void testToSpreadsheetConditionRightParserToken() {
        final SpreadsheetParserToken left = this.number1();
        final SpreadsheetParserToken symbol = this.operatorSymbol();
        final SpreadsheetParserToken right = this.number2();

        this.toConditionRightSpreadsheetParserTokenAndCheck(
                SpreadsheetParserToken.greaterThan(
                        Lists.of(
                                left,
                                symbol,
                                right
                        ),
                        "1>22"
                ),
                SpreadsheetParserToken.conditionRightGreaterThan(
                        Lists.of(
                                symbol,
                                right
                        ),
                        ">22"
                )
        );
    }

    @Test
    public void testToSpreadsheetConditionRightParserTokenIncludesWhitespace() {
        final SpreadsheetParserToken left = this.number1();
        final SpreadsheetParserToken whitespace1 = this.whitespace();
        final SpreadsheetParserToken symbol = this.operatorSymbol();
        final SpreadsheetParserToken whitespace2 = this.whitespace();
        final SpreadsheetParserToken right = this.number2();

        this.toConditionRightSpreadsheetParserTokenAndCheck(
                SpreadsheetParserToken.greaterThan(
                        Lists.of(
                                left,
                                whitespace1,
                                symbol,
                                whitespace2,
                                right
                        ),
                        "1   >   22"
                ),
                SpreadsheetParserToken.conditionRightGreaterThan(
                        Lists.of(
                                symbol,
                                whitespace2,
                                right
                        ),
                        ">   22"
                )
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final BinarySpreadsheetParserToken binary = this.createToken();
        final SpreadsheetParserToken left = binary.left();
        final SpreadsheetParserToken right = binary.right();
        final SpreadsheetParserToken symbol = operatorSymbol();

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
            protected Visiting startVisit(final GreaterThanSpreadsheetParserToken t) {
                assertSame(binary, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final GreaterThanSpreadsheetParserToken t) {
                assertSame(binary, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final NumberSpreadsheetParserToken t) {
                b.append("5");
                visited.add(t);
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final NumberSpreadsheetParserToken t) {
                b.append("6");
                visited.add(t);
            }

            @Override
            protected void visit(final GreaterThanSymbolSpreadsheetParserToken t) {
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
    GreaterThanSpreadsheetParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.greaterThan(tokens, text);
    }

    @Override
    SpreadsheetParserToken operatorSymbol() {
        return SpreadsheetParserToken.greaterThanSymbol(">", ">");
    }

    @Override
    Expression expressionNode(final Expression left, final Expression right) {
        return Expression.greaterThan(left, right);
    }

    @Override
    public Class<GreaterThanSpreadsheetParserToken> type() {
        return GreaterThanSpreadsheetParserToken.class;
    }

    @Override
    public GreaterThanSpreadsheetParserToken unmarshall(final JsonNode from,
                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallGreaterThan(from, context);
    }
}
