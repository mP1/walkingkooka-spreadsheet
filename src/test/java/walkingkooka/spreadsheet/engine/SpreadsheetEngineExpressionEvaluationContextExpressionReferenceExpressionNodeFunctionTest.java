package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.util.FunctionTestCase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionTest extends FunctionTestCase<
        SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction,
        ExpressionReference, Optional<ExpressionNode>> {

    @Test
    public void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(null,
                    this.labelStore(),
                    this.spreadsheetEngineContext());
        });
    }

    @Test
    public void testWithNullLabelStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                    null,
                    this.spreadsheetEngineContext());
        });
    }

    @Test
    public void testWithNullLabelSpreadsheetEngineContextFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.with(this.engine(),
                    this.labelStore(),
                    null);
        });
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
