package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;

import java.util.Collection;
import java.util.Optional;

/**
 * Performs operations on rows for delete or insertion.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow {

    static BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow with(final int value,
                                                                   final int count,
                                                                   final BasicSpreadsheetEngine engine) {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow(value, count, engine);
    }

    /**
     * Private ctor use static factory.
     */
    private BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow(final int value,
                                                               final int count,
                                                               final BasicSpreadsheetEngine engine) {
        super(value, count, engine);
    }

    @Override
    final int max() {
        return this.maxRow();
    }

    @Override
    final Collection<SpreadsheetCell> cells(final int row) {
        return this.rowCells(row);
    }

    @Override
    SpreadsheetParserToken fixCellReferencesWithinExpression(final SpreadsheetParserToken token) {
        return BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.expressionFixReferences(token,
                this);
    }

    @Override
    Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetColumnReferenceParserToken token) {
        // only fixing rows refs not cols
        return Optional.of(token);
    }

    @Override
    Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetRowReferenceParserToken token) {
        return this.deleteOrInsert.isDeletedReference(token) ?
                INVALID_CELL_REFERENCE :
                this.fixCellReferencesWithinExpression0(token);
    }

    private Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression0(final SpreadsheetRowReferenceParserToken token) {
        final SpreadsheetRowReference old = token.value();
        final int value = old.value();

        SpreadsheetRowReferenceParserToken result = token;

        if (value > this.value) {
            final SpreadsheetRowReference reference = old.setValue(value + this.deleteOrInsert.fixReferenceOffset(this.count));
            result = SpreadsheetParserToken.rowReference(reference, reference.toString());
        }

        return Optional.of(result);
    }

    @Override
    SpreadsheetCellReference fixCellReference(final SpreadsheetCellReference reference) {
        return reference.add(0, this.deleteOrInsert.fixReferenceOffset(this.count));
    }

    @Override
    final int columnOrRowValue(final SpreadsheetCellReference cell) {
        return cell.row().value();
    }
}
