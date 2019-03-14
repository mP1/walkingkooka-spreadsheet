package walkingkooka.spreadsheet;

import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.Visiting;

public class FakeSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor implements Fake {

    public FakeSpreadsheetExpressionReferenceVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final ExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(SpreadsheetLabelName label) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        throw new UnsupportedOperationException();
    }
}
