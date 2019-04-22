package walkingkooka.spreadsheet.store.label;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Map;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that visits all mappings, and aims to return only {@link SpreadsheetLabelName} that map to the given {@link SpreadsheetCellReference}.
 */
final class TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static Set<SpreadsheetLabelName> gather(final SpreadsheetCellReference reference,
                                            final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        final TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor visitor = new TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor(reference,
                mappings);

        mappings.values()
                .forEach(visitor::acceptMapping);

        return visitor.labels;
    }

    // VisibleForTesting
    TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor(final SpreadsheetCellReference reference,
                                                                            final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        super();

        this.reference = reference;
        this.mappings = mappings;
    }

    private void acceptMapping(final SpreadsheetLabelMapping mapping) {
        this.add = false;
        this.accept(mapping.reference());
        if (this.add) {
            this.labels.add(mapping.label());
        }
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.add = this.reference.compareTo(reference) == 0;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        if (false == this.add) {
            final SpreadsheetLabelMapping mapping = this.mappings.get(label);
            if (null != mapping) {
                this.accept(mapping.reference());
            }
        }

        if (this.add) {
            this.labels.add(label);
        }
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.add = this.add | range.cellStream()
                .filter(r -> r.compareTo(reference) == 0)
                .count() > 0;
    }

    private final SpreadsheetCellReference reference;
    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings;
    private final Set<SpreadsheetLabelName> labels = Sets.ordered();

    private boolean add;

    @Override
    public String toString() {
        return this.labels.toString();
    }
}
