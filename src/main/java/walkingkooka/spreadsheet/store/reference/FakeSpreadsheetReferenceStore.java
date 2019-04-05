package walkingkooka.spreadsheet.store.reference;

import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class FakeSpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> extends FakeStore<T, Set<SpreadsheetCellReference>> implements SpreadsheetReferenceStore<T>, Fake {

    @Override
    public void saveReferences(final T id, final Set<SpreadsheetCellReference> targets) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(targets, "targets");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        Objects.requireNonNull(targetAndReference, "targetAndReference");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addAddReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        Objects.requireNonNull(targetAndReference, "targetAndReference");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addRemoveReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> loadReferred(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        throw new UnsupportedOperationException();
    }
}
