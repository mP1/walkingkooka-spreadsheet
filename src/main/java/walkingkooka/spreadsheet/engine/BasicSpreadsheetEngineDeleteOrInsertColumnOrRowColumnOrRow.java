package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;

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
                                                               final BasicSpreadsheetEngine engine) {
        super();
        this.value = value;
        this.count = count;
        this.engine = engine;
    }

    final void delete() {
        BasicSpreadsheetEngineDeleteColumnOrRow.delete(this);
    }

    final void insert() {
        BasicSpreadsheetEngineInsertColumnOrRow.insert(this);
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
        this.cells(columnOrRow).stream()
                .forEach(c -> this.cellStore().delete(c.reference()));
    }

    // copy .............................................................................................................

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
    final void fixAllExpressionCellReferences() {
        final int rows = this.maxRow();
        for (int i = 0; i <= rows; i++) {
            this.fixRowCellReferences(i);
        }
    }

    private void fixRowCellReferences(final int row) {
        this.rowCells(row).stream()
                .map(this::fixExpressionCellReferences)
                .forEach(this::saveCell);
    }

    /**
     * Attempts to parse the formula if necessary and then update cell references that may have shifted due to a
     * delete or insert.
     */
    private SpreadsheetCell fixExpressionCellReferences(final SpreadsheetCell cell) {
        return cell.setFormula(
                this.engine.parse(
                        cell.formula(),
                        this::fixCellReferencesWithinExpression));
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

    // label mappings............................................................................................

    // fix references in all label mappings .............................................................................

    /**
     * Visits all label mappings and adjusts the given references.
     */
    final void fixAllLabelMappings() {
        this.labelStore().all()
                .stream()
                .filter(this::shouldProcessLabelMapping)
                .forEach(this.deleteOrInsert::fixLabelMapping);
    }

    /**
     * Filter only columns or rows that will be deleted or inserted.
     */
    private boolean shouldProcessLabelMapping(final SpreadsheetLabelMapping mapping) {
        return this.columnOrRowValue(mapping.cell()) >= this.value;
    }

    /**
     * Deletes the mapping because the target was deleted or fixes the reference.
     */
    final void deleteFixReference(final SpreadsheetLabelMapping mapping) {
        if (this.columnOrRowValue(mapping.cell()) >= this.value + this.count) {
            this.fixReferenceAndSave(mapping);
        } else {
            this.labelStore().delete(mapping.label());
        }
    }

    /**
     * Fixes a mapping because the target was moved because of inserted columns or rows.
     */
    final void insertFixReference(final SpreadsheetLabelMapping mapping) {
        this.fixReferenceAndSave(mapping);
    }

    private void fixReferenceAndSave(final SpreadsheetLabelMapping mapping) {
        this.saveLabel(mapping.setCell(this.fixCellReference(mapping.cell())));
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
    final Collection<SpreadsheetCell> rowCells(final int row) {
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

    final void saveLabel(final SpreadsheetLabelMapping mapping) {
        this.labelStore().save(mapping);
    }

    final SpreadsheetLabelStore labelStore() {
        return this.engine.labelStore;
    }

    private final BasicSpreadsheetEngine engine;

    BasicSpreadsheetEngineDeleteOrInsertColumnOrRow deleteOrInsert;
}
