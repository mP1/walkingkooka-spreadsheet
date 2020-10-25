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
import walkingkooka.tree.expression.Expression;
import walkingkooka.visit.Visiting;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetNegativeParserTokenTest extends SpreadsheetUnaryParserTokenTestCase<SpreadsheetNegativeParserToken> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetUnaryParserToken unary = this.createToken();
        final SpreadsheetParserToken symbol = this.minusSymbol();
        final SpreadsheetParserToken parameter = unary.parameter();

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
            protected Visiting startVisit(final SpreadsheetNegativeParserToken t) {
                assertSame(unary, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetNegativeParserToken t) {
                assertSame(unary, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetExpressionNumberParserToken t) {
                b.append("5");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetMinusSymbolParserToken t) {
                b.append("6");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("7");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("8");
                visited.add(t);
            }
        }.accept(unary);
        assertEquals("7137162871528428", b.toString());
        assertEquals(Lists.of(unary, unary, unary,
                symbol, symbol, symbol, symbol, symbol,
                parameter, parameter, parameter, parameter, parameter,
                unary, unary, unary),
                visited,
                "visited");
    }

    @Test
    public final void testToExpression() {
        this.toExpressionAndCheck(Expression.negative(this.expressionNumberExpression()));
    }

    @Override
    SpreadsheetNegativeParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.negative(tokens, text);
    }

    @Override
    public String text() {
        return "-" + NUMBER1;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.minusSymbol(), this.number1());
    }

    @Override
    public SpreadsheetNegativeParserToken createDifferentToken() {
        return this.createToken("-" + NUMBER2, this.minusSymbol(), this.number2());
    }

    @Override
    public Class<SpreadsheetNegativeParserToken> type() {
        return SpreadsheetNegativeParserToken.class;
    }
}
