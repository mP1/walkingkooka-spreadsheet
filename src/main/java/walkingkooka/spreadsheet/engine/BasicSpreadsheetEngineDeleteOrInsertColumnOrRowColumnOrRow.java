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
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Collection;
import java.util.Optional;

/**
 * Base class that acts as a bridge to either columns or rows.
 */
abstract class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow {

    /**
     * Package private to limit sub classing.
     */
    BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow(final int value,
                                                               final int count,
                                                               final BasicSpreadsheetEngine engine,
                                                               final SpreadsheetEngineContext context) {
        super();
        this.value = value;
        this.count = count;
        this.engine = engine;
        this.context = context;
    }

    final void delete() {
        BasicSpreadsheetEngineDeleteColumnOrRow.delete(this, this.context);
    }

    final void insert() {
        BasicSpreadsheetEngineInsertColumnOrRow.insert(this, this.context);
    }

    // delete....................................................................................................

    /**
     * Deletes the selected range of row or columns.
     */
    final void delete(final int start, final int count) {
        for (int i = 0; i < count; i++) {
            this.delete(start + i);
        }
    }

    /**
     * Delete all the cells in the selected row/column.
     */
    final void delete(final int columnOrRow) {
        this.cells(columnOrRow)
                .forEach(c -> this.cellStore().delete(c.reference()));
    }

    // move .............................................................................................................

    /**
     * Moves all the cells in the selected row/column.
     */
    final void move(final int source) {
        this.cells(source).stream()
                .forEach(this::moveCell);
    }

    /**
     * Moves the cell to its new location, expressions will be updated later.
     */
    private void moveCell(final SpreadsheetCell cell) {
        final SpreadsheetCellReference reference = cell.reference();
        this.deleteCell(reference);
        this.saveCell(cell.setReference(this.fixCellReference(cell.reference())));
    }

    // fix references in all cells .............................................................................

    /**
     * Scans all cell expressions for references and fixes those needing fixing.
     */
    final void fixAllExpressionReferences(final SpreadsheetEngineContext context) {
        final int rows = this.maxRow();
        for (int i = 0; i <= rows; i++) {
            this.fixRowReferences(SpreadsheetReferenceKind.ABSOLUTE.row(i), context);
        }
    }

    private void fixRowReferences(final SpreadsheetRowReference row, final SpreadsheetEngineContext context) {
        this.rowCells(row).stream()
                .map(r -> this.fixExpressionReferences(r, context))
                .forEach(this::saveCell);
    }

    /**
     * Attempts to parse the formula if necessary and then update cell references that may have shifted due to a
     * delete or insert.
     */
    private SpreadsheetCell fixExpressionReferences(final SpreadsheetCell cell,
                                                    final SpreadsheetEngineContext context) {
        return cell.setFormula(
                this.engine.parse(
                        cell.formula(),
                        this::fixCellReferencesWithinExpression,
                        context));
    }

    /**
     * Attempts to parse the formula if necessary and then update cell references that may have shifted due to a
     * delete or insert. Note that references to deleted cells, will be replaced by a function that when executed
     * reports an error that the cell was deleted.
     */
    private SpreadsheetParserToken fixCellReferencesWithinExpression(final SpreadsheetParserToken token) {
        return BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token,
                this);
    }

    /**
     * Returned when the input reference was deleted.
     */
    final static Optional<SpreadsheetParserToken> INVALID_CELL_REFERENCE = Optional.empty();

    /**
     * Handles a column {@link SpreadsheetColumnReferenceParserToken} within an expression.
     * It may be returned unmodified, replaced by a function if the reference was deleted or simply have the reference adjusted.
     */
    abstract Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetColumnReferenceParserToken token);

    /**
     * Handles a column {@link SpreadsheetRowReferenceParserToken} within an expression.
     * It may be returned unmodified, replaced by a function if the reference was deleted or simply have the reference adjusted.
     */
    abstract Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetRowReferenceParserToken token);

    /**
     * Adjusts the reference to match the delete or insert column/row value to it still points to the "same" cell.
     */
    abstract SpreadsheetCellReference fixCellReference(final SpreadsheetCellReference reference);

//    // cell reference.................................................................................................

    /**
     * Returns the row or column int value
     */
    abstract int columnOrRowValue(final SpreadsheetCellReference cell);

    /**
     * Setter that updates the column or row value
     */
    abstract SpreadsheetCellReference setColumnOrRowValue(final SpreadsheetCellReference cell, final int value);

    /**
     * Adds a delta value to a column or row value
     */
    abstract SpreadsheetCellReference addColumnOrRowValue(final SpreadsheetCellReference cell, final int value);

    // label mappings............................................................................................

    // fix references in all label mappings .............................................................................

    /**
     * Visits all label mappings and adjusts the given references.
     */
    final void fixAllLabelMappings() {
        this.labelStore().all()
                .stream()
                .forEach(this.deleteOrInsert::fixLabelMapping);
    }

    // DELETE .......................................................................................................

    /**
     * Deletes the mapping because the target was deleted or fixes the reference.
     */
    final void deleteOrFixLabelMapping(final SpreadsheetLabelMapping mapping) {
        new SpreadsheetExpressionReferenceVisitor() {
            @Override
            protected void visit(final SpreadsheetCellReference reference) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.deleteOrFixSpreadsheetCellReference(reference, mapping);
            }

            @Override
            protected void visit(final SpreadsheetLabelName label) {
                throw new UnsupportedOperationException(mapping.toString());
            }

            @Override
            protected void visit(final SpreadsheetRange range) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.deleteOrFixSpreadsheetRange(range, mapping);
            }
        }.accept(mapping.reference());
    }

    private void deleteOrFixSpreadsheetCellReference(final SpreadsheetCellReference reference,
                                                     final SpreadsheetLabelMapping mapping) {
        final int diff = this.columnOrRowValue(reference) - this.value;
        if (diff >= 0) {
            if (diff >= this.count) {
                this.fixCellReferenceAndSaveLabel(reference, mapping);
            } else {
                this.deleteLabel(mapping);
            }
        }
    }

    private void deleteOrFixSpreadsheetRange(final SpreadsheetRange range,
                                             final SpreadsheetLabelMapping mapping) {

        SpreadsheetCellReference rangeEnd = range.end();
        final int rangeEndValue = this.columnOrRowValue(rangeEnd);
        final int deleteBegin = this.value;

        // deleted col/rows are after end of range.
        if (deleteBegin < rangeEndValue) {

            SpreadsheetCellReference rangeBegin = range.begin();
            final int rangeBeginValue = this.columnOrRowValue(rangeBegin);
            final int deleteEnd = deleteBegin + this.count;

            do {
                final int beginDiff = deleteBegin - rangeBeginValue;
                final int endDiff = deleteEnd - rangeEndValue;

                if (beginDiff <= 0 && endDiff >= 0) {
                    this.deleteLabel(mapping);
                    break;
                }

                final int deleteLength = deleteEnd - deleteBegin;
                if (deleteEnd < rangeBeginValue) {
                    rangeBegin = this.addColumnOrRowValue(rangeBegin, -deleteLength);
                    rangeEnd = this.addColumnOrRowValue(rangeEnd, -deleteLength);
                    break;
                }

                if (beginDiff < 0 && endDiff < 0) {
                    rangeBegin = this.setColumnOrRowValue(rangeBegin, deleteBegin);
                    rangeEnd = this.setColumnOrRowValue(rangeBegin, deleteBegin + rangeEndValue - rangeBeginValue - deleteLength);
                    break;
                }

                if (endDiff < 0) {
                    rangeEnd = this.addColumnOrRowValue(rangeEnd, -deleteLength);
                    break;
                }

                rangeEnd = this.setColumnOrRowValue(rangeBegin, deleteBegin);

            } while (false);

            this.saveLabelIfUpdated(
                    rangeBegin.spreadsheetRange(rangeEnd),
                    mapping);
        }
    }

    // INSERT .....................................................................................................

    /**
     * Fixes a mapping because the target was moved because of inserted columns or rows.
     */
    final void insertFixLabelMapping(final SpreadsheetLabelMapping mapping) {
        new SpreadsheetExpressionReferenceVisitor() {
            @Override
            protected void visit(final SpreadsheetCellReference reference) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.insertFixSpreadsheetCellReference(reference, mapping);
            }

            @Override
            protected void visit(final SpreadsheetLabelName label) {
                throw new UnsupportedOperationException(mapping.toString());
            }

            @Override
            protected void visit(final SpreadsheetRange range) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.insertFixSpreadsheetRange(range, mapping);
            }
        }.accept(mapping.reference());
    }

    private void insertFixSpreadsheetCellReference(final SpreadsheetCellReference reference,
                                                   final SpreadsheetLabelMapping mapping) {
        final int diff = this.columnOrRowValue(reference) - this.value;
        if (diff >= 0) {
            this.fixCellReferenceAndSaveLabel(reference, mapping);
        }
    }

    private void insertFixSpreadsheetRange(final SpreadsheetRange range,
                                           final SpreadsheetLabelMapping mapping) {
        SpreadsheetCellReference begin = range.begin();
        SpreadsheetCellReference end = range.end();

        if (this.columnOrRowValue(begin) >= this.value) {
            begin = this.fixCellReference(begin);
        }
        if (this.columnOrRowValue(end) >= this.value) {
            end = this.fixCellReference(end);
        }

        this.saveLabelIfUpdated(begin.spreadsheetRange(end), mapping);
    }

    // cells....................................................................................................

    /**
     * Returns the max column or row.
     */
    abstract int max();

    /**
     * Returns the max or last row
     */
    final int maxRow() {
        return this.cellStore().rows();
    }

    /**
     * Find all the cells int the given column/row.
     */
    abstract Collection<SpreadsheetCell> cells(final int columnOrRow);

    /**
     * Returns all the cells for the requested row.
     */
    final Collection<SpreadsheetCell> rowCells(final SpreadsheetRowReference row) {
        return this.cellStore().row(row);
    }

    /**
     * Deletes the cell.
     */
    final void deleteCell(final SpreadsheetCellReference cell) {
        this.cellStore().delete(cell);
    }

    /**
     * Saves the cell.
     */
    final void saveCell(final SpreadsheetCell cell) {
        this.cellStore().save(cell);
    }

    final SpreadsheetCellStore cellStore() {
        return this.engine.cellStore;
    }

    final int value;
    final int count;

    // labels....................................................................................................

    final void fixCellReferenceAndSaveLabel(final SpreadsheetCellReference reference,
                                            final SpreadsheetLabelMapping mapping) {
        this.saveLabelIfUpdated(this.fixCellReference(reference), mapping);
    }

    final void saveLabelIfUpdated(final ExpressionReference reference, final SpreadsheetLabelMapping mapping) {
        final SpreadsheetLabelMapping updated = mapping.setReference(reference);
        if (mapping != updated) {
            this.labelStore().save(updated);
        }
    }

    final void saveLabel(final SpreadsheetLabelMapping mapping) {
        this.labelStore().save(mapping);
    }

    final void deleteLabel(final SpreadsheetLabelMapping mapping) {
        this.labelStore().delete(mapping.label());
    }

    final SpreadsheetLabelStore labelStore() {
        return this.engine.labelStore;
    }

    private final BasicSpreadsheetEngine engine;

    private final SpreadsheetEngineContext context;

    BasicSpreadsheetEngineDeleteOrInsertColumnOrRow deleteOrInsert;
}
