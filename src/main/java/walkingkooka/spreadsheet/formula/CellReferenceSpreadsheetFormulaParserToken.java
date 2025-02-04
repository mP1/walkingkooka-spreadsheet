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
package walkingkooka.spreadsheet.formula;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * A reference that includes a defined name or column and row.
 */
public final class CellReferenceSpreadsheetFormulaParserToken extends ParentSpreadsheetFormulaParserToken
        implements SpreadsheetReferenceParserToken,
        HasSpreadsheetReference<SpreadsheetCellReference> {

    static CellReferenceSpreadsheetFormulaParserToken with(final List<ParserToken> value, final String text) {
        return new CellReferenceSpreadsheetFormulaParserToken(Lists.immutable(value), checkText(text));
    }

    private CellReferenceSpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final CellReferenceSpreadsheetFormulaParserTokenVisitor visitor = CellReferenceSpreadsheetFormulaParserTokenVisitor.with();
        visitor.accept(this);

        final RowReferenceSpreadsheetFormulaParserToken row = visitor.row;
        if (null == row) {
            throw new IllegalArgumentException("Cell missing row=" + text);
        }
        final ColumnReferenceSpreadsheetFormulaParserToken column = visitor.column;
        if (null == column) {
            throw new IllegalArgumentException("Cell missing column=" + text);
        }
        this.cell = row.value().setColumn(column.value());
    }

    public SpreadsheetCellReference cell() {
        return this.cell;
    }

    private final SpreadsheetCellReference cell;

    // children.........................................................................................................

    @Override
    public CellReferenceSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                CellReferenceSpreadsheetFormulaParserToken::with
        );
    }

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetFormulaParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // HasSpreadsheetReference..........................................................................................

    @Override
    public SpreadsheetCellReference reference() {
        return this.cell();
    }
}
