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

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;

import java.util.Collection;
import java.util.Optional;

/**
 * Performs operations on columns for delete or insertion.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow {

    static BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn with(final int value,
                                                                                 final int count,
                                                                                 final BasicSpreadsheetEngine engine,
                                                                                 final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn(value, count, engine, context);
    }

    /**
     * Private ctor use static factory.
     */
    private BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRowColumn(final int value,
                                                                             final int count,
                                                                             final BasicSpreadsheetEngine engine,
                                                                             final SpreadsheetEngineContext context) {
        super(value, count, engine, context);
    }

    @Override
    int max() {
        return this.cellStore()
            .columnCount()
            - 1;
    }

    @Override
    Collection<SpreadsheetCell> cells(final int column) {
        return this.cellStore().column(SpreadsheetReferenceKind.ABSOLUTE.column(column));
    }

    @Override
    void deleteColumnOrRow(final int column) {
        this.deleteColumn(
            SpreadsheetReferenceKind.RELATIVE.column(column)
        );
    }

    private void deleteColumn(final SpreadsheetColumnReference column) {
        this.columnStore()
            .delete(column);
    }

    @Override
    Optional<ColumnSpreadsheetFormulaParserToken> fixColumnReferenceParserToken(final ColumnSpreadsheetFormulaParserToken token) {
        return this.deleteOrInsert.isColumnDeleted(token) ?
            Optional.empty() :
            this.fixColumnReferenceParserToken0(token);
    }

    private Optional<ColumnSpreadsheetFormulaParserToken> fixColumnReferenceParserToken0(final ColumnSpreadsheetFormulaParserToken token) {
        final SpreadsheetColumnReference old = token.value();
        final int value = old.value();

        ColumnSpreadsheetFormulaParserToken result = token;

        if (value > this.value) {
            final SpreadsheetColumnReference reference = old.setValue(value + this.deleteOrInsert.fixColumnOrRowReference(this.count));
            result = SpreadsheetFormulaParserToken.column(reference, reference.toString());
        }

        return Optional.of(result);
    }

    @Override
    Optional<RowSpreadsheetFormulaParserToken> fixRowReferenceParserToken(final RowSpreadsheetFormulaParserToken token) {
        // only fixing cols refs not rows
        return Optional.of(token);
    }

    @Override
    SpreadsheetCellReference fixCellReference(final SpreadsheetCellReference reference) {
        return reference.addColumn(this.deleteOrInsert.fixColumnOrRowReference(this.count));
    }

    @Override
    int columnOrRowValue(final SpreadsheetCellReference cell) {
        return cell.column().value();
    }

    @Override
    void moveColumnOrRows(final int column) {
        this.moveColumn(
            SpreadsheetReferenceKind.RELATIVE.column(column)
        );
    }

    private void moveColumn(final SpreadsheetColumnReference columnReference) {
        final SpreadsheetColumnStore store = this.columnStore();

        final Optional<SpreadsheetColumn> maybeColumn = store.load(columnReference);
        if (maybeColumn.isPresent()) {
            final SpreadsheetColumn column = maybeColumn.get();
            store.delete(columnReference);

            final SpreadsheetColumn fixed = column.setReference(
                this.fixColumnReference(columnReference)
            );
            if (!fixed.equals(column)) {
                store.save(fixed);
            }
        }
    }

    @Override
    SpreadsheetCellReference setColumnOrRowValue(final SpreadsheetCellReference cell, final int value) {
        return cell.setColumn(cell.column().setValue(value));
    }

    @Override
    SpreadsheetCellReference addColumnOrRowValue(final SpreadsheetCellReference cell, final int value) {
        return cell.setColumn(cell.column().add(value));
    }
}
