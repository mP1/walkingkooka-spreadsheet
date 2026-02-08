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
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetRow;

import java.util.Collection;
import java.util.Optional;

/**
 * Performs operations on rows for delete or insertion.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow {

    static BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow with(final int value,
                                                                              final int count,
                                                                              final BasicSpreadsheetEngine engine,
                                                                              final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow(value, count, engine, context);
    }

    /**
     * Private ctor use static factory.
     */
    private BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowRow(final int value,
                                                                          final int count,
                                                                          final BasicSpreadsheetEngine engine,
                                                                          final SpreadsheetEngineContext context) {
        super(value, count, engine, context);
    }

    @Override
    int max() {
        return this.maxRow();
    }

    @Override
    Collection<SpreadsheetCell> cells(final int row) {
        return this.rowCells(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    @Override
    void deleteColumnOrRow(final int row) {
        this.deleteRow(
            SpreadsheetReferenceKind.RELATIVE.row(row)
        );
    }

    private void deleteRow(final SpreadsheetRowReference row) {
        this.rowStore()
            .delete(row);
    }

    @Override
    Optional<ColumnSpreadsheetFormulaParserToken> fixColumnReferenceParserToken(final ColumnSpreadsheetFormulaParserToken token) {
        // only fixing rows refs not cols
        return Optional.of(token);
    }

    @Override
    Optional<RowSpreadsheetFormulaParserToken> fixRowReferenceParserToken(final RowSpreadsheetFormulaParserToken token) {
        return this.deleteOrInsert.isRowDeleted(token) ?
            Optional.empty() :
            this.fixRowReferenceParserToken0(token);
    }

    private Optional<RowSpreadsheetFormulaParserToken> fixRowReferenceParserToken0(final RowSpreadsheetFormulaParserToken token) {
        final SpreadsheetRowReference old = token.value();
        final int value = old.value();

        RowSpreadsheetFormulaParserToken result = token;

        if (value > this.value) {
            final SpreadsheetRowReference reference = old.setValue(value + this.deleteOrInsert.fixColumnOrRowReference(this.count));
            result = SpreadsheetFormulaParserToken.row(reference, reference.toString());
        }

        return Optional.of(result);
    }

    @Override
    SpreadsheetCellReference fixCellReference(final SpreadsheetCellReference reference) {
        return reference.addRow(this.deleteOrInsert.fixColumnOrRowReference(this.count));
    }

    @Override
    int columnOrRowValue(final SpreadsheetCellReference cell) {
        return cell.row().value();
    }

    @Override
    void moveColumnOrRows(final int row) {
        this.moveRow(
            SpreadsheetReferenceKind.RELATIVE.row(row)
        );
    }

    private void moveRow(final SpreadsheetRowReference rowReference) {
        final SpreadsheetRowStore store = this.rowStore();

        final Optional<SpreadsheetRow> maybeRow = store.load(rowReference);
        if (maybeRow.isPresent()) {
            final SpreadsheetRow row = maybeRow.get();
            store.delete(rowReference);

            final SpreadsheetRow fixed = row.setReference(
                this.fixRowReference(rowReference)
            );
            if (!fixed.equals(row)) {
                store.save(fixed);
            }
        }
    }

    @Override
    SpreadsheetCellReference setColumnOrRowValue(final SpreadsheetCellReference cell, final int value) {
        return cell.setRow(cell.row().setValue(value));
    }

    @Override
    SpreadsheetCellReference addColumnOrRowValue(final SpreadsheetCellReference cell,
                                                 final int value) {
        return cell.addRow(value);
    }
}
