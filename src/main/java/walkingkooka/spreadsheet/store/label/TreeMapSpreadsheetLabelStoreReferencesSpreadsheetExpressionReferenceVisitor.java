package walkingkooka.spreadsheet.store.label;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Map;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that visits all label targets, and aims to return only {@link SpreadsheetCellReference} and {@link SpreadsheetRange}.
 */
final class TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static Set<? super ExpressionReference> gather(final SpreadsheetLabelName label,
                                                   final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        final TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor visitor = new TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor(mappings);
        visitor.accept(label);
        return visitor.references;
    }

    // VisibleForTesting
    TreeMapSpreadsheetLabelStoreReferencesSpreadsheetExpressionReferenceVisitor(final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        super();

        this.mappings = mappings;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.references.add(reference);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        final SpreadsheetLabelMapping mapping = this.mappings.get(label);
        if (null != mapping) {
            final ExpressionReference reference = mapping.reference();
            if (this.seen.add(reference)) {
                this.accept(mapping.reference());
            }
        }
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.references.add(range);
    }

    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings;
    private final Set<ExpressionReference> seen = Sets.hash();
    private final Set<? super ExpressionReference> references = Sets.ordered();

    @Override
    public String toString() {
        return this.references.toString();
    }
}
