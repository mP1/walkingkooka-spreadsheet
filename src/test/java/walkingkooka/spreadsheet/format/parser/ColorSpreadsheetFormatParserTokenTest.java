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
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ColorSpreadsheetFormatParserTokenTest extends ParentSpreadsheetFormatParserTokenTestCase<ColorSpreadsheetFormatParserToken> {

    @Test
    public void testWithMissingColorNameOrColorNumberFails() {
        assertThrows(IllegalArgumentException.class, () -> ColorSpreadsheetFormatParserToken.with(this.missingColorNameOrColorNumber(), "[RED]"));
    }

    private List<ParserToken> missingColorNameOrColorNumber() {
        return Lists.of(this.bracketOpen(), this.number1(), this.bracketClose());
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final ColorSpreadsheetFormatParserToken token = this.createToken();
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
            protected Visiting startVisit(final ColorSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ColorSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final BracketOpenSymbolSpreadsheetFormatParserToken t) {
                assertSame(open, t);
                b.append("5");
                visited.add(t);
            }

            @Override
            protected void visit(final ColorNameSpreadsheetFormatParserToken t) {
                assertSame(name, t);
                b.append("6");
                visited.add(t);
            }

            @Override
            protected void visit(final WhitespaceSpreadsheetFormatParserToken t) {
                checkEquals(whitespace, t);
                b.append("7");
                visited.add(t);
            }

            @Override
            protected void visit(final BracketCloseSymbolSpreadsheetFormatParserToken t) {
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

        this.checkEquals("9139152A9162A9172A9182A42A", b.toString());
        this.checkEquals(
            Lists.of(token, token, token,
                open, open, open, open, open,
                name, name, name, name, name,
                whitespace, whitespace, whitespace, whitespace, whitespace,
                close, close, close, close, close,
                token, token, token),
            visited,
            "visited"
        );
    }

    // kind............................................................................................................

    @Test
    public void testKindColorName() {
        this.kindAndCheck(
            ColorSpreadsheetFormatParserToken.with(
                Lists.of(
                    this.bracketOpen(),
                    SpreadsheetFormatParserToken.colorName("RED", "RED"),
                    this.whitespace(),
                    this.bracketClose()
                ),
                "[RED]"
            ),
            SpreadsheetFormatParserTokenKind.COLOR_NAME
        );
    }

    @Test
    public void testKindColorNumber() {
        this.kindAndCheck(
            ColorSpreadsheetFormatParserToken.with(
                Lists.of(
                    this.bracketOpen(),
                    SpreadsheetFormatParserToken.colorNumber(12, "12"),
                    this.whitespace(),
                    this.bracketClose()
                ),
                "[Color 12]"
            ),
            SpreadsheetFormatParserTokenKind.COLOR_NUMBER
        );
    }

    // helpers..........................................................................................................

    @Override
    ColorSpreadsheetFormatParserToken createToken(final String text, final List<ParserToken> tokens) {
        return ColorSpreadsheetFormatParserToken.with(tokens, text);
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
    public ColorSpreadsheetFormatParserToken createDifferentToken() {
        return ColorSpreadsheetFormatParserToken.with(Lists.of(this.bracketOpen(),
                SpreadsheetFormatParserToken.colorName("GREEN", "GREEN"),
                this.bracketClose()),
            "[GREEN]");
    }

    @Override
    public Class<ColorSpreadsheetFormatParserToken> type() {
        return ColorSpreadsheetFormatParserToken.class;
    }

    @Override
    public ColorSpreadsheetFormatParserToken unmarshall(final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatParserToken.unmarshallColor(node, context);
    }
}
