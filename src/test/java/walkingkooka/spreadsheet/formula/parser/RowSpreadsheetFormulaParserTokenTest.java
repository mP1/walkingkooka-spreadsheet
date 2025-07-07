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
package walkingkooka.spreadsheet.formula.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RowSpreadsheetFormulaParserTokenTest extends NonSymbolSpreadsheetFormulaParserTokenTestCase<RowSpreadsheetFormulaParserToken, SpreadsheetRowReference> {

    @Test
    public void testWithEmptyTextFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(""));
    }

    @Test
    public void testToStringAbsolute() {
        this.toStringAndCheck(this.createToken(SpreadsheetReferenceKind.ABSOLUTE.row(555), "$ABC"),
            "$ABC");
    }

    @Test
    public void testToStringRelative() {
        this.toStringAndCheck(this.createToken(), "1");
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final RowSpreadsheetFormulaParserToken token = this.createToken();

        new FakeSpreadsheetFormulaParserTokenVisitor() {
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
            protected Visiting startVisit(final SpreadsheetFormulaParserToken t) {
                assertSame(token, t);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormulaParserToken t) {
                assertSame(token, t);
                b.append("4");
            }

            @Override
            protected void visit(final RowSpreadsheetFormulaParserToken t) {
                assertSame(token, t);
                b.append("5");
            }
        }.accept(token);
        this.checkEquals("13542", b.toString());
    }

    @Override
    public String text() {
        return "1";
    }

    @Override
    SpreadsheetRowReference value() {
        return SpreadsheetReferenceKind.RELATIVE.row(Integer.parseInt(this.text()));
    }

    @Override
    RowSpreadsheetFormulaParserToken createToken(final SpreadsheetRowReference value, final String text) {
        return RowSpreadsheetFormulaParserToken.with(value, text);
    }

    @Override
    public RowSpreadsheetFormulaParserToken createDifferentToken() {
        return RowSpreadsheetFormulaParserToken.with(SpreadsheetReferenceKind.RELATIVE.row(999), "ABC");
    }

    @Override
    public Class<RowSpreadsheetFormulaParserToken> type() {
        return RowSpreadsheetFormulaParserToken.class;
    }

    @Override
    public RowSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallRow(from, context);
    }
}
