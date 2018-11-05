package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;

import java.util.Collection;
import java.util.Optional;

/**
 * Performs operations on columns for delete or insertion.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow {

    static BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn with(final int value,
                                                                      final int count,
                                                                      final BasicSpreadsheetEngine engine,
                                                                      final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn(value, count, engine, context);
    }

    /**
     * Private ctor use static factory.
     */
    private BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn(final int value,
                                                                  final int count,
                                                                  final BasicSpreadsheetEngine engine,
                                                                  final SpreadsheetEngineContext context) {
        super(value, count, engine, context);
    }

    @Override
    int max() {
        return this.cellStore().columns();
    }

    @Override
    Collection<SpreadsheetCell> cells(final int column) {
        return this.cellStore().column(column);
    }

    @Override
    Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetColumnReferenceParserToken token) {
        return this.deleteOrInsert.isDeletedReference(token) ?
                INVALID_CELL_REFERENCE :
                this.fixCellReferencesWithinExpression0(token);
    }

    private Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression0(final SpreadsheetColumnReferenceParserToken token) {
        final SpreadsheetColumnReference old = token.value();
        final int value = old.value();

        SpreadsheetColumnReferenceParserToken result = token;

        if (value > this.value) {
            final SpreadsheetColumnReference reference = old.setValue(value + this.deleteOrInsert.fixReferenceOffset(this.count));
            result = SpreadsheetParserToken.columnReference(reference, reference.toString());
        }

        return Optional.of(result);
    }

    @Override
    Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetRowReferenceParserToken token) {
        // only fixing cols refs not rows
        return Optional.of(token);
    }

    @Override
    SpreadsheetCellReference fixCellReference(final SpreadsheetCellReference reference) {
        return reference.add(this.deleteOrInsert.fixReferenceOffset(this.count), 0);
    }

    @Override
    int columnOrRowValue(final SpreadsheetCellReference cell) {
        return cell.column().value();
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
