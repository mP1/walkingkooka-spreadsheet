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

package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.formula.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.RowReferenceSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;

/**
 * Inserts the requested columns or rows.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowInsert extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRow {

    static void insert(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowInsert(columnOrRow).insert0();
    }

    private BasicSpreadsheetEngineDeleteOrInsertColumnOrRowInsert(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super(columnOrRow);
    }

    /**
     * Inserts the requested number of columns or rows.
     */
    private void insert0() {
        this.move();
        this.columnOrRow.fixAllExpressionReferences();
        this.columnOrRow.fixAllLabelMappings();
    }

    private void move() {
        final int offset = this.columnOrRow.value;
        final int moveCount = this.columnOrRow.max() - offset;

        for (int i = 0; i <= moveCount; i++) {
            this.columnOrRow.move(offset + moveCount - i);
        }
    }

    @Override
    int fixColumnOrRowReference(final int count) {
        return +count;
    }

    @Override
    boolean isColumnDeleted(final ColumnSpreadsheetFormulaParserToken column) {
        return false; // no references are ever deleted during an insert.
    }

    @Override
    boolean isRowDeleted(final RowReferenceSpreadsheetFormulaParserToken row) {
        return false; // no references are ever deleted during an insert.
    }

    @Override
    void fixLabelMapping(final SpreadsheetLabelMapping mapping) {
        this.columnOrRow.insertFixLabelMapping(mapping);
    }
}
