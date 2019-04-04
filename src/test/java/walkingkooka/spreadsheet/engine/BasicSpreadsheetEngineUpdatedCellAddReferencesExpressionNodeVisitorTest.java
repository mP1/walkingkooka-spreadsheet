package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeVisitor;
import walkingkooka.tree.expression.ExpressionNodeVisitorTesting;

public final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor>
        implements ExpressionNodeVisitorTesting<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor> {

    @Test
    public void testProcessReferences() {
        BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor.processReferences(ExpressionNode.text("abc123"), null, null);
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return ExpressionNodeVisitor.class.getSimpleName();
    }

    // ClassTesting..........................................................................

    @Override
    public Class<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor> type() {
        return BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor.class;
    }

    // VisitingTesting....................................................................................

    @Override
    public BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor createVisitor() {
        return new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor(null, null);
    }
}
