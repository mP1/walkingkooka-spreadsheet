package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeVisitor;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.ExpressionReferenceNode;

/**
 * Accepts an {@link ExpressionNode} passes all {@link ExpressionReference} to a {@link SpreadsheetExpressionReferenceVisitor}.
 */
final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor extends ExpressionNodeVisitor {

    static void processReferences(final ExpressionNode node,
                                  final SpreadsheetCellReference target,
                                  final BasicSpreadsheetEngine engine) {
        new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor(target, engine).accept(node);
    }

    // VisibleForTesting
    BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor(final SpreadsheetCellReference target,
                                                                        final BasicSpreadsheetEngine engine) {
        super();
        this.target = target;
        this.engine = engine;
    }

    @Override
    protected void visit(final ExpressionReferenceNode node) {
        if (null == this.visitor) {
            this.visitor = BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor.with(this.target,
                    this.engine);
        }
        this.visitor.accept(node.value());
    }

    /**
     * The target cell.
     */
    private final SpreadsheetCellReference target;

    /**
     * The engine holds stores which will have references to this cell updated.
     */
    private final BasicSpreadsheetEngine engine;

    /**
     * Cache of the {@link BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor} that will process each and every encountered {@link ExpressionReferenceNode}.
     */
    private BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor visitor;

    @Override
    public String toString() {
        return this.target.toString();
    }
}
