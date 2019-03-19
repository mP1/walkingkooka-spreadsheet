package walkingkooka.spreadsheet.store.reference;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetReferenceStore} that uses a {@link Map} to store an entity to {@link SpreadsheetCellReference}
 */
final class TreeMapSpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> implements SpreadsheetReferenceStore<T> {

    static TreeMapSpreadsheetReferenceStore create() {
        return new TreeMapSpreadsheetReferenceStore();
    }

    private TreeMapSpreadsheetReferenceStore() {
        super();
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final T id) {
        checkId(id);
        return Optional.ofNullable(this.targetToReferences.get(id)).map(v -> Sets.readOnly(v));
    }

    @Override
    public Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final T id) {
        checkId(id);
        this.targetToReferences.remove(id);
    }

    @Override
    public int count() {
        return this.targetToReferences.size();
    }

    @Override
    public void saveReferences(final T id, final Set<SpreadsheetCellReference> referrers) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(referrers, "referrers");

        final Set<SpreadsheetCellReference> copy = Sets.sorted();
        copy.addAll(referrers);

        if (copy.contains(id)) {
            throw new IllegalArgumentException("referrer includes " + id + "=" + referrers);
        }

        if (copy.isEmpty()) {
            this.targetToReferences.remove(id);
        } else {
            this.targetToReferences.put(id, copy);
        }
    }

    @Override
    public void addReference(final T id, final SpreadsheetCellReference referrer) {
        checkId(id);
        checkReferrer(referrer);

        if(id instanceof SpreadsheetCellReference) {
            if (referrer.compareTo(SpreadsheetCellReference.class.cast(id)) == 0) {
                throw new IllegalArgumentException("referrer includes " + id + "=" + referrer);
            }
        }

        Set<SpreadsheetCellReference> referrers = this.targetToReferences.get(id);
        if (null != referrer) {
            referrers.add(referrer);
        } else {
            referrers = Sets.ordered();
            referrers.add(referrer);
            this.targetToReferences.put(id, referrers);
        }
    }

    @Override
    public void removeReference(final T id, final SpreadsheetCellReference referrer) {
        checkId(id);
        checkReferrer(referrer);

        final Set<SpreadsheetCellReference> referrers = this.targetToReferences.get(id);
        if (null != referrer) {
            referrers.remove(referrer);
            if (referrers.isEmpty()) {
                this.targetToReferences.remove(id);
            }
        }
    }

    private void checkId(final T id) {
        Objects.requireNonNull(id, "id");
    }

    private static void checkReferrer(final SpreadsheetCellReference referrer) {
        Objects.requireNonNull(referrer, "referrer");
    }

    private final Map<T, Set<SpreadsheetCellReference>> targetToReferences = Maps.sorted();

    @Override
    public String toString() {
        return this.targetToReferences.toString();
    }
}
