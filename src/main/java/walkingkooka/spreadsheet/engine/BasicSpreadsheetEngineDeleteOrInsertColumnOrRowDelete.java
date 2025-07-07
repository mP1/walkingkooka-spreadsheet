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

import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;

/**
 * Deletes the selected columns or rows.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowDelete extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRow {

    static void delete(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowDelete(columnOrRow)
            .delete0();
    }

    private BasicSpreadsheetEngineDeleteOrInsertColumnOrRowDelete(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super(columnOrRow);
    }

    /**
     * Delete the selected columns or rows.
     */
    private void delete0() {
        this.columnOrRow.deleteColumnOrRowRange(this.columnOrRow.value, this.columnOrRow.count);
        this.move();
        this.columnOrRow.fixAllExpressionReferences();
        this.columnOrRow.fixAllLabelMappings();
    }

    private void move() {
        final int offset = this.columnOrRow.value + this.columnOrRow.count;
        final int moveCount = this.columnOrRow.max() - offset;

        for (int i = 0; i <= moveCount; i++) {
            this.columnOrRow.move(offset + i);
        }
    }

    @Override
    boolean isColumnDeleted(final ColumnSpreadsheetFormulaParserToken column) {
        return this.isDeletedReference(column.value().value());
    }

    @Override
    boolean isRowDeleted(final RowSpreadsheetFormulaParserToken row) {
        return this.isDeletedReference(row.value().value());
    }

    private boolean isDeletedReference(final int value) {
        final int deleted = this.columnOrRow.value;
        return deleted <= value && value <= deleted + this.columnOrRow.count;
    }

    @Override
    int fixColumnOrRowReference(final int count) {
        return -count;
    }

    @Override
    void fixLabelMapping(final SpreadsheetLabelMapping mapping) {
        this.columnOrRow.deleteOrFixLabelMapping(mapping);
    }
}
