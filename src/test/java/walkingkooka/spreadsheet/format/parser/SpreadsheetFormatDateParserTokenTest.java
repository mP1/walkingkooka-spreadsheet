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
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetFormatDateParserTokenTest extends SpreadsheetFormatDateDateTimeExpressionTimeParserTokenTestCase<SpreadsheetFormatDateParserToken> {

    @Override
    @SuppressWarnings("unused")
    public void testWithWhitespaceTextFails() {
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatDateParserToken token = this.createToken();
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
            protected Visiting startVisit(final SpreadsheetFormatDateParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormatDateParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatTextLiteralParserToken t) {
                assertSame(text, t);
                b.append("5");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatDayParserToken t) {
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
        assertEquals("7137152871628428", b.toString());
        assertEquals(Lists.of(token, token, token,
                text, text, text, text, text,
                day, day, day, day, day,
                token, token, token),
                visited,
                "visited");
    }

    @Override
    SpreadsheetFormatDateParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormatDateParserToken.with(tokens, text);
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
    public SpreadsheetFormatDateParserToken createDifferentToken() {
        return SpreadsheetFormatDateParserToken.with(Lists.of(this.text2()), TEXT2);
    }

    @Override
    public Class<SpreadsheetFormatDateParserToken> type() {
        return SpreadsheetFormatDateParserToken.class;
    }
}
