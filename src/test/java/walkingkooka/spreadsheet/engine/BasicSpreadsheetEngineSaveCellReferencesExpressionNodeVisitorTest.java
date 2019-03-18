package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.FakeSpreadsheetExpressionReferenceVisitor;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeVisitor;

public final class BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor> {

    @Test
    public void testProcessReferences() {
        BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor.processReferences(ExpressionNode.text("abc123"),
                new FakeSpreadsheetExpressionReferenceVisitor());
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return ExpressionNodeVisitor.class.getSimpleName();
    }

    // ClassTesting..........................................................................

    @Override
    public Class<BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor> type() {
        return BasicSpreadsheetEngineSaveCellReferencesExpressionNodeVisitor.class;
    }
}
