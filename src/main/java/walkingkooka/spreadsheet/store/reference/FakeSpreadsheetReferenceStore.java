package walkingkooka.spreadsheet.store.reference;

import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Set;

public class FakeSpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> extends FakeStore<T, Set<SpreadsheetCellReference>> implements SpreadsheetReferenceStore<T>, Fake {

    @Override
    public void saveReferences(final T id, final Set<SpreadsheetCellReference> targets) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(targets, "targets");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addReference(final T id,
                             final SpreadsheetCellReference target) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(target, "target");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReference(final T id,
                                final SpreadsheetCellReference target) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(target, "target");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> loadReferred(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        throw new UnsupportedOperationException();
    }
}
