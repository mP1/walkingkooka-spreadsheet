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

public final class DateSpreadsheetFormatParserTokenTest extends DateDateTimeExpressionTimeSpreadsheetFormatParserTokenTestCase<DateSpreadsheetFormatParserToken> {

    @Override
    @SuppressWarnings("unused")
    public void testWithWhitespaceTextFails() {
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final DateSpreadsheetFormatParserToken token = this.createToken();
        final SpreadsheetFormatParserToken text = token.value().get(0).cast(SpreadsheetFormatParserToken.class);
        final SpreadsheetFormatParserToken day = token.value().get(1).cast(SpreadsheetFormatParserToken.class);

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
            protected Visiting startVisit(final DateSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final DateSpreadsheetFormatParserToken t) {
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
            protected void visit(final DaySpreadsheetFormatParserToken t) {
                assertSame(day, t);
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
        }.accept(token);
        this.checkEquals("7137152871628428", b.toString());
        this.checkEquals(Lists.of(token, token, token,
                text, text, text, text, text,
                day, day, day, day, day,
                token, token, token),
            visited,
            "visited");
    }

    @Override
    DateSpreadsheetFormatParserToken createToken(final String text, final List<ParserToken> tokens) {
        return DateSpreadsheetFormatParserToken.with(tokens, text);
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.text1(),
            SpreadsheetFormatParserToken.day("d", "d"));
    }

    @Override
    public String text() {
        return TEXT1 + "d";
    }

    @Override
    public DateSpreadsheetFormatParserToken createDifferentToken() {
        return DateSpreadsheetFormatParserToken.with(Lists.of(this.text2()), TEXT2);
    }

    @Override
    public Class<DateSpreadsheetFormatParserToken> type() {
        return DateSpreadsheetFormatParserToken.class;
    }

    @Override
    public DateSpreadsheetFormatParserToken unmarshall(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatParserToken.unmarshallDate(node, context);
    }
}
