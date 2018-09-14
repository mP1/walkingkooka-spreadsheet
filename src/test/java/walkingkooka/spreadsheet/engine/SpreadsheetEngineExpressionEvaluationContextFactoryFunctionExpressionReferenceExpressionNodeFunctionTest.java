package walkingkooka.spreadsheet.engine;

import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.util.FunctionTestCase;

public final class SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunctionTest extends FunctionTestCase<
        SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction,
        ExpressionReference, ExpressionNode> {

    @Override
    protected SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction createFunction() {
        return SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction.with(SpreadsheetEngines.fake());
    }

    @Override
    protected Class<SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction> type() {
        return SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction.class;
    }
}
