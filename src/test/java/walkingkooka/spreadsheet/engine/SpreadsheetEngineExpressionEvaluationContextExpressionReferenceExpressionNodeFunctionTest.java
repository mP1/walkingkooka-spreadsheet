package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.util.FunctionTestCase;

public final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionTest extends FunctionTestCase<
        SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction,
        ExpressionReference, ExpressionNode> {

    @Test(expected = NullPointerException.class)
    public void testWithNullEngineFails() {
        SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(null,
                this.labelStore(),
                this.spreadsheetEngineContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullLabelStoreFails() {
        SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                null,
                this.spreadsheetEngineContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullLabelSpreadsheetEngineContextFails() {
        SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                this.labelStore(),
                null);
    }

    @Override
    protected SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction createFunction() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                this.labelStore(),
                this.spreadsheetEngineContext());
    }

    private SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    private SpreadsheetLabelStore labelStore() {
        return SpreadsheetLabelStores.fake();
    }

    private SpreadsheetEngineContext spreadsheetEngineContext() {
        return SpreadsheetEngineContexts.fake();
    }

    @Override
    protected Class<SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction> type() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.class;
    }
}
