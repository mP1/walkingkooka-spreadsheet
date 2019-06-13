package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;
import walkingkooka.type.JavaVisibility;

public final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitorTest
        implements VisitorTesting<SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor, ExpressionReference> {

    @Override
    public SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor(null);
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunction.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor> type() {
        return SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionNodeFunctionSpreadsheetExpressionReferenceVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
