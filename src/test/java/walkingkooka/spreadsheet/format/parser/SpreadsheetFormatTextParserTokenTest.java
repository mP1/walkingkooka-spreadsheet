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

public final class SpreadsheetFormatTextParserTokenTest extends SpreadsheetFormatParentParserTokenTestCase<SpreadsheetFormatTextParserToken> {

    @Override
    @SuppressWarnings("unused")
    public void testWithEmptyTokensFails() {
    }

    @Override
    @SuppressWarnings("unused")
    public void testWithEmptyTextFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unused")
    public void testWithWhitespaceTextFails() {
    }

    @Test
    public void testWithEmptyText() {
        final List<ParserToken> tokens = this.tokens();
        final SpreadsheetFormatTextParserToken token = SpreadsheetFormatTextParserToken.with(tokens, "");
        this.checkValue(token, tokens);
        this.textAndCheck(token, "");
    }

    @Test
    public void testWithEmptyValue() {
        final SpreadsheetFormatTextParserToken token = SpreadsheetFormatTextParserToken.with(SpreadsheetFormatTextParserToken.EMPTY, "a");
        this.checkValue(token, SpreadsheetFormatTextParserToken.EMPTY);
        this.textAndCheck(token, "a");
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatTextParserToken token = this.createToken();
        final SpreadsheetFormatParserToken text = token.value().get(0).cast(SpreadsheetFormatParserToken.class);
        final SpreadsheetFormatParserToken placeholder = token.value().get(1).cast(SpreadsheetFormatParserToken.class);

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
            protected Visiting startVisit(final SpreadsheetFormatTextParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormatTextParserToken t) {
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
            protected void visit(final SpreadsheetFormatTextPlaceholderParserToken t) {
                assertSame(placeholder, t);
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
                        placeholder, placeholder, placeholder, placeholder, placeholder,
                        token, token, token),
                visited,
                "visited");
    }

    // kind............................................................................................................

    @Test
    public void testKind() {
        this.kindAndCheck();
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetFormatTextParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormatTextParserToken.with(tokens, text);
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.text1(),
                SpreadsheetFormatParserToken.textPlaceholder("@", "@"));
    }

    @Override
    public String text() {
        return TEXT1 + "@";
    }

    @Override
    public SpreadsheetFormatTextParserToken createDifferentToken() {
        return SpreadsheetFormatTextParserToken.with(Lists.of(this.text2()), TEXT2);
    }

    @Override
    public Class<SpreadsheetFormatTextParserToken> type() {
        return SpreadsheetFormatTextParserToken.class;
    }

    @Override
    public SpreadsheetFormatTextParserToken unmarshall(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatParserToken.unmarshallText(node, context);
    }
}
