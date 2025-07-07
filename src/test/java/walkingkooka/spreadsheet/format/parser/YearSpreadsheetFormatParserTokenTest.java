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

public final class YearSpreadsheetFormatParserTokenTest extends NonSymbolSpreadsheetFormatParserTokenTestCase<YearSpreadsheetFormatParserToken, String> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final YearSpreadsheetFormatParserToken token = this.createToken();

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
            protected void visit(final YearSpreadsheetFormatParserToken t) {
                assertSame(token, t);
                b.append("5");
            }
        }.accept(token);
        this.checkEquals("13542", b.toString());
    }

    // kind............................................................................................................

    @Test
    public void testKind1() {
        this.kindAndCheck(
            "y",
            SpreadsheetFormatParserTokenKind.YEAR_TWO_DIGIT
        );
    }

    @Test
    public void testKind2() {
        this.kindAndCheck(
            "yy",
            SpreadsheetFormatParserTokenKind.YEAR_TWO_DIGIT
        );
    }

    @Test
    public void testKind3() {
        this.kindAndCheck(
            "yyy",
            SpreadsheetFormatParserTokenKind.YEAR_FULL
        );
    }

    @Test
    public void testKind4() {
        this.kindAndCheck(
            "yyyy",
            SpreadsheetFormatParserTokenKind.YEAR_FULL
        );
    }

    @Test
    public void testKind5() {
        this.kindAndCheck(
            "yyyyy",
            SpreadsheetFormatParserTokenKind.YEAR_FULL
        );
    }

    @Test
    public void testKind6() {
        this.kindAndCheck(
            "yyyyyy",
            SpreadsheetFormatParserTokenKind.YEAR_FULL
        );
    }

    // helpers..........................................................................................................

    @Override
    public String text() {
        return "..";
    }

    @Override
    String value() {
        return this.text();
    }

    @Override
    YearSpreadsheetFormatParserToken createToken(final String value, final String text) {
        return YearSpreadsheetFormatParserToken.with(value, text);
    }

    @Override
    public YearSpreadsheetFormatParserToken createDifferentToken() {
        return YearSpreadsheetFormatParserToken.with(this.text(), "different");
    }

    @Override
    public Class<YearSpreadsheetFormatParserToken> type() {
        return YearSpreadsheetFormatParserToken.class;
    }

    @Override
    public YearSpreadsheetFormatParserToken unmarshall(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatParserToken.unmarshallYear(node, context);
    }
}
