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
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SeparatorSymbolSpreadsheetFormatParserTokenTest extends SymbolSpreadsheetFormatParserTokenTestCase<SeparatorSymbolSpreadsheetFormatParserToken> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SeparatorSymbolSpreadsheetFormatParserToken token = this.createToken();

        new FakeSpreadsheetFormatParserTokenVisitor() {
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
            protected Visiting startVisit(final SpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("4");
            }

            @Override
            protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("5");
            }
        }.accept(token);
        this.checkEquals("13542", b.toString());
    }

    // kind............................................................................................................

    @Test
    public void testKind() {
        this.kindAndCheck(
            SpreadsheetFormatParserTokenKind.SEPARATOR
        );
    }

    // helpers..........................................................................................................

    @Override
    public String text() {
        return "#";
    }

    @Override
    SeparatorSymbolSpreadsheetFormatParserToken createToken(final String value, final String text) {
        return SeparatorSymbolSpreadsheetFormatParserToken.with(value, text);
    }

    @Override
    public SeparatorSymbolSpreadsheetFormatParserToken createDifferentToken() {
        return SeparatorSymbolSpreadsheetFormatParserToken.with("ABC", "'different'");
    }

    @Override
    public Class<SeparatorSymbolSpreadsheetFormatParserToken> type() {
        return SeparatorSymbolSpreadsheetFormatParserToken.class;
    }

    @Override
    public SeparatorSymbolSpreadsheetFormatParserToken unmarshall(final JsonNode node,
                                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatParserToken.unmarshallSeparatorSymbol(node, context);
    }
}
