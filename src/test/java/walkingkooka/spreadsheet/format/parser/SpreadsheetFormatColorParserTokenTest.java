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
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatColorParserTokenTest extends SpreadsheetFormatParentParserTokenTestCase<SpreadsheetFormatColorParserToken> {

    @Test
    public void testWithMissingColorNameOrColorNumberFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetFormatColorParserToken.with(this.missingColorNameOrColorNumber(), "[RED]");
        });
    }

    private List<ParserToken> missingColorNameOrColorNumber() {
        return Lists.of(this.bracketOpen(), this.number1(), this.bracketClose());
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatColorParserToken token = this.createToken();
        final SpreadsheetFormatParserToken open = token.value().get(0).cast(SpreadsheetFormatParserToken.class);
        final SpreadsheetFormatParserToken name = token.value().get(1).cast(SpreadsheetFormatParserToken.class);
        final SpreadsheetFormatParserToken whitespace = token.value().get(2).cast(SpreadsheetFormatParserToken.class);
        final SpreadsheetFormatParserToken close = token.value().get(3).cast(SpreadsheetFormatParserToken.class);

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
            protected Visiting startVisit(final SpreadsheetFormatColorParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormatColorParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatBracketOpenSymbolParserToken t) {
                assertSame(open, t);
                b.append("5");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatColorNameParserToken t) {
                assertSame(name, t);
                b.append("6");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatWhitespaceParserToken t) {
                assertEquals(whitespace, t);
                b.append("7");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatBracketCloseSymbolParserToken t) {
                assertSame(close, t);
                b.append("8");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("9");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("A");
                visited.add(t);
            }
        }.accept(token);
        assertEquals("9139152A9162A9172A9182A42A", b.toString());
        assertEquals(Lists.of(token, token, token,
                open, open, open, open, open,
                name, name, name, name, name,
                whitespace, whitespace, whitespace, whitespace, whitespace,
                close, close, close, close, close,
                token, token, token),
                visited,
                "visited");
    }

    @Override
    SpreadsheetFormatColorParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormatColorParserToken.with(tokens, text);
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.bracketOpen(),
                SpreadsheetFormatParserToken.colorName("RED", "RED"),
                this.whitespace(),
                this.bracketClose());
    }

    @Override
    public String text() {
        return "[RED   ]";
    }

    @Override
    public SpreadsheetFormatColorParserToken createDifferentToken() {
        return SpreadsheetFormatColorParserToken.with(Lists.of(this.bracketOpen(),
                SpreadsheetFormatParserToken.colorName("GREEN", "GREEN"),
                this.bracketClose()),
                "[GREEN]");
    }

    @Override
    public Class<SpreadsheetFormatColorParserToken> type() {
        return SpreadsheetFormatColorParserToken.class;
    }
}
