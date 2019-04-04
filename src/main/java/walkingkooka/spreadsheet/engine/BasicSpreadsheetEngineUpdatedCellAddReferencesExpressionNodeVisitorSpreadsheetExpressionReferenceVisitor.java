package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that adds references to each reference present within cell formula.
 */
final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor with(final SpreadsheetCellReference target,
                                                                                                                         final BasicSpreadsheetEngine engine) {
        return new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor(target, engine);
    }

    // VisibleForTesting
    BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor(final SpreadsheetCellReference target,
                                                                                                             final BasicSpreadsheetEngine engine) {
        super();
        this.target = target;
        this.engine = engine;
    }

    @Override
    final protected void visit(final SpreadsheetCellReference reference) {
        this.engine.cellReferencesStore.addReference(this.target, reference);
    }

    @Override
    final protected void visit(final SpreadsheetLabelName label) {
        this.engine.labelReferencesStore.addReference(label, this.target);
    }

    @Override
    final protected void visit(final SpreadsheetRange range) {
        this.engine.rangeToCellStore.addValue(range, this.target);
    }

    /**
     * The target cell.
     */
    private final SpreadsheetCellReference target;

    /**
     * The engine holds stores which will have references to this cell updated.
     */
    private final BasicSpreadsheetEngine engine;

    @Override
    public final String toString() {
        return this.target.toString();
    }
}