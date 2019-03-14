package walkingkooka.spreadsheet.store.label;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;
import walkingkooka.type.MemberVisibility;

public final class TreeMapSpreadsheetLabelStoreSpreadsheetExpressionReferenceVisitorTest implements VisitorTesting<TreeMapSpreadsheetLabelStoreSpreadsheetExpressionReferenceVisitor,
        ExpressionReference> {

    @Override
    public TreeMapSpreadsheetLabelStoreSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new TreeMapSpreadsheetLabelStoreSpreadsheetExpressionReferenceVisitor(null, null);
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return TreeMapSpreadsheetLabelStore.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<TreeMapSpreadsheetLabelStoreSpreadsheetExpressionReferenceVisitor> type() {
        return TreeMapSpreadsheetLabelStoreSpreadsheetExpressionReferenceVisitor.class;
    }
}
