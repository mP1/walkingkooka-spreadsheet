package walkingkooka.spreadsheet.store.reference;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A read only wrapper around a {@link SpreadsheetReferenceStore}
 */
final class ReadOnlySpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> implements SpreadsheetReferenceStore<T> {

    static <T extends ExpressionReference & Comparable<T>> ReadOnlySpreadsheetReferenceStore<T> with(final SpreadsheetReferenceStore<T> store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetReferenceStore<>(store);
    }

    private ReadOnlySpreadsheetReferenceStore(final SpreadsheetReferenceStore<T> store) {
        this.store = store;
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final T id) {
        return this.store.load(id);
    }

    @Override
    public Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> references) {
        Objects.requireNonNull(references, "references");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final T id) {
        checkId(id);
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<T> ids(final int from, final int count) {
        return this.store.ids(from, count);
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final T from, final int count) {
        return this.store.values(from, count);
    }

    @Override
    public void saveReferences(final T id, final Set<SpreadsheetCellReference> referrers) {
        checkId(id);
        Objects.requireNonNull(referrers, "referrers");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addReference(final T id, final SpreadsheetCellReference target) {
        checkId(id);
        checkTarget(target);

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReference(final T id, final SpreadsheetCellReference target) {
        checkId(id);
        checkTarget(target);

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> loadReferred(final SpreadsheetCellReference reference) {
        return store.loadReferred(reference);
    }

    private SpreadsheetReferenceStore<T> store;

    @Override
    public String toString() {
        return this.store.toString();
    }

    private void checkId(final T id) {
        Objects.requireNonNull(id, "id");
    }

    private static void checkTarget(final SpreadsheetCellReference target) {
        Objects.requireNonNull(target, "target");
    }
}
