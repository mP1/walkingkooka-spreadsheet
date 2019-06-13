package walkingkooka.spreadsheet.store.label;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;
import walkingkooka.type.JavaVisibility;

public final class TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitorTest implements VisitorTesting<TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor,
        ExpressionReference> {

    @Override
    public TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor(null);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
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
    public Class<TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor> type() {
        return TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor.class;
    }
}
