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

public final class ExponentSpreadsheetFormatParserTokenTest extends ParentSpreadsheetFormatParserTokenTestCase<ExponentSpreadsheetFormatParserToken> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final ExponentSpreadsheetFormatParserToken token = this.createToken();
        final SpreadsheetFormatParserToken symbol = token.value().get(0).cast(SpreadsheetFormatParserToken.class);
        final SpreadsheetFormatParserToken digit = token.value().get(1).cast(SpreadsheetFormatParserToken.class);

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
            protected Visiting startVisit(final ExponentSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExponentSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final ExponentSymbolSpreadsheetFormatParserToken t) {
                assertSame(symbol, t);
                b.append("5");
                visited.add(t);
            }

            @Override
            protected void visit(final DigitSpreadsheetFormatParserToken t) {
                assertSame(digit, t);
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
                symbol, symbol, symbol, symbol, symbol,
                digit, digit, digit, digit, digit,
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
    ExponentSpreadsheetFormatParserToken createToken(final String text, final List<ParserToken> tokens) {
        return ExponentSpreadsheetFormatParserToken.with(tokens, text);
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.exponentSymbol(), this.digit());
    }

    @Override
    public String text() {
        return "E+#";
    }

    @Override
    public ExponentSpreadsheetFormatParserToken createDifferentToken() {
        return ExponentSpreadsheetFormatParserToken.with(Lists.of(this.exponentSymbol(),
                this.whitespace(),
                this.digit(),
                this.digit()),
            "E+ ##");
    }

    private SpreadsheetFormatParserToken exponentSymbol() {
        return SpreadsheetFormatParserToken.exponentSymbol("E+", "E+");
    }

    @Override
    public Class<ExponentSpreadsheetFormatParserToken> type() {
        return ExponentSpreadsheetFormatParserToken.class;
    }

    @Override
    public ExponentSpreadsheetFormatParserToken unmarshall(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatParserToken.unmarshallExponent(node, context);
    }
}
