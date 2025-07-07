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

package walkingkooka.spreadsheet.format.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class ExpressionSpreadsheetFormatParserTokenTest extends DateDateTimeExpressionTimeSpreadsheetFormatParserTokenTestCase<ExpressionSpreadsheetFormatParserToken> {

    @Override
    @SuppressWarnings("unused")
    public void testWithWhitespaceTextFails() {
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final ExpressionSpreadsheetFormatParserToken token = this.createToken();
        final SpreadsheetFormatParserToken text = token.value().get(0).cast(SpreadsheetFormatParserToken.class);

        new FakeSpreadsheetFormatParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetFormatParserToken n) {
                b.append("1");
                visited.add(n);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormatParserToken n) {
                b.append("2");
                visited.add(n);
            }

            @Override
            protected Visiting startVisit(final ExpressionSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final TextLiteralSpreadsheetFormatParserToken t) {
                assertSame(text, t);
                b.append("5");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("6");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("A");
                visited.add(t);
            }
        }.accept(token);
        this.checkEquals("6136152A42A", b.toString());
        this.checkEquals(Lists.of(token, token, token,
                text, text, text, text, text,
                token, token, token),
            visited,
            "visited");
    }

    @Override
    ExpressionSpreadsheetFormatParserToken createToken(final String text, final List<ParserToken> tokens) {
        return ExpressionSpreadsheetFormatParserToken.with(tokens, text);
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.text1());
    }

    @Override
    public String text() {
        return this.text1().text();
    }

    @Override
    public ExpressionSpreadsheetFormatParserToken createDifferentToken() {
        final SpreadsheetFormatParserToken text = this.text2();
        return ExpressionSpreadsheetFormatParserToken.with(Lists.of(text),
            text.text());
    }

    @Override
    public Class<ExpressionSpreadsheetFormatParserToken> type() {
        return ExpressionSpreadsheetFormatParserToken.class;
    }

    @Override
    public ExpressionSpreadsheetFormatParserToken unmarshall(final JsonNode node,
                                                             final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatParserToken.unmarshallExpression(node, context);
    }
}
