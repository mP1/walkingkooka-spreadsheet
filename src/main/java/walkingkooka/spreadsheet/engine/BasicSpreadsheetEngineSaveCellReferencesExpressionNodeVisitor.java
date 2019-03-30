package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeVisitor;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.ExpressionReferenceNode;

/**
 * Accepts an {@link ExpressionNode} passes all {@link ExpressionReference} to a {@link SpreadsheetExpressionReferenceVisitor}.
 */
final class BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor extends ExpressionNodeVisitor {

    static void processReferences(final ExpressionNode node,
                                  final SpreadsheetExpressionReferenceVisitor visitor) {
        new BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor(visitor).accept(node);
    }

    private BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor(final SpreadsheetExpressionReferenceVisitor visitor) {
        super();
        this.visitor = visitor;
    }

    @Override
    protected void visit(final ExpressionReferenceNode node) {
        this.visitor.accept(node.value());
    }

    private final SpreadsheetExpressionReferenceVisitor visitor;

    @Override
    public String toString() {
        return this.visitor.toString();
    }
}
