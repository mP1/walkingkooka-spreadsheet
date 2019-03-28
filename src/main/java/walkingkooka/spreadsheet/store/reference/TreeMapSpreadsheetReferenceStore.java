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
        this.deleteAll(id);
    }

    /**
     * Delete the reference and all referrers to that reference.
     */
    private void deleteAll(final T id) {
        // where id=label remove label to cells, then remove cell to label.
        final Set<SpreadsheetCellReference> referrers = this.targetToReferences.remove(id);
        if (null != referrers) {
            this.removeAllReferences(referrers, id);
        }
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

        // complain if adding a reference to self
        if (copy.contains(id)) {
            throw new IllegalArgumentException("referrer includes " + id + "=" + referrers);
        }

        if (copy.isEmpty()) {
            // save is actually a delete...
            this.deleteAll(id);
        } else {
            final Set<SpreadsheetCellReference> previousReferrers = this.targetToReferences.put(id, copy);
            if (null != previousReferrers) {

                for (SpreadsheetCellReference reference : previousReferrers) {
                    if (copy.contains(reference)) {
                        continue;
                    }
                    this.removeReference0(id, reference);
                }

                for (SpreadsheetCellReference reference : copy) {
                    if (previousReferrers.contains(reference)) {
                        continue;
                    }
                    this.addReference0(id, reference);
                }
            } else {
                copy.forEach(r -> this.addReference0(id, r));
            }
        }
    }

    @Override
    public void addReference(final T id, final SpreadsheetCellReference referrer) {
        checkId(id);
        checkReferrer(referrer);

        // complain if adding a reference to self
        if (id instanceof SpreadsheetCellReference) {
            if (referrer.compareTo(SpreadsheetCellReference.class.cast(id)) == 0) {
                throw new IllegalArgumentException("referrer includes " + id + "=" + referrer);
            }
        }

        Set<SpreadsheetCellReference> referrers = this.targetToReferences.get(id);
        if (null == referrers) {
            referrers = Sets.sorted();
            this.targetToReferences.put(id, referrers);
        }
        referrers.add(referrer);

        this.addReference0(id, referrer);
    }

    private void addReference0(final T id, final SpreadsheetCellReference referrer) {
        Set<T> targets = this.referenceToTargets.get(referrer);
        if (null == targets) {
            targets = Sets.sorted();
            this.referenceToTargets.put(referrer, targets);
        }
        targets.add(id);
    }

    @Override
    public void removeReference(final T id, final SpreadsheetCellReference referrer) {
        checkId(id);
        checkReferrer(referrer);

        if(this.removeReference0(id, referrer)) {
            final Set<T> ids = this.referenceToTargets.get(referrer);
            if(null!=ids) {
                if(ids.remove(id)){
                    if(ids.isEmpty()) {
                        this.referenceToTargets.remove(referrer);
                    }
                }
            }
        }
    }

    /**
     * Remove all mappings between a reference and the target(id).
     */
    private void removeAllReferences(Set<SpreadsheetCellReference> referrers, final T id) {
        referrers.forEach(r -> this.removeReference0(id, r));
    }

    private boolean removeReference0(final T id, final SpreadsheetCellReference referrer) {
        final Set<SpreadsheetCellReference> referrers = this.targetToReferences.get(id);
        final boolean removed = null != referrers;
        if (removed) {
            referrers.remove(referrer);
            if (referrers.isEmpty()) {
                this.targetToReferences.remove(id);
            }
        }
        return removed;
    }

    @Override
    public Set<T> loadReferred(final SpreadsheetCellReference referrer) {
        checkReferrer(referrer);

        final Set<T> targets = this.referenceToTargets.get(referrer);
        return null != targets ?
                this.copy(targets) :
                Sets.empty();
    }

    /**
     * Make a defensive copy so future updates do not affect this copy.
     */
    private Set<T> copy(final Set<T> source) {
        final Set<T> copy = Sets.ordered();
        copy.addAll(source);
        return Sets.readOnly(copy);
    }

    // helpers..........................................................................................

    private void checkId(final T id) {
        Objects.requireNonNull(id, "id");
    }

    private static void checkReferrer(final SpreadsheetCellReference referrer) {
        Objects.requireNonNull(referrer, "referrer");
    }

    /**
     * Something like labels and the cell references expressions containing the label.
     */
    private final Map<T, Set<SpreadsheetCellReference>> targetToReferences = Maps.sorted();

    /**
     * The inverse of {@link #targetToReferences}
     */
    private final Map<SpreadsheetCellReference, Set<T>> referenceToTargets = Maps.sorted();

    @Override
    public String toString() {
        return this.targetToReferences.toString();
    }
}
