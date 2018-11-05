package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;

/**
 * Deletes the selected columns or rows.
 */
final class BasicSpreadsheetEngineDeleteColumnOrRow extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRow {

    static void delete(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow,
                       final SpreadsheetEngineContext context) {
        new BasicSpreadsheetEngineDeleteColumnOrRow(columnOrRow).delete0(context);
    }

    private BasicSpreadsheetEngineDeleteColumnOrRow(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super(columnOrRow);
    }

    /**
     * Delete the selected columns or rows.
     */
    private void delete0(final SpreadsheetEngineContext context) {
        this.columnOrRow.delete(this.columnOrRow.value, this.columnOrRow.count);
        this.move();
        this.columnOrRow.fixAllExpressionReferences(context);
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
    boolean isDeletedReference(final SpreadsheetColumnReferenceParserToken column) {
        return this.isDeletedReference(column.value().value());
    }

    @Override
    boolean isDeletedReference(final SpreadsheetRowReferenceParserToken row) {
        return this.isDeletedReference(row.value().value());
    }

    private boolean isDeletedReference(final int value) {
        final int deleted = this.columnOrRow.value;
        return deleted <= value && value <= deleted + this.columnOrRow.count;
    }

    @Override
    int fixReferenceOffset(final int count) {
        return -count;
    }

    @Override
    void fixLabelMapping(final SpreadsheetLabelMapping mapping) {
        this.columnOrRow.deleteOrFixLabelMapping(mapping);
    }
}
