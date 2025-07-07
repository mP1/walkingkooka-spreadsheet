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
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CellSpreadsheetFormulaParserTokenTest extends ParentSpreadsheetFormulaParserTokenTestCase<CellSpreadsheetFormulaParserToken>
    implements HasSpreadsheetReferenceTesting {

    private final static String ROW_TEXT = "B";
    private final static int ROW_VALUE = 2;
    private final static int COLUMN_VALUE = 3;
    private final static String COLUMN_TEXT = String.valueOf(COLUMN_VALUE);

    @Test
    public void testWithZeroTokensFails() {
        this.createToken(" k ");
    }

    @Test
    public void testWithoutColumnFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(ROW_TEXT, this.row()));
    }

    @Test
    public void testWithoutRowFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(COLUMN_TEXT, this.column()));
    }

    @Test
    public void testWith() {
        final ColumnSpreadsheetFormulaParserToken column = this.column();
        final RowSpreadsheetFormulaParserToken row = this.row();
        final String text = ROW_TEXT + ":" + COLUMN_TEXT;
        final CellSpreadsheetFormulaParserToken cell = this.createToken(text, row, column);
        this.textAndCheck(cell, text);
        this.checkValue(cell, row, column);
        this.checkCell(cell, row, column);
    }

    @Test
    public void testToExpression() {
        this.toExpressionAndCheck(Expression.reference(column().value().setRow(row().value())));
    }

    @Override
    CellSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.cell(tokens, text);
    }

    private ColumnSpreadsheetFormulaParserToken column() {
        return column(COLUMN_VALUE);
    }

    private ColumnSpreadsheetFormulaParserToken column(final int value) {
        return SpreadsheetFormulaParserToken.column(
            SpreadsheetReferenceKind.RELATIVE.column(value),
            String.valueOf(value)
        );
    }

    private RowSpreadsheetFormulaParserToken row() {
        return row(ROW_VALUE, ROW_TEXT);
    }

    private RowSpreadsheetFormulaParserToken row(final int value, final String text) {
        return SpreadsheetFormulaParserToken.row(
            SpreadsheetReferenceKind.RELATIVE.row(value),
            text
        );
    }

    private void checkCell(final CellSpreadsheetFormulaParserToken cell,
                           final RowSpreadsheetFormulaParserToken row,
                           final ColumnSpreadsheetFormulaParserToken column) {
        this.checkEquals(
            SpreadsheetSelection.cell(
                column.value(),
                row.value()
            ),
            cell.cell(),
            "cell"
        );
    }

    @Override
    public String text() {
        return ROW_TEXT + COLUMN_TEXT;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.row(), this.column());
    }

    @Override
    public CellSpreadsheetFormulaParserToken createDifferentToken() {
        return this.createToken("D9", Lists.of(this.column(9), this.row(3, "D")));
    }

    @Override
    public Class<CellSpreadsheetFormulaParserToken> type() {
        return CellSpreadsheetFormulaParserToken.class;
    }

    @Override
    public CellSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallCell(from, context);
    }

    // HasSpreadsheetReference..........................................................................................

    @Test
    public void testReference() {
        final CellSpreadsheetFormulaParserToken token = this.createToken();

        this.referenceAndCheck(
            token,
            token.cell()
        );
    }
}
