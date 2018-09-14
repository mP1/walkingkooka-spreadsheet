package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;

/**
 * Template that includes most of the methods that may be used to delete or insert columns or rows,
 * including fixing of cell references.
 */
abstract class BasicSpreadsheetEngineDeleteOrInsertColumnOrRow {

    BasicSpreadsheetEngineDeleteOrInsertColumnOrRow(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super();
        this.columnOrRow = columnOrRow;
        columnOrRow.deleteOrInsert = this;
    }

    abstract boolean isDeletedReference(final SpreadsheetColumnReferenceParserToken row);

    abstract boolean isDeletedReference(final SpreadsheetRowReferenceParserToken row);

    abstract int fixReferenceOffset(final int count);

    abstract void fixLabelMapping(final SpreadsheetLabelMapping mapping);

    final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow;
}
