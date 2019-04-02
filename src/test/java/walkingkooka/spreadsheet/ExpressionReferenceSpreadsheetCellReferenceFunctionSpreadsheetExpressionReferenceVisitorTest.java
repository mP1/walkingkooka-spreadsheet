package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;
import walkingkooka.type.MemberVisibility;

public final class ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitorTest implements
        VisitorTesting<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor, ExpressionReference>,
        ToStringTesting<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor> {

    @Test
    public void testToString() {
        final ExpressionReferenceSpreadsheetCellReferenceFunction f = ExpressionReferenceSpreadsheetCellReferenceFunction.with(SpreadsheetLabelStores.fake(),
                SpreadsheetRangeStores.fake());
        this.toStringAndCheck(new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(f), f.toString() + " null");
    }

    @Override
    public ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(null);
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return ExpressionReferenceSpreadsheetCellReferenceFunction.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor> type() {
        return ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor.class;
    }
}
