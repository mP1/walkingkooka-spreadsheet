package walkingkooka.spreadsheet.store.cellreferences;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetCellReferenceStore} that uses a {@link Map} to store cell-ref to many cell-refs
 */
final class TreeMapSpreadsheetCellReferenceStore implements SpreadsheetCellReferenceStore {

    static TreeMapSpreadsheetCellReferenceStore create() {
        return new TreeMapSpreadsheetCellReferenceStore();
    }

    private TreeMapSpreadsheetCellReferenceStore() {
        super();
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final SpreadsheetCellReference id) {
        checkId(id);
        return Optional.ofNullable(this.cellToReferences.get(id)).map(v -> Sets.readOnly(v));
    }

    @Override
    public Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final SpreadsheetCellReference id) {
        checkId(id);
        this.cellToReferences.remove(id);
    }

    @Override
    public int count() {
        return this.cellToReferences.size();
    }

    @Override
    public void saveReferences(final SpreadsheetCellReference id, final Set<SpreadsheetCellReference> targets) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(targets, "targets");

        final Set<SpreadsheetCellReference> copy = Sets.sorted();
        copy.addAll(targets);

        if (copy.contains(id)) {
            throw new IllegalArgumentException("Target includes " + id + "=" + targets);
        }

        if (copy.isEmpty()) {
            this.cellToReferences.remove(id);
        } else {
            this.cellToReferences.put(id, copy);
        }
    }

    @Override
    public void addReference(final SpreadsheetCellReference id, final SpreadsheetCellReference target) {
        checkId(id);
        checkTarget(target);

        if (target.compareTo(id) == 0) {
            throw new IllegalArgumentException("Target includes " + id + "=" + target);
        }

        Set<SpreadsheetCellReference> targets = this.cellToReferences.get(id);
        if (null != target) {
            targets.add(target);
        } else {
            targets = Sets.ordered();
            targets.add(target);
            this.cellToReferences.put(id, targets);
        }
    }

    @Override
    public void removeReference(final SpreadsheetCellReference id, final SpreadsheetCellReference target) {
        checkId(id);
        checkTarget(target);

        final Set<SpreadsheetCellReference> targets = this.cellToReferences.get(id);
        if (null != target) {
            targets.remove(target);
            if (targets.isEmpty()) {
                this.cellToReferences.remove(id);
            }
        }
    }

    private static void checkId(final SpreadsheetCellReference id) {
        Objects.requireNonNull(id, "id");
    }

    private static void checkTarget(final SpreadsheetCellReference target) {
        Objects.requireNonNull(target, "target");
    }

    private final Map<SpreadsheetCellReference, Set<SpreadsheetCellReference>> cellToReferences = Maps.sorted();

    @Override
    public String toString() {
        return this.cellToReferences.toString();
    }
}
