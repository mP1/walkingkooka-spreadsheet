package walkingkooka.spreadsheet.store.cellreferences;

import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Objects;
import java.util.Set;

public class FakeSpreadsheetCellReferenceStore extends FakeStore<SpreadsheetCellReference, Set<SpreadsheetCellReference>> implements SpreadsheetCellReferenceStore, Fake {

    @Override
    public void saveReferences(final SpreadsheetCellReference id, final Set<SpreadsheetCellReference> targets) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(targets, "targets");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addReference(final SpreadsheetCellReference id,
                             final SpreadsheetCellReference target) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(target, "target");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReference(final SpreadsheetCellReference id,
                                final SpreadsheetCellReference target) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(target, "target");

        throw new UnsupportedOperationException();
    }
}
