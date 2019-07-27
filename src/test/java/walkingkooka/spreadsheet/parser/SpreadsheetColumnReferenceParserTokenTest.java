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
package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetColumnReferenceParserTokenTest extends SpreadsheetNonSymbolParserTokenTestCase<SpreadsheetColumnReferenceParserToken, SpreadsheetColumnReference> {

    @Test
    public void testToStringAbsolute() {
        this.toStringAndCheck(this.createToken(SpreadsheetReferenceKind.ABSOLUTE.column(555), "$999"),
                "$999");
    }

    @Test
    public void testToStringRelative() {
        this.toStringAndCheck(this.createToken(), "1");
    }

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnReferenceParserToken token = this.createToken();

        new FakeSpreadsheetParserTokenVisitor() {
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
            protected Visiting startVisit(final SpreadsheetParserToken t) {
                assertSame(token, t);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetParserToken t) {
                assertSame(token, t);
                b.append("4");
            }

            @Override
            protected void visit(final SpreadsheetColumnReferenceParserToken t) {
                assertSame(token, t);
                b.append("5");
            }
        }.accept(token);
        assertEquals("13542", b.toString());
    }

    @Override
    public String text() {
        return "1";
    }

    @Override
    SpreadsheetColumnReference value() {
        return SpreadsheetReferenceKind.RELATIVE.column(Integer.parseInt(this.text()));
    }

    @Override
    protected SpreadsheetColumnReferenceParserToken createToken(final SpreadsheetColumnReference value, final String text) {
        return SpreadsheetColumnReferenceParserToken.with(value, text);
    }

    @Override
    public SpreadsheetColumnReferenceParserToken createDifferentToken() {
        return SpreadsheetColumnReferenceParserToken.with(SpreadsheetReferenceKind.RELATIVE.column(999), "999");
    }

    @Override
    public Class<SpreadsheetColumnReferenceParserToken> type() {
        return SpreadsheetColumnReferenceParserToken.class;
    }
}
