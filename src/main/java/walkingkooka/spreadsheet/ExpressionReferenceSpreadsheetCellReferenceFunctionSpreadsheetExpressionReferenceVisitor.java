package walkingkooka.spreadsheet;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that resolves labels and ranges to a {@link SpreadsheetCellReference}
 */
final class ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static Optional<SpreadsheetCellReference> toSpreadsheetCellReference(final ExpressionReference reference,
                                                                         final ExpressionReferenceSpreadsheetCellReferenceFunction function) {
        final ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor visitor = new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(function);
        visitor.accept(reference);
        return Optional.ofNullable(visitor.reference);
    }

    // @VisibleForTesting
    ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(final ExpressionReferenceSpreadsheetCellReferenceFunction function) {
        super();
        this.function = function;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.reference = reference;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.function.labelStore.load(label).ifPresent(m -> this.accept(m.reference()));
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.function.rangeToCellStore.load(range).ifPresent(cells -> this.accept(cells.get(0)));
    }

    private final ExpressionReferenceSpreadsheetCellReferenceFunction function;
    SpreadsheetCellReference reference;

    @Override
    public String toString() {
        return this.function + " " + this.reference;
    }
}
