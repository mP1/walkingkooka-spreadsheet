package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;

public final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor>
        implements VisitorTesting<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor, ExpressionReference> {

    private final static SpreadsheetCellReference CELL = SpreadsheetCellReference.parse("A99");

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), CELL.toString());
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor> type() {
        return BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor.class;
    }

    // VisitingTesting.............................................................................................................

    @Override
    public BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor(CELL, null);
    }
}
