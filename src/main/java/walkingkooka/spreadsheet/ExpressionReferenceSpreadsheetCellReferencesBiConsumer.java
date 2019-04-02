package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Finds all {@link SpreadsheetCellReference} for an {@link ExpressionReference} and feeds them to a {@link Consumer}.
 */
final class ExpressionReferenceSpreadsheetCellReferencesBiConsumer implements BiConsumer<ExpressionReference, Consumer<SpreadsheetCellReference>> {

    static ExpressionReferenceSpreadsheetCellReferencesBiConsumer with(final SpreadsheetLabelStore labelStore,
                                                                       final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(rangeToCellStore, "rangeToCellStore");

        return new ExpressionReferenceSpreadsheetCellReferencesBiConsumer(labelStore, rangeToCellStore);
    }

    private ExpressionReferenceSpreadsheetCellReferencesBiConsumer(final SpreadsheetLabelStore labelStore,
                                                                   final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        super();
        this.labelStore = labelStore;
        this.rangeToCellStore = rangeToCellStore;
    }

    @Override
    public void accept(final ExpressionReference reference,
                       final Consumer<SpreadsheetCellReference> spreadsheetCellReferences) {
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(rangeToCellStore, "rangeToCellStore");
        ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor.findSpreadsheetCellReferences(reference,
                this,
                spreadsheetCellReferences);
    }

    final SpreadsheetLabelStore labelStore;
    final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore;

    @Override
    public String toString() {
        return ExpressionReference.class.getSimpleName() + "->Consumer<" + SpreadsheetCellReference.class.getSimpleName() + ">(" + this.labelStore + " " + this.rangeToCellStore + ")";
    }
}
