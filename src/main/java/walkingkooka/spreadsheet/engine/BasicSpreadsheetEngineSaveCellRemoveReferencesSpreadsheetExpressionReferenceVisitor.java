package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

/**
 * Dispatches the types of {@link walkingkooka.tree.expression.ExpressionReference} to remove references from their respective {@Link Store} for a {@link SpreadsheetCellReference cell}.
 */
final class BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor extends BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitor {

    static BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor with(final BasicSpreadsheetEngineSaveCell saveCell) {
        return new BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor(saveCell);
    }

    private BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor(final BasicSpreadsheetEngineSaveCell saveCell) {
        super(saveCell);
    }

    @Override
    void visit(final SpreadsheetCellReference reference, final SpreadsheetCellReference target, final BasicSpreadsheetEngine engine) {
        engine.cellReferencesStore.removeReference(reference, target);
    }

    @Override
    void visit(final SpreadsheetCellReference reference, final SpreadsheetLabelName target, final BasicSpreadsheetEngine engine) {
        engine.labelReferencesStore.removeReference(target, reference);
    }

    @Override
    void visit(final SpreadsheetCellReference reference, final SpreadsheetRange target, final BasicSpreadsheetEngine engine) {
        engine.rangeToCellStore.removeValue(target, reference);
    }
}
