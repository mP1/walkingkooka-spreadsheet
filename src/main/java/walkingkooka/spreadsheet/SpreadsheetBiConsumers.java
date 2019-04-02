package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.PublicStaticHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class SpreadsheetBiConsumers implements PublicStaticHelper {

    /**
     * {@see ExpressionReferenceSpreadsheetCellReferencesBiConsumer}
     */
    public static BiConsumer<ExpressionReference, Consumer<SpreadsheetCellReference>> expressionReferenceSpreadsheetCellReferences(final SpreadsheetLabelStore labelStore,
                                                                                                                                   final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        return ExpressionReferenceSpreadsheetCellReferencesBiConsumer.with(labelStore, rangeToCellStore);
    }

    private SpreadsheetBiConsumers() {
        throw new UnsupportedOperationException();
    }
}
