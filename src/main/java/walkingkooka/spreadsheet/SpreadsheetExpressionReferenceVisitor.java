package walkingkooka.spreadsheet;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.Visiting;
import walkingkooka.tree.visit.Visitor;

import java.util.Objects;

/**
 * A {@link Visitor} for all known implementations of {@link ExpressionReference}.
 */
public abstract class SpreadsheetExpressionReferenceVisitor extends Visitor<ExpressionReference> {

    protected SpreadsheetExpressionReferenceVisitor() {
        super();
    }

    public final void accept(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        if (Visiting.CONTINUE == this.startVisit(reference)) {
            do {
                if (reference instanceof SpreadsheetCellReference) {
                    this.visit(SpreadsheetCellReference.class.cast(reference));
                    break;
                }
                if (reference instanceof SpreadsheetLabelName) {
                    this.visit(SpreadsheetLabelName.class.cast(reference));
                    break;
                }
                if (reference instanceof SpreadsheetRange) {
                    this.visit(SpreadsheetRange.class.cast(reference));
                    break;
                }
                throw new IllegalArgumentException("Unknown reference type: " + reference.getClass().getName() + "=" + reference);
            } while (false);
        }
        this.endVisit(reference);
    }

    protected Visiting startVisit(final ExpressionReference reference) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ExpressionReference reference) {
        // nop
    }

    protected void visit(final SpreadsheetCellReference reference) {
        // nop
    }

    protected void visit(final SpreadsheetLabelName label) {
        // nop
    }

    protected void visit(final SpreadsheetRange range) {
        // nop
    }
}
