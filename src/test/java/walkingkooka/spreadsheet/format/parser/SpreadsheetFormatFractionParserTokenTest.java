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

public final class SpreadsheetFormatFractionParserTokenTest extends SpreadsheetFormatParentParserTokenTestCase<SpreadsheetFormatFractionParserToken> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetFormatFractionParserToken token = this.createToken();
        final SpreadsheetFormatParserToken digit1 = token.value().get(0).cast();
        final SpreadsheetFormatParserToken symbol = token.value().get(1).cast();
        final SpreadsheetFormatParserToken digit2 = token.value().get(2).cast();

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
            protected Visiting startVisit(final SpreadsheetFormatFractionParserToken t) {
                assertSame(token, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormatFractionParserToken t) {
                assertSame(token, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatDigitParserToken t) {
                b.append("5");
                visited.add(t);
            }

            @Override
            protected void visit(final SpreadsheetFormatFractionSymbolParserToken t) {
                assertSame(symbol, t);
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
        assertEquals("713715287162871528428", b.toString());
        assertEquals(Lists.of(token, token, token,
                digit1, digit1, digit1, digit1, digit1,
                symbol, symbol, symbol, symbol, symbol,
                digit2, digit2, digit2, digit2, digit2,
                token, token, token),
                visited,
                "visited");
    }

    @Override
    SpreadsheetFormatFractionParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormatFractionParserToken.with(tokens, text);
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.digit(), this.fractionSymbol(), this.digit());
    }

    @Override
    public String text() {
        return "#/#";
    }

    @Override
    public SpreadsheetFormatFractionParserToken createDifferentToken() {
        return SpreadsheetFormatFractionParserToken.with(Lists.of(this.digit(),
                this.digit(),
                this.fractionSymbol(),
                this.digit(),
                this.digit()),
                "##/##");
    }

    @Override
    public Class<SpreadsheetFormatFractionParserToken> type() {
        return SpreadsheetFormatFractionParserToken.class;
    }
}
