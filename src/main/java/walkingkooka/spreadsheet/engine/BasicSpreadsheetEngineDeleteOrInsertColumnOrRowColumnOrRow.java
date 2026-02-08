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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Base class that acts as a bridge to either columns or rows.
 */
abstract class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow {

    /**
     * Package private to limit subclassing.
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
        if (this.count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowDelete.delete(this);
        }
    }

    final void insert() {
        if (this.count > 0) {
            BasicSpreadsheetEngineDeleteOrInsertColumnOrRowInsert.insert(this);
        }
    }

    // delete....................................................................................................

    /**
     * Deletes the selected range of row or columns.
     */
    final void deleteColumnOrRowRange(final int start, final int count) {
        for (int i = 0; i < count; i++) {
            this.delete(start + i);
        }
    }

    /**
     * Delete all the cells in the selected row/column.
     */
    final void delete(final int columnOrRow) {
        this.deleteCells(columnOrRow);
        this.deleteColumnOrRow(columnOrRow);
    }

    private void deleteCells(final int columnOrRow) {
        this.cells(columnOrRow)
            .forEach(c -> this.cellStore().delete(c.reference()));
    }

    abstract void deleteColumnOrRow(final int columnOrRow);

    // move .............................................................................................................

    /**
     * Moves all the cells in the selected row/column.
     */
    final void move(final int columnOrRow) {
        this.moveCells(columnOrRow);
        this.moveColumnOrRows(columnOrRow);
    }

    private void moveCells(final int columnOrRow) {
        this.cells(columnOrRow)
            .forEach(this::moveCell);
    }

    /**
     * Moves the cell to its new location, expressions will be updated later.
     */
    private void moveCell(final SpreadsheetCell cell) {
        final SpreadsheetCellReference reference = cell.reference();

        this.deleteCell(reference);

        final SpreadsheetCell fixed = cell.setReference(this.fixCellReference(cell.reference()));
        if (!fixed.equals(cell)) {
            this.saveCell(fixed);
        }
    }

    abstract void moveColumnOrRows(final int columnOrRow);

    // fix references in all cells .............................................................................

    /**
     * Scans all cell expressions for references and fixes those needing fixing.
     */
    final void fixAllExpressionReferences() {
        final int rows = this.maxRow();
        for (int i = SpreadsheetSelection.MIN_ROW; i <= rows; i++) {
            this.fixRowReferences(SpreadsheetReferenceKind.ABSOLUTE.row(i));
        }
    }

    private void fixRowReferences(final SpreadsheetRowReference row) {
        this.rowCells(row)
            .forEach(this::fixExpressionReferences);
    }

    /**
     * Attempts to parse the formula if necessary and then update cell references that may have shifted due to a
     * delete or insert and if the cell changed saves the updated.
     */
    private void fixExpressionReferences(final SpreadsheetCell cell) {
        final SpreadsheetCell fixed = this.engine.parseFormulaIfNecessary(
            cell,
            this::fixExpressionReferences0,
            this.context
        );
        if (!cell.equals(fixed)) {
            this.saveCell(fixed);
        }
    }

    /**
     * Updates any column/row references within any {@link CellSpreadsheetFormulaParserToken} in the given {@link SpreadsheetFormulaParserToken}.
     */
    private SpreadsheetFormulaParserToken fixExpressionReferences0(final SpreadsheetFormulaParserToken token) {
        return token.replaceIf(
            (t) -> t instanceof CellSpreadsheetFormulaParserToken,// predicate
            (c) -> {
                boolean invalid = false;

                List<ParserToken> newChildren = Lists.array();
                for (final ParserToken parserToken : c.children()) {
                    ParserToken add = parserToken;
                    if (parserToken instanceof ColumnSpreadsheetFormulaParserToken) {
                        add = this.fixColumnReferenceParserToken(
                            parserToken.cast(ColumnSpreadsheetFormulaParserToken.class)
                        ).orElse(null);
                    } else {
                        if (parserToken instanceof RowSpreadsheetFormulaParserToken) {
                            add = this.fixRowReferenceParserToken(
                                parserToken.cast(RowSpreadsheetFormulaParserToken.class)
                            ).orElse(null);
                        }
                    }
                    if (null == add) {
                        invalid = true;
                        break;
                    }
                    newChildren.add(add);
                }

                return invalid ?
                    REF_ERROR :
                    c.setChildren(newChildren);
            }// mapper
        ).cast(SpreadsheetFormulaParserToken.class);
    }

    /**
     * Handles a column {@link ColumnSpreadsheetFormulaParserToken} within an expression.
     * It may be returned unmodified, replaced by an expression if the reference was deleted or simply have the reference adjusted.
     */
    abstract Optional<ColumnSpreadsheetFormulaParserToken> fixColumnReferenceParserToken(final ColumnSpreadsheetFormulaParserToken token);

    /**
     * Handles a column {@link RowSpreadsheetFormulaParserToken} within an expression.
     * It may be returned unmodified, replaced by an expression if the reference was deleted or simply have the reference adjusted.
     */
    abstract Optional<RowSpreadsheetFormulaParserToken> fixRowReferenceParserToken(final RowSpreadsheetFormulaParserToken token);

    /**
     * This token will replace any {@link SpreadsheetCellReference} that become invalid such as a reference to a deleted cell.
     */
    private final static SpreadsheetFormulaParserToken REF_ERROR = SpreadsheetFormulaParserToken.error(
        SpreadsheetError.selectionDeleted(),
        SpreadsheetError.selectionDeleted()
            .kind()
            .text()
    );

    /**
     * Adjusts the reference to match the delete or insert column/row value to it still points to the "same" cell.
     */
    abstract SpreadsheetCellReference fixCellReference(final SpreadsheetCellReference reference);

    /**
     * Fixes the given {@link SpreadsheetColumnReference}
     */
    final SpreadsheetColumnReference fixColumnReference(final SpreadsheetColumnReference reference) {
        return reference.add(
            this.deleteOrInsert.fixColumnOrRowReference(this.count)
        );
    }

    /**
     * Fixes the given {@link SpreadsheetRowReference}
     */
    final SpreadsheetRowReference fixRowReference(final SpreadsheetRowReference reference) {
        return reference.add(
            this.deleteOrInsert.fixColumnOrRowReference(this.count)
        );
    }

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
            .forEach(this.deleteOrInsert::fixLabelMapping);
    }

    // DELETE .......................................................................................................

    /**
     * Deletes the mapping because the target was deleted or fixes the reference.
     */
    final void deleteOrFixLabelMapping(final SpreadsheetLabelMapping mapping) {
        new SpreadsheetSelectionVisitor() {
            @Override
            protected void visit(final SpreadsheetCellReference cell) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.deleteOrFixSpreadsheetCellReference(cell, mapping);
            }

            @Override
            protected void visit(final SpreadsheetLabelName label) {
                throw new UnsupportedOperationException(mapping.toString());
            }

            @Override
            protected void visit(final SpreadsheetCellRangeReference cellRange) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.deleteOrFixSpreadsheetCellRange(cellRange, mapping);
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

    private void deleteOrFixSpreadsheetCellRange(final SpreadsheetCellRangeReference range,
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
                rangeBegin.cellRange(rangeEnd),
                mapping);
        }
    }

    // INSERT .....................................................................................................

    /**
     * Fixes a mapping because the target was moved because of inserted columns or rows.
     */
    final void insertFixLabelMapping(final SpreadsheetLabelMapping mapping) {
        new SpreadsheetSelectionVisitor() {
            @Override
            protected void visit(final SpreadsheetCellReference cell) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.insertFixSpreadsheetCellReference(cell, mapping);
            }

            @Override
            protected void visit(final SpreadsheetLabelName label) {
                throw new UnsupportedOperationException(mapping.toString());
            }

            @Override
            protected void visit(final SpreadsheetCellRangeReference cellRange) {
                BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow.this.insertFixSpreadsheetCellRange(cellRange, mapping);
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

    private void insertFixSpreadsheetCellRange(final SpreadsheetCellRangeReference range,
                                               final SpreadsheetLabelMapping mapping) {
        SpreadsheetCellReference begin = range.begin();
        SpreadsheetCellReference end = range.end();

        if (this.columnOrRowValue(begin) >= this.value) {
            begin = this.fixCellReference(begin);
        }
        if (this.columnOrRowValue(end) >= this.value) {
            end = this.fixCellReference(end);
        }

        this.saveLabelIfUpdated(begin.cellRange(end), mapping);
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
        return this.cellStore()
            .rowCount();
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

    final SpreadsheetColumnStore columnStore() {
        return this.context.storeRepository()
            .columns();
    }

    final SpreadsheetCellStore cellStore() {
        return this.context.storeRepository()
            .cells();
    }

    final SpreadsheetRowStore rowStore() {
        return this.context.storeRepository()
            .rows();
    }

    final int value;
    final int count;

    // labels....................................................................................................

    final void fixCellReferenceAndSaveLabel(final SpreadsheetCellReference reference,
                                            final SpreadsheetLabelMapping mapping) {
        this.saveLabelIfUpdated(this.fixCellReference(reference), mapping);
    }

    final void saveLabelIfUpdated(final SpreadsheetExpressionReference reference,
                                  final SpreadsheetLabelMapping mapping) {
        final SpreadsheetLabelMapping updated = mapping.setReference(reference);
        if (mapping != updated) {
            this.labelStore().save(updated);
        }
    }

    final void deleteLabel(final SpreadsheetLabelMapping mapping) {
        this.labelStore().delete(mapping.label());
    }

    final SpreadsheetLabelStore labelStore() {
        return this.context.storeRepository()
            .labels();
    }

    private final BasicSpreadsheetEngine engine;

    private final SpreadsheetEngineContext context;

    BasicSpreadsheetEngineDeleteOrInsertColumnOrRow deleteOrInsert;
}
