package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.PublicStaticHelper;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetFunctions implements PublicStaticHelper {

    /**
     * {@see ExpressionReferenceSpreadsheetCellReferenceFunction}
     */
    public static Function<ExpressionReference, Optional<SpreadsheetCellReference>> expressionReferenceSpreadsheetCellReference(final SpreadsheetLabelStore labelStore,
                                                                                                                                final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore) {
        return ExpressionReferenceSpreadsheetCellReferenceFunction.with(labelStore, rangeToCellStore);
    }

    private SpreadsheetFunctions() {
        throw new UnsupportedOperationException();
    }
}
