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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetLongParserTokenTest extends SpreadsheetNumericParserTokenTestCase<SpreadsheetLongParserToken, Long> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetLongParserToken token = this.createToken();

        new FakeSpreadsheetParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                assertSame(token, t);
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetParserToken t) {
                assertSame(token, t);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetParserToken t) {
                assertSame(token, t);
                b.append("4");
            }

            @Override
            protected void visit(final SpreadsheetLongParserToken t) {
                assertSame(token, t);
                b.append("5");
            }
        }.accept(token);
        assertEquals("13542", b.toString());
    }

    @Test
    public void testToExpressionNode() {
        this.toExpressionNodeAndCheck(ExpressionNode.longNode(this.value()));
    }

    @Override
    public String text() {
        return "123";
    }

    @Override
    Long value() {
        return Long.valueOf(this.text());
    }

    @Override
    SpreadsheetLongParserToken createToken(final Long value, final String text) {
        return SpreadsheetLongParserToken.with(value, text);
    }

    @Override
    public SpreadsheetLongParserToken createDifferentToken() {
        return SpreadsheetLongParserToken.with(Long.valueOf(-1), "'different'");
    }

    @Override
    public Class<SpreadsheetLongParserToken> type() {
        return SpreadsheetLongParserToken.class;
    }
}
