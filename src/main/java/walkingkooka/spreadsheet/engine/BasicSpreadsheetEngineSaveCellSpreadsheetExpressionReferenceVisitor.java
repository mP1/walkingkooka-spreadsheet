package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

/**
 * Base class for {@link SpreadsheetExpressionReferenceVisitor} used during a {@link SpreadsheetEngine#saveCell(SpreadsheetCell, SpreadsheetEngineContext)}.
 */
abstract class BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitor(final BasicSpreadsheetEngineSaveCell saveCell) {
        super();
        this.saveCell = saveCell;
    }

    @Override
    final protected void visit(final SpreadsheetCellReference reference) {
        final BasicSpreadsheetEngineSaveCell saveCell = this.saveCell;
        this.visit(saveCell.unsaved.reference(), reference, saveCell.engine);
    }

    abstract void visit(final SpreadsheetCellReference reference, final SpreadsheetCellReference target, final BasicSpreadsheetEngine engine);

    @Override
    final protected void visit(final SpreadsheetLabelName label) {
        final BasicSpreadsheetEngineSaveCell saveCell = this.saveCell;
        this.visit(saveCell.unsaved.reference(), label, saveCell.engine);
    }

    abstract void visit(final SpreadsheetCellReference reference, final SpreadsheetLabelName target, final BasicSpreadsheetEngine engine);

    @Override
    final protected void visit(final SpreadsheetRange range) {
        final BasicSpreadsheetEngineSaveCell saveCell = this.saveCell;
        this.visit(saveCell.unsaved.reference(), range, saveCell.engine);
    }

    abstract void visit(final SpreadsheetCellReference reference, final SpreadsheetRange target, final BasicSpreadsheetEngine engine);

    /**
     * The engine holds stores which will have references to this cell updated.
     */
    private final BasicSpreadsheetEngineSaveCell saveCell;

    @Override
    public final String toString() {
        return this.saveCell.toString();
    }
}