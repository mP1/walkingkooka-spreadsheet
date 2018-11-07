package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.PublicStaticHelper;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetEngines implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngine}
     */
    public static SpreadsheetEngine basic(final SpreadsheetId id,
                                          final SpreadsheetCellStore cellStore,
                                          final SpreadsheetLabelStore labelStore,
                                          final SpreadsheetRangeStore<SpreadsheetConditionalFormattingRule> conditionalFormattingRules) {
        return BasicSpreadsheetEngine.with(id, cellStore, labelStore, conditionalFormattingRules);
    }

    /**
     * {@see FakeSpreadsheetEngine}
     */
    public static SpreadsheetEngine fake() {
        return new FakeSpreadsheetEngine();
    }

    /**
     * {@see SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction}
     */
    public static Function<ExpressionReference, Optional<ExpressionNode>> expressionEvaluationContextExpressionReferenceExpressionNodeFunction(final SpreadsheetEngine engine,
                                                                                                                                               final SpreadsheetLabelStore labelStore,
                                                                                                                                               final SpreadsheetEngineContext context) {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(engine, labelStore, context);
    }

    /**
     * Stop creation
     */
    private SpreadsheetEngines() {
        throw new UnsupportedOperationException();
    }
}
