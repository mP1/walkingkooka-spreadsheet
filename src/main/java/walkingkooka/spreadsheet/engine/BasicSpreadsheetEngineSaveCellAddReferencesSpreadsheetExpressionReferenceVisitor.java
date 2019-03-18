package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

/**
 * Dispatches the types of {@link walkingkooka.tree.expression.ExpressionReference} to add references from their respective {@Link Store} for a {@link SpreadsheetCellReference cell}.
 */
final class BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor extends BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitor {

    static BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor with(final BasicSpreadsheetEngineSaveCell saveCell) {
        return new BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor(saveCell);
    }

    private BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor(final BasicSpreadsheetEngineSaveCell saveCell) {
        super(saveCell);
    }

    @Override
    void visit(final SpreadsheetCellReference reference, final SpreadsheetCellReference target, final BasicSpreadsheetEngine engine) {
        engine.cellReferencesStore.addReference(reference, target);
    }

    @Override
    void visit(final SpreadsheetCellReference reference, final SpreadsheetLabelName target, final BasicSpreadsheetEngine engine) {
        engine.labelReferencesStore.addReference(target, reference);
    }

    @Override
    void visit(final SpreadsheetCellReference reference, final SpreadsheetRange target, final BasicSpreadsheetEngine engine) {
        engine.rangeToCellStore.addValue(target, reference);
    }
}
