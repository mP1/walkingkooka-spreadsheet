package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.LoadStoreException;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionReference;

/**
 * A visitor which resolves any {@link ExpressionReference} down to a {@link SpreadsheetCellReference}.
 */
final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static SpreadsheetCellReference reference(final ExpressionReference reference,
                                              final SpreadsheetLabelStore store) {
        final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor visitor =
                new SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor(store);
        visitor.accept(reference);
        return visitor.reference;
    }

    // @VisibleForTesting
    SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.reference = reference;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        try {
            this.accept(this.store.loadOrFail(label).reference());
        } catch (final LoadStoreException cause) {
            throw new ExpressionEvaluationException("Unknown label: " + label);
        }
    }

    private final SpreadsheetLabelStore store;

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.reference = range.begin();
    }

    private SpreadsheetCellReference reference = null;

    @Override
    public String toString() {
        return String.valueOf(this.reference);
    }
}