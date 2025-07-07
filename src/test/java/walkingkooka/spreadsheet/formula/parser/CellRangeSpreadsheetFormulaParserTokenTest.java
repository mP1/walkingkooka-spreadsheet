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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class CellRangeSpreadsheetFormulaParserTokenTest extends BinarySpreadsheetFormulaParserTokenTestCase<CellRangeSpreadsheetFormulaParserToken>
    implements HasSpreadsheetReferenceTesting {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final BinarySpreadsheetFormulaParserToken binary = this.createToken();
        final SpreadsheetFormulaParserToken left = binary.left();
        final SpreadsheetFormulaParserToken right = binary.right();
        final SpreadsheetFormulaParserToken symbol = operatorSymbol();

        new FakeSpreadsheetFormulaParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetFormulaParserToken n) {
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetFormulaParserToken n) {
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final CellRangeSpreadsheetFormulaParserToken t) {
                assertSame(binary, t);
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final CellRangeSpreadsheetFormulaParserToken t) {
                assertSame(binary, t);
                b.append("4");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final CellSpreadsheetFormulaParserToken t) {
                b.append("5");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final CellSpreadsheetFormulaParserToken t) {
                b.append("6");
                visited.add(t);
            }


            @Override
            protected void visit(final ColumnSpreadsheetFormulaParserToken t) {
                b.append("7");
            }

            @Override
            protected void visit(final RowSpreadsheetFormulaParserToken t) {
                b.append("8");
            }

            @Override
            protected void visit(final BetweenSymbolSpreadsheetFormulaParserToken t) {
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
    CellRangeSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.cellRange(tokens, text);
    }

    @Override
    SpreadsheetFormulaParserToken leftToken() {
        return SpreadsheetFormulaParserToken.cell(
            Lists.of(
                this.column("A"),
                this.row("1")
            ),
            "A1"
        );
    }

    @Override
    SpreadsheetFormulaParserToken rightToken() {
        return SpreadsheetFormulaParserToken.cell(
            Lists.of(
                this.column("B"),
                this.row("2")
            ),
            "B2"
        );
    }

    private ColumnSpreadsheetFormulaParserToken column(final String text) {
        return SpreadsheetFormulaParserToken.column(
            SpreadsheetSelection.parseColumn(text),
            text
        );
    }

    private RowSpreadsheetFormulaParserToken row(final String text) {
        return SpreadsheetFormulaParserToken.row(
            SpreadsheetSelection.parseRow(text),
            text
        );
    }

    @Override
    SpreadsheetFormulaParserToken operatorSymbol() {
        return SpreadsheetFormulaParserToken.betweenSymbol(":", ":");
    }

    @Override
    public Class<CellRangeSpreadsheetFormulaParserToken> type() {
        return CellRangeSpreadsheetFormulaParserToken.class;
    }

    @Override
    public CellRangeSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                             final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallCellRange(from, context);
    }

    // HasSpreadsheetReference..........................................................................................

    @Test
    public void testReference() {
        final CellRangeSpreadsheetFormulaParserToken token = this.createToken();

        this.referenceAndCheck(
            token,
            token.toCellRange()
        );
    }
}
