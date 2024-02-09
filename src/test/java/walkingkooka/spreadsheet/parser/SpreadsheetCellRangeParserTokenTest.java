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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetCellRangeParserTokenTest extends SpreadsheetBinaryParserTokenTestCase<SpreadsheetCellRangeParserToken>
        implements HasSpreadsheetReferenceTesting {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final SpreadsheetBinaryParserToken binary = this.createToken();
        final SpreadsheetParserToken left = binary.left();
        final SpreadsheetParserToken right = binary.right();
        final SpreadsheetParserToken symbol = operatorSymbol();

        new FakeSpreadsheetParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetParserToken n) {
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetParserToken n) {
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetCellRangeParserToken t) {
                assertSame(binary, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetCellRangeParserToken t) {
                assertSame(binary, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final SpreadsheetCellReferenceParserToken t) {
                b.append("5");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetCellReferenceParserToken t) {
                b.append("6");
                visited.add(t);
            }


            @Override
            protected void visit(final SpreadsheetColumnReferenceParserToken t) {
                b.append("7");
            }

            @Override
            protected void visit(final SpreadsheetRowReferenceParserToken t) {
                b.append("8");
            }

            @Override
            protected void visit(final SpreadsheetBetweenSymbolParserToken t) {
                b.append("9");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("A");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("B");
            }
        }.accept(binary);
        this.checkEquals("A13A15A172BA182B62BA192BA15A172BA182B62B42B", b.toString());
        this.checkEquals(
                Lists.of(
                        binary,
                        left, left,
                        symbol,
                        right, right,
                        binary
                ),
                visited,
                "visited"
        );
    }

    @Override
    SpreadsheetCellRangeParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.cellRange(tokens, text);
    }

    @Override
    SpreadsheetParserToken leftToken() {
        return SpreadsheetParserToken.cellReference(
                Lists.of(
                        this.column("A"),
                        this.row("1")
                ),
                "A1"
        );
    }

    @Override
    SpreadsheetParserToken rightToken() {
        return SpreadsheetParserToken.cellReference(
                Lists.of(
                        this.column("B"),
                        this.row("2")
                ),
                "B2"
        );
    }

    private SpreadsheetColumnReferenceParserToken column(final String text) {
        return SpreadsheetParserToken.columnReference(
                SpreadsheetSelection.parseColumn(text),
                text
        );
    }

    private SpreadsheetRowReferenceParserToken row(final String text) {
        return SpreadsheetParserToken.rowReference(
                SpreadsheetSelection.parseRow(text),
                text
        );
    }

    @Override
    SpreadsheetParserToken operatorSymbol() {
        return SpreadsheetParserToken.betweenSymbol(":", ":");
    }

    @Override
    public Class<SpreadsheetCellRangeParserToken> type() {
        return SpreadsheetCellRangeParserToken.class;
    }

    @Override
    public SpreadsheetCellRangeParserToken unmarshall(final JsonNode from,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallCellRange(from, context);
    }

    // HasSpreadsheetReference..........................................................................................

    @Test
    public void testReference() {
        final SpreadsheetCellRangeParserToken token = this.createToken();

        this.referenceAndCheck(
                token,
                token.toCellRange()
        );
    }
}
