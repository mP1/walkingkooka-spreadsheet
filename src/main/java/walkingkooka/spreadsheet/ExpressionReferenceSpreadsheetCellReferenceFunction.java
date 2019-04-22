package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Resolves an {@link ExpressionReference} to a {@link SpreadsheetCellReference}.
 */
final class ExpressionReferenceSpreadsheetCellReferenceFunction implements Function<ExpressionReference, Optional<SpreadsheetCellReference>> {

    static ExpressionReferenceSpreadsheetCellReferenceFunction with(final SpreadsheetLabelStore labelStore,
                                                                    final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(rangeToCellStore, "rangeToCellStore");

        return new ExpressionReferenceSpreadsheetCellReferenceFunction(labelStore, rangeToCellStore);
    }

    private ExpressionReferenceSpreadsheetCellReferenceFunction(final SpreadsheetLabelStore labelStore,
                                                                final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        this.labelStore = labelStore;
        this.rangeToCellStore = rangeToCellStore;
    }

    @Override
    public Optional<SpreadsheetCellReference> apply(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor.toSpreadsheetCellReference(reference, this);
    }

    final SpreadsheetLabelStore labelStore;
    final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore;

    @Override
    public String toString() {
        return ExpressionReference.class.getSimpleName() + "->" + SpreadsheetCellReference.class.getSimpleName() + "(" + this.labelStore + " " + this.rangeToCellStore + ")";
    }
}
