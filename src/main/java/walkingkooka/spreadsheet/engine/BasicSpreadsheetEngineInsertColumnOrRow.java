package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;

/**
 * Inserts the requested columns or rows.
 */
final class BasicSpreadsheetEngineInsertColumnOrRow extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRow {

    static void insert(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        new BasicSpreadsheetEngineInsertColumnOrRow(columnOrRow).insert0();
    }

    private BasicSpreadsheetEngineInsertColumnOrRow(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super(columnOrRow);
    }

    /**
     * Inserts the requested number of columns or rows.
     */
    private void insert0() {
        this.move();
        this.columnOrRow.fixAllExpressionCellReferences();
        ;
        this.columnOrRow.fixAllLabelMappings();
    }

    private void move() {
        final int value = this.columnOrRow.value;
        final int moveCount = this.columnOrRow.max() - value;
        final int offset = value;

        for (int i = 0; i <= moveCount; i++) {
            this.columnOrRow.move(offset + moveCount - i);
        }
    }

    @Override
    int fixReferenceOffset(final int count) {
        return +count;
    }

    @Override
    boolean isDeletedReference(final SpreadsheetColumnReferenceParserToken column) {
        return false; // no references are ever deleted during an insert.
    }

    @Override
    boolean isDeletedReference(final SpreadsheetRowReferenceParserToken row) {
        return false; // no references are ever deleted during an insert.
    }

    @Override
    void fixLabelMapping(final SpreadsheetLabelMapping mapping) {
        this.columnOrRow.insertFixReference(mapping);
    }
}
