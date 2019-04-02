package walkingkooka.spreadsheet;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.function.Consumer;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that resolves labels and ranges to a {@link SpreadsheetCellReference}
 */
final class ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static void findSpreadsheetCellReferences(final ExpressionReference reference,
                                              final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores,
                                              final Consumer<SpreadsheetCellReference> references) {
        new ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor(stores, references)
                .accept(reference);
    }

    // @VisibleForTesting
    ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor(final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores,
                                                                                                final Consumer<SpreadsheetCellReference> references) {
        super();
        this.stores = stores;
        this.references = references;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.references.accept(reference);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.stores.labelStore.load(label).ifPresent(m -> this.accept(m.reference()));
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.stores.rangeToCellStore.load(range).ifPresent(r -> r.forEach(this::accept));
    }

    private final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores;
    private final Consumer<SpreadsheetCellReference> references;

    @Override
    public String toString() {
        return this.stores + " " + this.references;
    }
}
